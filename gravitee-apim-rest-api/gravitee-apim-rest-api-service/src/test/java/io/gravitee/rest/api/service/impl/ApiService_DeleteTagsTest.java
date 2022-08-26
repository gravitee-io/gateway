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
package io.gravitee.rest.api.service.impl;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.ApiRepository;
import io.gravitee.repository.management.model.Api;
import io.gravitee.rest.api.model.EnvironmentEntity;
import io.gravitee.rest.api.model.MemberEntity;
import io.gravitee.rest.api.model.PrimaryOwnerEntity;
import io.gravitee.rest.api.model.RoleEntity;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.model.permissions.RoleScope;
import io.gravitee.rest.api.service.*;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.configuration.flow.FlowService;
import io.gravitee.rest.api.service.converter.ApiConverter;
import io.gravitee.rest.api.service.v4.ApiNotificationService;
import io.gravitee.rest.api.service.v4.PrimaryOwnerService;
import io.gravitee.rest.api.service.v4.impl.PrimaryOwnerServiceImpl;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiService_DeleteTagsTest {

    @InjectMocks
    private final ApiService apiService = new ApiServiceImpl();

    @Mock
    EnvironmentService environmentService;

    @Mock
    ApiRepository apiRepository;

    @Mock
    AuditService auditService;

    @Mock
    CategoryService categoryService;

    @Mock
    ApiConverter apiConverter;

    @Mock
    ApiNotificationService apiNotificationService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    PrimaryOwnerService primaryOwnerService;

    @Test
    public void shouldDeleteTags() throws TechnicalException, JsonProcessingException {
        final ExecutionContext executionContext = new ExecutionContext("DEFAULT", null);

        final EnvironmentEntity environment = new EnvironmentEntity();
        environment.setId("DEFAULT");

        final Api api = new Api();
        api.setId("api-id");
        api.setDefinition("{\"tags\": [\"intranet\"]}");

        final ApiEntity apiEntity = new ApiEntity();
        apiEntity.setId(api.getId());
        apiEntity.setTags(Set.of("intranet"));

        final io.gravitee.definition.model.Api apiDefinition = new io.gravitee.definition.model.Api();
        apiDefinition.setTags(new HashSet<>(apiEntity.getTags()));

        when(environmentService.findByOrganization("DEFAULT")).thenReturn(List.of(environment));
        when(apiRepository.search(any())).thenReturn(List.of(api));
        when(apiRepository.findById(any())).thenReturn(Optional.of(api));

        when(primaryOwnerService.getPrimaryOwners(any(), anyList())).thenReturn(Map.of(api.getId(), new PrimaryOwnerEntity()));
        when(apiConverter.toApiEntity(any(), any(), any(), any(), anyBoolean())).thenReturn(apiEntity);

        when(objectMapper.readValue(api.getDefinition(), io.gravitee.definition.model.Api.class)).thenReturn(apiDefinition);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        apiService.deleteTagFromAPIs(executionContext, "intranet");

        verify(apiRepository, times(1)).update(argThat(apiUpdate -> !apiUpdate.getDefinition().contains("intranet")));
    }
}
