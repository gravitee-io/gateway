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
package io.gravitee.gateway.debug.reactor;

import static io.gravitee.gateway.debug.utils.Stubs.getADebugApiDefinition;
import static io.gravitee.gateway.debug.utils.Stubs.getAReactorEvent;
import static io.gravitee.gateway.debug.utils.Stubs.getAnEvent;
import static io.gravitee.repository.management.model.Event.EventProperties.API_DEBUG_STATUS;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.definition.model.HttpRequest;
import io.gravitee.gateway.debug.definition.DebugApi;
import io.gravitee.gateway.reactor.ReactorEvent;
import io.gravitee.gateway.reactor.handler.ReactorHandlerRegistry;
import io.gravitee.gateway.reactor.impl.ReactableWrapper;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.EventRepository;
import io.gravitee.repository.management.model.ApiDebugStatus;
import io.gravitee.repository.management.model.Event;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class DebugReactorTest {

    private static final String EVENT_ID = "evt-id";

    private static final String PAYLOAD = "Debugged Api payload";

    @InjectMocks
    private final DebugReactor debugReactor = new DebugReactor();

    @Mock
    private ReactorHandlerRegistry reactorHandlerRegistry;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Vertx vertx;

    @Captor
    ArgumentCaptor<io.gravitee.repository.management.model.Event> eventCaptor;

    @Test
    public void shouldDebugApiSuccessfully() throws TechnicalException, JsonProcessingException {
        io.gravitee.definition.model.DebugApi debugApiModel = getADebugApiDefinition();
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod("GET");
        httpRequest.setPath("/path1");
        httpRequest.setBody("request body");
        debugApiModel.setRequest(httpRequest);
        when(objectMapper.readValue(anyString(), any(DebugApi.class.getClass()))).thenReturn(debugApiModel);

        Event anEvent = getAnEvent(EVENT_ID, PAYLOAD);
        final ReactableWrapper<io.gravitee.repository.management.model.Event> reactableWrapper = new ReactableWrapper(anEvent);

        when(reactorHandlerRegistry.contains(any(DebugApi.class))).thenReturn(false);

        final HttpClient mockHttpClient = mock(HttpClient.class);
        when(vertx.createHttpClient()).thenReturn(mockHttpClient);

        // Mock successful Buffer body in HttpClientResponse
        final HttpClientResponse httpClientResponse = mock(HttpClientResponse.class);
        when(httpClientResponse.statusCode()).thenReturn(200);
        when(httpClientResponse.headers()).thenReturn(new HeadersMultiMap().add("X-Graviteeio-test", "testing_api_debugging"));
        final Buffer bodyBuffer = mock(Buffer.class);
        when(bodyBuffer.toString()).thenReturn("response body");
        when(objectMapper.writeValueAsString(any())).thenReturn(PAYLOAD);
        final Future<Buffer> bodyFuture = Future.succeededFuture(bodyBuffer);
        when(httpClientResponse.body()).thenReturn(bodyFuture);

        // Mock successful HttpClientResponse future
        final Future<HttpClientResponse> responseFuture = Future.succeededFuture(httpClientResponse);
        final HttpClientRequest httpClientRequest = mock(HttpClientRequest.class);
        when(httpClientRequest.setChunked(true)).thenReturn(httpClientRequest);
        when(httpClientRequest.send(any(String.class))).thenReturn(responseFuture);

        // Mock successful HttpClientRequest future
        final Future<HttpClientRequest> requestFuture = Future.succeededFuture(httpClientRequest);
        when(mockHttpClient.request(any())).thenReturn(requestFuture);

        debugReactor.onEvent(getAReactorEvent(ReactorEvent.DEBUG, reactableWrapper));

        verify(reactorHandlerRegistry, times(1)).contains(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).create(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).remove(any(DebugApi.class));
        verify(eventRepository, times(2)).update(eventCaptor.capture());

        final List<io.gravitee.repository.management.model.Event> events = eventCaptor.getAllValues();
        assertThat(events.get(1).getProperties()).containsKey(API_DEBUG_STATUS.getValue());
        assertThat(events.get(1).getProperties().get(API_DEBUG_STATUS.getValue())).isEqualTo(ApiDebugStatus.SUCCESS.name());
        assertThat(events.get(1).getPayload()).isEqualTo(PAYLOAD);
    }

    @Test
    public void shouldDebugApiSuccessfullyNullBody() throws TechnicalException, JsonProcessingException {
        io.gravitee.definition.model.DebugApi debugApiModel = getADebugApiDefinition();
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod("GET");
        httpRequest.setPath("/path1");
        httpRequest.setBody(null);
        debugApiModel.setRequest(httpRequest);
        when(objectMapper.readValue(anyString(), any(DebugApi.class.getClass()))).thenReturn(debugApiModel);

        Event anEvent = getAnEvent(EVENT_ID, PAYLOAD);
        final ReactableWrapper<io.gravitee.repository.management.model.Event> reactableWrapper = new ReactableWrapper(anEvent);

        when(reactorHandlerRegistry.contains(any(DebugApi.class))).thenReturn(false);
        final HttpClient mockHttpClient = mock(HttpClient.class);
        when(vertx.createHttpClient()).thenReturn(mockHttpClient);

        // Mock successful Buffer body in HttpClientResponse
        final HttpClientResponse httpClientResponse = mock(HttpClientResponse.class);
        when(httpClientResponse.statusCode()).thenReturn(200);
        when(httpClientResponse.headers()).thenReturn(new HeadersMultiMap().add("X-Graviteeio-test", "testing_api_debugging"));
        final Buffer bodyBuffer = mock(Buffer.class);
        when(bodyBuffer.toString()).thenReturn("response body");
        when(objectMapper.writeValueAsString(any())).thenReturn(PAYLOAD);
        final Future<Buffer> bodyFuture = Future.succeededFuture(bodyBuffer);
        when(httpClientResponse.body()).thenReturn(bodyFuture);

        // Mock successful HttpClientResponse future
        final Future<HttpClientResponse> responseFuture = Future.succeededFuture(httpClientResponse);
        final HttpClientRequest httpClientRequest = mock(HttpClientRequest.class);
        when(httpClientRequest.setChunked(true)).thenReturn(httpClientRequest);
        when(httpClientRequest.send()).thenReturn(responseFuture);

        // Mock successful HttpClientRequest future
        final Future<HttpClientRequest> requestFuture = Future.succeededFuture(httpClientRequest);
        when(mockHttpClient.request(any())).thenReturn(requestFuture);

        debugReactor.onEvent(getAReactorEvent(ReactorEvent.DEBUG, reactableWrapper));

        verify(reactorHandlerRegistry, times(1)).create(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).contains(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).remove(any(DebugApi.class));
        verify(eventRepository, times(2)).update(eventCaptor.capture());

        final List<io.gravitee.repository.management.model.Event> events = eventCaptor.getAllValues();
        assertThat(events.get(1).getProperties()).containsKey(API_DEBUG_STATUS.getValue());
        assertThat(events.get(1).getProperties().get(API_DEBUG_STATUS.getValue())).isEqualTo(ApiDebugStatus.SUCCESS.name());
        assertThat(events.get(1).getPayload()).isEqualTo(PAYLOAD);
    }

    @Test
    public void shouldRemoveReactorHandlerIfBodyDoNotCompleteSuccessfully() throws TechnicalException, JsonProcessingException {
        io.gravitee.definition.model.DebugApi debugApiModel = getADebugApiDefinition();
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod("GET");
        httpRequest.setPath("/path1");
        httpRequest.setBody("request body");
        debugApiModel.setRequest(httpRequest);
        when(objectMapper.readValue(anyString(), any(DebugApi.class.getClass()))).thenReturn(debugApiModel);

        Event anEvent = getAnEvent(EVENT_ID, PAYLOAD);
        final ReactableWrapper<io.gravitee.repository.management.model.Event> reactableWrapper = new ReactableWrapper(anEvent);

        when(reactorHandlerRegistry.contains(any(DebugApi.class))).thenReturn(false);
        final HttpClient mockHttpClient = mock(HttpClient.class);
        when(vertx.createHttpClient()).thenReturn(mockHttpClient);

        // Mock failing Buffer body in HttpClientResponse future
        final HttpClientResponse httpClientResponse = mock(HttpClientResponse.class);
        when(httpClientResponse.statusCode()).thenReturn(200);
        when(httpClientResponse.headers()).thenReturn(new HeadersMultiMap().add("X-Graviteeio-test", "testing_api_debugging"));
        final Future<Buffer> bodyFuture = Future.failedFuture("");
        when(httpClientResponse.body()).thenReturn(bodyFuture);

        // Mock successful HttpClientResponse future
        final Future<HttpClientResponse> responseFuture = Future.succeededFuture(httpClientResponse);
        final HttpClientRequest httpClientRequest = mock(HttpClientRequest.class);
        when(httpClientRequest.setChunked(true)).thenReturn(httpClientRequest);
        when(httpClientRequest.send(any(String.class))).thenReturn(responseFuture);

        // Mock successful HttpClientRequest future
        final Future<HttpClientRequest> requestFuture = Future.succeededFuture(httpClientRequest);
        when(mockHttpClient.request(any())).thenReturn(requestFuture);

        debugReactor.onEvent(getAReactorEvent(ReactorEvent.DEBUG, reactableWrapper));

        verify(reactorHandlerRegistry, times(1)).create(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).contains(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).remove(any(DebugApi.class));

        verify(eventRepository, times(2)).update(eventCaptor.capture());

        final List<io.gravitee.repository.management.model.Event> events = eventCaptor.getAllValues();
        assertThat(events.get(1).getProperties()).containsKey(API_DEBUG_STATUS.getValue());
        assertThat(events.get(1).getProperties().get(API_DEBUG_STATUS.getValue())).isEqualTo(ApiDebugStatus.ERROR.name());
    }

    @Test
    public void shouldRemoveReactorHandlerIfResponseDoNotCompleteSuccessfully() throws TechnicalException, JsonProcessingException {
        io.gravitee.definition.model.DebugApi debugApiModel = getADebugApiDefinition();
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod("GET");
        httpRequest.setPath("/path1");
        httpRequest.setBody("request body");
        debugApiModel.setRequest(httpRequest);
        when(objectMapper.readValue(anyString(), any(DebugApi.class.getClass()))).thenReturn(debugApiModel);

        Event anEvent = getAnEvent(EVENT_ID, PAYLOAD);
        final ReactableWrapper<io.gravitee.repository.management.model.Event> reactableWrapper = new ReactableWrapper(anEvent);

        when(reactorHandlerRegistry.contains(any(DebugApi.class))).thenReturn(false);
        final HttpClient mockHttpClient = mock(HttpClient.class);
        when(vertx.createHttpClient()).thenReturn(mockHttpClient);

        // Mock failing HttpClientResponse future
        final Future<HttpClientResponse> responseFuture = Future.failedFuture("");
        final HttpClientRequest httpClientRequest = mock(HttpClientRequest.class);
        when(httpClientRequest.setChunked(true)).thenReturn(httpClientRequest);
        when(httpClientRequest.send(any(String.class))).thenReturn(responseFuture);

        // Mock successful HttpClientRequest future
        final Future<HttpClientRequest> requestFuture = Future.succeededFuture(httpClientRequest);
        when(mockHttpClient.request(any())).thenReturn(requestFuture);

        debugReactor.onEvent(getAReactorEvent(ReactorEvent.DEBUG, reactableWrapper));

        verify(reactorHandlerRegistry, times(1)).create(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).contains(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).remove(any(DebugApi.class));

        verify(eventRepository, times(2)).update(eventCaptor.capture());

        final List<io.gravitee.repository.management.model.Event> events = eventCaptor.getAllValues();
        assertThat(events.get(1).getProperties()).containsKey(API_DEBUG_STATUS.getValue());
        assertThat(events.get(1).getProperties().get(API_DEBUG_STATUS.getValue())).isEqualTo(ApiDebugStatus.ERROR.name());
    }

    @Test
    public void shouldRemoveReactorHandlerIfRequestDoNotCompleteSuccessfully() throws TechnicalException, JsonProcessingException {
        io.gravitee.definition.model.DebugApi debugApiModel = getADebugApiDefinition();
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod("GET");
        httpRequest.setPath("/path1");
        httpRequest.setBody("request body");
        debugApiModel.setRequest(httpRequest);
        when(objectMapper.readValue(anyString(), any(DebugApi.class.getClass()))).thenReturn(debugApiModel);

        Event anEvent = getAnEvent(EVENT_ID, PAYLOAD);
        final ReactableWrapper<io.gravitee.repository.management.model.Event> reactableWrapper = new ReactableWrapper(anEvent);

        when(reactorHandlerRegistry.contains(any(DebugApi.class))).thenReturn(false);

        // Mock failing HttpClientRequest future
        final HttpClient mockHttpClient = mock(HttpClient.class);
        when(vertx.createHttpClient()).thenReturn(mockHttpClient);
        final Future<HttpClientRequest> requestFuture = Future.failedFuture("");
        when(mockHttpClient.request(any())).thenReturn(requestFuture);

        debugReactor.onEvent(getAReactorEvent(ReactorEvent.DEBUG, reactableWrapper));

        verify(reactorHandlerRegistry, times(1)).create(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).contains(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).remove(any(DebugApi.class));

        verify(eventRepository, times(2)).update(eventCaptor.capture());

        final List<io.gravitee.repository.management.model.Event> events = eventCaptor.getAllValues();
        assertThat(events.get(1).getProperties()).containsKey(API_DEBUG_STATUS.getValue());
        assertThat(events.get(1).getProperties().get(API_DEBUG_STATUS.getValue())).isEqualTo(ApiDebugStatus.ERROR.name());
    }

    @Test
    public void shouldRemoveReactorHandlerIfTechnicalExceptionDuringEventUpdating() throws TechnicalException, JsonProcessingException {
        io.gravitee.definition.model.DebugApi debugApiModel = getADebugApiDefinition();
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod("GET");
        httpRequest.setPath("/path1");
        httpRequest.setBody("request body");
        debugApiModel.setRequest(httpRequest);
        when(objectMapper.readValue(anyString(), any(DebugApi.class.getClass()))).thenReturn(debugApiModel);

        Event anEvent = getAnEvent(EVENT_ID, PAYLOAD);
        final ReactableWrapper<io.gravitee.repository.management.model.Event> reactableWrapper = new ReactableWrapper(anEvent);

        when(reactorHandlerRegistry.contains(any(DebugApi.class))).thenReturn(false);
        when(eventRepository.update(any())).thenThrow(TechnicalException.class);

        debugReactor.onEvent(getAReactorEvent(ReactorEvent.DEBUG, reactableWrapper));

        verify(reactorHandlerRegistry, times(1)).create(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).contains(any(DebugApi.class));
        verify(reactorHandlerRegistry, times(1)).remove(any(DebugApi.class));

        verify(eventRepository, times(2)).update(eventCaptor.capture());

        final List<io.gravitee.repository.management.model.Event> events = eventCaptor.getAllValues();
        assertThat(events.get(1).getProperties()).containsKey(API_DEBUG_STATUS.getValue());
        assertThat(events.get(1).getProperties().get(API_DEBUG_STATUS.getValue())).isEqualTo(ApiDebugStatus.ERROR.name());
    }

    @Test
    public void shouldDoNothingWhenReactableAlreadyDebugging() throws JsonProcessingException, TechnicalException {
        io.gravitee.definition.model.DebugApi debugApiModel = getADebugApiDefinition();
        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod("GET");
        httpRequest.setPath("/path1");
        httpRequest.setBody("request body");
        debugApiModel.setRequest(httpRequest);
        when(objectMapper.readValue(anyString(), any(DebugApi.class.getClass()))).thenReturn(debugApiModel);

        Event anEvent = getAnEvent(EVENT_ID, PAYLOAD);
        final ReactableWrapper<io.gravitee.repository.management.model.Event> reactableWrapper = new ReactableWrapper(anEvent);

        when(reactorHandlerRegistry.contains(any(DebugApi.class))).thenReturn(true);

        debugReactor.onEvent(getAReactorEvent(ReactorEvent.DEBUG, reactableWrapper));

        verify(reactorHandlerRegistry, times(0)).create(any());
        verify(reactorHandlerRegistry, times(1)).contains(any());
        verify(reactorHandlerRegistry, times(0)).remove(any());
    }

    @Test
    public void shouldDoNothing() {
        debugReactor.onEvent(getAReactorEvent(ReactorEvent.DEPLOY, null));

        verify(reactorHandlerRegistry, times(0)).create(any());
        verify(reactorHandlerRegistry, times(0)).contains(any());
        verify(reactorHandlerRegistry, times(0)).remove(any());
    }

    @Test
    public void shouldConvertMultiMapHeadersToSimpleMap() {
        final HeadersMultiMap headers = new HeadersMultiMap();
        headers
            .add("transfer-encoding", "chunked")
            .add("X-Gravitee-Transaction-Id", "transaction-id")
            .add("content-type", "application/json")
            .add("X-Gravitee-Request-Id", "request-id")
            .add("accept-encoding", "deflate")
            .add("accept-encoding", "gzip")
            .add("accept-encoding", "compress");

        final Map<String, List<String>> result = debugReactor.convertHeaders(headers);

        assertThat(result.get("transfer-encoding")).hasSize(1);
        assertThat(result.get("transfer-encoding")).contains("chunked");
        assertThat(result.get("X-Gravitee-Transaction-Id")).hasSize(1);
        assertThat(result.get("X-Gravitee-Transaction-Id")).contains("transaction-id");
        assertThat(result.get("content-type")).hasSize(1);
        assertThat(result.get("content-type")).contains("application/json");
        assertThat(result.get("X-Gravitee-Request-Id")).hasSize(1);
        assertThat(result.get("X-Gravitee-Request-Id")).contains("request-id");
        assertThat(result.get("accept-encoding")).hasSize(3);
        assertThat(result.get("accept-encoding")).contains("deflate", "gzip", "compress");
    }

    @Test
    public void shouldConvertSimpleMapHeadersToMultiMapHeaders() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("transfer-encoding", singletonList("chunked"));
        headers.put("X-Gravitee-Transaction-Id", singletonList("transaction-id"));
        headers.put("content-type", singletonList("application/json"));
        headers.put("X-Gravitee-Request-Id", singletonList("request-id"));
        headers.put("accept-encoding", List.of("deflate", "gzip", "compress"));

        final MultiMap result = debugReactor.convertHeaders(headers);

        assertThat(result.get("transfer-encoding")).contains("chunked");
        assertThat(result.get("X-Gravitee-Transaction-Id")).contains("transaction-id");
        assertThat(result.get("content-type")).contains("application/json");
        assertThat(result.get("X-Gravitee-Request-Id")).contains("request-id");
        assertThat(result.get("accept-encoding")).contains("deflate", "gzip", "compress");
    }
}
