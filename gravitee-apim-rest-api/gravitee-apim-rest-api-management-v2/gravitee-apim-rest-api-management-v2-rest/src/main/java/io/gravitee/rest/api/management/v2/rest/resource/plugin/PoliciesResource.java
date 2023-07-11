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
import io.gravitee.rest.api.management.v2.rest.mapper.PolicyPluginMapper;
import io.gravitee.rest.api.management.v2.rest.model.PolicyPlugin;
import io.gravitee.rest.api.management.v2.rest.resource.AbstractResource;
import io.gravitee.rest.api.model.platform.plugin.SchemaDisplayFormat;
import io.gravitee.rest.api.model.v4.policy.PolicyPluginEntity;
import io.gravitee.rest.api.service.PolicyService;
import io.gravitee.rest.api.service.v4.PolicyPluginService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Path("/plugins/policies")
public class PoliciesResource extends AbstractResource {

    @Inject
    private PolicyPluginService policyPluginService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<PolicyPlugin> getPolicies() {
        return PolicyPluginMapper.INSTANCE
            .map(policyPluginService.findAll())
            .stream()
            .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PolicyPlugin::getName))));
    }

    @Path("/{policyId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PolicyPlugin getPolicy(@PathParam("policyId") String policyId) {
        return PolicyPluginMapper.INSTANCE.map(policyPluginService.findById(policyId));
    }

    @GET
    @Path("/{policyId}/schema")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPolicySchema(@PathParam("policyId") String policyId, @QueryParam("display") String display) {
        // Check that the endpoint exists
        policyPluginService.findById(policyId);
        if (display != null) {
            return policyPluginService.getSchema(policyId, SchemaDisplayFormat.fromLabel(display));
        }
        return policyPluginService.getSchema(policyId);
    }

    @GET
    @Path("/{policyId}/documentation")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPolicyDocumentation(@PathParam("policyId") String policyId) {
        // Check that the endpoint exists
        policyPluginService.findById(policyId);

        return policyPluginService.getDocumentation(policyId);
    }
}
