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
package io.gravitee.rest.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.gravitee.repository.management.model.Api;
import io.gravitee.rest.api.service.common.ExecutionContext;

/**
 * @author GraviteeSource Team
 */
public interface ApiDefinitionService {
    io.gravitee.definition.model.Api buildGatewayApiDefinition(ExecutionContext executionContext, Api api) throws JsonProcessingException;
}
