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
package io.gravitee.rest.api.management.v4.rest.resource.api;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.rest.api.management.v4.rest.model.*;
import io.gravitee.rest.api.management.v4.rest.resource.AbstractResourceTest;
import io.gravitee.rest.api.model.EnvironmentEntity;
import io.gravitee.rest.api.model.permissions.RolePermission;
import io.gravitee.rest.api.model.v4.api.ApiEntity;
import io.gravitee.rest.api.model.v4.api.NewApiEntity;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.exceptions.EnvironmentNotFoundException;
import io.gravitee.rest.api.service.exceptions.TechnicalManagementException;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ApisResourceTest extends AbstractResourceTest {

    private static final String ENVIRONMENT = "fake-env";

    @Override
    protected String contextPath() {
        return "/environments/" + ENVIRONMENT + "/apis";
    }

    @Before
    public void init() {
        GraviteeContext.cleanContext();
        GraviteeContext.setCurrentOrganization(ORGANIZATION);
        GraviteeContext.setCurrentEnvironment(ENVIRONMENT);

        EnvironmentEntity environment = new EnvironmentEntity();
        environment.setId(ENVIRONMENT);
        environment.setOrganizationId(ORGANIZATION);

        doReturn(environment).when(environmentService).findById(ENVIRONMENT);
        doReturn(environment).when(environmentService).findByOrgAndIdOrHrid(ORGANIZATION, ENVIRONMENT);
    }

    @Test
    public void shouldNotCreateApiWithNoContent() {
        final Response response = rootTarget().request().post(null);
        assertEquals(HttpStatusCode.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void shouldNotCreateApiWithoutName() {
        final CreateApiV4 apiEntity = new CreateApiV4();
        apiEntity.setName("");
        apiEntity.setApiVersion("v1");
        apiEntity.setDescription("my description");

        ApiEntity returnedApi = new ApiEntity();
        returnedApi.setId("my-beautiful-api");
        doReturn(returnedApi)
            .when(apiServiceV4)
            .create(eq(GraviteeContext.getExecutionContext()), Mockito.any(NewApiEntity.class), Mockito.eq(USER_NAME));

        final Response response = rootTarget().request().post(Entity.json(apiEntity));
        assertEquals(HttpStatusCode.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void shouldNotCreateApiWithoutListeners() {
        final CreateApiV4 apiEntity = new CreateApiV4();
        apiEntity.setName("My beautiful api");
        apiEntity.setApiVersion("v1");
        apiEntity.setDescription("my description");

        ApiEntity returnedApi = new ApiEntity();
        returnedApi.setId("my-beautiful-api");
        doReturn(returnedApi)
            .when(apiServiceV4)
            .create(eq(GraviteeContext.getExecutionContext()), Mockito.any(NewApiEntity.class), Mockito.eq(USER_NAME));

        final Response response = rootTarget().request().post(Entity.json(apiEntity));
        assertEquals(HttpStatusCode.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void shouldNotCreateApiWithoutEndpoints() {
        final CreateApiV4 apiEntity = new CreateApiV4();
        apiEntity.setName("My beautiful api");
        apiEntity.setApiVersion("v1");
        apiEntity.setDescription("my description");
        HttpListener httpListener = new HttpListener();
        httpListener.setPaths(List.of(new Path().path("/context")));
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setType("sse");
        httpListener.setEntrypoints(List.of(entrypoint));
        apiEntity.setListeners(List.of(new Listener(httpListener)));

        ApiEntity returnedApi = new ApiEntity();
        returnedApi.setId("my-beautiful-api");
        doReturn(returnedApi)
            .when(apiServiceV4)
            .create(eq(GraviteeContext.getExecutionContext()), Mockito.any(NewApiEntity.class), Mockito.eq(USER_NAME));

        final Response response = rootTarget().request().post(Entity.json(apiEntity));
        assertEquals(HttpStatusCode.BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void shouldNotCreateApiWhenServiceThrowException() {
        final CreateApiV4 apiEntity = new CreateApiV4();
        apiEntity.setName("My beautiful api");
        apiEntity.setApiVersion("v1");
        apiEntity.setDescription("my description");
        HttpListener httpListener = new HttpListener();
        httpListener.setPaths(List.of(new Path().path("/context")));
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setType("sse");
        httpListener.setEntrypoints(List.of(entrypoint));
        apiEntity.setListeners(List.of(new Listener(httpListener)));

        EndpointGroupV4 endpoint = new EndpointGroupV4();
        endpoint.setName("default");
        endpoint.setType("http");
        apiEntity.setEndpointGroups(List.of(endpoint));

        when(apiServiceV4.create(eq(GraviteeContext.getExecutionContext()), Mockito.any(NewApiEntity.class), Mockito.eq(USER_NAME)))
            .thenThrow(new TechnicalManagementException());
        final Response response = rootTarget().request().post(Entity.json(apiEntity));
        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR_500, response.getStatus());
    }

    @Test
    public void shouldReturn404WhenEnvironmentDoesNotExist() {
        doThrow(new EnvironmentNotFoundException(ENVIRONMENT)).when(environmentService).findByOrgAndIdOrHrid(ORGANIZATION, ENVIRONMENT);

        final CreateApiV4 apiEntity = new CreateApiV4();
        apiEntity.setName("My beautiful api");
        apiEntity.setApiVersion("v1");
        apiEntity.setDescription("my description");
        HttpListener httpListener = new HttpListener();
        httpListener.setPaths(List.of(new Path().path("/context")));
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setType("sse");
        httpListener.setEntrypoints(List.of(entrypoint));
        apiEntity.setListeners(List.of(new Listener(httpListener)));

        EndpointGroupV4 endpoint = new EndpointGroupV4();
        endpoint.setName("default");
        endpoint.setType("http");
        apiEntity.setEndpointGroups(List.of(endpoint));

        final Response response = rootTarget().request().post(Entity.json(apiEntity));
        assertEquals(HttpStatusCode.NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void shouldCreateApi() {
        final CreateApiV4 api = new CreateApiV4();
        api.setName("My beautiful api");
        api.setApiVersion("v1");
        api.setDescription("my description");
        api.setType(ApiType.PROXY);

        HttpListener httpListener = new HttpListener();
        httpListener.setPaths(List.of(new Path().path("/context")));
        httpListener.entrypoints(List.of(new Entrypoint().type("sse")));
        httpListener.type(ListenerType.HTTP);

        SubscriptionListener subscriptionListener = new SubscriptionListener();
        subscriptionListener.setType(ListenerType.SUBSCRIPTION);
        ObjectNode entrypointConfiguration = JsonNodeFactory.instance.objectNode();
        entrypointConfiguration.put("callbackUrl", "https://webhook.site/86195609-6ffe-4fda-8011-b8b8f91aa54b");
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setType("Webhook");
        entrypoint.setConfiguration(entrypointConfiguration);
        subscriptionListener.setEntrypoints(List.of(entrypoint));

        TcpListener tcpListener = new TcpListener();
        tcpListener.setType(ListenerType.TCP);
        tcpListener.setEntrypoints(List.of(new Entrypoint()));

        api.setListeners(List.of(new Listener(httpListener), new Listener(subscriptionListener), new Listener(tcpListener)));

        api.setEndpointGroups(List.of(new EndpointGroupV4().name("default").type("http")));

        FlowV4 flow = new FlowV4();
        flow.setName("flowName");
        flow.setEnabled(true);

        StepV4 step = new StepV4();
        step.setEnabled(true);
        step.setPolicy("my-policy");
        step.setCondition("my-condition");
        flow.setRequest(List.of(step));
        flow.setTags(Set.of("tag1", "tag2"));

        HttpSelector httpSelector = new HttpSelector();
        httpSelector.setPath("/test");
        httpSelector.setMethods(Set.of(HttpMethod.GET, HttpMethod.POST));
        httpSelector.setPathOperator(Operator.STARTS_WITH);

        ChannelSelector channelSelector = new ChannelSelector();
        channelSelector.setChannel("my-channel");
        channelSelector.setChannelOperator(Operator.STARTS_WITH);
        channelSelector.setOperations(Set.of(ChannelSelector.OperationsEnum.SUBSCRIBE));
        channelSelector.setEntrypoints(Set.of("my-entrypoint"));

        ConditionSelector conditionSelector = new ConditionSelector();
        conditionSelector.setCondition("my-condition");

        flow.setSelectors(List.of(new Selector(httpSelector), new Selector(channelSelector), new Selector(conditionSelector)));
        api.setFlows(List.of(flow));

        ApiEntity returnedApi = new ApiEntity();
        returnedApi.setId("my-beautiful-api");
        doReturn(returnedApi)
            .when(apiServiceV4)
            .create(eq(GraviteeContext.getExecutionContext()), Mockito.any(NewApiEntity.class), Mockito.eq(USER_NAME));
        Entity<CreateApiV4> json = Entity.json(api);
        final Response response = rootTarget().request().post(json);
        assertEquals(HttpStatusCode.CREATED_201, response.getStatus());
        assertEquals(rootTarget().path("my-beautiful-api").getUri().toString(), response.getHeaders().getFirst(HttpHeaders.LOCATION));
    }

    @Test
    public void shouldReturn403WhenUserNotPermittedToCreateApi() {
        final CreateApiV4 apiEntity = new CreateApiV4();
        apiEntity.setName("My beautiful api");
        apiEntity.setApiVersion("v1");
        apiEntity.setDescription("my description");
        apiEntity.setApiVersion("v1");
        apiEntity.setType(ApiType.PROXY);
        apiEntity.setDescription("Ma description");
        HttpListener httpListener = new HttpListener();
        httpListener.setPaths(List.of(new Path().path("/context")));
        Entrypoint entrypoint = new Entrypoint();
        entrypoint.setType("sse");
        httpListener.setEntrypoints(List.of(entrypoint));
        apiEntity.setListeners(List.of(new Listener(httpListener)));

        EndpointGroupV4 endpoint = new EndpointGroupV4();
        endpoint.setName("default");
        endpoint.setType("http");
        apiEntity.setEndpointGroups(List.of(endpoint));

        when(
            permissionService.hasPermission(
                eq(GraviteeContext.getExecutionContext()),
                eq(RolePermission.ENVIRONMENT_API),
                eq(ENVIRONMENT),
                any()
            )
        )
            .thenReturn(false);

        final Response response = rootTarget().request().post(Entity.json(apiEntity));
        assertEquals(HttpStatusCode.FORBIDDEN_403, response.getStatus());
    }
}
