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

import io.gravitee.apim.core.portal_menu_link.model.PortalMenuLink;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PortalMenuLinkAdapter {
    PortalMenuLinkAdapter INSTANCE = Mappers.getMapper(PortalMenuLinkAdapter.class);

    PortalMenuLink toEntity(io.gravitee.repository.management.model.PortalMenuLink portalMenuLink);
    io.gravitee.repository.management.model.PortalMenuLink toRepository(PortalMenuLink portalMenuLink);
}
