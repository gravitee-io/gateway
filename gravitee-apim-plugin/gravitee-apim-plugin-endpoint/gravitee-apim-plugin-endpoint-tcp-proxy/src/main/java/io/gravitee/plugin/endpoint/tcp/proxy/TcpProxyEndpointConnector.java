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
package io.gravitee.plugin.endpoint.tcp.proxy;

import io.gravitee.common.service.AbstractService;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.reactive.api.ApiType;
import io.gravitee.gateway.reactive.api.ConnectorMode;
import io.gravitee.gateway.reactive.api.connector.Connector;
import io.gravitee.gateway.reactive.api.connector.endpoint.EndpointConnector;
import io.gravitee.gateway.reactive.api.context.ExecutionContext;
import io.gravitee.gateway.tcp.reactive.VertxReadStreamUtil;
import io.gravitee.plugin.endpoint.tcp.proxy.configuration.TcpProxyEndpointConnectorConfiguration;
import io.gravitee.plugin.endpoint.tcp.proxy.configuration.TcpProxyEndpointConnectorSharedConfiguration;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.net.NetClientOptions;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.net.NetClient;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public class TcpProxyEndpointConnector extends AbstractService<Connector> implements EndpointConnector {

    private static final String ENDPOINT_ID = "tcp-proxy";
    static final Set<ConnectorMode> SUPPORTED_MODES = Set.of(ConnectorMode.SOCKET);
    private final TcpProxyEndpointConnectorConfiguration config;
    private final TcpProxyEndpointConnectorSharedConfiguration sharedConfig;

    TcpProxyEndpointConnector(TcpProxyEndpointConnectorConfiguration config, TcpProxyEndpointConnectorSharedConfiguration sharedConfig) {
        this.config = config;
        this.sharedConfig = sharedConfig;
    }

    @Override
    public String id() {
        return ENDPOINT_ID;
    }

    @Override
    public ApiType supportedApi() {
        return ApiType.PROXY;
    }

    @Override
    public Set<ConnectorMode> supportedModes() {
        return SUPPORTED_MODES;
    }

    @Override
    public Completable connect(ExecutionContext ctx) {
        Vertx vertx = ctx.getComponent(Vertx.class);
        NetClient client = vertx.createNetClient(buildNetClientOptions());

        return client
            .rxConnect(this.config.getTcpTarget().getPort(), this.config.getTcpTarget().getHost())
            .doOnSuccess(backendSocket -> {
                // pause as soon as possible
                backendSocket.pause();
                // configure response as all will happen in end() method
                ctx.response().chunks(backendSocket.toFlowable().map(Buffer::buffer));
                // Read request chunks and write to backendSocket
                ctx.request().pipeUpstream(VertxReadStreamUtil.toVertxRxReadStream(ctx.request().chunks()).rxPipeTo(backendSocket));
            })
            .ignoreElement();
    }

    private NetClientOptions buildNetClientOptions() {
        var options = new NetClientOptions().setMetricsName("tcp-client");
        if (this.sharedConfig.getTcpClientOptions() != null) {
            options
                .setConnectTimeout(this.sharedConfig.getTcpClientOptions().getConnectTimeout())
                .setReconnectAttempts(this.sharedConfig.getTcpClientOptions().getReconnectAttempts())
                .setReconnectInterval(this.sharedConfig.getTcpClientOptions().getReconnectInterval());
        }
        if (this.sharedConfig.getSslOptions() != null) {
            options.setTrustAll(this.sharedConfig.getSslOptions().isTrustAll());
            if (!this.sharedConfig.getSslOptions().isTrustAll()) {
                // FIXME do abstract client for both VertxHttpClient and a new VertTcpClient
            }
        }

        return options;
    }

    TcpProxyEndpointConnectorConfiguration getConfig() {
        return config;
    }

    TcpProxyEndpointConnectorSharedConfiguration getSharedConfig() {
        return sharedConfig;
    }
}
