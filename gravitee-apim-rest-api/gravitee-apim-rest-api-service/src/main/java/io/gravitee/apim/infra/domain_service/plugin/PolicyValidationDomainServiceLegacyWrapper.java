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
package io.gravitee.apim.infra.domain_service.plugin;

import io.gravitee.apim.core.plugin.domain_service.PolicyValidationDomainService;
import io.gravitee.rest.api.service.PolicyService;
import org.springframework.stereotype.Service;

@Service
public class PolicyValidationDomainServiceLegacyWrapper implements PolicyValidationDomainService {

    private final PolicyService policyService;

    public PolicyValidationDomainServiceLegacyWrapper(PolicyService policyService) {
        this.policyService = policyService;
    }

    @Override
    public String validateAndSanitizeConfiguration(String policyName, String configuration) {
        return policyService.validatePolicyConfiguration(policyName, configuration);
    }
}
