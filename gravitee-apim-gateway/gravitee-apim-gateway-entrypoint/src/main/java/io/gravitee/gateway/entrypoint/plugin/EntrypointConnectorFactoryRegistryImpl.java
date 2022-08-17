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
package io.gravitee.gateway.entrypoint.plugin;

import io.gravitee.gateway.entrypoint.EntrypointConnectorFactoryRegistry;
import io.gravitee.gateway.jupiter.api.entrypoint.EntrypointConnectorFactory;
import io.gravitee.plugin.entrypoint.EntrypointConnectorPluginManager;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class EntrypointConnectorFactoryRegistryImpl implements EntrypointConnectorFactoryRegistry {

    private final EntrypointConnectorPluginManager entrypointConnectorPluginManager;

    public EntrypointConnectorFactoryRegistryImpl(final EntrypointConnectorPluginManager pluginManager) {
        this.entrypointConnectorPluginManager = pluginManager;
    }

    @Override
    public EntrypointConnectorFactory<?> getById(final String id) {
        return entrypointConnectorPluginManager.getFactoryById(id);
    }
}
