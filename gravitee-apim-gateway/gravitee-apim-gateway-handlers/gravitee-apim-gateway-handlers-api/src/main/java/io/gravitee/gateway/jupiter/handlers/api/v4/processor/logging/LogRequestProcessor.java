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
package io.gravitee.gateway.jupiter.handlers.api.v4.processor.logging;

import static io.gravitee.gateway.jupiter.api.context.ContextAttributes.ATTR_API;

import io.gravitee.gateway.jupiter.api.context.HttpExecutionContext;
import io.gravitee.gateway.jupiter.api.context.HttpRequest;
import io.gravitee.gateway.jupiter.api.context.InternalContextAttributes;
import io.gravitee.gateway.jupiter.core.condition.ExpressionLanguageConditionFilter;
import io.gravitee.gateway.jupiter.core.context.MutableExecutionContext;
import io.gravitee.gateway.jupiter.core.processor.Processor;
import io.gravitee.gateway.jupiter.core.v4.analytics.AnalyticsContext;
import io.gravitee.gateway.jupiter.core.v4.analytics.LoggingContext;
import io.gravitee.gateway.jupiter.handlers.api.v4.analytics.logging.request.LogEntrypointRequest;
import io.gravitee.reporter.api.v4.log.Log;
import io.reactivex.rxjava3.core.Completable;

/**
 * Processor in charge of initializing the {@link Log} entity during the request phase if logging condition is evaluated to true.
 *
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class LogRequestProcessor implements Processor {

    public static final String ID = "processor-logging-request";
    private static final ExpressionLanguageConditionFilter<LoggingContext> CONDITION_FILTER = new ExpressionLanguageConditionFilter<>();

    public static LogRequestProcessor instance() {
        return Holder.INSTANCE;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Completable execute(final MutableExecutionContext ctx) {
        return Completable.defer(
            () -> {
                final AnalyticsContext analyticsContext = ctx.getInternalAttribute(
                    InternalContextAttributes.ATTR_INTERNAL_ANALYTICS_CONTEXT
                );

                if (!analyticsContext.isLoggingEnabled()) {
                    return Completable.complete();
                }

                return CONDITION_FILTER
                    .filter(ctx, analyticsContext.getLoggingContext())
                    .doOnSuccess(activeLoggingContext -> initLogEntity(ctx, activeLoggingContext))
                    .ignoreElement();
            }
        );
    }

    private void initLogEntity(final HttpExecutionContext ctx, final LoggingContext loggingContext) {
        HttpRequest request = ctx.request();

        Log log = Log
            .builder()
            .timestamp(request.timestamp())
            .requestId(request.id())
            .clientIdentifier(request.clientIdentifier())
            .apiId(ctx.getAttribute(ATTR_API))
            .build();
        ctx.metrics().setLog(log);

        if (loggingContext.entrypointRequest()) {
            final LogEntrypointRequest logRequest = new LogEntrypointRequest(loggingContext, request);
            log.setEntrypointRequest(logRequest);
        }
    }

    private static class Holder {

        private static final LogRequestProcessor INSTANCE = new LogRequestProcessor();
    }
}
