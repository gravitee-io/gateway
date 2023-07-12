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
package io.gravitee.plugin.entrypoint.http.post;

import static org.assertj.core.api.Assertions.assertThat;

import io.gravitee.gateway.jupiter.api.ApiType;
import io.gravitee.gateway.jupiter.api.ConnectorMode;
import io.gravitee.plugin.entrypoint.http.post.HttpPostEntrypointConnector;
import io.gravitee.plugin.entrypoint.http.post.HttpPostEntrypointConnectorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
class HttpPostEntrypointConnectorFactoryTest {

    private HttpPostEntrypointConnectorFactory httpPostEntrypointConnectorFactory;

    @BeforeEach
    void beforeEach() {
        httpPostEntrypointConnectorFactory = new HttpPostEntrypointConnectorFactory();
    }

    @Test
    void shouldSupportSubscribeMode() {
        assertThat(httpPostEntrypointConnectorFactory.supportedModes()).contains(ConnectorMode.PUBLISH);
    }

    @ParameterizedTest
    @ValueSource(strings = { "wrong", "", "  " })
    void shouldCreateConnectorWithWrongConfiguration(String configuration) {
        HttpPostEntrypointConnector connector = httpPostEntrypointConnectorFactory.createConnector(configuration);
        assertThat(connector).isNotNull();
    }

    @Test
    void shouldCreateConnectorWithNullConfiguration() {
        HttpPostEntrypointConnector connector = httpPostEntrypointConnectorFactory.createConnector(null);
        assertThat(connector).isNotNull();
    }
}
