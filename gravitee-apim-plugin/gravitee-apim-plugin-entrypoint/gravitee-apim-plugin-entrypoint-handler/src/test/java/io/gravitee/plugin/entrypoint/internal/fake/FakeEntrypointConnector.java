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
package io.gravitee.plugin.entrypoint.internal.fake;

import io.gravitee.gateway.jupiter.api.ApiType;
import io.gravitee.gateway.jupiter.api.ConnectorMode;
import io.gravitee.gateway.jupiter.api.connector.entrypoint.EntrypointConnector;
import io.gravitee.gateway.jupiter.api.context.RequestExecutionContext;
import io.reactivex.Completable;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@Builder
@Getter
public class FakeEntrypointConnector implements EntrypointConnector<RequestExecutionContext> {

    static final ApiType SUPPORTED_API = ApiType.ASYNC;
    static final Set<ConnectorMode> SUPPORTED_MODES = Set.of(ConnectorMode.SUBSCRIBE);

    private FakeEntrypointConnectorConfiguration configuration;

    @Override
    public ApiType supportedApi() {
        return SUPPORTED_API;
    }

    @Override
    public Set<ConnectorMode> supportedModes() {
        return SUPPORTED_MODES;
    }

    @Override
    public int matchCriteriaCount() {
        return 0;
    }

    @Override
    public boolean matches(final RequestExecutionContext executionContext) {
        return true;
    }

    @Override
    public Completable handleRequest(final RequestExecutionContext executionContext) {
        return Completable.complete();
    }

    @Override
    public Completable handleResponse(final RequestExecutionContext executionContext) {
        return Completable.complete();
    }
}
