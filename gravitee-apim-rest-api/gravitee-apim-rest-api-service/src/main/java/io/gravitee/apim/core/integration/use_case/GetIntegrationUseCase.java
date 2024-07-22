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

import static io.gravitee.apim.core.exception.NotAllowedDomainException.noLicenseForFederation;

import io.gravitee.apim.core.UseCase;
import io.gravitee.apim.core.integration.crud_service.IntegrationCrudService;
import io.gravitee.apim.core.integration.exception.IntegrationNotFoundException;
import io.gravitee.apim.core.integration.model.Integration;
import io.gravitee.apim.core.integration.model.IntegrationView;
import io.gravitee.apim.core.integration.query_service.IntegrationJobQueryService;
import io.gravitee.apim.core.integration.service_provider.IntegrationAgent;
import io.gravitee.apim.core.license.domain_service.LicenseDomainService;
import lombok.Builder;

/**
 * @author Remi Baptiste (remi.baptiste at graviteesource.com)
 * @author GraviteeSource Team
 */
@UseCase
public class GetIntegrationUseCase {

    private final IntegrationCrudService integrationCrudService;
    private final IntegrationJobQueryService integrationJobQueryService;
    private final LicenseDomainService licenseDomainService;
    private final IntegrationAgent integrationAgent;

    public GetIntegrationUseCase(
        IntegrationCrudService integrationCrudService,
        IntegrationJobQueryService integrationJobQueryService,
        LicenseDomainService licenseDomainService,
        IntegrationAgent integrationAgent
    ) {
        this.integrationCrudService = integrationCrudService;
        this.integrationJobQueryService = integrationJobQueryService;
        this.licenseDomainService = licenseDomainService;
        this.integrationAgent = integrationAgent;
    }

    public GetIntegrationUseCase.Output execute(GetIntegrationUseCase.Input input) {
        var integrationId = input.integrationId();

        if (!licenseDomainService.isFederationFeatureAllowed(input.organizationId())) {
            throw noLicenseForFederation();
        }

        Integration integration = integrationCrudService
            .findById(integrationId)
            .orElseThrow(() -> new IntegrationNotFoundException(integrationId));

        var agentStatus = integrationAgent
            .getAgentStatusFor(integrationId)
            .map(status -> IntegrationView.AgentStatus.valueOf(status.name()))
            .blockingGet();

        var pendingJob = integrationJobQueryService.findPendingJobFor(integrationId);

        return new GetIntegrationUseCase.Output(new IntegrationView(integration, agentStatus, pendingJob.orElse(null)));
    }

    @Builder
    public record Input(String integrationId, String organizationId) {}

    public record Output(IntegrationView integration) {}
}
