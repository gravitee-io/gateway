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

import io.gravitee.apim.core.UseCase;
import io.gravitee.apim.core.integration.crud_service.IntegrationCrudService;
import io.gravitee.apim.core.integration.exception.IntegrationNotFoundException;
import io.gravitee.apim.core.integration.model.Integration;
import io.gravitee.common.utils.TimeProvider;
import lombok.Builder;

@UseCase
public class UpdateIntegrationUseCase {

    IntegrationCrudService integrationCrudService;

    public UpdateIntegrationUseCase(IntegrationCrudService integrationCrudService) {
        this.integrationCrudService = integrationCrudService;
    }

    public Output execute(Input input) {
        var now = TimeProvider.now();
        var integrationId = input.integration.getId();

        var integration = integrationCrudService.findById(integrationId).orElseThrow(() -> new IntegrationNotFoundException(integrationId));
        var integrationToUpdate = integration
            .toBuilder()
            .name(input.integration.getName())
            .description(input.integration.getDescription())
            .updatedAt(now)
            .build();

        return new Output(integrationCrudService.update(integrationToUpdate));
    }

    @Builder
    public record Input(Integration integration) {}

    public record Output(Integration integration) {}
}
