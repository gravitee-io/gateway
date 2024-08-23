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
package io.gravitee.apim.infra.query_service.portal_menu_link;

import io.gravitee.apim.core.exception.TechnicalDomainException;
import io.gravitee.apim.core.portal_menu_link.model.PortalMenuLink;
import io.gravitee.apim.core.portal_menu_link.query_service.PortalMenuLinkQueryService;
import io.gravitee.apim.infra.adapter.PortalMenuLinkAdapter;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.PortalMenuLinkRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class PortalMenuLinkQueryServiceImpl implements PortalMenuLinkQueryService {

    private final PortalMenuLinkRepository portalMenuLinkRepository;
    private final PortalMenuLinkAdapter portalMenuLinkAdapter;
    private static final Logger logger = LoggerFactory.getLogger(PortalMenuLinkQueryServiceImpl.class);

    public PortalMenuLinkQueryServiceImpl(
        @Lazy final PortalMenuLinkRepository portalMenuLinkRepository,
        PortalMenuLinkAdapter portalMenuLinkAdapter
    ) {
        this.portalMenuLinkRepository = portalMenuLinkRepository;
        this.portalMenuLinkAdapter = portalMenuLinkAdapter;
    }

    @Override
    public List<PortalMenuLink> findByEnvironmentIdSortByOrder(String environmentId) {
        try {
            var result = portalMenuLinkRepository.findByEnvironmentIdSortByOrder(environmentId);

            return result.stream().map(portalMenuLinkAdapter::toEntity).toList();
        } catch (TechnicalException e) {
            logger.error("An error occurred while searching portal menu links by environment ID {}", environmentId, e);
            throw new TechnicalDomainException(
                "An error occurred while trying to search portal menu links by environment ID: " + environmentId,
                e
            );
        }
    }
}
