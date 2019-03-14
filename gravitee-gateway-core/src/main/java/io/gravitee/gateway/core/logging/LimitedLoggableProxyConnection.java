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
package io.gravitee.gateway.core.logging;

import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.api.proxy.ProxyConnection;
import io.gravitee.gateway.api.proxy.ProxyRequest;
import io.gravitee.gateway.api.proxy.ProxyResponse;

/**
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
public class LimitedLoggableProxyConnection extends LoggableProxyConnection {

    private final int maxSizeLogMessage;

    public LimitedLoggableProxyConnection(ProxyConnection proxyConnection, ProxyRequest proxyRequest, int maxSizeLogMessage) {
        super(proxyConnection, proxyRequest);
        this.maxSizeLogMessage = maxSizeLogMessage;
    }

    @Override
    protected void appendLog(Buffer buffer, Buffer chunk) {
        buffer.appendBuffer(chunk, maxSizeLogMessage);
    }

    protected ProxyConnection responseHandler(ProxyConnection proxyConnection, Handler<ProxyResponse> responseHandler) {
        return proxyConnection.responseHandler(new LimitedLoggableProxyResponseHandler(responseHandler));
    }

    class LimitedLoggableProxyResponseHandler extends LoggableProxyResponseHandler {

        LimitedLoggableProxyResponseHandler(Handler<ProxyResponse> responseHandler) {
            super(responseHandler);
        }

        @Override
        protected void handle(Handler<ProxyResponse> responseHandler, ProxyResponse proxyResponse) {
            responseHandler.handle(new LimitedLoggableProxyResponse(proxyResponse));
        }
    }

    class LimitedLoggableProxyResponse extends LoggableProxyResponse {

        LimitedLoggableProxyResponse(ProxyResponse proxyResponse) {
            super(proxyResponse);
        }

        @Override
        protected void appendLog(Buffer buffer, Buffer chunk) {
            buffer.appendBuffer(chunk, maxSizeLogMessage);
        }
    }
}
