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
package io.gravitee.apim.core.scoring.use_case;

import io.gravitee.apim.core.UseCase;
import io.gravitee.apim.core.audit.model.AuditInfo;
import io.gravitee.apim.core.scoring.crud_service.ScoringRulesetCrudService;
import io.gravitee.apim.core.scoring.model.ScoringRuleset;
import io.gravitee.common.utils.TimeProvider;
import io.gravitee.rest.api.service.common.UuidString;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class ImportEnvironmentRulesetUseCase {

    private final ScoringRulesetCrudService scoringRulesetCrudService;

    public Output execute(Input input) {
        var created = scoringRulesetCrudService.create(
            new ScoringRuleset(
                UuidString.generateRandom(),
                input.newRuleset.name(),
                input.newRuleset.description(),
                input.auditInfo.environmentId(),
                ScoringRuleset.ReferenceType.ENVIRONMENT,
                input.newRuleset.payload(),
                TimeProvider.now()
            )
        );
        return new Output(created.id());
    }

    public record Input(NewRuleset newRuleset, AuditInfo auditInfo) {}

    public record NewRuleset(String name, String description, String payload) {}

    public record Output(String rulesetId) {}
}
