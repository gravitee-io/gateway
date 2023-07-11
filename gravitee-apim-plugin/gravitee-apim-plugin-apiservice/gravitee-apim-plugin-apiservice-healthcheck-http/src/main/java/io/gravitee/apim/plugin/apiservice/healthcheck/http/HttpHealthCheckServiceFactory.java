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
package io.gravitee.apim.plugin.apiservice.healthcheck.http;

import io.gravitee.apim.plugin.apiservice.healthcheck.http.helper.HttpHealthCheckHelper;
import io.gravitee.gateway.env.GatewayConfiguration;
import io.gravitee.gateway.reactive.api.apiservice.ApiServiceFactory;
import io.gravitee.gateway.reactive.api.context.DeploymentContext;
import io.gravitee.gateway.reactive.api.helper.PluginConfigurationHelper;
import io.gravitee.gateway.reactive.handlers.api.v4.Api;
import lombok.AllArgsConstructor;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@AllArgsConstructor
public class HttpHealthCheckServiceFactory implements ApiServiceFactory<HttpHealthCheckService> {

    @Override
    public HttpHealthCheckService createService(DeploymentContext deploymentContext) {
        final Api api = deploymentContext.getComponent(Api.class);
        final GatewayConfiguration gatewayConfiguration = deploymentContext.getComponent(GatewayConfiguration.class);

        if (HttpHealthCheckHelper.canHandle(api.getDefinition(), gatewayConfiguration.tenant().orElse(null))) {
            return new HttpHealthCheckService(api, deploymentContext, gatewayConfiguration);
        }

        return null;
    }
}
