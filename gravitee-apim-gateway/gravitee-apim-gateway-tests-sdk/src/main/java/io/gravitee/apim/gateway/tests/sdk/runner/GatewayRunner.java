/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.apim.gateway.tests.sdk.runner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.apim.gateway.tests.sdk.AbstractGatewayTest;
import io.gravitee.apim.gateway.tests.sdk.annotations.DeployApi;
import io.gravitee.apim.gateway.tests.sdk.annotations.GatewayTest;
import io.gravitee.apim.gateway.tests.sdk.configuration.GatewayConfigurationBuilder;
import io.gravitee.apim.gateway.tests.sdk.configuration.GatewayMode;
import io.gravitee.apim.gateway.tests.sdk.connector.ConnectorBuilder;
import io.gravitee.apim.gateway.tests.sdk.connector.EndpointBuilder;
import io.gravitee.apim.gateway.tests.sdk.container.GatewayTestContainer;
import io.gravitee.apim.gateway.tests.sdk.converters.ApiDeploymentPreparer;
import io.gravitee.apim.gateway.tests.sdk.converters.LegacyApiDeploymentPreparer;
import io.gravitee.apim.gateway.tests.sdk.converters.V4ApiDeploymentPreparer;
import io.gravitee.apim.gateway.tests.sdk.plugin.PluginManifestLoader;
import io.gravitee.apim.gateway.tests.sdk.policy.KeylessPolicy;
import io.gravitee.apim.gateway.tests.sdk.policy.PolicyBuilder;
import io.gravitee.apim.gateway.tests.sdk.reporter.FakeReporter;
import io.gravitee.common.component.Lifecycle;
import io.gravitee.common.event.impl.SimpleEvent;
import io.gravitee.connector.http.HttpConnectorFactory;
import io.gravitee.definition.jackson.datatype.GraviteeMapper;
import io.gravitee.definition.model.Api;
import io.gravitee.definition.model.DefinitionVersion;
import io.gravitee.gateway.handlers.api.manager.ApiManager;
import io.gravitee.gateway.platform.Organization;
import io.gravitee.gateway.platform.manager.OrganizationManager;
import io.gravitee.gateway.reactor.ReactableApi;
import io.gravitee.gateway.standalone.vertx.VertxEmbeddedContainer;
import io.gravitee.node.reporter.ReporterManager;
import io.gravitee.plugin.connector.ConnectorPlugin;
import io.gravitee.plugin.connector.ConnectorPluginManager;
import io.gravitee.plugin.core.api.ConfigurablePluginManager;
import io.gravitee.plugin.core.api.PluginEvent;
import io.gravitee.plugin.core.api.PluginManifest;
import io.gravitee.plugin.core.internal.PluginEventListener;
import io.gravitee.plugin.core.internal.PluginFactory;
import io.gravitee.plugin.core.internal.PluginImpl;
import io.gravitee.plugin.endpoint.EndpointConnectorPlugin;
import io.gravitee.plugin.endpoint.EndpointConnectorPluginManager;
import io.gravitee.plugin.endpoint.mock.MockEndpointConnectorFactory;
import io.gravitee.plugin.entrypoint.EntrypointConnectorPlugin;
import io.gravitee.plugin.entrypoint.EntrypointConnectorPluginManager;
import io.gravitee.plugin.policy.PolicyPlugin;
import io.gravitee.plugin.resource.ResourcePlugin;
import io.gravitee.reporter.api.Reporter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.junit.platform.commons.PreconditionViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

/**
 * <pre>
 * Manages the lifecycle of the testing Gateway Instance.
 * Before tests, it will configure (properties and plugin registration) and start the gateway, deploy the apis declared at class level then at method level with {@link DeployApi}.
 * After tests, it will gracefully undeploy all the apis and then stop the Gateway.
 * </pre>
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public class GatewayRunner {

    public static final String DEFAULT_CONFIGURATION_FOLDER = "/gravitee-default";

    public static final Logger LOGGER = LoggerFactory.getLogger(GatewayRunner.class);
    public static final String ALREADY_DEPLOYED_MESSAGE = "An API has already been deployed with id {%s}";

    private final GatewayConfigurationBuilder gatewayConfigurationBuilder;
    private final AbstractGatewayTest testInstance;
    private final GatewayMode gatewayMode;
    private final ObjectMapper graviteeMapper;
    private final Map<String, ReactableApi<?>> deployedForTestClass;
    private final Map<String, ReactableApi<?>> deployedForTest;
    private final Map<DefinitionVersion, ApiDeploymentPreparer> apiDeploymentPreparers;
    private Organization deployedOrganization = null;
    private GatewayTestContainer gatewayContainer;
    private VertxEmbeddedContainer vertxContainer;
    private Path tempDir;
    private boolean isRunning = false;

    public GatewayRunner(GatewayConfigurationBuilder gatewayConfigurationBuilder, AbstractGatewayTest testInstance) {
        this.gatewayConfigurationBuilder = gatewayConfigurationBuilder;
        this.testInstance = testInstance;
        this.gatewayMode = testInstance.getClass().getAnnotation(GatewayTest.class).mode();
        graviteeMapper = new GraviteeMapper();
        deployedForTestClass = new HashMap<>();
        deployedForTest = new HashMap<>();

        // Allow test instance to access api deployed at class level
        testInstance.setDeployedClassApis(deployedForTestClass);

        apiDeploymentPreparers =
            Map.of(
                DefinitionVersion.V1,
                new LegacyApiDeploymentPreparer(),
                DefinitionVersion.V2,
                new LegacyApiDeploymentPreparer(),
                DefinitionVersion.V4,
                new V4ApiDeploymentPreparer()
            );
    }

    /**
     * Configures the testing gateway:
     *  - uses the right `gravite.home` and `gravitee.conf`
     *  - overrides variables thanks to {@link AbstractGatewayTest#configureGateway(GatewayConfigurationBuilder)}
     *  - registers the reporters, connectors, policies and resources
     *  And then, starts the gateway
     * @param gatewayPort is the port used to reach the apis deployed on the gateway
     * @param technicalApiPort is the port used to reach the technical api.
     * @throws Exception
     */
    public void configureAndStart(int gatewayPort, int technicalApiPort) throws IOException, InterruptedException {
        try {
            configure(gatewayPort, technicalApiPort);
            gatewayContainer = new GatewayTestContainer();
            final ApplicationContext applicationContext = gatewayContainer.applicationContext();

            testInstance.setApplicationContext(applicationContext);

            registerReporters(gatewayContainer);

            registerEntrypoints(gatewayContainer);

            registerConnectors(gatewayContainer);

            registerEndpoints(gatewayContainer);

            registerPolicies(gatewayContainer);

            registerResources(gatewayContainer);

            vertxContainer = startServer(gatewayContainer);
            isRunning = true;
        } finally {
            removeTemporaryFolderIfNeeded();
        }
    }

    private void configure(int gatewayPort, int technicalApiPort) throws IOException {
        String graviteeHome;
        final String homeFolder = testInstance.getClass().getAnnotation(GatewayTest.class).configFolder();

        // Try to get graviteeHome from jar. Useful for policies having a maven dependency to this sdk.
        graviteeHome = loadConfigurationFromJar(homeFolder);

        // If no graviteeHome, get the resource in the module.
        if (graviteeHome == null) {
            final URL home = GatewayRunner.class.getResource(homeFolder);
            if (home == null) {
                throw new IllegalStateException("Configuration folder for gateway does not exists");
            }
            graviteeHome = URLDecoder.decode(home.getPath(), StandardCharsets.UTF_8.name());
        }

        System.setProperty("gravitee.home", graviteeHome);
        System.setProperty("gravitee.conf", graviteeHome + File.separator + "config" + File.separator + "gravitee.yml");

        System.setProperty("api.jupiterMode.enabled", gatewayMode.getJupiterEnabled().toString());
        gatewayConfigurationBuilder.build().forEach((k, v) -> System.setProperty(String.valueOf(k), String.valueOf(v)));
        System.setProperty("http.port", String.valueOf(gatewayPort));
        System.setProperty("services.core.http.port", String.valueOf(technicalApiPort));
        System.setProperty("services.core.http.enabled", String.valueOf(false));
    }

    /**
     * Allow users of the SDK to use it without creating a dedicated /resources/gravitee-default folder containing the configuration.
     *
     * The Gateway is loading configuration for "graviteeProperties" with a FileSystemResource. If the resource is inside a jar, it throws an error.
     *
     * This method create a temporary folder to copy sdk default configuration in.
     * This temporary folder will be removed when gateway stops or an exception occurs.
     *
     * @param homeFolder is the home folder configured by {@link GatewayTest#configFolder()}. We load from jar only if value is equal to {@link GatewayRunner#DEFAULT_CONFIGURATION_FOLDER}
     * @return
     * @throws IOException
     */
    private String loadConfigurationFromJar(String homeFolder) throws IOException {
        if (DEFAULT_CONFIGURATION_FOLDER.equals(homeFolder)) {
            final URL configFolderURL = getClass().getResource(DEFAULT_CONFIGURATION_FOLDER);
            assert configFolderURL != null;
            final URLConnection urlConnection = configFolderURL.openConnection();
            if (urlConnection instanceof JarURLConnection && Files.notExists(Path.of(DEFAULT_CONFIGURATION_FOLDER))) {
                final Path targetDir = Files.createTempDirectory(String.format("%s-config", testInstance.getClass().getSimpleName()));
                tempDir = targetDir;

                copyJarResourcesRecursively(targetDir, (JarURLConnection) urlConnection);
                return targetDir + DEFAULT_CONFIGURATION_FOLDER;
            }
        }
        return null;
    }

    /**
     * Stops the gateway and remove temporary configuration folder if needed.
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException {
        stopServer(gatewayContainer, vertxContainer);
    }

    /**
     * Deploys an API, declared at class level, thanks to it definition
     * @param apiDefinitionPath is the definition file of the api to deploy
     * @throws Exception
     */
    public void deployForClass(String apiDefinitionPath) throws IOException {
        final ReactableApi<?> reactableApi = toReactableApi(apiDefinitionPath);
        if (deployedForTestClass.containsKey(reactableApi.getId())) {
            throw new PreconditionViolationException(String.format(ALREADY_DEPLOYED_MESSAGE, reactableApi.getId()));
        }
        deploy(reactableApi, deployedForTestClass);
    }

    /**
     * Deploys an Organization, thanks to {@link io.gravitee.apim.gateway.tests.sdk.annotations.DeployOrganization}
     * @param organizationDefinition is the definition of the organization to deploy
     * @throws Exception
     */
    public void deployOrganization(String organizationDefinition) throws IOException {
        final Organization organization = loadOrganizationDefinition(organizationDefinition);

        OrganizationManager organizationManager = gatewayContainer.applicationContext().getBean(OrganizationManager.class);

        testInstance.ensureMinimalRequirementForOrganization(organization);
        organization.setUpdatedAt(new Date());

        try {
            organizationManager.register(organization);
            deployedOrganization = organization;
            // When deploying an organization at method level, it's important to set the organization id on every already deployed apis (at class level)
            final ApiManager apiManager = gatewayContainer.applicationContext().getBean(ApiManager.class);
            apiManager.apis().forEach(api -> api.setOrganizationId(deployedOrganization.getId()));
        } catch (Exception e) {
            LOGGER.error("An error occurred deploying the organization {}: {}", organization.getId(), e.getMessage());
            throw e;
        }
    }

    /**
     * Deploys an API, declared at method level, thanks to it definition
     * @param apiDefinition is the definition of the api to deploy
     * @throws Exception
     */
    public void deployForTest(String apiDefinition) throws IOException {
        final ReactableApi<?> reactableApi = toReactableApi(apiDefinition);
        if (deployedForTestClass.containsKey(reactableApi.getId()) || deployedForTest.containsKey(reactableApi.getId())) {
            throw new PreconditionViolationException(String.format(ALREADY_DEPLOYED_MESSAGE, reactableApi.getId()));
        }
        deploy(reactableApi, deployedForTest);
    }

    /**
     * Access the list of deployed API for the current test.
     * It allows to update an API directly for testing purpose, for example, the {@link io.gravitee.definition.model.Endpoint.Status}
     * @return the list of deployed API for the current test.
     */
    public Map<String, ReactableApi<?>> deployedApis() {
        return deployedForTest;
    }

    private ReactableApi<?> toReactableApi(String apiDefinitionPath) throws IOException {
        final DefinitionVersion definitionVersion = extractApiDefinitionVersion(apiDefinitionPath);
        final ReactableApi<?> reactableApi;
        if (DefinitionVersion.V4.equals(definitionVersion)) {
            final io.gravitee.definition.model.v4.Api api = loadApiDefinition(apiDefinitionPath, io.gravitee.definition.model.v4.Api.class);
            reactableApi = apiDeploymentPreparers.get(definitionVersion).toReactable(api);
        } else {
            final Api api = loadApiDefinition(apiDefinitionPath, Api.class);
            reactableApi = apiDeploymentPreparers.get(definitionVersion).toReactable(api);
        }
        return reactableApi;
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Deploy an api and add it to the map belonging to its context (Class or Method).
     * Before deploying the api, we can enrich configuration if user has overridden {@link AbstractGatewayTest#configureApi(Api)}
     * Then, we ensure the api met the minimal requirement before been deployed.
     * @param reactableApi the ReactableApi to deploy
     * @param deployedApis the map containing deployed apis.
     */
    private void deploy(ReactableApi<?> reactableApi, Map<String, ReactableApi<?>> deployedApis) {
        ApiManager apiManager = gatewayContainer.applicationContext().getBean(ApiManager.class);

        if (!DefinitionVersion.V4.equals(reactableApi.getDefinitionVersion())) {
            final Api api = (Api) reactableApi.getDefinition();
            api.setExecutionMode(gatewayMode.getExecutionMode());
            testInstance.configureApi(api);
        }

        testInstance.configureApi(reactableApi, reactableApi.getDefinition().getClass());

        ensureMinimalRequirementForApi(reactableApi);

        try {
            reactableApi.setDeployedAt(new Date());
            // For each new deployed API, set the organization id if one deployed
            if (deployedOrganization != null) {
                reactableApi.setOrganizationId(deployedOrganization.getId());
            }
            apiManager.register(reactableApi);
        } catch (Exception e) {
            LOGGER.error("An error occurred deploying the api {}: {}", reactableApi.getId(), e.getMessage());
            throw e;
        }
        deployedApis.put(reactableApi.getId(), reactableApi);
    }

    /**
     * Undeploys APIs declared at class level.
     */
    public void undeployForClass() {
        deployedForTestClass.forEach((key, value) -> undeploy(value));
        deployedForTestClass.clear();
    }

    /**
     * Undeploys APIs declared at method level.
     */
    public void undeployForTest() {
        deployedForTest.forEach((key, value) -> undeploy(value));
        deployedForTest.clear();
    }

    private void undeploy(ReactableApi<?> api) {
        ApiManager apiManager = gatewayContainer.applicationContext().getBean(ApiManager.class);
        apiManager.unregister(api.getId());
    }

    public void undeployOrganization() {
        OrganizationManager organizationManager = gatewayContainer.applicationContext().getBean(OrganizationManager.class);
        if (organizationManager.getCurrentOrganization() != null) {
            organizationManager.unregister(organizationManager.getCurrentOrganization().getId());
        }
        deployedOrganization = null;
    }

    private VertxEmbeddedContainer startServer(GatewayTestContainer container) throws InterruptedException {
        final Thread starterThread = new Thread(() -> {
            try {
                container.start();
            } catch (Exception e) {
                System.exit(-1);
            }
        });

        starterThread.start();

        final VertxEmbeddedContainer vertxEmbeddedContainer = container.applicationContext().getBean(VertxEmbeddedContainer.class);

        // First, wait for the vertxEmbeddedContainer to be started
        while (vertxEmbeddedContainer.lifecycleState() != Lifecycle.State.STARTED) {
            Thread.sleep(5);
        }
        // Then, wait for the GatewayTestContainer to be started
        while (container.lifecycleState() != Lifecycle.State.STARTED) {
            Thread.sleep(5);
        }
        return vertxEmbeddedContainer;
    }

    private void stopServer(GatewayTestContainer container, VertxEmbeddedContainer vertxContainer) throws InterruptedException {
        if (container != null) {
            final Thread stopThread = new Thread(() -> {
                try {
                    container.stop();
                } catch (Exception e) {
                    System.exit(-1);
                }
            });

            stopThread.start();

            while (vertxContainer.lifecycleState() != Lifecycle.State.STOPPED) {
                Thread.sleep(5);
            }
        }
    }

    private void ensureMinimalRequirementForApi(ReactableApi<?> reactableApi) {
        apiDeploymentPreparers.get(reactableApi.getDefinitionVersion()).ensureMinimalRequirementForApi(reactableApi.getDefinition());
    }

    private void registerReporters(GatewayTestContainer container) {
        ReporterManager reporterManager = container.applicationContext().getBean(ReporterManager.class);

        Map<String, Reporter> reportersMap = new HashMap<>();
        testInstance.configureReporters(reportersMap);
        ensureMinimalRequirementForReporters(container, reportersMap);
        reportersMap.forEach((key, value) -> reporterManager.register(value));
    }

    private void ensureMinimalRequirementForReporters(GatewayTestContainer container, Map<String, Reporter> reporters) {
        reporters.putIfAbsent("fakeReporter", (FakeReporter) container.applicationContext().getBean("fakeReporter"));
    }

    private void registerResources(GatewayTestContainer container) {
        String[] resourceBeanNamesForType = container
            .applicationContext()
            .getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurablePluginManager.class, ResourcePlugin.class));

        final ConfigurablePluginManager<ResourcePlugin> resourceManager = (ConfigurablePluginManager<ResourcePlugin>) container
            .applicationContext()
            .getBean(resourceBeanNamesForType[0]);

        Map<String, ResourcePlugin> resourcesMap = new HashMap<>();
        testInstance.configureResources(resourcesMap);
        resourcesMap.forEach((key, value) -> resourceManager.register(value));
    }

    private void registerPolicies(GatewayTestContainer container) {
        String[] policyBeanNamesForType = container
            .applicationContext()
            .getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurablePluginManager.class, PolicyPlugin.class));
        final ConfigurablePluginManager<PolicyPlugin> policyManager = (ConfigurablePluginManager<PolicyPlugin>) container
            .applicationContext()
            .getBean(policyBeanNamesForType[0]);

        Map<String, PolicyPlugin> policyMap = new HashMap<>();
        loadPolicyAsAPlugin(container.applicationContext(), policyMap);

        testInstance.configurePolicies(policyMap);
        ensureMinimalRequirementForPolicies(policyMap);
        policyMap.forEach((key, value) -> policyManager.register(value));
    }

    /**
     * This method will load the PluginManifest generated from "plugin.properties" file, load the plugin needed by the user and then fake the {@link PluginEvent#DEPLOYED} and {@link PluginEvent#ENDED} to apply the initialization phase of a policy (see {@link io.gravitee.policy.api.PolicyContext})
     * @param applicationContext is the application context of the container
     * @param policies is the Map of policies to initialize
     */
    private void loadPolicyAsAPlugin(ApplicationContext applicationContext, Map<String, PolicyPlugin> policies) {
        final PluginManifest manifest = PluginManifestLoader.readManifest();
        testInstance.loadPolicy(manifest, policies);
        if (manifest != null && !policies.isEmpty()) {
            final PluginEventListener pluginEventListener = applicationContext.getBean(PluginEventListener.class);
            final PluginImpl plugin = (PluginImpl) PluginFactory.from(manifest);
            plugin.setDependencies(new URL[0]);
            pluginEventListener.onEvent(new SimpleEvent<>(PluginEvent.DEPLOYED, plugin));
            pluginEventListener.onEvent(new SimpleEvent<>(PluginEvent.ENDED, null));
        }
    }

    private void ensureMinimalRequirementForPolicies(Map<String, PolicyPlugin> policies) {
        policies.putIfAbsent("api-key", PolicyBuilder.build("api-key", KeylessPolicy.class));
        policies.putIfAbsent("key-less", PolicyBuilder.build("key-less", KeylessPolicy.class));
        policies.putIfAbsent("oauth2", PolicyBuilder.build("oauth2", KeylessPolicy.class));
        policies.putIfAbsent("jwt", PolicyBuilder.build("jwt", KeylessPolicy.class));
    }

    private void registerConnectors(GatewayTestContainer container) {
        ConnectorPluginManager connectorPluginManager = container.applicationContext().getBean(ConnectorPluginManager.class);

        Map<String, ConnectorPlugin> connectorsMap = new HashMap<>();
        testInstance.configureConnectors(connectorsMap);
        ensureMinimalRequirementForConnectors(connectorsMap);
        connectorsMap.forEach((key, value) -> connectorPluginManager.register(value));
    }

    private void ensureMinimalRequirementForConnectors(Map<String, ConnectorPlugin> connectors) {
        connectors.putIfAbsent("connector-http", ConnectorBuilder.build("connector-http", HttpConnectorFactory.class));
    }

    private void registerEntrypoints(GatewayTestContainer container) {
        final EntrypointConnectorPluginManager entrypointPluginManager = container
            .applicationContext()
            .getBean(EntrypointConnectorPluginManager.class);

        Map<String, EntrypointConnectorPlugin<?, ?>> entrypointsMap = new HashMap<>();
        testInstance.configureEntrypoints(entrypointsMap);
        entrypointsMap.forEach((key, value) -> entrypointPluginManager.register(value));
    }

    private void registerEndpoints(GatewayTestContainer container) {
        final EndpointConnectorPluginManager endpointPluginManager = container
            .applicationContext()
            .getBean(EndpointConnectorPluginManager.class);

        Map<String, EndpointConnectorPlugin<?, ?>> endpointsMap = new HashMap<>();
        testInstance.configureEndpoints(endpointsMap);
        ensureMinimalRequirementForEndpoints(endpointsMap);
        endpointsMap.forEach((key, value) -> endpointPluginManager.register(value));
    }

    private void ensureMinimalRequirementForEndpoints(Map<String, EndpointConnectorPlugin<?, ?>> endpoints) {
        endpoints.putIfAbsent("mock", EndpointBuilder.build("mock", MockEndpointConnectorFactory.class));
    }

    private <T> T loadApiDefinition(String apiDefinitionPath, Class<T> toApiClass) throws IOException {
        return loadResource(apiDefinitionPath, toApiClass);
    }

    private Organization loadOrganizationDefinition(String orgDefinitionPath) throws IOException {
        final io.gravitee.definition.model.Organization organization = loadResource(
            orgDefinitionPath,
            io.gravitee.definition.model.Organization.class
        );
        return new Organization(organization);
    }

    private <T> T loadResource(String resourcePath, Class<T> toClass) throws IOException {
        URL jsonFile = loadURL(resourcePath);
        return loadResource(jsonFile, toClass);
    }

    private <T> T loadResource(URL jsonFile, Class<T> toClass) throws IOException {
        return graviteeMapper.readValue(jsonFile, toClass);
    }

    private URL loadURL(String resourcePath) {
        return GatewayRunner.class.getResource(resourcePath);
    }

    /**
     * Utility method to copy a folder from a jar.
     * @param destination is the destination path.
     * @param jarConnection is the connection get from source URL.
     * @throws IOException
     */
    private void copyJarResourcesRecursively(Path destination, JarURLConnection jarConnection) throws IOException {
        JarFile jarFile = jarConnection.getJarFile();
        for (Iterator<JarEntry> it = jarFile.entries().asIterator(); it.hasNext();) {
            JarEntry entry = it.next();
            if (entry.getName().startsWith(jarConnection.getEntryName())) {
                if (entry.getName().contains("./") || entry.getName().contains("../")) {
                    throw new SecurityException("JarEntry trying to access FileSystem with relative path: " + entry.getName());
                }
                if (!entry.isDirectory()) {
                    try (InputStream entryInputStream = jarFile.getInputStream(entry)) {
                        Files.copy(entryInputStream, Paths.get(destination.toString(), entry.getName()));
                    }
                } else {
                    Files.createDirectories(Paths.get(destination.toString(), entry.getName()));
                }
            }
        }
    }

    /**
     * Remove the temporary folder from variable "tempDir". This folder has been created to copy the resources from jar.
     * @throws IOException
     */
    private void removeTemporaryFolderIfNeeded() throws IOException {
        if (tempDir != null) {
            LOGGER.debug("Removing temp folder: {}", tempDir);
            Files.walkFileTree(
                tempDir,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (exc != null) {
                            throw exc;
                        }
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                }
            );
        }
    }

    /**
     * Read the definition JsonNode and try to find the definition version.
     * First, check if `definitionVersion` field is present and use its value
     * Then, check if `gravitee` field is present and use its value
     * Default to V2.
     * @param apiDefinitionPath is the path to the api definition to load
     * @return the definitionVersion found in definition
     * @throws IOException
     */
    private DefinitionVersion extractApiDefinitionVersion(String apiDefinitionPath) throws IOException {
        final URL url = loadURL(apiDefinitionPath);
        final JsonNode apiAsJson = graviteeMapper.readTree(url);

        if (apiAsJson.has("definitionVersion")) {
            return DefinitionVersion.valueOfLabel(apiAsJson.get("definitionVersion").asText());
        } else if (apiAsJson.has("gravitee")) {
            return DefinitionVersion.valueOfLabel(apiAsJson.get("gravitee").asText());
        } else {
            return DefinitionVersion.V2;
        }
    }
}
