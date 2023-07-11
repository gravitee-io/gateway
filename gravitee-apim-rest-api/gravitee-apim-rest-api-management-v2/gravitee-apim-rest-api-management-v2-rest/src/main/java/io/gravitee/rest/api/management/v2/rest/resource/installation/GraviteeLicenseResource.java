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
package io.gravitee.rest.api.management.v2.rest.resource.installation;

import io.gravitee.common.http.MediaType;
import io.gravitee.node.api.license.NodeLicenseService;
import io.gravitee.rest.api.management.v2.rest.mapper.GraviteeLicenseMapper;
import io.gravitee.rest.api.management.v2.rest.model.GraviteeLicense;
import io.gravitee.rest.api.management.v2.rest.resource.AbstractResource;
import io.gravitee.rest.api.model.v4.license.GraviteeLicenseEntity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;

/**
 * @author Antoine CORDIER (antoine.cordier at graviteesource.com)
 * @author GraviteeSource Team
 */
@Path("/license")
public class GraviteeLicenseResource extends AbstractResource {

    @Inject
    private NodeLicenseService nodeLicenseService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GraviteeLicense get() {
        return GraviteeLicenseMapper.INSTANCE.map(
            GraviteeLicenseEntity
                .builder()
                .tier(nodeLicenseService.getTier())
                .packs(nodeLicenseService.getPacks())
                .features(nodeLicenseService.getFeatures())
                .build()
        );
    }
}
