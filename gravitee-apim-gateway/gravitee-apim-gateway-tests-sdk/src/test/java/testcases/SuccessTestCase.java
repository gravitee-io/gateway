package testcases;/**
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.gravitee.apim.gateway.tests.sdk.AbstractGatewayTest;
import io.gravitee.apim.gateway.tests.sdk.annotations.DeployApi;
import io.gravitee.apim.gateway.tests.sdk.annotations.GatewayTest;
import io.gravitee.apim.gateway.tests.sdk.policy.PolicyBuilder;
import io.gravitee.apim.gateway.tests.sdk.policy.fakes.Header1Policy;
import io.gravitee.apim.gateway.tests.sdk.policy.fakes.OnRequestPolicy;
import io.gravitee.apim.gateway.tests.sdk.policy.fakes.Stream1Policy;
import io.gravitee.apim.gateway.tests.sdk.policy.fakes.Stream2Policy;
import io.gravitee.plugin.policy.PolicyPlugin;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava3.core.http.HttpClient;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@GatewayTest
@DeployApi({ "/apis/success-flow.json" })
@EnableForGatewayTestingExtensionTesting
public class SuccessTestCase extends AbstractGatewayTest {

    public static final String ON_REQUEST_POLICY = "on-request-policy";

    @Test
    @DisplayName("Should test policy")
    void testConditional(HttpClient httpClient) throws InterruptedException {
        wiremock.stubFor(get("/team/my_team").willReturn(ok()));

        httpClient
            .rxRequest(HttpMethod.GET, "/test/my_team")
            .flatMap(request -> request.rxSend())
            .flatMapPublisher(response -> {
                assertThat(response.statusCode()).isEqualTo(200);
                assertThat(response.headers().contains("X-Gravitee-Policy")).isFalse();
                return response.toFlowable();
            })
            .test()
            .await()
            .assertComplete()
            .assertValue(body -> {
                assertThat(body.toString()).isEqualTo("OnResponseContent2Policy");
                return true;
            })
            .assertNoErrors();

        wiremock.verify(
            getRequestedFor(urlPathEqualTo("/team/my_team"))
                .withHeader("X-Gravitee-Policy", equalTo("request-header1"))
                .withHeader(ON_REQUEST_POLICY, equalTo("invoked"))
        );
    }

    @Override
    public void configurePolicies(Map<String, PolicyPlugin> policies) {
        policies.put("header-policy1", PolicyBuilder.build("header-policy1", Header1Policy.class));
        policies.put(ON_REQUEST_POLICY, PolicyBuilder.build(ON_REQUEST_POLICY, OnRequestPolicy.class));
        policies.put("stream-policy", PolicyBuilder.build("stream-policy", Stream1Policy.class));
        policies.put("stream-policy2", PolicyBuilder.build("stream-policy2", Stream2Policy.class));
    }
}
