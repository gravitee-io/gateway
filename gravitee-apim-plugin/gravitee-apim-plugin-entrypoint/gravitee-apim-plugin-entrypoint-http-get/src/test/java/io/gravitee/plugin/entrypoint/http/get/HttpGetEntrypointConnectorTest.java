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
package io.gravitee.plugin.entrypoint.http.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.gravitee.common.http.HttpMethod;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.common.http.MediaType;
import io.gravitee.common.util.LinkedMultiValueMap;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.http.HttpHeaderNames;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.gateway.jupiter.api.ApiType;
import io.gravitee.gateway.jupiter.api.ConnectorMode;
import io.gravitee.gateway.jupiter.api.ExecutionFailure;
import io.gravitee.gateway.jupiter.api.ListenerType;
import io.gravitee.gateway.jupiter.api.context.ExecutionContext;
import io.gravitee.gateway.jupiter.api.context.InternalContextAttributes;
import io.gravitee.gateway.jupiter.api.context.Request;
import io.gravitee.gateway.jupiter.api.context.Response;
import io.gravitee.gateway.jupiter.api.message.DefaultMessage;
import io.gravitee.gateway.jupiter.api.message.Message;
import io.gravitee.gateway.jupiter.core.context.interruption.InterruptionFailureException;
import io.gravitee.plugin.entrypoint.http.get.configuration.HttpGetEntrypointConnectorConfiguration;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(MockitoExtension.class)
class HttpGetEntrypointConnectorTest {

    @Mock
    private ExecutionContext mockExecutionContext;

    @Mock
    private Request mockRequest;

    @Mock
    private Response mockResponse;

    @Mock
    private HttpGetEntrypointConnectorConfiguration configuration = new HttpGetEntrypointConnectorConfiguration();

    @Captor
    private ArgumentCaptor<Flowable<Buffer>> chunksCaptor;

    private HttpGetEntrypointConnector httpGetEntrypointConnector;

    @BeforeEach
    void beforeEach() {
        httpGetEntrypointConnector = new HttpGetEntrypointConnector(configuration);
        lenient().when(configuration.isHeadersInPayload()).thenReturn(true);
        lenient().when(configuration.isMetadataInPayload()).thenReturn(true);
    }

    @Test
    void shouldIdReturnHttpGet() {
        assertThat(httpGetEntrypointConnector.id()).isEqualTo("http-get");
    }

    @Test
    void shouldSupportAsyncApi() {
        assertThat(httpGetEntrypointConnector.supportedApi()).isEqualTo(ApiType.ASYNC);
    }

    @Test
    void shouldSupportHttpListener() {
        assertThat(httpGetEntrypointConnector.supportedListenerType()).isEqualTo(ListenerType.HTTP);
    }

    @Test
    void shouldSupportSubscribeModeOnly() {
        assertThat(httpGetEntrypointConnector.supportedModes()).containsOnly(ConnectorMode.SUBSCRIBE);
    }

    @Test
    void shouldMatchesCriteriaReturnValidCount() {
        assertThat(httpGetEntrypointConnector.matchCriteriaCount()).isEqualTo(1);
    }

    @Test
    void shouldMatchesWithValidContext() {
        when(mockRequest.method()).thenReturn(HttpMethod.GET);
        when(mockExecutionContext.request()).thenReturn(mockRequest);

        boolean matches = httpGetEntrypointConnector.matches(mockExecutionContext);

        assertThat(matches).isTrue();
    }

    @Test
    void shouldNotMatchesWithBadMethod() {
        when(mockRequest.method()).thenReturn(HttpMethod.POST);
        when(mockExecutionContext.request()).thenReturn(mockRequest);

        boolean matches = httpGetEntrypointConnector.matches(mockExecutionContext);

        assertThat(matches).isFalse();
    }

    @Test
    void shouldInterruptWithFailureBadAcceptHeader() {
        final HttpHeaders requestHttpHeaders = HttpHeaders.create();
        requestHttpHeaders.add(HttpHeaderNames.ACCEPT, MediaType.APPLICATION_GRPC);
        when(mockRequest.headers()).thenReturn(requestHttpHeaders);

        when(mockExecutionContext.request()).thenReturn(mockRequest);

        when(mockExecutionContext.interruptWith(any(ExecutionFailure.class)))
            .thenAnswer(i -> Completable.error(new InterruptionFailureException(i.getArgument(0))));

        httpGetEntrypointConnector
            .handleRequest(mockExecutionContext)
            .test()
            .assertError(
                e -> {
                    assertTrue(e instanceof InterruptionFailureException);
                    assertEquals(HttpStatusCode.BAD_REQUEST_400, ((InterruptionFailureException) e).getExecutionFailure().statusCode());
                    assertEquals(
                        "Unsupported accept header: " + MediaType.APPLICATION_GRPC,
                        ((InterruptionFailureException) e).getExecutionFailure().message()
                    );
                    return true;
                }
            );
    }

    @ParameterizedTest
    @CsvSource({ "0,2", "1,2", "6,2" })
    void shouldSetupInternalAttributeWithConfigurationValuesOnRequest(String limitQueryParam, int messagesLimitCount) {
        final HttpHeaders requestHttpHeaders = HttpHeaders.create();
        requestHttpHeaders.add(HttpHeaderNames.ACCEPT, MediaType.WILDCARD);
        when(mockRequest.headers()).thenReturn(requestHttpHeaders);

        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(HttpGetEntrypointConnector.CURSOR_QUERY_PARAM, "1234");
        queryParams.add(HttpGetEntrypointConnector.LIMIT_QUERY_PARAM, limitQueryParam);
        when(mockRequest.parameters()).thenReturn(queryParams);
        when(mockExecutionContext.request()).thenReturn(mockRequest);
        when(configuration.getMessagesLimitCount()).thenReturn(messagesLimitCount);
        when(configuration.getMessagesLimitDurationMs()).thenReturn(1_000L);
        httpGetEntrypointConnector.handleRequest(mockExecutionContext).test().assertComplete();

        verify(mockExecutionContext)
            .putInternalAttribute(
                InternalContextAttributes.ATTR_INTERNAL_MESSAGES_LIMIT_COUNT,
                Math.min(messagesLimitCount, Integer.parseInt(limitQueryParam))
            );
        verify(mockExecutionContext).putInternalAttribute(InternalContextAttributes.ATTR_INTERNAL_MESSAGES_LIMIT_DURATION_MS, 1_000L);
        verify(mockExecutionContext).putInternalAttribute(InternalContextAttributes.ATTR_INTERNAL_MESSAGES_RESUME_LASTID, "1234");
        verify(mockExecutionContext)
            .putInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_RESPONSE_CONTENT_TYPE, MediaType.TEXT_PLAIN);
    }

    @Test
    void shouldCompleteAndEndWhenResponseMessagesComplete() {
        when(mockExecutionContext.getInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_RESPONSE_CONTENT_TYPE))
            .thenReturn(MediaType.APPLICATION_JSON);

        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        when(mockRequest.parameters()).thenReturn(queryParams);
        when(mockExecutionContext.request()).thenReturn(mockRequest);

        final HttpHeaders httpHeaders = HttpHeaders.create();
        when(mockResponse.headers()).thenReturn(httpHeaders);
        Flowable<Message> empty = Flowable.empty();
        when(mockResponse.messages()).thenReturn(empty);
        when(mockExecutionContext.response()).thenReturn(mockResponse);

        httpGetEntrypointConnector.handleResponse(mockExecutionContext).test().assertComplete();

        assertThat(httpHeaders).isNotNull();
        assertThat(httpHeaders.get(HttpHeaderNames.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
        verify(mockResponse).chunks(chunksCaptor.capture());

        final TestSubscriber<Buffer> chunkObs = chunksCaptor.getValue().test();

        chunkObs
            .assertComplete()
            .assertValueCount(4)
            .assertValueAt(0, message -> message.toString().equals("{\"items\":["))
            .assertValueAt(1, message -> message.toString().equals("]"))
            .assertValueAt(2, message -> message.toString().equals(",\"pagination\":{}"))
            .assertValueAt(3, message -> message.toString().equals("}"));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void shouldJsonArrayOfMessageAndCompleteAndEndWhenResponseMessagesComplete(boolean withHeadersAndMetadata) throws InterruptedException {
        when(mockExecutionContext.getInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_RESPONSE_CONTENT_TYPE))
            .thenReturn(MediaType.APPLICATION_JSON);
        when(mockExecutionContext.getInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_LAST_MESSAGE_ID)).thenReturn("2");
        when(mockExecutionContext.getInternalAttribute(InternalContextAttributes.ATTR_INTERNAL_MESSAGES_LIMIT_DURATION_MS))
            .thenReturn(30_000L);
        when(mockExecutionContext.getInternalAttribute(InternalContextAttributes.ATTR_INTERNAL_MESSAGES_LIMIT_COUNT)).thenReturn(2);

        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(HttpGetEntrypointConnector.CURSOR_QUERY_PARAM, "0");
        queryParams.add(HttpGetEntrypointConnector.LIMIT_QUERY_PARAM, "2");
        when(mockRequest.parameters()).thenReturn(queryParams);
        when(mockRequest.timestamp()).thenReturn(Instant.now().toEpochMilli());
        when(mockExecutionContext.request()).thenReturn(mockRequest);

        final HttpHeaders httpHeaders = HttpHeaders.create();
        when(mockResponse.headers()).thenReturn(httpHeaders);
        Flowable<Message> messages = Flowable.just(
            createMessage("1", MediaType.APPLICATION_JSON),
            createMessage("2", null),
            createMessage("Non returned messaged due to count limit", null)
        );
        when(mockResponse.messages()).thenReturn(messages);
        when(mockExecutionContext.response()).thenReturn(mockResponse);

        when(configuration.isHeadersInPayload()).thenReturn(withHeadersAndMetadata);
        when(configuration.isMetadataInPayload()).thenReturn(withHeadersAndMetadata);

        httpGetEntrypointConnector.handleResponse(mockExecutionContext).test().assertComplete();

        assertThat(httpHeaders).isNotNull();
        assertThat(httpHeaders.get(HttpHeaderNames.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON);
        verify(mockResponse).chunks(chunksCaptor.capture());

        final TestSubscriber<Buffer> chunkObs = chunksCaptor.getValue().test();

        chunkObs
            .await()
            .assertComplete()
            .assertValueCount(6)
            .assertValueAt(0, message -> message.toString().equals("{\"items\":["))
            .assertValueAt(
                1,
                message ->
                    withHeadersAndMetadata
                        ? message
                            .toString()
                            .equals(
                                "{\"headers\":{\"X-My-Header-1\":[\"headerValue1\"]},\"id\":\"1\",\"content\":\"{\\\"foo\\\": \\\"1\\\"}\",\"metadata\":{\"myKey\":\"myValue1\"}}"
                            )
                        : message.toString().equals("{\"id\":\"1\",\"content\":\"{\\\"foo\\\": \\\"1\\\"}\"}")
            )
            .assertValueAt(
                2,
                message ->
                    withHeadersAndMetadata
                        ? message
                            .toString()
                            .equals(
                                ",{\"headers\":{\"X-My-Header-2\":[\"headerValue2\"]},\"id\":\"2\",\"content\":\"2\",\"metadata\":{\"myKey\":\"myValue2\"}}"
                            )
                        : message.toString().equals(",{\"id\":\"2\",\"content\":\"2\"}")
            )
            .assertValueAt(3, message -> message.toString().equals("]"))
            .assertValueAt(
                4,
                message -> message.toString().equals(",\"pagination\":{\"cursor\":\"0\",\"nextCursor\":\"2\",\"limit\":\"2\"}")
            )
            .assertValueAt(5, message -> message.toString().equals("}"));

        verify(mockExecutionContext).putInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_LAST_MESSAGE_ID, "2");
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void shouldXmlArrayOfMessageAndCompleteAndEndWhenResponseMessagesComplete(boolean withHeadersAndMetadata) throws InterruptedException {
        when(mockExecutionContext.getInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_RESPONSE_CONTENT_TYPE))
            .thenReturn(MediaType.APPLICATION_XML);
        when(mockExecutionContext.getInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_LAST_MESSAGE_ID)).thenReturn("2");
        when(mockExecutionContext.getInternalAttribute(InternalContextAttributes.ATTR_INTERNAL_MESSAGES_LIMIT_DURATION_MS))
            .thenReturn(30_000L);
        when(mockExecutionContext.getInternalAttribute(InternalContextAttributes.ATTR_INTERNAL_MESSAGES_LIMIT_COUNT)).thenReturn(2);

        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(HttpGetEntrypointConnector.CURSOR_QUERY_PARAM, "0");
        queryParams.add(HttpGetEntrypointConnector.LIMIT_QUERY_PARAM, "2");
        when(mockRequest.parameters()).thenReturn(queryParams);
        when(mockRequest.timestamp()).thenReturn(Instant.now().toEpochMilli());
        when(mockExecutionContext.request()).thenReturn(mockRequest);

        final HttpHeaders responseHttpHeaders = HttpHeaders.create();
        when(mockResponse.headers()).thenReturn(responseHttpHeaders);
        Flowable<Message> messages = Flowable.just(
            createMessage("1", MediaType.APPLICATION_XML),
            createMessage("2", null),
            createMessage("Non returned messaged due to count limit", null)
        );
        when(mockResponse.messages()).thenReturn(messages);
        when(mockExecutionContext.response()).thenReturn(mockResponse);

        when(configuration.isHeadersInPayload()).thenReturn(withHeadersAndMetadata);
        when(configuration.isMetadataInPayload()).thenReturn(withHeadersAndMetadata);

        httpGetEntrypointConnector.handleResponse(mockExecutionContext).test().assertComplete();

        assertThat(responseHttpHeaders).isNotNull();
        assertThat(responseHttpHeaders.get(HttpHeaderNames.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_XML);
        verify(mockResponse).chunks(chunksCaptor.capture());

        final TestSubscriber<Buffer> chunkObs = chunksCaptor.getValue().test();

        chunkObs
            .await()
            .assertComplete()
            .assertValueCount(6)
            .assertValueAt(0, message -> message.toString().equals("<response><items>"))
            .assertValueAt(
                1,
                message ->
                    withHeadersAndMetadata
                        ? message
                            .toString()
                            .equals(
                                "<item><headers><X-My-Header-1>headerValue1</X-My-Header-1></headers><id>1</id><content><![CDATA[<foo>1</foo>]]></content><metadata><myKey>myValue1</myKey></metadata></item>"
                            )
                        : message.toString().equals("<item><id>1</id><content><![CDATA[<foo>1</foo>]]></content></item>")
            )
            .assertValueAt(
                2,
                message ->
                    withHeadersAndMetadata
                        ? message
                            .toString()
                            .equals(
                                "<item><headers><X-My-Header-2>headerValue2</X-My-Header-2></headers><id>2</id><content><![CDATA[2]]></content><metadata><myKey>myValue2</myKey></metadata></item>"
                            )
                        : message.toString().equals("<item><id>2</id><content><![CDATA[2]]></content></item>")
            )
            .assertValueAt(3, message -> message.toString().equals("</items>"))
            .assertValueAt(
                4,
                message ->
                    message.toString().equals("<pagination><cursor>0</cursor><nextCursor>2</nextCursor><limit>2</limit></pagination>")
            )
            .assertValueAt(5, message -> message.toString().equals("</response>"));

        verify(mockExecutionContext).putInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_LAST_MESSAGE_ID, "2");
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void shouldTextPlainArrayOfMessageAndCompleteAndEndWhenResponseMessagesComplete(boolean withHeadersAndMetadata)
        throws InterruptedException {
        when(mockExecutionContext.getInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_RESPONSE_CONTENT_TYPE))
            .thenReturn(MediaType.TEXT_PLAIN);
        when(mockExecutionContext.getInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_LAST_MESSAGE_ID)).thenReturn("2");
        when(mockExecutionContext.getInternalAttribute(InternalContextAttributes.ATTR_INTERNAL_MESSAGES_LIMIT_DURATION_MS))
            .thenReturn(30_000L);
        when(mockExecutionContext.getInternalAttribute(InternalContextAttributes.ATTR_INTERNAL_MESSAGES_LIMIT_COUNT)).thenReturn(2);

        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(HttpGetEntrypointConnector.CURSOR_QUERY_PARAM, "0");
        queryParams.add(HttpGetEntrypointConnector.LIMIT_QUERY_PARAM, "2");
        when(mockRequest.parameters()).thenReturn(queryParams);
        when(mockRequest.timestamp()).thenReturn(Instant.now().toEpochMilli());
        when(mockExecutionContext.request()).thenReturn(mockRequest);

        final HttpHeaders responseHttpHeaders = HttpHeaders.create();
        when(mockResponse.headers()).thenReturn(responseHttpHeaders);
        Flowable<Message> messages = Flowable.just(
            createMessage("1", null),
            createMessage("2", null),
            createMessage("Non returned messaged due to count limit", null)
        );
        when(mockResponse.messages()).thenReturn(messages);
        when(mockExecutionContext.response()).thenReturn(mockResponse);

        when(configuration.isHeadersInPayload()).thenReturn(withHeadersAndMetadata);
        when(configuration.isMetadataInPayload()).thenReturn(withHeadersAndMetadata);

        when(configuration.isHeadersInPayload()).thenReturn(withHeadersAndMetadata);
        when(configuration.isMetadataInPayload()).thenReturn(withHeadersAndMetadata);

        httpGetEntrypointConnector.handleResponse(mockExecutionContext).test().assertComplete();

        assertThat(responseHttpHeaders).isNotNull();
        assertThat(responseHttpHeaders.get(HttpHeaderNames.CONTENT_TYPE)).isEqualTo(MediaType.TEXT_PLAIN);
        verify(mockResponse).chunks(chunksCaptor.capture());

        final TestSubscriber<Buffer> chunkObs = chunksCaptor.getValue().test();

        chunkObs
            .await()
            .assertComplete()
            .assertValueCount(4)
            .assertValueAt(0, message -> message.toString().equals("items="))
            .assertValueAt(
                1,
                message ->
                    withHeadersAndMetadata
                        ? message.toString().equals("({X-My-Header-1=[headerValue1]}, id=1, content=1, {myKey=myValue1})")
                        : message.toString().equals("(id=1, content=1)")
            )
            .assertValueAt(
                2,
                message ->
                    withHeadersAndMetadata
                        ? message.toString().equals(", ({X-My-Header-2=[headerValue2]}, id=2, content=2, {myKey=myValue2})")
                        : message.toString().equals(", (id=2, content=2)")
            )
            .assertValueAt(3, message -> message.toString().equals("\npagination=(cursor=0, nextCursor=2, limit=2)"));

        verify(mockExecutionContext).putInternalAttribute(HttpGetEntrypointConnector.ATTR_INTERNAL_LAST_MESSAGE_ID, "2");
    }

    private Message createMessage(String messageContent, String contentType) {
        String content = messageContent;
        if (MediaType.APPLICATION_JSON.equals(contentType)) {
            content = "{\"foo\": \"" + messageContent + "\"}";
        } else if (MediaType.APPLICATION_XML.equals(contentType)) {
            content = "<foo>" + messageContent + "</foo>";
        }

        return DefaultMessage
            .builder()
            .id(messageContent)
            .headers(HttpHeaders.create().set("X-My-Header-" + messageContent, "headerValue" + messageContent))
            .content(Buffer.buffer(content))
            .metadata(Map.of("myKey", "myValue" + messageContent))
            .build();
    }
}
