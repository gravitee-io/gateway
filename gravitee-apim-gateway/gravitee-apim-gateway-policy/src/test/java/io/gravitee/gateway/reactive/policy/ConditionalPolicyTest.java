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
package io.gravitee.gateway.reactive.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.gravitee.gateway.reactive.api.context.ExecutionContext;
import io.gravitee.gateway.reactive.api.context.Request;
import io.gravitee.gateway.reactive.api.context.Response;
import io.gravitee.gateway.reactive.api.message.DefaultMessage;
import io.gravitee.gateway.reactive.api.message.Message;
import io.gravitee.gateway.reactive.api.policy.Policy;
import io.gravitee.gateway.reactive.core.condition.ConditionFilter;
import io.gravitee.gateway.reactive.core.condition.MessageConditionFilter;
import io.gravitee.gateway.reactive.core.context.MutableExecutionContext;
import io.gravitee.gateway.reactive.core.context.MutableRequest;
import io.gravitee.gateway.reactive.core.context.MutableResponse;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(MockitoExtension.class)
class ConditionalPolicyTest {

    protected static final String CONDITION = "{#context.attributes != null}";
    protected static final String POLICY_ID = "policyId";

    @Mock
    private Policy policy;

    @Mock
    private ConditionFilter<ConditionalPolicy> conditionFilter;

    @Mock(extraInterfaces = MutableExecutionContext.class)
    private ExecutionContext ctx;

    @Mock(extraInterfaces = MutableRequest.class)
    private Request request;

    @Mock(extraInterfaces = MutableResponse.class)
    private Response response;

    @Spy
    private Completable spyCompletable = Completable.complete();

    @BeforeEach
    void init() {
        lenient().when(policy.onRequest(ctx)).thenReturn(spyCompletable);
        lenient().when(policy.onMessageRequest(ctx)).thenReturn(spyCompletable);
        lenient().when(policy.onResponse(ctx)).thenReturn(spyCompletable);
        lenient().when(policy.onMessageResponse(ctx)).thenReturn(spyCompletable);

        lenient().when(ctx.request()).thenReturn(request);
        lenient().when(ctx.response()).thenReturn(response);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotExecuteConditionOnRequestWhenNoCondition(String condition) {
        final ConditionalPolicy cut = new ConditionalPolicy(policy, condition, conditionFilter);

        cut.onRequest(ctx).test().assertComplete();

        verify(policy).onRequest(ctx);
        verify(spyCompletable).subscribe(any(CompletableObserver.class));
        verifyNoMoreInteractions(policy);
        verifyNoInteractions(conditionFilter);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotExecuteConditionOnResponseWhenNoCondition(String condition) {
        final ConditionalPolicy cut = new ConditionalPolicy(policy, condition, conditionFilter);

        cut.onResponse(ctx).test().assertComplete();

        verify(policy).onResponse(ctx);
        verify(spyCompletable).subscribe(any(CompletableObserver.class));
        verifyNoMoreInteractions(policy);
        verifyNoInteractions(conditionFilter);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldExecuteConditionOnMessageRequestWhenNoCondition(String condition) {
        final ConditionalPolicy cut = new ConditionalPolicy(policy, condition, conditionFilter);

        cut.onMessageRequest(ctx).test().assertComplete();

        verify(policy, never()).onMessageRequest(ctx);
        verifyNoMoreInteractions(policy);
        verifyNoInteractions(conditionFilter);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldExecuteConditionOnMessageResponseWhenNoCondition(String condition) {
        final ConditionalPolicy cut = new ConditionalPolicy(policy, condition, conditionFilter);

        cut.onMessageResponse(ctx).test().assertComplete();

        verify(policy, never()).onMessageResponse(ctx);
        verifyNoMoreInteractions(policy);
        verifyNoInteractions(conditionFilter);
    }

    @Test
    void shouldExecuteConditionAndPolicyOnRequest() {
        final ConditionalPolicy cut = new ConditionalPolicy(policy, CONDITION, conditionFilter);

        when(conditionFilter.filter(ctx, cut)).thenReturn(Maybe.just(cut));

        cut.onRequest(ctx).test().assertComplete();

        verify(policy).onRequest(ctx);
        verify(spyCompletable).subscribe(any(CompletableObserver.class));
        verifyNoMoreInteractions(policy);
    }

    @Test
    void shouldNotExecutePolicyOnRequestWhenConditionIsNotMet() {
        final ConditionalPolicy cut = new ConditionalPolicy(policy, CONDITION, conditionFilter);

        when(conditionFilter.filter(ctx, cut)).thenReturn(Maybe.empty());

        cut.onRequest(ctx).test().assertComplete();

        verify(policy, never()).onRequest(ctx);
        verify(spyCompletable, never()).subscribe(any(CompletableObserver.class));
        verifyNoMoreInteractions(policy);
    }

    @Test
    void shouldExecuteConditionAndPolicyOnResponse() {
        final ConditionalPolicy cut = new ConditionalPolicy(policy, CONDITION, conditionFilter);

        when(conditionFilter.filter(ctx, cut)).thenReturn(Maybe.just(cut));

        cut.onResponse(ctx).test().assertComplete();

        verify(policy).onResponse(ctx);
        verify(spyCompletable).subscribe(any(CompletableObserver.class));
        verifyNoMoreInteractions(policy);
    }

    @Test
    void shouldNotExecutePolicyOnResponseWhenConditionIsNotMet() {
        final ConditionalPolicy cut = new ConditionalPolicy(policy, CONDITION, conditionFilter);

        when(conditionFilter.filter(ctx, cut)).thenReturn(Maybe.empty());

        cut.onResponse(ctx).test().assertComplete();

        verify(policy, never()).onResponse(ctx);
        verify(spyCompletable, never()).subscribe(any(CompletableObserver.class));
        verifyNoMoreInteractions(policy);
    }

    @Test
    void shouldGetOriginalPolicyId() {
        final ConditionalPolicy cut = new ConditionalPolicy(policy, null, conditionFilter);

        when(policy.id()).thenReturn(POLICY_ID);
        assertEquals(POLICY_ID, cut.id());
    }

    @Test
    void shouldGetCondition() {
        final ConditionalPolicy cut = new ConditionalPolicy(policy, CONDITION, conditionFilter);

        assertEquals(CONDITION, cut.getCondition());
    }
}
