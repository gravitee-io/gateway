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
package io.gravitee.apim.core.plugin.use_case;

import io.gravitee.apim.core.plugin.domain_service.PluginFilterByLicenseDomainService;
import io.gravitee.apim.core.plugin.model.ConnectorPlugin;
import io.gravitee.apim.core.plugin.query_service.EndpointPluginQueryService;
import java.util.Set;

public class GetEndpointPluginsUseCase {

    private final EndpointPluginQueryService endpointPluginQueryService;
    private final PluginFilterByLicenseDomainService licenseChecker;

    public GetEndpointPluginsUseCase(
        EndpointPluginQueryService endpointPluginQueryService,
        PluginFilterByLicenseDomainService licenseChecker
    ) {
        this.endpointPluginQueryService = endpointPluginQueryService;
        this.licenseChecker = licenseChecker;
    }

    public Output getEndpointPluginsByOrganization(Input input) {
        return new Output(
            this.licenseChecker.setPluginDeployedStatusDependingOnLicense(this.endpointPluginQueryService.findAll(), input.organizationId)
        );
    }

    public record Input(String organizationId) {}

    public record Output(Set<ConnectorPlugin> plugins) {}
}
