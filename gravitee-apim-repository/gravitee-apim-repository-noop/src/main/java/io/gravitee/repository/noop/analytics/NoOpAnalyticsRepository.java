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
package io.gravitee.repository.noop.analytics;

import io.gravitee.repository.analytics.AnalyticsException;
import io.gravitee.repository.analytics.api.AnalyticsRepository;
import io.gravitee.repository.analytics.query.Query;
import io.gravitee.repository.analytics.query.response.Response;

/**
 * @author Kamiel Ahmadpour (kamiel.ahmadpour at graviteesource.com)
 * @author GraviteeSource Team
 */
public class NoOpAnalyticsRepository implements AnalyticsRepository {

    @Override
    public <T extends Response> T query(final Query<T> query) throws AnalyticsException {
        return null;
    }
}
