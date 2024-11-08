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
package io.gravitee.repository.log.v4.model.analytics;

import java.time.Instant;
import java.util.Optional;
import lombok.Builder;

@Builder(toBuilder = true)
public record RequestsCountQuery(Optional<String> apiId, Optional<Instant> from, Optional<Instant> to) {
    public RequestsCountQuery() {
        this(Optional.empty(), Optional.empty(), Optional.empty());
    }

    public RequestsCountQuery(String apiId) {
        this(Optional.ofNullable(apiId), Optional.empty(), Optional.empty());
    }

    public RequestsCountQuery(String apiId, Instant from, Instant to) {
        this(Optional.ofNullable(apiId), Optional.ofNullable(from), Optional.ofNullable(to));
    }
}
