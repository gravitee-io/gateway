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
package io.gravitee.apim.core.access_point.query_service;

import io.gravitee.apim.core.access_point.model.RestrictedDomainEntity;
import io.gravitee.rest.api.service.common.ReferenceContext;
import java.util.List;
import java.util.Optional;

public interface AccessPointQueryService {
    Optional<ReferenceContext> getReferenceContext(final String host);

    List<String> getConsoleUrls(final String organizationId, final boolean includeDefault);

    String getConsoleUrl(final String organizationId);

    String getConsoleApiUrl(String organizationId);

    List<String> getPortalUrls(final String environmentId, final boolean includeDefault);
    String getPortalUrl(final String environmentId);

    String getPortalApiUrl(String environmentId);

    List<RestrictedDomainEntity> getGatewayRestrictedDomains(final String environmentId);
}
