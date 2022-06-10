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
package io.gravitee.gateway.reactive.policy.impl;

import io.gravitee.gateway.reactive.api.ExecutionPhase;
import io.gravitee.gateway.reactive.api.context.ExecutionContext;
import io.gravitee.gateway.reactive.api.context.async.AsyncExecutionContext;
import io.gravitee.gateway.reactive.api.context.sync.SyncExecutionContext;
import io.gravitee.gateway.reactive.api.policy.Policy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class PolicyChain implements io.gravitee.gateway.reactive.policy.PolicyChain {

    private final Logger log = LoggerFactory.getLogger(PolicyChain.class);

    private final String id;
    private final Flowable<Policy> policies;
    private final ExecutionPhase phase;

    public PolicyChain(String id, List<Policy> policies, ExecutionPhase phase) {
        this.id = id;
        this.phase = phase;
        this.policies = Flowable.fromIterable(policies);
    }

    @Override
    public Completable execute(ExecutionContext<?, ?> ctx) {
        log.debug("Executing chain {}", id);

        return policies.flatMapCompletable(policy -> executePolicy(ctx, policy), false, 1);
    }

    protected Completable executePolicy(ExecutionContext<?, ?> ctx, Policy policy) {
        if (ctx.isInterrupted()) {
            return Completable.complete();
        }

        log.debug("Executing policy {} on phase {}", policy.getId(), phase);

        switch (phase) {
            case REQUEST:
                return policy.onRequest((SyncExecutionContext) ctx);
            case RESPONSE:
                return policy.onResponse((SyncExecutionContext) ctx);
            case ASYNC_REQUEST:
                return policy.onAsyncRequest((AsyncExecutionContext) ctx);
            case ASYNC_RESPONSE:
                return policy.onAsyncResponse((AsyncExecutionContext) ctx);
            default:
                return Completable.error(new RuntimeException("Invalid execution phase " + phase.name()));
        }
    }

    public String getId() {
        return id;
    }
}
