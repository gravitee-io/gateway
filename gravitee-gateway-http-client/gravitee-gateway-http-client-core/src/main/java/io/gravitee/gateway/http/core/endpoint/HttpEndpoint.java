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
package io.gravitee.gateway.http.core.endpoint;

import io.gravitee.definition.model.Endpoint;
import io.gravitee.gateway.api.http.client.HttpClient;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class HttpEndpoint implements io.gravitee.gateway.api.endpoint.Endpoint<HttpClient> {

    private final Endpoint endpoint;
    private final HttpClient httpClient;

    public HttpEndpoint(final Endpoint endpoint, final HttpClient httpClient) {
        this.endpoint = endpoint;
        this.httpClient = httpClient;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    @Override
    public String name() {
        return endpoint.getName();
    }

    @Override
    public String target() {
        return endpoint.getTarget();
    }

    @Override
    public HttpClient connector() {
        return httpClient;
    }
}
