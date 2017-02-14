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
package io.gravitee.gateway.http.core.endpoint.impl.tenant;

import io.gravitee.definition.model.Endpoint;
import io.gravitee.gateway.env.GatewayConfiguration;
import io.gravitee.gateway.http.core.endpoint.impl.DefaultEndpointLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Predicate;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class MultiTenantAwareEndpointLifecycleManager extends DefaultEndpointLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(MultiTenantAwareEndpointLifecycleManager.class);

    @Autowired
    private GatewayConfiguration gatewayConfiguration;

    @Override
    protected void doStart() throws Exception {
        // Check if tenant is supported by the gateway
        if (! gatewayConfiguration.tenant().isPresent()) {
            logger.error("API is configured with multi-tenant support but the gateway is not linked to any tenant");
            throw new IllegalStateException("API is configured with multi-tenant support but the gateway is not linked to any tenant");
        }

        super.doStart();
    }

    @Override
    protected Predicate<Endpoint> filter() {
        return super.filter()
                .and(endpoint -> endpoint.getTenant() != null &&
                        endpoint.getTenant().equalsIgnoreCase(gatewayConfiguration.tenant().get()));
    }
}
