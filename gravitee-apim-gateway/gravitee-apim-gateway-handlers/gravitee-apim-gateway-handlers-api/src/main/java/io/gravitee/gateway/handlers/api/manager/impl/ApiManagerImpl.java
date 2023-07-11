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
package io.gravitee.gateway.handlers.api.manager.impl;

import io.gravitee.common.event.EventManager;
import io.gravitee.common.util.DataEncryptor;
import io.gravitee.gateway.env.GatewayConfiguration;
import io.gravitee.gateway.handlers.api.definition.Api;
import io.gravitee.gateway.handlers.api.manager.ActionOnApi;
import io.gravitee.gateway.handlers.api.manager.ApiManager;
import io.gravitee.gateway.handlers.api.manager.Deployer;
import io.gravitee.gateway.handlers.api.manager.deployer.ApiDeployer;
import io.gravitee.gateway.reactor.ReactableApi;
import io.gravitee.gateway.reactor.ReactorEvent;
import io.vertx.core.impl.ConcurrentHashSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
@RequiredArgsConstructor
public class ApiManagerImpl implements ApiManager {

    private static final int PARALLELISM = Runtime.getRuntime().availableProcessors() * 2;

    private final EventManager eventManager;
    private final GatewayConfiguration gatewayConfiguration;
    private final Map<String, ReactableApi<?>> apis = new ConcurrentHashMap<>();
    private final Map<Class<? extends ReactableApi<?>>, ? extends Deployer<?>> deployers;

    public ApiManagerImpl(
        final EventManager eventManager,
        final GatewayConfiguration gatewayConfiguration,
        final DataEncryptor dataEncryptor
    ) {
        this.eventManager = eventManager;
        this.gatewayConfiguration = gatewayConfiguration;
        deployers =
            Map.of(
                Api.class,
                new ApiDeployer(gatewayConfiguration, dataEncryptor),
                io.gravitee.gateway.reactive.handlers.api.v4.Api.class,
                new io.gravitee.gateway.reactive.handlers.api.v4.deployer.ApiDeployer(gatewayConfiguration, dataEncryptor)
            );
    }

    private boolean register(ReactableApi<?> api, boolean force) {
        // Get deployed API
        ReactableApi<?> deployedApi = get(api.getId());

        // Does the API have a matching sharding tags ?
        // Keep the check of Sharding Tags for io.gravitee.gateway.services.localregistry.LocalApiDefinitionRegistry
        if (gatewayConfiguration.hasMatchingTags(api.getTags())) {
            boolean apiToDeploy = deployedApi == null || force;
            boolean apiToUpdate = !apiToDeploy && deployedApi.getDeployedAt().before(api.getDeployedAt());

            // if API will be deployed or updated
            if (apiToDeploy || apiToUpdate) {
                Deployer deployer = deployers.get(api.getClass());
                deployer.initialize(api);
            }

            // API is not yet deployed, so let's do it
            if (apiToDeploy) {
                deploy(api);
                return true;
            }
            // API has to be updated, so update it
            else if (apiToUpdate) {
                update(api);
                return true;
            }
        } else {
            log.debug("The API {} has been ignored because not in configured tags {}", api.getName(), api.getTags());

            // Check that the API was not previously deployed with other tags
            // In that case, we must undeploy it
            if (deployedApi != null) {
                undeploy(api.getId());
            }
        }

        return false;
    }

    @Override
    public ActionOnApi requiredActionFor(final ReactableApi<?> reactableApi) {
        ReactableApi<?> deployedApi = get(reactableApi.getId());
        if (gatewayConfiguration.hasMatchingTags(reactableApi.getTags())) {
            boolean apiToDeploy = deployedApi == null;
            boolean apiToUpdate = !apiToDeploy && deployedApi.getDeployedAt().before(reactableApi.getDeployedAt());

            // API will be deployed or updated
            if (apiToDeploy || apiToUpdate) {
                return ActionOnApi.DEPLOY;
            }
        } else if (deployedApi != null) {
            // Undeploy if previously deployed with other tags
            return ActionOnApi.UNDEPLOY;
        }
        // Nothing to do
        return ActionOnApi.NONE;
    }

    @Override
    public boolean register(ReactableApi api) {
        return register(api, false);
    }

    @Override
    public void unregister(String apiId) {
        undeploy(apiId);
    }

    @Override
    public void refresh() {
        if (apis != null && !apis.isEmpty()) {
            final long begin = System.currentTimeMillis();

            log.info("Starting apis refresh. {} apis to be refreshed.", apis.size());

            // Create an executor to parallelize a refresh for all the apis.
            final ExecutorService refreshAllExecutor = createExecutor(Math.min(PARALLELISM, apis.size()));

            final List<Callable<Boolean>> toInvoke = apis
                .values()
                .stream()
                .map(api -> ((Callable<Boolean>) () -> register(api, true)))
                .collect(Collectors.toList());

            try {
                refreshAllExecutor.invokeAll(toInvoke);
                refreshAllExecutor.shutdown();
                while (!refreshAllExecutor.awaitTermination(100, TimeUnit.MILLISECONDS));
            } catch (InterruptedException e) {
                log.error("Unable to refresh apis", e);
                Thread.currentThread().interrupt();
            } finally {
                refreshAllExecutor.shutdown();
            }

            log.info("Apis refresh done in {}ms", (System.currentTimeMillis() - begin));
        }
    }

    private void deploy(ReactableApi api) {
        MDC.put("api", api.getId());
        log.debug("Deployment of {}", api);

        if (api.isEnabled()) {
            Deployer deployer = deployers.get(api.getClass());
            List<String> plans = deployer.getPlans(api);

            // Deploy the API only if there is at least one plan
            if (!plans.isEmpty()) {
                log.debug("Deploying {} plan(s) for {}:", plans.size(), api);
                for (String plan : plans) {
                    log.debug("\t- {}", plan);
                }

                apis.put(api.getId(), api);
                eventManager.publishEvent(ReactorEvent.DEPLOY, api);
                log.info("{} has been deployed", api);
            } else {
                log.warn("There is no published plan associated to this API, skipping deployment...");
            }
        } else {
            log.debug("{} is not enabled. Skip deployment.", api);
        }

        MDC.remove("api");
    }

    private ExecutorService createExecutor(int threadCount) {
        return Executors.newFixedThreadPool(
            threadCount,
            new ThreadFactory() {
                private int counter = 0;

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "gio.api-manager-" + counter++);
                }
            }
        );
    }

    private void update(ReactableApi<?> api) {
        MDC.put("api", api.getId());
        log.debug("Updating {}", api);

        Deployer deployer = deployers.get(api.getClass());
        List<String> plans = deployer.getPlans(api);

        if (!plans.isEmpty()) {
            log.debug("Deploying {} plan(s) for {}:", plans.size(), api);
            for (String plan : plans) {
                log.info("\t- {}", plan);
            }

            apis.put(api.getId(), api);
            eventManager.publishEvent(ReactorEvent.UPDATE, api);
            log.info("{} has been updated", api);
        } else {
            log.warn("There is no published plan associated to this API, undeploy it...");
            undeploy(api.getId());
        }

        MDC.remove("api");
    }

    private void undeploy(String apiId) {
        ReactableApi<?> currentApi = apis.remove(apiId);
        if (currentApi != null) {
            MDC.put("api", apiId);
            log.debug("Undeployment of {}", currentApi);

            eventManager.publishEvent(ReactorEvent.UNDEPLOY, currentApi);
            log.info("{} has been undeployed", currentApi);
            MDC.remove("api");
        }
    }

    @Override
    public Collection<ReactableApi<?>> apis() {
        return apis.values();
    }

    @Override
    public ReactableApi<?> get(String name) {
        return apis.get(name);
    }
}
