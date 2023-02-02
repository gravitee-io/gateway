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
package io.gravitee.plugin.entrypoint.webhook;

import static io.gravitee.plugin.entrypoint.webhook.WebhookEntrypointConnector.SUPPORTED_QOS;
import static io.gravitee.plugin.entrypoint.webhook.configuration.WebhookEntrypointConnectorConfiguration.eval;

import io.gravitee.common.http.HttpHeader;
import io.gravitee.definition.model.v4.http.HttpProxyOptions;
import io.gravitee.el.TemplateEngine;
import io.gravitee.gateway.jupiter.api.ConnectorMode;
import io.gravitee.gateway.jupiter.api.ListenerType;
import io.gravitee.gateway.jupiter.api.connector.ConnectorHelper;
import io.gravitee.gateway.jupiter.api.connector.entrypoint.async.EntrypointAsyncConnectorFactory;
import io.gravitee.gateway.jupiter.api.context.DeploymentContext;
import io.gravitee.gateway.jupiter.api.qos.Qos;
import io.gravitee.plugin.entrypoint.webhook.configuration.WebhookEntrypointConnectorConfiguration;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
@AllArgsConstructor
public class WebhookEntrypointConnectorFactory implements EntrypointAsyncConnectorFactory<WebhookEntrypointConnector> {

    private ConnectorHelper connectorHelper;

    @Override
    public Set<ConnectorMode> supportedModes() {
        return WebhookEntrypointConnector.SUPPORTED_MODES;
    }

    @Override
    public Set<Qos> supportedQos() {
        return SUPPORTED_QOS;
    }

    @Override
    public ListenerType supportedListenerType() {
        return WebhookEntrypointConnector.SUPPORTED_LISTENER_TYPE;
    }

    @Override
    public WebhookEntrypointConnector createConnector(
        final DeploymentContext deploymentContext,
        final Qos qos,
        final String configuration
    ) {
        try {
            return new WebhookEntrypointConnector(
                connectorHelper,
                qos,
                eval(deploymentContext, connectorHelper.readConfiguration(WebhookEntrypointConnectorConfiguration.class, configuration))
            );
        } catch (Exception e) {
            log.error("Can't create connector cause no valid configuration", e);
            return null;
        }
    }
}
