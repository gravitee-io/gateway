/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.management.v2.rest.resource.plugin;

import io.gravitee.common.http.MediaType;
import io.gravitee.rest.api.management.v2.rest.mapper.ConnectorPluginMapper;
import io.gravitee.rest.api.management.v2.rest.mapper.MoreInformationMapper;
import io.gravitee.rest.api.management.v2.rest.model.ConnectorPlugin;
import io.gravitee.rest.api.management.v2.rest.model.MoreInformation;
import io.gravitee.rest.api.management.v2.rest.resource.AbstractResource;
import io.gravitee.rest.api.service.v4.EndpointConnectorPluginService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;
import java.util.Set;

/**
 * Defines the REST resources to manage endpoints.
 *
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@Path("/plugins/endpoints")
public class EndpointsResource extends AbstractResource {

    @Context
    private ResourceContext resourceContext;

    @Inject
    private EndpointConnectorPluginService endpointService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<ConnectorPlugin> getEndpoints() {
        return ConnectorPluginMapper.INSTANCE.map(endpointService.findAll());
    }

    @Path("/{endpointId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ConnectorPlugin getEndpoint(@PathParam("endpointId") String endpointId) {
        return ConnectorPluginMapper.INSTANCE.map(endpointService.findById(endpointId));
    }

    @GET
    @Path("/{endpointId}/schema")
    @Produces(MediaType.APPLICATION_JSON)
    public String getEndpointSchema(@PathParam("endpointId") String endpointId) {
        // Check that the endpoint exists
        endpointService.findById(endpointId);

        return endpointService.getSchema(endpointId);
    }

    @GET
    @Path("/{endpointId}/documentation")
    @Produces(MediaType.TEXT_PLAIN)
    public String getEndpointDocumentation(@PathParam("endpointId") String endpointId) {
        // Check that the endpoint exists
        endpointService.findById(endpointId);

        return endpointService.getDocumentation(endpointId);
    }

    @GET
    @Path("/{endpointId}/more-information")
    @Produces(MediaType.APPLICATION_JSON)
    public MoreInformation getEndpointMoreInformation(@PathParam("endpointId") String endpointId) {
        // Check that the entrypoint exists
        endpointService.findById(endpointId);

        return MoreInformationMapper.INSTANCE.map(endpointService.getMoreInformation(endpointId));
    }

    @GET
    @Path("/{endpointId}/shared-configuration-schema")
    @Produces(MediaType.APPLICATION_JSON)
    public String getEndpointSharedConfigurationSchema(@PathParam("endpointId") String endpointId) {
        // Check that the entrypoint exists
        endpointService.findById(endpointId);

        return endpointService.getSharedConfigurationSchema(endpointId);
    }
}
