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
package io.gravitee.rest.api.management.v2.rest.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import fixtures.ApiFixtures;
import fixtures.CorsFixtures;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class ApiMapperTest {

    private final ApiMapper apiMapper = Mappers.getMapper(ApiMapper.class);

    @Test
    void shouldMapToUpdateApiEntityV4() {
        var updateApi = ApiFixtures.anUpdateApiV4();

        var updateApiEntity = apiMapper.map(updateApi, "api-id");
        assertThat(updateApiEntity).isNotNull();
        assertThat(updateApiEntity.getId()).isEqualTo("api-id");
        assertThat(updateApiEntity.getCrossId()).isNull();
        assertThat(updateApiEntity.getName()).isEqualTo(updateApi.getName());
        assertThat(updateApiEntity.getApiVersion()).isEqualTo(updateApi.getApiVersion());
        assertThat(updateApiEntity.getDefinitionVersion().name()).isEqualTo(updateApi.getDefinitionVersion().name());
        assertThat(updateApiEntity.getType().name()).isEqualTo(updateApi.getType().name());
        assertThat(updateApiEntity.getDescription()).isEqualTo(updateApi.getDescription());
        assertThat(new ArrayList<>(updateApiEntity.getTags())).isEqualTo(updateApi.getTags());
        assertThat(updateApiEntity.getListeners()).isNotNull(); // Tested in ListenerMapperTest
        assertThat(updateApiEntity.getEndpointGroups()).isNotNull(); // Tested in EndpointMapperTest
        assertThat(updateApiEntity.getAnalytics()).isNotNull();
        assertThat(updateApiEntity.getProperties()).isNotNull(); // Tested in PropertiesMapperTest
        assertThat(updateApiEntity.getResources()).isNotNull(); // Tested in ResourceMapperTest
        assertThat(updateApiEntity.getPlans().size()).isEqualTo(0);
        assertThat(updateApiEntity.getFlowExecution()).usingRecursiveAssertion().isEqualTo(updateApi.getFlowExecution());
        assertThat(updateApiEntity.getFlows()).isNotNull(); // Tested in FlowMapperTest
        assertThat(updateApiEntity.getResponseTemplates()).isNotNull();
        assertThat(updateApiEntity.getServices()).isNotNull(); // Tested in ServiceMapperTest
        assertThat(new ArrayList<>(updateApiEntity.getGroups())).isEqualTo(updateApi.getGroups());
        assertThat(updateApiEntity.getVisibility().name()).isEqualTo(updateApi.getVisibility().name());
        assertThat(new ArrayList<>(updateApiEntity.getCategories())).isEqualTo(updateApi.getCategories());
        assertThat(updateApiEntity.getLabels()).isEqualTo(updateApi.getLabels());
        assertThat(updateApiEntity.getMetadata()).isNull();
        assertThat(updateApiEntity.getLifecycleState().name()).isEqualTo(updateApi.getLifecycleState().name());
        assertThat(updateApiEntity.isDisableMembershipNotifications()).isEqualTo(updateApi.getDisableMembershipNotifications());
    }
}
