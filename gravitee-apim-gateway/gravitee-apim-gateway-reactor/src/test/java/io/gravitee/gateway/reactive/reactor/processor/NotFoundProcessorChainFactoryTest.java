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
package io.gravitee.gateway.reactive.reactor.processor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.gateway.reactive.api.context.Request;
import io.gravitee.gateway.reactive.api.context.RequestExecutionContext;
import io.gravitee.gateway.reactive.api.context.Response;
import io.gravitee.gateway.reactive.core.processor.ProcessorChain;
import io.gravitee.gateway.reactive.http.vertx.VertxHttpServerRequest;
import io.gravitee.gateway.reactive.reactor.handler.context.ExecutionContextFactory;
import io.gravitee.gateway.report.ReporterService;
import io.gravitee.reporter.api.http.Metrics;
import io.reactivex.Completable;
import io.vertx.reactivex.core.MultiMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(MockitoExtension.class)
class NotFoundProcessorChainFactoryTest {

    @Mock
    private ReporterService reporterService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    private ExecutionContextFactory executionContextFactory;
    private Metrics metrics;

    @BeforeEach
    public void beforeEach() {
        executionContextFactory = new ExecutionContextFactory();
        metrics = Metrics.on(System.currentTimeMillis()).build();
        when(request.metrics()).thenReturn(metrics);
        when(response.end()).thenReturn(Completable.complete());
        when(response.headers()).thenReturn(HttpHeaders.create());
        when(response.body(any(Buffer.class))).thenReturn(Completable.complete());
    }

    @Test
    public void shouldExecuteNotFoundProcessorChain() {
        NotFoundProcessorChainFactory notFoundProcessorChainFactory = new NotFoundProcessorChainFactory(
            new StandardEnvironment(),
            reporterService,
            false
        );
        ProcessorChain processorChain = notFoundProcessorChainFactory.processorChain();
        RequestExecutionContext notFoundRequestContext = executionContextFactory.createRequestContext(request, response);

        processorChain.execute(notFoundRequestContext).test().assertResult();
        verify(response).status(HttpStatusCode.NOT_FOUND_404);
        verify(response).end();
        verify(reporterService).report(any());
        assertTrue(metrics.getProxyResponseTimeMs() > 0);
        assertTrue(metrics.getProxyLatencyMs() > 0);
        assertEquals("1", metrics.getApi());
        assertEquals("1", metrics.getApplication());
    }
}
