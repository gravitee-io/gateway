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
package io.gravitee.apim.infra.spring;

import io.gravitee.apim.core.api.crud_service.ApiCrudService;
import io.gravitee.apim.core.api.domain_service.ApiMetadataDomainService;
import io.gravitee.apim.core.api.domain_service.CreateApiDomainService;
import io.gravitee.apim.core.api.domain_service.DeployApiDomainService;
import io.gravitee.apim.core.api.domain_service.UpdateApiDomainService;
import io.gravitee.apim.core.api.domain_service.VerifyApiPathDomainService;
import io.gravitee.apim.core.api.query_service.ApiQueryService;
import io.gravitee.apim.core.api.use_case.ImportCRDUseCase;
import io.gravitee.apim.core.api.use_case.VerifyApiPathsUseCase;
import io.gravitee.apim.core.api_key.domain_service.RevokeApiKeyDomainService;
import io.gravitee.apim.core.api_key.query_service.ApiKeyQueryService;
import io.gravitee.apim.core.api_key.use_case.RevokeApiSubscriptionApiKeyUseCase;
import io.gravitee.apim.core.api_key.use_case.RevokeApplicationApiKeyUseCase;
import io.gravitee.apim.core.api_key.use_case.RevokeApplicationSubscriptionApiKeyUseCase;
import io.gravitee.apim.core.api_key.use_case.RevokeSubscriptionApiKeyUseCase;
import io.gravitee.apim.core.application.crud_service.ApplicationCrudService;
import io.gravitee.apim.core.console.use_case.GetConsoleCustomizationUseCase;
import io.gravitee.apim.core.documentation.crud_service.PageCrudService;
import io.gravitee.apim.core.documentation.domain_service.ApiDocumentationDomainService;
import io.gravitee.apim.core.documentation.domain_service.CreateApiDocumentationDomainService;
import io.gravitee.apim.core.documentation.domain_service.DeleteApiDocumentationDomainService;
import io.gravitee.apim.core.documentation.domain_service.DocumentationValidationDomainService;
import io.gravitee.apim.core.documentation.domain_service.HomepageDomainService;
import io.gravitee.apim.core.documentation.domain_service.UpdateApiDocumentationDomainService;
import io.gravitee.apim.core.documentation.query_service.PageQueryService;
import io.gravitee.apim.core.documentation.use_case.ApiCreateDocumentationPageUseCase;
import io.gravitee.apim.core.documentation.use_case.ApiDeleteDocumentationPageUseCase;
import io.gravitee.apim.core.documentation.use_case.ApiGetDocumentationPageUseCase;
import io.gravitee.apim.core.documentation.use_case.ApiGetDocumentationPagesUseCase;
import io.gravitee.apim.core.documentation.use_case.ApiPublishDocumentationPageUseCase;
import io.gravitee.apim.core.documentation.use_case.ApiUnpublishDocumentationPageUseCase;
import io.gravitee.apim.core.documentation.use_case.ApiUpdateDocumentationPageUseCase;
import io.gravitee.apim.core.environment.crud_service.EnvironmentCrudService;
import io.gravitee.apim.core.license.domain_service.GraviteeLicenseDomainService;
import io.gravitee.apim.core.log.crud_service.ConnectionLogsCrudService;
import io.gravitee.apim.core.log.crud_service.MessageLogCrudService;
import io.gravitee.apim.core.log.use_case.SearchConnectionLogUseCase;
import io.gravitee.apim.core.log.use_case.SearchConnectionLogsUseCase;
import io.gravitee.apim.core.log.use_case.SearchMessageLogsUseCase;
import io.gravitee.apim.core.parameters.domain_service.ParametersDomainService;
import io.gravitee.apim.core.plan.crud_service.PlanCrudService;
import io.gravitee.apim.core.plan.domain_service.CreatePlanDomainService;
import io.gravitee.apim.core.plan.domain_service.DeletePlanDomainService;
import io.gravitee.apim.core.plan.domain_service.ReorderPlanDomainService;
import io.gravitee.apim.core.plan.domain_service.UpdatePlanDomainService;
import io.gravitee.apim.core.plan.query_service.PlanQueryService;
import io.gravitee.apim.core.subscription.crud_service.SubscriptionCrudService;
import io.gravitee.apim.core.subscription.domain_service.CloseSubscriptionDomainService;
import io.gravitee.apim.core.subscription.query_service.SubscriptionQueryService;
import io.gravitee.apim.core.subscription.use_case.CloseExpiredSubscriptionsUseCase;
import io.gravitee.apim.core.subscription.use_case.CloseSubscriptionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ CoreServiceSpringConfiguration.class })
public class UsecaseSpringConfiguration {

    @Bean
    public CloseSubscriptionUseCase closeSubscriptionUsecase(
        SubscriptionCrudService subscriptionCrudService,
        CloseSubscriptionDomainService closeSubscriptionDomainService
    ) {
        return new CloseSubscriptionUseCase(subscriptionCrudService, closeSubscriptionDomainService);
    }

    @Bean
    public CloseExpiredSubscriptionsUseCase closeExpiredSubscriptionsUsecase(
        SubscriptionQueryService subscriptionQueryService,
        ApiQueryService apiQueryService,
        EnvironmentCrudService environmentCrudService,
        CloseSubscriptionDomainService closeSubscriptionDomainService
    ) {
        return new CloseExpiredSubscriptionsUseCase(
            subscriptionQueryService,
            apiQueryService,
            environmentCrudService,
            closeSubscriptionDomainService
        );
    }

    @Bean
    public RevokeApiSubscriptionApiKeyUseCase revokeApiSubscriptionApiKeyUsecase(
        SubscriptionCrudService subscriptionCrudService,
        ApplicationCrudService applicationCrudService,
        ApiKeyQueryService apiKeyQueryService,
        RevokeApiKeyDomainService revokeApiKeyDomainService
    ) {
        return new RevokeApiSubscriptionApiKeyUseCase(
            subscriptionCrudService,
            applicationCrudService,
            apiKeyQueryService,
            revokeApiKeyDomainService
        );
    }

    @Bean
    public RevokeApplicationApiKeyUseCase revokeApplicationApiKeyUsecase(
        ApplicationCrudService applicationCrudService,
        ApiKeyQueryService apiKeyQueryService,
        RevokeApiKeyDomainService revokeApiKeyDomainService
    ) {
        return new RevokeApplicationApiKeyUseCase(applicationCrudService, apiKeyQueryService, revokeApiKeyDomainService);
    }

    @Bean
    public RevokeApplicationSubscriptionApiKeyUseCase revokeApplicationSubscriptionApiKeyUsecase(
        SubscriptionCrudService subscriptionCrudService,
        ApplicationCrudService applicationCrudService,
        ApiKeyQueryService apiKeyQueryService,
        RevokeApiKeyDomainService revokeApiKeyDomainService
    ) {
        return new RevokeApplicationSubscriptionApiKeyUseCase(
            subscriptionCrudService,
            applicationCrudService,
            apiKeyQueryService,
            revokeApiKeyDomainService
        );
    }

    @Bean
    public RevokeSubscriptionApiKeyUseCase revokeSubscriptionApiKeyUsecase(
        SubscriptionCrudService subscriptionCrudService,
        ApplicationCrudService applicationCrudService,
        ApiKeyQueryService apiKeyQueryService,
        RevokeApiKeyDomainService revokeApiKeyDomainService
    ) {
        return new RevokeSubscriptionApiKeyUseCase(
            subscriptionCrudService,
            applicationCrudService,
            apiKeyQueryService,
            revokeApiKeyDomainService
        );
    }

    @Bean
    public SearchConnectionLogsUseCase searchConnectionLogsUsecase(
        ConnectionLogsCrudService connectionLogsCrudService,
        PlanCrudService planCrudService,
        ApplicationCrudService applicationCrudService
    ) {
        return new SearchConnectionLogsUseCase(connectionLogsCrudService, planCrudService, applicationCrudService);
    }

    @Bean
    public SearchConnectionLogUseCase searchConnectionLogUsecase(ConnectionLogsCrudService connectionLogsCrudService) {
        return new SearchConnectionLogUseCase(connectionLogsCrudService);
    }

    @Bean
    public SearchMessageLogsUseCase searchMessageLogsUsecase(MessageLogCrudService messageLogCrudService) {
        return new SearchMessageLogsUseCase(messageLogCrudService);
    }

    @Bean
    public VerifyApiPathsUseCase verifyApiPathDomainUsecase(VerifyApiPathDomainService verifyApiPathDomainService) {
        return new VerifyApiPathsUseCase(verifyApiPathDomainService);
    }

    @Bean
    public ApiGetDocumentationPagesUseCase apiGetDocumentationPagesUsecase(
        ApiDocumentationDomainService apiDocumentationService,
        ApiCrudService apiCrudService,
        PageCrudService pageCrudService
    ) {
        return new ApiGetDocumentationPagesUseCase(apiDocumentationService, apiCrudService, pageCrudService);
    }

    @Bean
    public ApiGetDocumentationPageUseCase apiGetDocumentationPageUsecase(
        ApiDocumentationDomainService apiDocumentationDomainService,
        ApiCrudService apiCrudService,
        PageCrudService pageCrudService
    ) {
        return new ApiGetDocumentationPageUseCase(apiDocumentationDomainService, apiCrudService, pageCrudService);
    }

    @Bean
    public ApiCreateDocumentationPageUseCase apiCreateDocumentationPageUsecase(
        CreateApiDocumentationDomainService createApiDocumentationDomainService,
        ApiDocumentationDomainService apiDocumentationDomainService,
        HomepageDomainService homepageDomainService,
        PageCrudService pageCrudService,
        PageQueryService pageQueryService,
        DocumentationValidationDomainService documentationValidationDomainService
    ) {
        return new ApiCreateDocumentationPageUseCase(
            createApiDocumentationDomainService,
            apiDocumentationDomainService,
            homepageDomainService,
            pageCrudService,
            pageQueryService,
            documentationValidationDomainService
        );
    }

    @Bean
    public ApiUpdateDocumentationPageUseCase apiUpdateDocumentationPageUsecase(
        UpdateApiDocumentationDomainService updateApiDocumentationDomainService,
        ApiDocumentationDomainService apiDocumentationDomainService,
        HomepageDomainService homepageDomainService,
        ApiCrudService apiCrudService,
        PageCrudService pageCrudService,
        PageQueryService pageQueryService,
        DocumentationValidationDomainService documentationValidationDomainService
    ) {
        return new ApiUpdateDocumentationPageUseCase(
            updateApiDocumentationDomainService,
            apiDocumentationDomainService,
            homepageDomainService,
            apiCrudService,
            pageCrudService,
            pageQueryService,
            documentationValidationDomainService
        );
    }

    @Bean
    public ApiPublishDocumentationPageUseCase apiPublishDocumentationPageUsecase(
        ApiDocumentationDomainService apiDocumentationDomainService,
        UpdateApiDocumentationDomainService updateApiDocumentationDomainService,
        ApiCrudService apiCrudService,
        PageCrudService pageCrudService
    ) {
        return new ApiPublishDocumentationPageUseCase(
            apiDocumentationDomainService,
            updateApiDocumentationDomainService,
            apiCrudService,
            pageCrudService
        );
    }

    @Bean
    public ApiUnpublishDocumentationPageUseCase apiUnpublishDocumentationPageUseCase(
        ApiDocumentationDomainService apiDocumentationDomainService,
        UpdateApiDocumentationDomainService updateApiDocumentationDomainService,
        ApiCrudService apiCrudService,
        PageCrudService pageCrudService
    ) {
        return new ApiUnpublishDocumentationPageUseCase(
            apiDocumentationDomainService,
            updateApiDocumentationDomainService,
            apiCrudService,
            pageCrudService
        );
    }

    @Bean
    public ApiDeleteDocumentationPageUseCase apiDeleteDocumentationPageUseCase(
        DeleteApiDocumentationDomainService deleteApiDocumentationDomainService,
        ApiCrudService apiCrudService
    ) {
        return new ApiDeleteDocumentationPageUseCase(deleteApiDocumentationDomainService, apiCrudService);
    }

    @Bean
    public GetConsoleCustomizationUseCase getConsoleCustomizationUseCase(
        GraviteeLicenseDomainService licenseDomainService,
        ParametersDomainService parametersDomainService
    ) {
        return new GetConsoleCustomizationUseCase(licenseDomainService, parametersDomainService);
    }

    @Bean
    public ImportCRDUseCase importCRDUseCase(
        ApiCrudService apiCrudService,
        ApiQueryService apiQueryService,
        CreateApiDomainService createApiDomainService,
        CreatePlanDomainService createPlanDomainService,
        ApiMetadataDomainService apiMetadataDomainService,
        DeployApiDomainService deployApiDomainService,
        UpdateApiDomainService updateApiDomainService,
        PlanQueryService planQueryService,
        UpdatePlanDomainService updatePlanDomainService,
        DeletePlanDomainService deletePlanDomainService,
        SubscriptionQueryService subscriptionQueryService,
        CloseSubscriptionDomainService closeSubscriptionDomainService,
        ReorderPlanDomainService reorderPlanDomainService
    ) {
        return new ImportCRDUseCase(
            apiCrudService,
            apiQueryService,
            createApiDomainService,
            createPlanDomainService,
            apiMetadataDomainService,
            deployApiDomainService,
            updateApiDomainService,
            planQueryService,
            updatePlanDomainService,
            deletePlanDomainService,
            subscriptionQueryService,
            closeSubscriptionDomainService,
            reorderPlanDomainService
        );
    }
}
