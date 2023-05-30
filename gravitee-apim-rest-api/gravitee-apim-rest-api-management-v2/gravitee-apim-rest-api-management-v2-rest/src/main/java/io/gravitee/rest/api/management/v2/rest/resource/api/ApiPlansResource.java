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
package io.gravitee.rest.api.management.v2.rest.resource.api;

import static java.util.Comparator.comparingInt;

import io.gravitee.common.http.MediaType;
import io.gravitee.rest.api.management.v2.rest.mapper.PlanMapper;
import io.gravitee.rest.api.management.v2.rest.model.*;
import io.gravitee.rest.api.management.v2.rest.model.Error;
import io.gravitee.rest.api.management.v2.rest.model.PlanSecurityType;
import io.gravitee.rest.api.management.v2.rest.resource.AbstractResource;
import io.gravitee.rest.api.management.v2.rest.resource.param.PaginationParam;
import io.gravitee.rest.api.management.v2.rest.security.Permission;
import io.gravitee.rest.api.management.v2.rest.security.Permissions;
import io.gravitee.rest.api.model.permissions.RolePermission;
import io.gravitee.rest.api.model.permissions.RolePermissionAction;
import io.gravitee.rest.api.model.v4.plan.*;
import io.gravitee.rest.api.model.v4.plan.PlanType;
import io.gravitee.rest.api.service.GroupService;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.v4.PlanSearchService;
import io.gravitee.rest.api.service.v4.PlanService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@Path("/environments/{envId}/apis/{apiId}/plans")
public class ApiPlansResource extends AbstractResource {

    private final PlanMapper planMapper = PlanMapper.INSTANCE;

    @Inject
    private PlanService planServiceV4;

    @Inject
    private io.gravitee.rest.api.service.PlanService planServiceV2;

    @Inject
    private PlanSearchService planSearchService;

    @Inject
    private GroupService groupService;

    @Context
    private ResourceContext resourceContext;

    @PathParam("apiId")
    private String apiId;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_PLAN, acls = { RolePermissionAction.READ }) })
    public PlansResponse getApiPlans(
        @QueryParam("status") @DefaultValue("PUBLISHED") final Set<PlanStatus> planStatusParamList,
        @QueryParam("security") final Set<PlanSecurityType> planSecurityTypeParamList,
        @BeanParam @Valid PaginationParam paginationParam
    ) {
        var planQuery = PlanQuery
            .builder()
            .apiId(apiId)
            .securityType(
                planSecurityTypeParamList
                    .stream()
                    .map(planSecurityType -> io.gravitee.rest.api.model.v4.plan.PlanSecurityType.valueOf(planSecurityType.name()))
                    .collect(Collectors.toList())
            )
            .status(
                planStatusParamList
                    .stream()
                    .map(planStatus -> io.gravitee.definition.model.v4.plan.PlanStatus.valueOf(planStatus.name()))
                    .collect(Collectors.toList())
            );

        List<GenericPlanEntity> plans = planSearchService
            .search(GraviteeContext.getExecutionContext(), planQuery.build(), getAuthenticatedUser(), isAdmin())
            .stream()
            .sorted(comparingInt(GenericPlanEntity::getOrder))
            .map(this::filterSensitiveData)
            .collect(Collectors.toList());

        List<GenericPlanEntity> paginationData = computePaginationData(plans, paginationParam);

        return new PlansResponse()
            .data(planMapper.convert(paginationData))
            .pagination(computePaginationInfo(plans.size(), paginationData.size(), paginationParam))
            .links(computePaginationLinks(plans.size(), paginationParam));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_PLAN, acls = { RolePermissionAction.CREATE }) })
    public Response createApiPlan(@Valid @NotNull CreateBasePlan createPlan) {
        if (createPlan.getDefinitionVersion() == DefinitionVersion.V4) {
            final NewPlanEntity newPlanEntity = planMapper.convert((CreatePlanV4) createPlan);
            newPlanEntity.setApiId(apiId);
            newPlanEntity.setType(PlanType.API);

            final PlanEntity planEntity = planServiceV4.create(GraviteeContext.getExecutionContext(), newPlanEntity);
            return Response.created(this.getLocationHeader(planEntity.getId())).entity(planMapper.convert(planEntity)).build();
        } else if (createPlan.getDefinitionVersion() == DefinitionVersion.V2) {
            final io.gravitee.rest.api.model.NewPlanEntity newPlanEntity = planMapper.convert((CreatePlanV2) createPlan);
            newPlanEntity.setApi(apiId);
            newPlanEntity.setType(io.gravitee.rest.api.model.PlanType.API);

            final io.gravitee.rest.api.model.PlanEntity planEntity = planServiceV2.create(
                GraviteeContext.getExecutionContext(),
                newPlanEntity
            );
            return Response.created(this.getLocationHeader(planEntity.getId())).entity(planMapper.convert(planEntity)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(planInvalid()).build();
    }

    @GET
    @Path("/{planId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_PLAN, acls = { RolePermissionAction.READ }) })
    public Response getApiPlan(@PathParam("planId") String planId) {
        final ExecutionContext executionContext = GraviteeContext.getExecutionContext();
        final GenericPlanEntity planEntity = planSearchService.findById(executionContext, planId);

        if (!planEntity.getApiId().equals(apiId)) {
            return Response.status(Response.Status.NOT_FOUND).entity(planNotFoundError(planId)).build();
        }

        return Response.ok(planMapper.convert(planEntity)).build();
    }

    @PUT
    @Path("/{planId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_PLAN, acls = { RolePermissionAction.UPDATE }) })
    public Response updateApiPlan(@PathParam("planId") String planId, @Valid @NotNull UpdateBasePlan updatePlan) {
        final ExecutionContext executionContext = GraviteeContext.getExecutionContext();
        final GenericPlanEntity planEntity = planSearchService.findById(executionContext, planId);

        if (!planEntity.getApiId().equals(apiId)) {
            return Response.status(Response.Status.NOT_FOUND).entity(planNotFoundError(planId)).build();
        }

        if (updatePlan.getDefinitionVersion() == DefinitionVersion.V4) {
            if (!(planEntity instanceof PlanEntity)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(planInvalid(planId)).build();
            }

            final UpdatePlanEntity updatePlanEntity = planMapper.convert((UpdatePlanV4) updatePlan);
            updatePlanEntity.setId(planId);
            PlanEntity responseEntity = planServiceV4.update(executionContext, updatePlanEntity);
            return Response.ok(planMapper.convert(responseEntity)).build();
        } else if (updatePlan.getDefinitionVersion() == DefinitionVersion.V2) {
            if (!(planEntity instanceof io.gravitee.rest.api.model.PlanEntity)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(planInvalid(planId)).build();
            }

            final io.gravitee.rest.api.model.UpdatePlanEntity updatePlanEntity = planMapper.convert((UpdatePlanV2) updatePlan);
            updatePlanEntity.setId(planId);
            io.gravitee.rest.api.model.PlanEntity responseEntity = planServiceV2.update(executionContext, updatePlanEntity);
            return Response.ok(planMapper.convert(responseEntity)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(planInvalid(planId)).build();
    }

    @DELETE
    @Path("/{planId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_PLAN, acls = { RolePermissionAction.DELETE }) })
    public Response deleteApiPlan(@PathParam("planId") String planId) {
        final ExecutionContext executionContext = GraviteeContext.getExecutionContext();
        final GenericPlanEntity planEntity = planSearchService.findById(executionContext, planId);

        if (!planEntity.getApiId().equals(apiId)) {
            return Response.status(Response.Status.NOT_FOUND).entity(planNotFoundError(planId)).build();
        }

        planServiceV4.delete(executionContext, planId);

        return Response.noContent().build();
    }

    @POST
    @Path("/{plan}/_close")
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_PLAN, acls = { RolePermissionAction.UPDATE }) })
    public Response closeApiPlan(@PathParam("plan") String plan) {
        final ExecutionContext executionContext = GraviteeContext.getExecutionContext();
        PlanEntity planEntity = planServiceV4.findById(executionContext, plan);
        if (!planEntity.getApiId().equals(apiId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("'plan' parameter does not correspond to the current API").build();
        }

        PlanEntity closedPlan = planServiceV4.close(executionContext, plan);

        return Response.ok(planMapper.convert(closedPlan)).build();
    }

    @POST
    @Path("/{plan}/_publish")
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_PLAN, acls = { RolePermissionAction.UPDATE }) })
    public Response publishApiPlan(@PathParam("plan") String plan) {
        final ExecutionContext executionContext = GraviteeContext.getExecutionContext();
        PlanEntity planEntity = planServiceV4.findById(executionContext, plan);
        if (!planEntity.getApiId().equals(apiId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("'plan' parameter does not correspond to the current API").build();
        }

        PlanEntity publishedPlan = planServiceV4.publish(executionContext, plan);

        return Response.ok(planMapper.convert(publishedPlan)).build();
    }

    @POST
    @Path("/{plan}/_deprecate")
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_PLAN, acls = { RolePermissionAction.UPDATE }) })
    public Response deprecateApiPlan(@PathParam("plan") String plan) {
        final ExecutionContext executionContext = GraviteeContext.getExecutionContext();
        PlanEntity planEntity = planServiceV4.findById(executionContext, plan);
        if (!planEntity.getApiId().equals(apiId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("'plan' parameter does not correspond to the current API").build();
        }

        PlanEntity deprecatedPlan = planServiceV4.deprecate(executionContext, plan);

        return Response.ok(deprecatedPlan).build();
    }

    private GenericPlanEntity filterSensitiveData(GenericPlanEntity entity) {
        if (
            hasPermission(
                GraviteeContext.getExecutionContext(),
                RolePermission.API_GATEWAY_DEFINITION,
                entity.getApiId(),
                RolePermissionAction.READ
            ) &&
            hasPermission(GraviteeContext.getExecutionContext(), RolePermission.API_PLAN, entity.getApiId(), RolePermissionAction.READ)
        ) {
            // Return complete information if user has permission.
            return entity;
        }

        PlanEntity filtered = new PlanEntity();

        filtered.setId(entity.getId());
        filtered.setCharacteristics(entity.getCharacteristics());
        filtered.setName(entity.getName());
        filtered.setDescription(entity.getDescription());
        filtered.setOrder(entity.getOrder());
        filtered.setSecurity(entity.getPlanSecurity());
        filtered.setType(filtered.getType());
        filtered.setValidation(filtered.getValidation());
        filtered.setCommentRequired(entity.isCommentRequired());
        filtered.setCommentMessage(entity.getCommentMessage());
        filtered.setGeneralConditions(entity.getGeneralConditions());

        return filtered;
    }

    private Error planNotFoundError(String plan) {
        return new Error()
            .httpStatus(Response.Status.NOT_FOUND.getStatusCode())
            .message("Plan [" + plan + "] cannot be found.")
            .putParametersItem("plan", plan)
            .technicalCode("plan.notFound");
    }

    private Error planInvalid() {
        return new Error()
            .httpStatus(Response.Status.BAD_REQUEST.getStatusCode())
            .message("Plan is not valid.")
            .technicalCode("plan.invalid");
    }

    private Error planInvalid(String plan) {
        return new Error()
            .httpStatus(Response.Status.BAD_REQUEST.getStatusCode())
            .message("Plan [" + plan + "] is not valid.")
            .putParametersItem("plan", plan)
            .technicalCode("plan.invalid");
    }
}
