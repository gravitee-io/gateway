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
package io.gravitee.gateway.reactive.core.context;

import io.gravitee.gateway.reactive.api.context.Request;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface MutableRequest extends Request, OnMessagesInterceptor {
    /**
     * Allow setting context path.
     *
     * @param contextPath the context path to set.
     * @return {@link MutableRequest}.
     */
    MutableRequest contextPath(final String contextPath);

    /**
     * Allow setting transaction id.
     *
     * @param transactionId the transaction identifier to set.
     * @return {@link MutableRequest}.
     */
    MutableRequest transactionId(final String transactionId);

    /**
     * Allow setting client identifier.
     *
     * @param clientIdentifier the client identifier to set.
     * @return {@link MutableRequest}.
     */
    MutableRequest clientIdentifier(final String clientIdentifier);

    /**
     * Allow overriding remote address.
     *
     * @param remoteAddress the remote address to set.
     * @return {@link MutableRequest}.
     */
    MutableRequest remoteAddress(final String remoteAddress);
}
