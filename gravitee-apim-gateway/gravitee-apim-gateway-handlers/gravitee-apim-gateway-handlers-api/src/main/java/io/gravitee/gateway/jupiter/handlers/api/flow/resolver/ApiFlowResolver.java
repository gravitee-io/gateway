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
package io.gravitee.gateway.jupiter.handlers.api.flow.resolver;

import io.gravitee.definition.model.Api;
import io.gravitee.definition.model.flow.Flow;
import io.gravitee.gateway.jupiter.api.context.GenericExecutionContext;
import io.gravitee.gateway.jupiter.core.condition.ConditionFilter;
import io.gravitee.gateway.jupiter.flow.AbstractFlowResolver;
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
        return Flowable.fromIterable(this.flows);
    }

    private List<Flow> provideFlows(Api api) {
        if (api.getFlows() == null || api.getFlows().isEmpty()) {
            return Collections.emptyList();
        }

        return api.getFlows().stream().filter(Flow::isEnabled).collect(Collectors.toList());
    }
}
