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
package io.gravitee.gateway.jupiter.handlers.api.v4.flow.resolver;

import io.gravitee.definition.model.v4.Api;
import io.gravitee.definition.model.v4.flow.Flow;
<<<<<<< HEAD:gravitee-apim-gateway/gravitee-apim-gateway-handlers/gravitee-apim-gateway-handlers-api/src/main/java/io/gravitee/gateway/jupiter/handlers/api/v4/flow/resolver/ApiFlowResolver.java
import io.gravitee.gateway.jupiter.api.context.GenericExecutionContext;
import io.gravitee.gateway.jupiter.core.condition.ConditionFilter;
import io.gravitee.gateway.jupiter.v4.flow.AbstractFlowResolver;
=======
import io.gravitee.definition.model.v4.flow.selector.HttpSelector;
import io.gravitee.definition.model.v4.flow.selector.SelectorType;
import io.gravitee.gateway.reactive.api.context.GenericExecutionContext;
import io.gravitee.gateway.reactive.core.condition.ConditionFilter;
import io.gravitee.gateway.reactive.v4.flow.AbstractFlowResolver;
>>>>>>> d5a816621b (fix(gateway): fix pathParameter for jupiter/v4):gravitee-apim-gateway/gravitee-apim-gateway-handlers/gravitee-apim-gateway-handlers-api/src/main/java/io/gravitee/gateway/reactive/handlers/api/v4/flow/resolver/ApiFlowResolver.java
import io.reactivex.rxjava3.core.Flowable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Allows resolving {@link Flow}s to execute for a given {@link Api}.
 *
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@SuppressWarnings("common-java:DuplicatedBlocks") // Needed for v4 definition. Will replace the other one at the end.
class ApiFlowResolver extends AbstractFlowResolver {

    private final List<Flow> flows;

    public ApiFlowResolver(Api api, ConditionFilter<Flow> filter) {
        super(filter);
        // Api flows can be determined once and then reused.
        this.flows = provideFlows(api);
    }

    @Override
    public Flowable<Flow> provideFlows(GenericExecutionContext ctx) {
        addContextRequestPathParameters(ctx, flows);
        return Flowable.fromIterable(flows);
    }

    private List<Flow> provideFlows(Api api) {
        if (api.getFlows() == null || api.getFlows().isEmpty()) {
            return Collections.emptyList();
        }

        return api.getFlows().stream().filter(Flow::isEnabled).collect(Collectors.toList());
    }
}
