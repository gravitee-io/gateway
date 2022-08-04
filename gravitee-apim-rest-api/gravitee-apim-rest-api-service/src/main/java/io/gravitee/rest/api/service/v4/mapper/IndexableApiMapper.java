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
package io.gravitee.rest.api.service.v4.mapper;

import io.gravitee.definition.model.DefinitionVersion;
import io.gravitee.repository.management.model.Api;
import io.gravitee.rest.api.model.PrimaryOwnerEntity;
import io.gravitee.rest.api.model.v4.api.IndexableApi;
import io.gravitee.rest.api.service.converter.ApiConverter;
import org.springframework.stereotype.Component;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@Component
public class IndexableApiMapper {

    private final ApiMapper apiMapper;
    private final ApiConverter apiConverter;

    public IndexableApiMapper(final ApiMapper apiMapper, final ApiConverter apiConverter) {
        this.apiMapper = apiMapper;
        this.apiConverter = apiConverter;
    }

    public IndexableApi toGenericApi(final Api api, final PrimaryOwnerEntity primaryOwner) {
        if (api.getDefinitionVersion() == DefinitionVersion.V4) {
            return apiMapper.toEntity(api, primaryOwner);
        } else {
            return apiConverter.toApiEntity(api, primaryOwner);
        }
    }
}
