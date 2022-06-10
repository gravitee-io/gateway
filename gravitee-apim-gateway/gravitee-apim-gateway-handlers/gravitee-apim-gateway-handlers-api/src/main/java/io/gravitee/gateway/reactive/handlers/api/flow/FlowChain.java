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
package io.gravitee.gateway.reactive.handlers.api.flow;

import static io.gravitee.gateway.policy.StreamType.ON_REQUEST;
import static io.gravitee.gateway.policy.StreamType.ON_RESPONSE;
import static io.gravitee.gateway.reactive.api.ExecutionPhase.*;

import io.gravitee.definition.model.flow.Flow;
import io.gravitee.definition.model.flow.Step;
import io.gravitee.gateway.policy.PolicyManager;
import io.gravitee.gateway.policy.PolicyMetadata;
import io.gravitee.gateway.policy.StreamType;
import io.gravitee.gateway.reactive.api.ExecutionPhase;
import io.gravitee.gateway.reactive.api.context.ExecutionContext;
import io.gravitee.gateway.reactive.api.policy.Policy;
import io.gravitee.gateway.reactive.handlers.api.flow.resolver.FlowResolver;
import io.gravitee.gateway.reactive.policy.adapter.policy.PolicyAdapter;
import io.gravitee.gateway.reactive.policy.impl.PolicyChainImpl;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class FlowChain {

    private final Logger log = LoggerFactory.getLogger(FlowChain.class);

    private final String id;
    private final FlowResolver resolver;
    private final PolicyManager policyManager;
    private Flowable<Flow> flows;

    public FlowChain(String id, FlowResolver resolver, PolicyManager policyManager) {
        this.id = id;
        this.resolver = resolver;
        this.policyManager = policyManager;
    }

    public Completable execute(ExecutionContext<?, ?> ctx, ExecutionPhase phase) {
        return resolveFlows(ctx)
            .doOnNext(flow -> log.debug("Executing flow {} ({} level, {} phase)", flow.getName(), id, phase.name()))
            .map(flow -> createPolicyChain(flow, phase))
            .flatMapCompletable(chain -> continueChain(ctx, chain), false, 1);
    }

    private PolicyChainImpl createPolicyChain(Flow flow, ExecutionPhase phase) {
        final List<Step> steps = getSteps(flow, phase);
        final StreamType streamType = toStreamType(phase);

        final List<Policy> policies = steps
            .stream()
            .filter(Step::isEnabled)
            .map(step -> new PolicyMetadata(step.getPolicy(), step.getConfiguration(), step.getCondition()))
            // TODO: manage V3 and V4 policies.
            .map(policyMetadata -> policyManager.create(streamType, policyMetadata))
            .map(PolicyAdapter::new)
            .collect(Collectors.toList());

        return new PolicyChainImpl(flow.getName() + " " + phase.name(), policies, phase);
    }

    private List<Step> getSteps(Flow flow, ExecutionPhase phase) {
        if (phase == REQUEST || phase == ASYNC_REQUEST) {
            return flow.getPre();
        }

        return flow.getPost();
    }

    private StreamType toStreamType(ExecutionPhase phase) {
        return phase == REQUEST || phase == ASYNC_REQUEST ? ON_REQUEST : ON_RESPONSE;
    }

    private Flowable<Flow> resolveFlows(ExecutionContext<?, ?> ctx) {
        if (this.flows == null) {
            // Resolves the flows once. Subsequent resolutions will return the same flows.
            this.flows = resolver.resolve(ctx).cache();
        }

        return this.flows;
    }

    private Completable continueChain(ExecutionContext<?, ?> ctx, PolicyChainImpl chain) {
        if (!ctx.isInterrupted()) {
            return chain.execute(ctx);
        } else {
            return Completable.complete();
        }
    }
}
