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
package io.gravitee.gateway.policy;

import io.gravitee.common.spring.factory.SpringFactoriesLoader;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.core.processor.StreamableProcessor;
import io.gravitee.gateway.core.processor.provider.ProcessorProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Guillaume CUSNIEUX (guillaume.cusnieux at graviteesource.com)
 * @author GraviteeSource Team
 */
public class PolicyChainProviderLoader {

    private final List<ConfigurablePolicyChainProvider> providers;

    public PolicyChainProviderLoader(List<ConfigurablePolicyChainProvider> providers) {
        this.providers = providers;
    }

    public List<ConfigurablePolicyChainProvider> getProviders() {
        return this.providers;
    }

    public List<ProcessorProvider<ExecutionContext, StreamableProcessor<ExecutionContext, Buffer>>> get(
        PolicyChainOrder policyChainOrder,
        StreamType streamType
    ) {
        return getProviders()
            .stream()
            .filter(provider -> provider.getChainOrder().equals(policyChainOrder) && provider.getStreamType().equals(streamType))
            .collect(Collectors.toList());
    }
}
