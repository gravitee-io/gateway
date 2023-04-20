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
package io.gravitee.apim.plugin.reactor.handlers.api.analytics;

import static io.gravitee.gateway.reactive.api.context.InternalContextAttributes.ATTR_INTERNAL_MESSAGE_RECORDABLE;
import static io.gravitee.gateway.reactive.api.context.InternalContextAttributes.ATTR_INTERNAL_MESSAGE_RECORDABLE_WITH_LOGGING;

import io.gravitee.apim.plugin.reactor.core.analytics.sampling.MessageSamplingStrategy;
import io.gravitee.gateway.reactive.api.message.Message;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class MessageAnalyticsHelper {

    private MessageAnalyticsHelper() {}

    public static boolean isRecordable(final Message message) {
        Boolean isRecordable = message.attribute(ATTR_INTERNAL_MESSAGE_RECORDABLE);
        return message.error() || Boolean.TRUE.equals(isRecordable);
    }

    public static boolean isRecordableWithLogging(final Message message) {
        Boolean recordableWithLogging = message.attribute(ATTR_INTERNAL_MESSAGE_RECORDABLE_WITH_LOGGING);
        return message.error() || Boolean.TRUE.equals(recordableWithLogging);
    }

    public static boolean computeRecordable(
        final Message message,
        final MessageSamplingStrategy messageSamplingStrategy,
        final int messageCount,
        final long lastMessageTimestamp
    ) {
        boolean recordable = message.error() || messageSamplingStrategy.isRecordable(message, messageCount, lastMessageTimestamp);
        message.attribute(ATTR_INTERNAL_MESSAGE_RECORDABLE, recordable);
        return recordable;
    }
}
