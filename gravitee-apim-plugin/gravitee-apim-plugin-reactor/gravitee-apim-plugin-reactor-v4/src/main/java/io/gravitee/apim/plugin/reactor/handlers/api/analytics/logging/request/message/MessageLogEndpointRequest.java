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
package io.gravitee.apim.plugin.reactor.handlers.api.analytics.logging.request.message;

import io.gravitee.apim.plugin.reactor.handlers.api.analytics.logging.MessageLog;
import io.gravitee.gateway.reactive.api.message.Message;
import io.gravitee.gateway.reactive.core.analytics.LoggingContext;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class MessageLogEndpointRequest extends MessageLog {

    public MessageLogEndpointRequest(LoggingContext loggingContext, Message message) {
        super(loggingContext, message);
    }

    @Override
    protected boolean isMessageLogPayload() {
        return loggingContext.endpointRequestMessagePayload();
    }

    @Override
    protected boolean isMessageLogHeaders() {
        return loggingContext.endpointRequestMessageHeaders();
    }

    @Override
    protected boolean isMessageLogMetadata() {
        return loggingContext.endpointRequestMessageMetadata();
    }
}
