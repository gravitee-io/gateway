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
package io.gravitee.gateway.reactive.handlers.api.processor.forward;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.http.HttpHeaderNames;
import io.gravitee.gateway.core.processor.AbstractProcessor;
import io.gravitee.gateway.reactive.api.context.RequestExecutionContext;
import io.gravitee.gateway.reactive.core.processor.Processor;
import io.reactivex.Completable;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class XForwardedPrefixProcessor implements Processor {

    public static final String ID = "x-forwarded-prefix-processor";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Completable execute(final RequestExecutionContext ctx) {
        return Completable.fromRunnable(
            () ->
                // Override the X-Forwarded-Prefix with context path
                ctx.request().headers().set(HttpHeaderNames.X_FORWARDED_PREFIX, ctx.request().contextPath())
        );
    }
}
