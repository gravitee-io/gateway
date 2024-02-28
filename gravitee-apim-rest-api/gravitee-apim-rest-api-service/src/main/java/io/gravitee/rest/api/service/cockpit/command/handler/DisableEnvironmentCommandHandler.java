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
package io.gravitee.rest.api.service.cockpit.command.handler;

import io.gravitee.apim.core.access_point.crud_service.AccessPointCrudService;
import io.gravitee.apim.core.access_point.model.AccessPoint;
import io.gravitee.cockpit.api.command.Command;
import io.gravitee.cockpit.api.command.CommandHandler;
import io.gravitee.cockpit.api.command.CommandStatus;
import io.gravitee.cockpit.api.command.environment.*;
import io.gravitee.repository.management.api.ApiRepository;
import io.gravitee.repository.management.api.search.ApiCriteria;
import io.gravitee.repository.management.api.search.ApiFieldFilter;
import io.gravitee.repository.management.model.LifecycleState;
import io.gravitee.rest.api.service.EnvironmentService;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.v4.ApiStateService;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DisableEnvironmentCommandHandler implements CommandHandler<DisableEnvironmentCommand, DisableEnvironmentReply> {

    private final EnvironmentService environmentService;
    private final ApiRepository apiRepository;
    private final ApiStateService apiStateService;
    private final AccessPointCrudService accessPointService;

    public DisableEnvironmentCommandHandler(
        EnvironmentService environmentService,
        ApiStateService apiStateService,
        @Lazy ApiRepository apiRepository,
        AccessPointCrudService accessPointService
    ) {
        this.environmentService = environmentService;
        this.apiStateService = apiStateService;
        this.apiRepository = apiRepository;
        this.accessPointService = accessPointService;
    }

    @Override
    public Command.Type handleType() {
        return Command.Type.DISABLE_ENVIRONMENT_COMMAND;
    }

    @Override
    public Single<DisableEnvironmentReply> handle(DisableEnvironmentCommand command) {
        var payload = command.getPayload();

        try {
            var environment = environmentService.findByCockpitId(payload.getCockpitId());
            var executionContext = new ExecutionContext(environment);

            // Stop all Environment APIs
            apiRepository
                .search(
                    new ApiCriteria.Builder().state(LifecycleState.STARTED).environmentId(environment.getId()).build(),
                    new ApiFieldFilter.Builder().excludeDefinition().excludePicture().build()
                )
                .forEach(api -> apiStateService.stop(executionContext, api.getId(), payload.getUserId()));

            // Delete related access points
            this.accessPointService.deleteAccessPoints(AccessPoint.ReferenceType.ENVIRONMENT, environment.getId());

            log.info("Environment [{}] with id [{}] has been disabled.", environment.getName(), environment.getId());
            return Single.just(new DisableEnvironmentReply(command.getId(), CommandStatus.SUCCEEDED));
        } catch (Exception e) {
            log.error("Error occurred when disabling environment [{}] with id [{}].", payload.getName(), payload.getId(), e);
            return Single.just(new DisableEnvironmentReply(command.getId(), CommandStatus.ERROR));
        }
    }
}
