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
package io.gravitee.rest.api.management.rest.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.rest.api.idp.api.authentication.UserDetails;
import io.gravitee.rest.api.model.UserEntity;
import io.gravitee.rest.api.service.common.GraviteeContext;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import javax.ws.rs.core.Response;
import org.junit.AfterClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * Unit tests for <code>CurrentUserResource</code> class.
 * @author Raphaël CALABRO (ddaeke-github [at] yahoo [dot] fr)
 * @author GraviteeSource Team
 */
public class CurrentUserResourceTest extends AbstractResourceTest {

    private static final String ID = "040f6a20-9fc2-429f-8f6a-209fc2629f8d";
    private static final String ORG = "MY-ORG";

    @AfterClass
    public static void afterClass() {
        // Clean up Spring security context.
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String contextPath() {
        return "user";
    }

    @Test
    public void shouldBeAbleToGetCurrentUser() {
        Mockito.reset(userService);

        final UserDetails userDetails = new UserDetails(USER_NAME, "PASSWORD", Collections.emptyList());
        assertThat(userDetails.getPassword()).isNotNull();

        Date now = new Date();
        setCurrentUserDetails(now, userDetails);

        final Response response = orgTarget().request().get();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatusCode.OK_200);

        JsonNode returnUserDetails = response.readEntity(JsonNode.class);
        assertThat(returnUserDetails).isNotNull();
        assertThat(returnUserDetails.get("created_at").asLong()).isEqualTo(now.getTime());
        assertThat(returnUserDetails.get("updated_at").asLong()).isEqualTo(now.getTime());
        assertThat(returnUserDetails.get("last_connection_at").asLong()).isEqualTo(now.getTime());
        assertThat(returnUserDetails.get("organizationId").asText()).isEqualTo(ORG);
    }

    @Test
    public void shouldBeAbleToGetCurrentUserEvenIfItsPasswordIsErased() {
        Mockito.reset(userService);

        final UserDetails userDetails = new UserDetails(USER_NAME, "PASSWORD", Collections.emptyList());
        userDetails.eraseCredentials();
        assertThat(userDetails.getPassword()).isNull();

        Date now = new Date();
        setCurrentUserDetails(now, userDetails);

        final Response response = orgTarget().request().get();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatusCode.OK_200);

        JsonNode returnUserDetails = response.readEntity(JsonNode.class);
        assertThat(returnUserDetails).isNotNull();
        assertThat(returnUserDetails.get("created_at").asLong()).isEqualTo(now.getTime());
        assertThat(returnUserDetails.get("updated_at").asLong()).isEqualTo(now.getTime());
        assertThat(returnUserDetails.get("last_connection_at").asLong()).isEqualTo(now.getTime());
        assertThat(returnUserDetails.get("organizationId").asText()).isEqualTo(ORG);
    }

    @Test
    public void shouldBeAbleToDeleteCurrentUser() {
        Mockito.reset(userService);

        final Authentication authentication = mock(Authentication.class);
        final UserDetails userDetails = new UserDetails(USER_NAME, "PASSWORD", Collections.emptyList());

        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));

        final Response response = orgTarget().request().delete();

        verify(userService, times(1)).delete(any(), eq(USER_NAME));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatusCode.NO_CONTENT_204);
    }

    @Test
    public void shouldReturn401WithNoAuthAtLogin() {
        Mockito.reset(userService);

        final Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(null);

        SecurityContextHolder.setContext(new SecurityContextImpl(null));

        final Response response = orgTarget().path("/login").request().post(null);
        assertThat(response.getStatus()).isEqualTo(HttpStatusCode.UNAUTHORIZED_401);
        assertThat(response.readEntity(Object.class)).isNull();
    }

    private void setCurrentUserDetails(Date now, final UserDetails userDetails) {
        final Authentication authentication = mock(Authentication.class);
        final UserEntity userEntity = new UserEntity();
        userEntity.setId(ID);
        userEntity.setRoles(Collections.emptySet());
        userEntity.setFirstConnectionAt(new Date());
        userEntity.setCreatedAt(now);
        userEntity.setUpdatedAt(now);
        userEntity.setLastConnectionAt(now);
        userEntity.setOrganizationId(ORG);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.findByIdWithRoles(any(), eq(USER_NAME))).thenReturn(userEntity);

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
    }
}
