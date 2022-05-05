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
package io.gravitee.gateway.reactive.handlers.api.processor.pathmapping;

import static java.util.Comparator.comparing;

import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.core.processor.AbstractProcessor;
import io.gravitee.gateway.reactive.api.context.RequestExecutionContext;
import io.gravitee.gateway.reactive.core.processor.Processor;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class PathMappingProcessor implements Processor {

    public PathMappingProcessor() {}

    @Override
    public String getId() {
        return "path-mapping-processor";
    }

    @Override
    public Completable execute(final RequestExecutionContext ctx) {
        return Maybe
            .fromCallable(() -> ctx.api().getPathMappings())
            .filter(pathMapping -> !pathMapping.isEmpty())
            .doOnSuccess(pathMapping -> {
                String path = ctx.request().pathInfo();
                if (path.length() == 0 || path.charAt(path.length() - 1) != '/') {
                    path += '/';
                }
                final String finalPath = path;
                pathMapping
                    .entrySet()
                    .stream()
                    .filter(regexMappedPath -> regexMappedPath.getValue().matcher(finalPath).matches())
                    .map(Map.Entry::getKey)
                    .min(comparing(this::countOccurrencesOf))
                    .ifPresent(resolvedMappedPath -> ctx.request().metrics().setMappedPath(resolvedMappedPath));
            })
            .ignoreElement();
    }

    private Integer countOccurrencesOf(final String str) {
        return str.length() - str.replace(":", "").length();
    }
}
