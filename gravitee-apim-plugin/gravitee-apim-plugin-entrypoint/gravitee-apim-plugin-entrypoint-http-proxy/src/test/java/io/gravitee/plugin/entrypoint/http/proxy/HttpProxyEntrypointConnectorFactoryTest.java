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
package io.gravitee.plugin.entrypoint.http.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.gateway.reactive.api.ConnectorMode;
import io.gravitee.gateway.reactive.api.ListenerType;
import io.gravitee.gateway.reactive.api.context.DeploymentContext;
import io.gravitee.gateway.reactive.api.helper.PluginConfigurationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(MockitoExtension.class)
class HttpProxyEntrypointConnectorFactoryTest {

    @Mock
    private DeploymentContext deploymentContext;

    private HttpProxyEntrypointConnectorFactory cut;

    @BeforeEach
    void beforeEach() {
        cut = new HttpProxyEntrypointConnectorFactory(new PluginConfigurationHelper(null, new ObjectMapper()));
    }

    @Test
    void shouldSupportRequestReponseMode() {
        assertThat(cut.supportedModes()).containsOnly(ConnectorMode.REQUEST_RESPONSE);
    }

    @Test
    void shouldSupportListenerType() {
        assertThat(cut.supportedListenerType()).isEqualTo(ListenerType.HTTP);
    }

    @ParameterizedTest
    @ValueSource(strings = { "wrong", "", "  " })
    void shouldNotCreateConnectorWithWrongConfiguration(String configuration) {
        HttpProxyEntrypointConnector connector = cut.createConnector(deploymentContext, configuration);
        assertThat(connector).isNull();
    }

    @Test
    void shouldCreateConnectorWithNullConfiguration() {
        HttpProxyEntrypointConnector connector = cut.createConnector(deploymentContext, null);
        assertThat(connector).isNotNull();
    }

    @Test
    void shouldCreateConnectorWithNullDeploymentContext() {
        HttpProxyEntrypointConnector connector = cut.createConnector(null, "{}");
        assertThat(connector).isNotNull();
    }
}
