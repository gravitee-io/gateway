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
package io.gravitee.gateway.http.vertx;

import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.api.proxy.ProxyRequestConnection;
import io.vertx.core.http.HttpClientRequest;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
class VertxProxyRequestConnection implements ProxyRequestConnection {

    private final HttpClientRequest httpClientRequest;
    private Handler<Throwable> timeoutHandler;

    VertxProxyRequestConnection(final HttpClientRequest httpClientRequest) {
        this.httpClientRequest = httpClientRequest;
    }

    @Override
    public VertxProxyRequestConnection connectTimeoutHandler(Handler<Throwable> timeoutHandler) {
        this.timeoutHandler = timeoutHandler;
        return this;
    }

    public Handler<Throwable> connectTimeoutHandler() {
        return this.timeoutHandler;
    }

    @Override
    public VertxProxyRequestConnection write(Buffer chunk) {
        httpClientRequest.write(io.vertx.core.buffer.Buffer.buffer(chunk.getBytes()));

        return this;
    }

    @Override
    public void end() {
        httpClientRequest.end();
    }
}
