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
package io.gravitee.rest.api.portal.rest.resource;

import static io.gravitee.repository.management.model.DashboardType.APPLICATION;

import io.gravitee.common.http.MediaType;
import io.gravitee.rest.api.model.DashboardEntity;
import io.gravitee.rest.api.model.DashboardReferenceType;
import io.gravitee.rest.api.portal.rest.mapper.DashboardMapper;
import io.gravitee.rest.api.portal.rest.model.Dashboard;
import io.gravitee.rest.api.service.DashboardService;
import io.gravitee.rest.api.service.common.GraviteeContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
public class DashboardsResource extends AbstractResource {

    @Inject
    private DashboardService dashboardService;

    @Inject
    private DashboardMapper dashboardMapper;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        final List<Dashboard> dashboards = dashboardService
            .findByReferenceAndType(DashboardReferenceType.ENVIRONMENT, GraviteeContext.getCurrentEnvironment(), APPLICATION)
            .stream()
            .filter(DashboardEntity::isEnabled)
            .map(dashboardMapper::convert)
            .toList();
        return Response.ok(dashboards).build();
    }
}
