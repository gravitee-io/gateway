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
package io.gravitee.gateway.handlers.api.processor.pathparameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.gravitee.common.http.HttpMethod;
import io.gravitee.common.util.LinkedMultiValueMap;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.Request;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(MockitoExtension.class)
class PathParametersProcessorTest {

    @Mock
    private PathParametersExtractor pathParametersExtractor;

    @Mock
    private ExecutionContext ctx;

    @Mock
    private Request request;

    private PathParametersProcessor cut;

    @BeforeEach
    void setUp() {
        when(ctx.request()).thenReturn(request);
        when(request.method()).thenReturn(HttpMethod.GET);
        cut = new PathParametersProcessor(pathParametersExtractor);
    }

    @Test
    void should_assign_path_params_to_request_and_call_next() {
        final AtomicBoolean nextCalled = new AtomicBoolean(false);
        cut.handler(ctx -> {
            nextCalled.set(true);
        });
        final LinkedMultiValueMap<String, String> pathParameters = new LinkedMultiValueMap<>();
        when(request.pathParameters()).thenReturn(pathParameters);
        when(pathParametersExtractor.extract(any(), any())).thenReturn(Map.of("key", "value", "anotherKey", "anotherValue"));

        cut.handle(ctx);

        assertThat(pathParameters).hasSize(2).containsEntry("key", List.of("value")).containsEntry("anotherKey", List.of("anotherValue"));
        assertThat(nextCalled.get()).isTrue();
    }
}
