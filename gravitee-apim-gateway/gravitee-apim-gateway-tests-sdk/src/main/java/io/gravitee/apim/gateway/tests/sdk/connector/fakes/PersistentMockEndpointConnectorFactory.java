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
package io.gravitee.apim.gateway.tests.sdk.connector.fakes;

import io.gravitee.gateway.reactive.api.ConnectorMode;
import io.gravitee.gateway.reactive.api.connector.endpoint.async.EndpointAsyncConnectorFactory;
import io.gravitee.gateway.reactive.api.context.DeploymentContext;
import io.gravitee.gateway.reactive.api.exception.PluginConfigurationException;
import io.gravitee.gateway.reactive.api.helper.PluginConfigurationHelper;
import io.gravitee.gateway.reactive.api.qos.Qos;
import io.gravitee.plugin.endpoint.mock.MockEndpointConnector;
import io.gravitee.plugin.endpoint.mock.MockEndpointConnectorFactory;
import io.gravitee.plugin.endpoint.mock.configuration.MockEndpointConnectorConfiguration;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public class PersistentMockEndpointConnectorFactory implements EndpointAsyncConnectorFactory<PersistentMockEndpointConnector> {

    private final PluginConfigurationHelper pluginConfigurationHelper;

    public PersistentMockEndpointConnectorFactory(PluginConfigurationHelper pluginConfigurationHelper) {
        this.pluginConfigurationHelper = pluginConfigurationHelper;
    }

    @Override
    public Set<ConnectorMode> supportedModes() {
        return Set.of(ConnectorMode.PUBLISH, ConnectorMode.SUBSCRIBE);
    }

    @Override
    public Set<Qos> supportedQos() {
        return Set.of(Qos.NONE, Qos.AUTO, Qos.AT_LEAST_ONCE, Qos.AT_MOST_ONCE);
    }

    @Override
    public PersistentMockEndpointConnector createConnector(
        DeploymentContext deploymentContext,
        String configuration,
        String sharedConfiguration
    ) {
        try {
            return new PersistentMockEndpointConnector(
                pluginConfigurationHelper.readConfiguration(MockEndpointConnectorConfiguration.class, configuration),
                deploymentContext.getComponent(MessageStorage.class)
            );
        } catch (PluginConfigurationException e) {
            return null;
        }
    }
}
