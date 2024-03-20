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
package io.gravitee.rest.api.service;

import io.gravitee.rest.api.model.OrganizationEntity;
import io.gravitee.rest.api.model.UpdateOrganizationEntity;
import java.util.Collection;
import java.util.Set;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface OrganizationService {
    Long count();

    OrganizationEntity findById(String organizationId);

    OrganizationEntity createOrUpdate(String organizationId, UpdateOrganizationEntity organizationEntity);

    OrganizationEntity updateOrganization(String organizationId, UpdateOrganizationEntity organizationEntity);

    OrganizationEntity updateOrganizationAndFlows(String organizationId, UpdateOrganizationEntity organizationEntity);

    void delete(String organizationId);

    OrganizationEntity initialize();

    /**
     * Find all existing organizations.
     *
     * @return the list of all organizations.
     */
    Collection<OrganizationEntity> findAll();

    OrganizationEntity getDefaultOrInitialize();

    OrganizationEntity findByCockpitId(String cockpitId);

    Set<OrganizationEntity> findByHrids(Set<String> hrids);
}
