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
package io.gravitee.apim.infra.adapter;

import io.gravitee.apim.core.documentation.model.*;
import java.util.List;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PageAdapter {
    PageAdapter INSTANCE = Mappers.getMapper(PageAdapter.class);

    @Mapping(target = "visibility", source = "visibility")
    Page toEntity(io.gravitee.repository.management.model.Page page);

    io.gravitee.repository.management.model.Page toRepository(Page page);

    @Mapping(target = "createdAt", source = "modificationDate")
    io.gravitee.repository.management.model.PageRevision toPageRevisionRepository(PageRevision page);

    @Mapping(target = "pageId", source = "id")
    @Mapping(target = "modificationDate", source = "updatedAt")
    @Mapping(target = "contributor", source = "lastContributor")
    PageRevision toPageRevision(Page page);

    List<Page> toEntityList(List<io.gravitee.repository.management.model.Page> pages);

    @Mapping(target = "modificationDate", source = "createdAt")
    PageRevision toEntity(io.gravitee.repository.management.model.PageRevision pageRevision);
}
