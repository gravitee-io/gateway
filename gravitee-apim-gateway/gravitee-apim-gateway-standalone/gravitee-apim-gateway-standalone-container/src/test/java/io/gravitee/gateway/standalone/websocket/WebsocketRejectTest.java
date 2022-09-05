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
package io.gravitee.gateway.standalone.websocket;

import static io.gravitee.common.http.HttpStatusCode.UNAUTHORIZED_401;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import io.gravitee.gateway.standalone.junit.annotation.ApiDescriptor;
import io.gravitee.gateway.standalone.junit.rules.ApiDeployer;
import io.vertx.core.http.UpgradeRejectedException;
import io.vertx.junit5.VertxTestContext;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

@ApiDescriptor("/io/gravitee/gateway/standalone/websocket/teams.json")
public class WebsocketRejectTest extends AbstractWebSocketGatewayTest {

    private static final Integer WEBSOCKET_PORT = getFreePort();

    @Override
    protected String getApiEndpointTarget() {
        return "http://localhost:" + WEBSOCKET_PORT;
    }

    @Rule
    public final TestRule chain = RuleChain.outerRule(new ApiDeployer(this));

    @Test
    public void websocket_rejected_request() throws InterruptedException {
        VertxTestContext testContext = new VertxTestContext();

        httpServer.webSocketHandler(webSocket -> webSocket.reject(UNAUTHORIZED_401)).listen(WEBSOCKET_PORT);

        httpClient.webSocket(
            "/test",
            event -> {
                testContext.verify(() -> assertThat(event.failed()).isTrue());
                testContext.verify(() -> assertThat(event.cause().getClass()).isEqualTo(UpgradeRejectedException.class));
                testContext.verify(() -> assertThat(((UpgradeRejectedException) event.cause()).getStatus()).isEqualTo(UNAUTHORIZED_401));
                testContext.completeNow();
            }
        );

        testContext.awaitCompletion(10, TimeUnit.SECONDS);

        String failureMessage = testContext.causeOfFailure() != null ? testContext.causeOfFailure().getMessage() : null;
        assertTrue(failureMessage, testContext.completed());
    }
}
