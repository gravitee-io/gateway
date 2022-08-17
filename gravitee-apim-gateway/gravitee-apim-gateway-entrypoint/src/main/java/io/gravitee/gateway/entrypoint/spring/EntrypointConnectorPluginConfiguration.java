/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.entrypoint.spring;

import io.gravitee.gateway.entrypoint.EntrypointConnectorFactoryRegistry;
import io.gravitee.gateway.entrypoint.plugin.EntrypointConnectorFactoryRegistryImpl;
import io.gravitee.plugin.entrypoint.EntrypointConnectorPluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@Configuration
@Import(io.gravitee.plugin.entrypoint.spring.EntrypointConnectorPluginConfiguration.class)
public class EntrypointConnectorPluginConfiguration {

    @Bean
    public EntrypointConnectorFactoryRegistry entrypointConnectorFactoryRegistry(
        final EntrypointConnectorPluginManager entrypointConnectorPluginManager
    ) {
        return new EntrypointConnectorFactoryRegistryImpl(entrypointConnectorPluginManager);
    }
}
