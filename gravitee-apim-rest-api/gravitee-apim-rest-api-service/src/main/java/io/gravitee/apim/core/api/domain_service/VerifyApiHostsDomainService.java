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
package io.gravitee.apim.core.api.domain_service;

import io.gravitee.apim.core.api.exception.DuplicatedHostException;
import io.gravitee.apim.core.api.exception.HostAlreadyExistsException;
import io.gravitee.apim.core.api.exception.InvalidHostException;
import io.gravitee.apim.core.api.model.ApiFieldFilter;
import io.gravitee.apim.core.api.model.ApiSearchCriteria;
import io.gravitee.apim.core.api.query_service.ApiQueryService;
import io.gravitee.definition.model.DefinitionVersion;
import io.gravitee.definition.model.v4.ApiType;
import io.gravitee.definition.model.v4.listener.tcp.TcpListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class VerifyApiHostsDomainService {

    private final ApiQueryService apiQueryService;

    public boolean checkApiHosts(String environmentId, String apiId, List<String> hosts) {
        if (hosts == null || hosts.isEmpty()) {
            throw new InvalidHostException("At least one host is required for the TCP listener.");
        }

        if (hosts.stream().anyMatch(host -> host == null || host.isBlank())) {
            throw new InvalidHostException("The hosts should not be null or blank.");
        }

        checkNoDuplicate(hosts);

        checkHostsAreAvailable(environmentId, apiId, hosts);

        return true;
    }

    private void checkNoDuplicate(List<String> hosts) throws DuplicatedHostException {
        var set = new HashSet<>();
        var duplicates = hosts.stream().filter(n -> !set.add(n)).toList();

        if (!duplicates.isEmpty()) {
            throw new DuplicatedHostException(String.join(", ", duplicates));
        }
    }

    private void checkHostsAreAvailable(String environmentId, String apiId, List<String> hosts) {
        var existingHosts = listHostsByEnvironmentId(environmentId, apiId);
        var duplicates = hosts.stream().filter(existingHosts::contains).toList();
        if (!duplicates.isEmpty()) {
            throw new HostAlreadyExistsException(String.join(", ", duplicates));
        }
    }

    private Set<String> listHostsByEnvironmentId(String environmentId, String apiId) {
        return apiQueryService
            .search(
                ApiSearchCriteria.builder().environmentId(environmentId).build(),
                null,
                ApiFieldFilter.builder().pictureExcluded(true).build()
            )
            .filter(api ->
                !api.getId().equals(apiId) &&
                ApiType.PROXY.equals(api.getType()) &&
                DefinitionVersion.V4.equals(api.getDefinitionVersion()) &&
                null != api.getApiDefinitionV4()
            )
            .flatMap(api -> api.getApiDefinitionV4().getListeners().stream())
            .filter(TcpListener.class::isInstance)
            .map(TcpListener.class::cast)
            .map(TcpListener::getHosts)
            .filter(extractedHosts -> !extractedHosts.isEmpty())
            .flatMap(List::stream)
            .collect(Collectors.toSet());
    }
}
