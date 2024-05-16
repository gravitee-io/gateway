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
package io.gravitee.repository.noop.healthcheck;

import io.gravitee.repository.analytics.AnalyticsException;
import io.gravitee.repository.common.query.QueryContext;
import io.gravitee.repository.healthcheck.api.HealthCheckRepository;
import io.gravitee.repository.healthcheck.query.Query;
import io.gravitee.repository.healthcheck.query.Response;
import io.gravitee.repository.healthcheck.query.log.ExtendedLog;

/**
 * @author Kamiel Ahmadpour (kamiel.ahmadpour at graviteesource.com)
 * @author GraviteeSource Team
 */
public class NoOpHealthCheckRepository implements HealthCheckRepository {

    @Override
    public <T extends Response> T query(final QueryContext queryContext, final Query<T> query) throws AnalyticsException {
        return null;
    }

    @Override
    public ExtendedLog findById(QueryContext queryContext, String id) throws AnalyticsException {
        return null;
    }
}
