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
package io.gravitee.gateway.security.jwt;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.security.core.AuthenticationPolicy;
import io.gravitee.gateway.security.core.HookAuthenticationPolicy;
import io.gravitee.gateway.security.core.PluginAuthenticationPolicy;
import io.gravitee.gateway.security.jwt.policy.CheckSubscriptionPolicy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Iterator;
import java.util.List;

import static io.gravitee.reporter.api.http.Metrics.on;
import static java.lang.System.currentTimeMillis;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class JWTAuthenticationHandlerTest {

    @InjectMocks
    private JWTAuthenticationHandler authenticationHandler = new JWTAuthenticationHandler();
    @Mock
    private Request request;

    @Before
    public void init() {
        when(request.metrics()).thenReturn(on(currentTimeMillis()).build());
    }

    @Test
    public void shouldNotHandleRequest_noAuthorizationHeader() {
        when(request.headers()).thenReturn(new HttpHeaders());

        boolean handle = authenticationHandler.canHandle(request);
        Assert.assertFalse(handle);
    }

    @Test
    public void shouldNotHandleRequest_invalidAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        when(request.headers()).thenReturn(headers);

        headers.add(HttpHeaders.AUTHORIZATION, "");

        boolean handle = authenticationHandler.canHandle(request);
        Assert.assertFalse(handle);
    }

    @Test
    public void shouldNotHandleRequest_noBearerAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        when(request.headers()).thenReturn(headers);

        headers.add(HttpHeaders.AUTHORIZATION, "Basic xxx-xx-xxx-xx-xx");

        boolean handle = authenticationHandler.canHandle(request);
        Assert.assertFalse(handle);
    }

    @Test
    public void shouldHandleRequest_validAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        when(request.headers()).thenReturn(headers);

        headers.add(HttpHeaders.AUTHORIZATION, JWTAuthenticationHandler.BEARER_AUTHORIZATION_TYPE + " xxx-xx-xxx-xx-xx");

        boolean handle = authenticationHandler.canHandle(request);
        Assert.assertTrue(handle);
    }

    @Test
    public void shouldHandleRequest_ignoreCaseAuthorizationHeader() {
        HttpHeaders headers = new HttpHeaders();
        when(request.headers()).thenReturn(headers);

        headers.add(HttpHeaders.AUTHORIZATION, "BeaRer xxx-xx-xxx-xx-xx");

        boolean handle = authenticationHandler.canHandle(request);
        Assert.assertTrue(handle);
    }

    @Test
    public void shouldNotHandleRequest_noBearerValue() {
        HttpHeaders headers = new HttpHeaders();
        when(request.headers()).thenReturn(headers);

        headers.add(HttpHeaders.AUTHORIZATION, JWTAuthenticationHandler.BEARER_AUTHORIZATION_TYPE + " ");

        boolean handle = authenticationHandler.canHandle(request);
        Assert.assertFalse(handle);
    }

    @Test
    public void shouldReturnPolicies() {
        ExecutionContext executionContext = mock(ExecutionContext.class);

        List<AuthenticationPolicy> jwtProviderPolicies = authenticationHandler.handle(executionContext);

        Assert.assertEquals(2, jwtProviderPolicies.size());
        Iterator<AuthenticationPolicy> policyIte = jwtProviderPolicies.iterator();
        PluginAuthenticationPolicy policy = (PluginAuthenticationPolicy) policyIte.next();
        Assert.assertEquals(JWTAuthenticationHandler.AUTHENTICATION_HANDLER_NAME, policy.name());

        HookAuthenticationPolicy policy2 = (HookAuthenticationPolicy) policyIte.next();
        Assert.assertEquals(CheckSubscriptionPolicy.class, policy2.clazz());
    }

    @Test
    public void shouldReturnName() {
        Assert.assertEquals(JWTAuthenticationHandler.AUTHENTICATION_HANDLER_NAME, authenticationHandler.name());
    }

    @Test
    public void shouldReturnOrder() {
        Assert.assertEquals(0, authenticationHandler.order());
    }
}
