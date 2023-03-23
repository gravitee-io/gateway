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
package io.gravitee.gateway.reactive.handlers.api.v4.analytics.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.gravitee.common.http.HttpMethod;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.http.HttpHeaderNames;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.gateway.http.vertx.VertxHttpHeaders;
import io.gravitee.gateway.reactive.api.context.ExecutionContext;
import io.gravitee.gateway.reactive.api.context.Request;
import io.gravitee.gateway.reactive.core.v4.analytics.AnalyticsUtils;
import io.gravitee.gateway.reactive.core.v4.analytics.LoggingContext;
import io.gravitee.gateway.reactive.handlers.api.v4.analytics.logging.request.LogEndpointRequest;
import io.gravitee.reporter.api.v4.metric.Metrics;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(MockitoExtension.class)
class LogEndpointRequestTest {

    protected static final String URI = "https://test.gravitee.io";
    protected static final String BODY_CONTENT = "Body content";

    @Mock
    protected LoggingContext loggingContext;

    @Mock
    protected ExecutionContext ctx;

    @Mock
    protected Request request;

    @Mock
    private Metrics metrics;

    @BeforeEach
    void init() {
        when(ctx.request()).thenReturn(request);
        when(ctx.metrics()).thenReturn(metrics);
        when(metrics.getEndpoint()).thenReturn(URI);
    }

    @Test
    void shouldLogMethodAndUriOnly() {
        final ArgumentCaptor<Flowable<Buffer>> chunksCaptor = ArgumentCaptor.forClass(Flowable.class);

        when(request.method()).thenReturn(HttpMethod.POST);
        when(loggingContext.endpointRequestHeaders()).thenReturn(false);
        when(loggingContext.endpointRequestPayload()).thenReturn(false);
        when(request.chunks()).thenReturn(Flowable.empty()).thenAnswer(i -> chunksCaptor.getValue());

        final LogEndpointRequest logRequest = new LogEndpointRequest(loggingContext, ctx);
        verify(request).chunks(chunksCaptor.capture());

        final TestSubscriber<Buffer> obs = chunksCaptor.getValue().test();
        obs.assertComplete();

        assertEquals(HttpMethod.POST, logRequest.getMethod());
        assertEquals(URI, logRequest.getUri());
        assertNull(logRequest.getHeaders());
        assertNull(logRequest.getBody());
    }

    @Test
    void shouldLogHeaders() {
        final ArgumentCaptor<Flowable<Buffer>> chunksCaptor = ArgumentCaptor.forClass(Flowable.class);
        final HttpHeaders headers = new VertxHttpHeaders(new HeadersMultiMap());

        headers.set("X-Test1", "Value1");
        headers.set("X-Test2", "Value2");
        headers.set("X-Test3", List.of("Value3-a", "Value3-b"));

        when(request.headers()).thenReturn(headers);
        when(request.method()).thenReturn(HttpMethod.POST);
        when(loggingContext.endpointRequestHeaders()).thenReturn(true);
        when(loggingContext.endpointRequestPayload()).thenReturn(false);
        when(request.chunks()).thenReturn(Flowable.empty()).thenAnswer(i -> chunksCaptor.getValue());

<<<<<<< HEAD:gravitee-apim-gateway/gravitee-apim-gateway-handlers/gravitee-apim-gateway-handlers-api/src/test/java/io/gravitee/gateway/reactive/handlers/api/v4/analytics/request/LogEndpointRequestTest.java
        final LogEndpointRequest logRequest = new LogEndpointRequest(loggingContext, ctx);
=======
        final LogProxyRequest logRequest = new LogProxyRequest(loggingContext, request);
        logRequest.setHeaders(headers);
>>>>>>> 038918e0c7 (fix: put backend host in proxy request headers log):gravitee-apim-gateway/gravitee-apim-gateway-handlers/gravitee-apim-gateway-handlers-api/src/test/java/io/gravitee/gateway/jupiter/handlers/api/logging/request/LogProxyRequestTest.java
        verify(request).chunks(chunksCaptor.capture());

        final TestSubscriber<Buffer> obs = chunksCaptor.getValue().test();
        obs.assertComplete();

        assertNotSame(headers, logRequest.getHeaders());
        assertTrue(logRequest.getHeaders().deeplyEquals(headers));
        assertNull(logRequest.getBody());
    }

    @Test
    void shouldLogBody() {
        final Flowable<Buffer> body = Flowable.just(Buffer.buffer(BODY_CONTENT));
        final ArgumentCaptor<Flowable<Buffer>> chunksCaptor = ArgumentCaptor.forClass(Flowable.class);

        when(request.chunks()).thenReturn(body).thenAnswer(i -> chunksCaptor.getValue());
        doNothing().when(request).chunks(chunksCaptor.capture());
        when(request.headers()).thenReturn(HttpHeaders.create());
        when(loggingContext.endpointRequestHeaders()).thenReturn(false);
        when(loggingContext.endpointRequestPayload()).thenReturn(true);
        when(loggingContext.getMaxSizeLogMessage()).thenReturn(-1);
        when(loggingContext.isContentTypeLoggable(any())).thenReturn(true);

        final LogEndpointRequest logRequest = new LogEndpointRequest(loggingContext, ctx);
        verify(request, times(2)).chunks(any(Flowable.class));

        final TestSubscriber<Buffer> obs = chunksCaptor.getValue().test();
        obs.assertComplete();

        assertNull(logRequest.getHeaders());
        assertEquals(BODY_CONTENT, logRequest.getBody());
    }

    @Test
    void shouldNotLogBodyWhenContentTypeExcluded() {
        final Flowable<Buffer> body = Flowable.just(Buffer.buffer(BODY_CONTENT));
        final ArgumentCaptor<Flowable<Buffer>> chunksCaptor = ArgumentCaptor.forClass(Flowable.class);
        final HttpHeaders headers = HttpHeaders.create();
        headers.set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");

        when(request.chunks()).thenReturn(body);
        doNothing().when(request).chunks(chunksCaptor.capture());
        when(request.headers()).thenReturn(headers);
        when(loggingContext.endpointRequestHeaders()).thenReturn(false);
        when(loggingContext.endpointRequestPayload()).thenReturn(true);
        when(loggingContext.isContentTypeLoggable("application/octet-stream")).thenReturn(false);

        final LogEndpointRequest logRequest = new LogEndpointRequest(loggingContext, ctx);
        verify(request).chunks(chunksCaptor.capture());

        final TestSubscriber<Buffer> obs = chunksCaptor.getValue().test();
        obs.assertComplete();

        assertNull(logRequest.getHeaders());
        assertNull(logRequest.getBody());
    }

    @Test
    void shouldLogPartialBodyWhenMaxPayloadSizeIsDefined() {
        final Flowable<Buffer> body = Flowable.just(Buffer.buffer(BODY_CONTENT));
        final int maxPayloadSize = 5;

        final ArgumentCaptor<Flowable<Buffer>> chunksCaptor = ArgumentCaptor.forClass(Flowable.class);
        when(request.chunks()).thenReturn(body).thenAnswer(i -> chunksCaptor.getValue());
        doNothing().when(request).chunks(chunksCaptor.capture());
        when(request.headers()).thenReturn(HttpHeaders.create());
        when(loggingContext.endpointRequestHeaders()).thenReturn(false);
        when(loggingContext.endpointRequestPayload()).thenReturn(true);
        when(loggingContext.isContentTypeLoggable(any())).thenReturn(true);
        when(loggingContext.getMaxSizeLogMessage()).thenReturn(maxPayloadSize);

        final LogEndpointRequest logRequest = new LogEndpointRequest(loggingContext, ctx);
        verify(request, times(2)).chunks(any(Flowable.class));

        final TestSubscriber<Buffer> obs = chunksCaptor.getAllValues().get(chunksCaptor.getAllValues().size() - 1).test();
        obs.assertComplete();

        assertNull(logRequest.getHeaders());
        assertEquals(BODY_CONTENT.substring(0, maxPayloadSize), logRequest.getBody());
    }
}
