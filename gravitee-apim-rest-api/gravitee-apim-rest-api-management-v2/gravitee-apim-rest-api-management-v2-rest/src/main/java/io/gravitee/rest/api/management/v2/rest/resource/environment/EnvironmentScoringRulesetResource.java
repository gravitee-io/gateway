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
package io.gravitee.rest.api.management.v2.rest.resource.environment;

import io.gravitee.apim.core.scoring.use_case.DeleteEnvironmentRulesetUseCase;
import io.gravitee.rest.api.management.v2.rest.resource.AbstractResource;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvironmentScoringRulesetResource extends AbstractResource {

    @PathParam("rulesetId")
    String rulesetId;

    @Inject
    private DeleteEnvironmentRulesetUseCase deleteEnvironmentRulesetUseCase;

    @DELETE
    public Response deleteRuleset() {
        deleteEnvironmentRulesetUseCase.execute(new DeleteEnvironmentRulesetUseCase.Input(rulesetId, getAuditInfo()));
        return Response.noContent().build();
    }
}
