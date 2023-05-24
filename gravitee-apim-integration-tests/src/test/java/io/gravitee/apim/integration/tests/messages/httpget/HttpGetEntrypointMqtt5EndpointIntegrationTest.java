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
package io.gravitee.apim.integration.tests.messages.httpget;

import com.graviteesource.entrypoint.http.get.HttpGetEntrypointConnectorFactory;
import io.gravitee.apim.gateway.tests.sdk.annotations.DeployApi;
import io.gravitee.apim.gateway.tests.sdk.annotations.GatewayTest;
import io.gravitee.apim.gateway.tests.sdk.connector.EntrypointBuilder;
import io.gravitee.apim.integration.tests.messages.AbstractMqtt5EndpointIntegrationTest;
import io.gravitee.common.http.MediaType;
import io.gravitee.gateway.api.http.HttpHeaderNames;
import io.gravitee.gateway.reactive.api.qos.Qos;
import io.gravitee.plugin.entrypoint.EntrypointConnectorPlugin;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.http.HttpClient;
import io.vertx.rxjava3.core.http.HttpClientResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@GatewayTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpGetEntrypointMqtt5EndpointIntegrationTest extends AbstractMqtt5EndpointIntegrationTest {


    @Override
    public void configureEntrypoints(Map<String, EntrypointConnectorPlugin<?, ?>> entrypoints) {
        entrypoints.putIfAbsent("http-get", EntrypointBuilder.build("http-get", HttpGetEntrypointConnectorFactory.class));
    }


    @Test
    @DeployApi({"/apis/v4/messages/http-get-entrypoint-mqtt5-endpoint.json"})
    void should_receive_messages(HttpClient client) {

        client.rxRequest(HttpMethod.GET, "/test2").flatMap(request -> {
                    request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                    return request.send();
                })
                .doOnSuccess(response -> assertThat(response.statusCode()).isEqualTo(200))
                .flatMap(response -> publishToMqtt5(mqtt5RxClient, TEST_TOPIC, "message")
                        .ignoreElements()
                        .andThen(response.body()))
                .test()
                .awaitDone(30, TimeUnit.SECONDS)
                .assertValue(body -> {
                    final JsonObject jsonResponse = new JsonObject(body.toString());
                    final JsonArray items = jsonResponse.getJsonArray("items");
                    assertThat(items).hasSize(1);
                    final JsonObject message = items.getJsonObject(0);
                    assertThat(message.getString("content")).isEqualTo("message");
                    return true;
                });
    }

    @EnumSource(value = Qos.class, names = {"AT_MOST_ONCE", "AT_LEAST_ONCE"})
    @ParameterizedTest(name = "should receive 400 bad request with {0} qos")
    @DeployApi({"/apis/v4/messages/http-get-entrypoint-mqtt5-endpoint-at-least-once.json",
            "/apis/v4/messages/http-get-entrypoint-mqtt5-endpoint-at-most-once.json"})
    void should_receive_400_bad_request_with_qos(Qos qos, HttpClient client) {
        client.rxRequest(HttpMethod.GET, "/test-" + qos.getLabel()).flatMap(request -> {
                    request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                    return request.send();
                })
                .doOnSuccess(response -> assertThat(response.statusCode()).isEqualTo(400))
                .flatMap(HttpClientResponse::body)
                .test()
                .awaitDone(30, TimeUnit.SECONDS)
                .assertValue(body -> {
                    final JsonObject jsonResponse = new JsonObject(body.toString());
                    assertThat(jsonResponse.getString("message")).isEqualTo("Incompatible Qos capabilities between entrypoint requirements and endpoint supports");
                    assertThat(jsonResponse.getInteger("http_status_code")).isEqualTo(400);
                    return true;
                });
    }

    @Test
    @DeployApi({"/apis/v4/messages/http-get-entrypoint-mqtt5-endpoint-failure.json"})
    void should_receive_error_messages_when_error_occurred(HttpClient client) {
        // First request should receive 2 first messages
        client.rxRequest(HttpMethod.GET, "/test-failure").flatMap(request -> {
                    request.putHeader(HttpHeaderNames.ACCEPT.toString(), MediaType.APPLICATION_JSON);
                    return request.rxSend();
                })
                .flatMap(response -> {
                    assertThat(response.statusCode()).isEqualTo(500);
                    return response.body();
                })
                .test()
                .awaitDone(10, TimeUnit.SECONDS)
                .assertValue(body -> {
                    final JsonObject jsonResponse = new JsonObject(body.toString());
                    assertThat(jsonResponse.getString("message")).isEqualTo("Endpoint connection failed");
                    assertThat(jsonResponse.getInteger("http_status_code")).isEqualTo(500);
                    return true;
                });
    }
}

