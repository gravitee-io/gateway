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
package io.gravitee.plugin.endpoint.internal.fake;

import io.gravitee.plugin.core.api.PluginManifest;
import io.gravitee.plugin.endpoint.EndpointConnectorPlugin;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author GraviteeSource Team
 */
public class FakeEndpointConnectorPlugin
    implements EndpointConnectorPlugin<FakeEndpointConnectorFactory, FakeEndpointConnectorConfiguration> {

    private static final String WITH_ENDPOINT_SHARED_CONFIGURATION_FILE = "with-endpoint-group-configuration";
    private static final String WITHOUT_ENDPOINT_SHARED_CONFIGURATION_FILE = "without-endpoint-group-configuration";

    private final String resourceFolder;

    private final boolean deployed;

    public FakeEndpointConnectorPlugin(boolean withoutResource) {
        this(withoutResource, true);
    }

    public FakeEndpointConnectorPlugin(boolean withoutResource, boolean deployed) {
        resourceFolder = withoutResource ? WITHOUT_ENDPOINT_SHARED_CONFIGURATION_FILE : WITH_ENDPOINT_SHARED_CONFIGURATION_FILE;
        this.deployed = deployed;
    }

    public FakeEndpointConnectorPlugin() {
        resourceFolder = WITH_ENDPOINT_SHARED_CONFIGURATION_FILE;
        this.deployed = true;
    }

    @Override
    public String id() {
        return "fake-endpoint";
    }

    @Override
    public String clazz() {
        return FakeEndpointConnectorFactory.class.getCanonicalName();
    }

    @Override
    public Class<FakeEndpointConnectorFactory> connectorFactory() {
        return FakeEndpointConnectorFactory.class;
    }

    @Override
    public String type() {
        return "endpoint-connector";
    }

    @Override
    public Path path() {
        try {
            return Paths.get(this.getClass().getClassLoader().getResource("files/" + resourceFolder).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PluginManifest manifest() {
        return null;
    }

    @Override
    public URL[] dependencies() {
        return new URL[0];
    }

    @Override
    public Class<FakeEndpointConnectorConfiguration> configuration() {
        return null;
    }

    @Override
    public boolean deployed() {
        return deployed;
    }
}
