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
package io.gravitee.apim.core.integration.use_case;

import static java.util.Optional.ofNullable;

import io.gravitee.apim.core.UseCase;
import io.gravitee.apim.core.api.crud_service.ApiCrudService;
import io.gravitee.apim.core.api.domain_service.CreateApiDomainService;
import io.gravitee.apim.core.api.domain_service.UpdateFederatedApiDomainService;
import io.gravitee.apim.core.api.domain_service.ValidateFederatedApiDomainService;
import io.gravitee.apim.core.api.model.Api;
import io.gravitee.apim.core.api.model.factory.ApiModelFactory;
import io.gravitee.apim.core.audit.model.AuditInfo;
import io.gravitee.apim.core.documentation.domain_service.CreateApiDocumentationDomainService;
import io.gravitee.apim.core.documentation.domain_service.UpdateApiDocumentationDomainService;
import io.gravitee.apim.core.documentation.model.Page;
import io.gravitee.apim.core.documentation.query_service.PageQueryService;
import io.gravitee.apim.core.exception.NotAllowedDomainException;
import io.gravitee.apim.core.integration.crud_service.IntegrationCrudService;
import io.gravitee.apim.core.integration.exception.IntegrationNotFoundException;
import io.gravitee.apim.core.integration.model.IntegrationApi;
import io.gravitee.apim.core.integration.service_provider.IntegrationAgent;
import io.gravitee.apim.core.license.domain_service.LicenseDomainService;
import io.gravitee.apim.core.membership.domain_service.ApiPrimaryOwnerFactory;
import io.gravitee.apim.core.membership.model.PrimaryOwnerEntity;
import io.gravitee.apim.core.plan.crud_service.PlanCrudService;
import io.gravitee.apim.core.plan.domain_service.CreatePlanDomainService;
import io.gravitee.apim.core.plan.domain_service.UpdatePlanDomainService;
import io.gravitee.apim.core.plan.model.factory.PlanModelFactory;
import io.gravitee.common.utils.TimeProvider;
import io.gravitee.rest.api.service.common.UuidString;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@Slf4j
@RequiredArgsConstructor
public class IngestIntegrationApisUseCase {

    private final IntegrationCrudService integrationCrudService;
    private final ApiPrimaryOwnerFactory apiPrimaryOwnerFactory;
    private final ValidateFederatedApiDomainService validateFederatedApi;
    private final ApiCrudService apiCrudService;
    private final CreateApiDomainService createApiDomainService;
    private final IntegrationAgent integrationAgent;
    private final LicenseDomainService licenseDomainService;
    private final UpdateFederatedApiDomainService updateFederatedApiDomainService;
    private final CreateApiDocumentationDomainService createApiDocumentationDomainService;
    private final UpdateApiDocumentationDomainService updateApiDocumentationDomainService;
    private final CreatePlanDomainService createPlanDomainService;
    private final UpdatePlanDomainService updatePlanDomainService;
    private final PlanCrudService planCrudService;
    private final PageQueryService pageQueryService;

    public Completable execute(Input input) {
        var integrationId = input.integrationId;
        var auditInfo = input.auditInfo;
        var organizationId = auditInfo.organizationId();
        var environmentId = auditInfo.environmentId();

        if (!licenseDomainService.isFederationFeatureAllowed(organizationId)) {
            return Completable.error(NotAllowedDomainException.noLicenseForFederation());
        }

        return Single
            .fromCallable(() ->
                integrationCrudService
                    .findById(integrationId)
                    .filter(integration -> integration.getEnvironmentId().equals(environmentId))
                    .orElseThrow(() -> new IntegrationNotFoundException(integrationId))
            )
            .flatMapPublisher(integration -> {
                var primaryOwner = apiPrimaryOwnerFactory.createForNewApi(organizationId, environmentId, input.auditInfo.actor().userId());
                return integrationAgent
                    .fetchAllApis(integration)
                    .doOnNext(api -> {
                        var federatedApi = ApiModelFactory.fromIntegration(api, integration);

                        apiCrudService
                            .findById(federatedApi.getId())
                            .ifPresentOrElse(
                                existingApi -> updateApi(federatedApi, existingApi, api, auditInfo, primaryOwner),
                                () -> createApi(federatedApi, api, auditInfo, primaryOwner)
                            );
                    });
            })
            .ignoreElements();
    }

    private void createApi(Api federatedApi, IntegrationApi integrationApi, AuditInfo auditInfo, PrimaryOwnerEntity primaryOwner) {
        try {
            createApiDomainService.create(federatedApi, primaryOwner, auditInfo, validateFederatedApi::validateAndSanitizeForCreation);

            ofNullable(integrationApi.plans())
                .stream()
                .flatMap(Collection::stream)
                .map(plan -> PlanModelFactory.fromIntegration(plan, federatedApi))
                .forEach(p -> createPlanDomainService.create(p, List.of(), federatedApi, auditInfo));

            ofNullable(integrationApi.pages())
                .stream()
                .flatMap(Collection::stream)
                .flatMap(page -> buildPage(page, integrationApi, federatedApi.getId()))
                .forEach(page -> createApiDocumentationDomainService.createPage(page, auditInfo));
        } catch (Exception e) {
            log.warn("An error occurred while importing api {}", federatedApi, e);
        }
    }

    private void updateApi(
        Api federatedApi,
        Api existingApi,
        IntegrationApi integrationApi,
        AuditInfo auditInfo,
        PrimaryOwnerEntity primaryOwner
    ) {
        log.debug("API already ingested [id={}] [name={}], performing update", federatedApi.getId(), federatedApi.getName());
        try {
            /*
             * Because we use page names as identifiers we need to look for the page by existing API name.
             * Otherwise, in case of an API name update, we would create a new page instead of updating the existing one.
             * If we move on to some predictable unique ID the existingApiName will become unnecessary.
             */
            var existingApiName = existingApi.getName();

            updateFederatedApiDomainService.update(
                existingApi
                    .toBuilder()
                    .name(federatedApi.getName())
                    .description(federatedApi.getDescription())
                    .version(federatedApi.getVersion())
                    .federatedApiDefinition(federatedApi.getFederatedApiDefinition())
                    .build(),
                auditInfo,
                primaryOwner
            );

            ofNullable(integrationApi.plans())
                .stream()
                .flatMap(Collection::stream)
                .map(p -> PlanModelFactory.fromIntegration(p, federatedApi))
                .forEach(p ->
                    planCrudService
                        .findById(p.getId())
                        .ifPresentOrElse(
                            existingPlan -> updatePlanDomainService.update(p, List.of(), Map.of(), null, auditInfo),
                            () -> createPlanDomainService.create(p, List.of(), federatedApi, auditInfo)
                        )
                );

            ofNullable(integrationApi.pages())
                .stream()
                .flatMap(Collection::stream)
                .flatMap(page -> buildPage(page, integrationApi, federatedApi.getId()))
                .forEach(page ->
                    pageQueryService
                        .findByNameAndReferenceId(generatePageName(existingApiName, page.getType()), federatedApi.getId())
                        .ifPresentOrElse(
                            existingPage -> {
                                var pageWithProperCreatedAt = page
                                    .toBuilder()
                                    .createdAt(existingPage.getCreatedAt())
                                    .id(existingPage.getId())
                                    .build();
                                updateApiDocumentationDomainService.updatePage(pageWithProperCreatedAt, existingPage, auditInfo);
                            },
                            () -> createApiDocumentationDomainService.createPage(page, auditInfo)
                        )
                );
        } catch (Exception e) {
            log.warn("An error occurred while updating api {}", federatedApi, e);
        }
    }

    private Stream<Page> buildPage(IntegrationApi.Page page, IntegrationApi integrationApi, String referenceId) {
        if (page == null || page.pageType() == null) {
            return Stream.empty();
        }
        return switch (page.pageType()) {
            case SWAGGER -> Stream.of(buildSwaggerPage(integrationApi.name(), referenceId, page.content()));
            case ASYNCAPI -> Stream.of(buildAsyncApiPage(integrationApi.name(), referenceId, page.content()));
            case ASCIIDOC, MARKDOWN, MARKDOWN_TEMPLATE -> {
                log.error("Impossible to import {} documentation for {}", page.pageType(), integrationApi.name());
                yield Stream.empty();
            }
        };
    }

    private Page buildSwaggerPage(String name, String referenceId, String content) {
        var now = Date.from(TimeProvider.instantNow());
        return Page
            .builder()
            .id(UuidString.generateRandom())
            .name(generatePageName(name, Page.Type.SWAGGER))
            .content(content)
            .type(Page.Type.valueOf(IntegrationApi.PageType.SWAGGER.name()))
            .referenceId(referenceId)
            .referenceType(Page.ReferenceType.API)
            .published(true)
            .visibility(Page.Visibility.PRIVATE)
            .homepage(true)
            .configuration(Map.of("tryIt", "true", "viewer", "Swagger"))
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    private Page buildAsyncApiPage(String name, String referenceId, String content) {
        var now = Date.from(TimeProvider.instantNow());
        return Page
            .builder()
            .id(UuidString.generateRandom())
            .name(generatePageName(name, Page.Type.ASYNCAPI))
            .content(content)
            .type(Page.Type.valueOf(IntegrationApi.PageType.ASYNCAPI.name()))
            .referenceId(referenceId)
            .referenceType(Page.ReferenceType.API)
            .published(true)
            .visibility(Page.Visibility.PRIVATE)
            .homepage(true)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    private String generatePageName(String apiName, Page.Type pageType) {
        return switch (pageType) {
            case SWAGGER -> apiName.concat("-oas.yml");
            case ASYNCAPI -> apiName.concat(".json");
            default -> throw new IllegalStateException("Unexpected value: " + pageType);
        };
    }

    public record Input(String integrationId, AuditInfo auditInfo) {}
}
