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
package io.gravitee.gateway.standalone.http;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.common.http.HttpMethod;
import io.gravitee.gateway.standalone.AbstractWiremockGatewayTest;
import io.gravitee.gateway.standalone.junit.annotation.ApiDescriptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@ApiDescriptor("/io/gravitee/gateway/standalone/http/cors_regex_origin.json")
public class CorsRegexTest extends AbstractWiremockGatewayTest {

    @Test
    public void preflight_request() throws Exception {
        HttpResponse response = Request
            .Options("http://localhost:8082/test/my_team")
            .addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
            .addHeader(HttpHeaders.ORIGIN, "http://api.mycompany.com")
            .execute()
            .returnResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        wireMockRule.verify(0, optionsRequestedFor(urlEqualTo("/team/my_team")));
    }

    @Test
    public void preflight_request_unauthorized() throws Exception {
        HttpResponse response = Request
            .Options("http://localhost:8082/test/my_team")
            .addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
            .addHeader(HttpHeaders.ORIGIN, "http://mycompany.com")
            .execute()
            .returnResponse();

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        wireMockRule.verify(0, optionsRequestedFor(urlEqualTo("/team/my_team")));
    }

    @Test
    public void simple_request_no_origin() throws Exception {
        wireMockRule.stubFor(get("/team/my_team").willReturn(ok()));

        HttpResponse response = Request.Get("http://localhost:8082/test/my_team").execute().returnResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        assertNull(response.getFirstHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));

        wireMockRule.verify(getRequestedFor(urlPathEqualTo("/team/my_team")));
    }

    @Test
    public void simple_request_with_origin() throws Exception {
        wireMockRule.stubFor(get("/team/my_team").willReturn(ok()));

        HttpResponse response = Request
            .Get("http://localhost:8082/test/my_team")
            .addHeader(HttpHeaders.ORIGIN, "https://api2.mycompany.com")
            .execute()
            .returnResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        assertEquals("*", response.getFirstHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN).getValue());
        assertEquals("x-forwarded-host", response.getFirstHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS).getValue());

        wireMockRule.verify(getRequestedFor(urlPathEqualTo("/team/my_team")));
    }
}
