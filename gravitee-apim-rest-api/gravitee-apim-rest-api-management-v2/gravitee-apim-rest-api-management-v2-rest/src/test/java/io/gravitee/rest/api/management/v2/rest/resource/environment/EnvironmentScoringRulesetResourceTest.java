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

import static assertions.MAPIAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

import assertions.MAPIAssertions;
import assertions.RestApiAssertions;
import fixtures.core.model.ScoringRulesetFixture;
import inmemory.InMemoryAlternative;
import inmemory.ScoringRulesetCrudServiceInMemory;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.rest.api.management.v2.rest.resource.AbstractResourceTest;
import io.gravitee.rest.api.model.EnvironmentEntity;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.common.UuidString;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.WebTarget;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EnvironmentScoringRulesetResourceTest extends AbstractResourceTest {

    private static final String ENVIRONMENT = "my-env";

    WebTarget rootTarget;

    @Inject
    ScoringRulesetCrudServiceInMemory scoringRulesetCrudService;

    @Override
    protected String contextPath() {
        return "/environments/" + ENVIRONMENT + "/scoring/rulesets";
    }

    @BeforeEach
    void setup() {
        rootTarget = rootTarget();

        EnvironmentEntity environmentEntity = EnvironmentEntity.builder().id(ENVIRONMENT).organizationId(ORGANIZATION).build();
        doReturn(environmentEntity).when(environmentService).findById(ENVIRONMENT);
        doReturn(environmentEntity).when(environmentService).findByOrgAndIdOrHrid(ORGANIZATION, ENVIRONMENT);

        GraviteeContext.setCurrentEnvironment(ENVIRONMENT);
        GraviteeContext.setCurrentOrganization(ORGANIZATION);
    }

    @AfterEach
    @Override
    public void tearDown() {
        super.tearDown();
        UuidString.reset();
        GraviteeContext.cleanContext();

        Stream.of(scoringRulesetCrudService).forEach(InMemoryAlternative::reset);
    }

    @Nested
    class DeleteEnvironmentRuleset {

        @Test
        void should_delete_a_ruleset() {
            // Given
            scoringRulesetCrudService.initWith(List.of(ScoringRulesetFixture.aRuleset("ruleset-id").withReferenceId(ENVIRONMENT)));

            // When
            var response = rootTarget.path("ruleset-id").request().delete();

            // Then
            assertThat(response).hasStatus(HttpStatusCode.NO_CONTENT_204);
            assertThat(scoringRulesetCrudService.storage()).isEmpty();
        }
    }
}
