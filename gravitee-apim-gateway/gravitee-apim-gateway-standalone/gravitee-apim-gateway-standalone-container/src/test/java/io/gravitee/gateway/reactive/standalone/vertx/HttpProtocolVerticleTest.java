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
package io.gravitee.gateway.reactive.standalone.vertx;

import static io.gravitee.common.http.HttpStatusCode.SERVICE_UNAVAILABLE_503;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.gateway.reactive.reactor.HttpRequestDispatcher;
import io.gravitee.node.vertx.ReactivexVertxHttpServerFactory;
import io.gravitee.node.vertx.VertxHttpServerFactory;
import io.gravitee.node.vertx.configuration.HttpServerConfiguration;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.http.HttpServerRequest;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

@ExtendWith(VertxExtension.class)
public class HttpProtocolVerticleTest {

    private HttpRequestDispatcher mockRequestDispatcher;
    private HttpServer httpServer;

    @BeforeEach
    @DisplayName("Deploy a new http protocol verticle")
    void deployVerticle(Vertx vertx, VertxTestContext testContext) throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int randomPort = socket.getLocalPort();
        socket.close();
        httpServer = vertx.createHttpServer(new HttpServerOptions().setPort(randomPort));
        mockRequestDispatcher = spy(new MockHttpRequestDispatcher());
        vertx.deployVerticle(new HttpProtocolVerticle(httpServer, mockRequestDispatcher), testContext.succeedingThenComplete());
    }

    @AfterEach
    @DisplayName("Check that the verticle is still there")
    void lastChecks(Vertx vertx) {
        assertThat(vertx.deploymentIDs()).isNotEmpty().hasSize(1);
    }

    @Test
    void verticle_should_be_deployed(Vertx vertx, VertxTestContext testContext) {
        testContext.completeNow();
    }

    @Test
    void http_server_should_listen(Vertx vertx, VertxTestContext testContext) {
        HttpClient client = vertx.createHttpClient();
        client
            .request(HttpMethod.GET, httpServer.actualPort(), "127.0.0.1", "/")
            .compose(HttpClientRequest::send)
            .onComplete(
                testContext.succeeding(
                    response ->
                        testContext.verify(
                            () -> {
                                assertThat(response.statusCode()).isEqualTo(HttpStatusCode.OK_200);
                                testContext.completeNow();
                            }
                        )
                )
            );
    }

    @Test
    void http_server_should_close_and_resume_on_error(Vertx vertx, VertxTestContext testContext) {
        doReturn(Completable.error(new RuntimeException("error"))).doCallRealMethod().when(mockRequestDispatcher).dispatch(any());
        HttpClient client = vertx.createHttpClient();
        client
            .request(HttpMethod.GET, httpServer.actualPort(), "127.0.0.1", "/")
            .compose(HttpClientRequest::send)
            .onComplete(
                testContext.succeeding(
                    response ->
                        testContext.verify(() -> assertThat(response.statusCode()).isEqualTo(HttpStatusCode.INTERNAL_SERVER_ERROR_500))
                )
            )
            .compose(httpClientResponse -> client.request(HttpMethod.GET, httpServer.actualPort(), "127.0.0.1", "/"))
            .compose(HttpClientRequest::send)
            .onComplete(
                testContext.succeeding(
                    response ->
                        testContext.verify(
                            () -> {
                                assertThat(response.statusCode()).isEqualTo(HttpStatusCode.OK_200);
                                testContext.completeNow();
                            }
                        )
                )
            );
    }

    @Test
    void http_server_should_dispose_when_connection_closed(Vertx vertx, VertxTestContext testContext) {
        doReturn(Completable.timer(2, TimeUnit.SECONDS).doOnDispose(testContext::completeNow)).when(mockRequestDispatcher).dispatch(any());
        HttpClient client = vertx.createHttpClient();
        client
            .request(HttpMethod.GET, httpServer.actualPort(), "127.0.0.1", "/")
            .compose(
                request -> {
                    request.send().otherwiseEmpty();
                    return request.connection().close();
                }
            );
    }

    @Test
    void http_server_should_ignore_already_ended_response_on_error(Vertx vertx, VertxTestContext testContext) {
        doAnswer(
                invocation -> {
                    HttpServerRequest httpServerRequest = invocation.getArgument(0);
                    return httpServerRequest
                        .response()
                        .setStatusCode(SERVICE_UNAVAILABLE_503)
                        .rxEnd()
                        .andThen(Completable.error(new RuntimeException("error")));
                }
            )
            .doCallRealMethod()
            .when(mockRequestDispatcher)
            .dispatch(any());

        HttpClient client = vertx.createHttpClient();
        client
            .request(HttpMethod.GET, httpServer.actualPort(), "127.0.0.1", "/")
            .compose(HttpClientRequest::send)
            .onComplete(
                testContext.succeeding(
                    response -> testContext.verify(() -> assertThat(response.statusCode()).isEqualTo(SERVICE_UNAVAILABLE_503))
                )
            )
            .compose(httpClientResponse -> client.request(HttpMethod.GET, httpServer.actualPort(), "127.0.0.1", "/"))
            .compose(HttpClientRequest::send)
            .onComplete(
                testContext.succeeding(
                    response ->
                        testContext.verify(
                            () -> {
                                assertThat(response.statusCode()).isEqualTo(HttpStatusCode.OK_200);
                                testContext.completeNow();
                            }
                        )
                )
            );
    }
}
