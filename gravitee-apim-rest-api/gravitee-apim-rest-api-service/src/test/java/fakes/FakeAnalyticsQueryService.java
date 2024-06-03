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
package fakes;

import io.gravitee.apim.core.analytics.query_service.AnalyticsQueryService;
import io.gravitee.rest.api.model.analytics.TopHitsAnalytics;
import io.gravitee.rest.api.model.v4.analytics.AverageConnectionDuration;
import io.gravitee.rest.api.model.v4.analytics.AverageMessagesPerRequest;
import io.gravitee.rest.api.model.v4.analytics.RequestsCount;
import java.util.Map;
import io.gravitee.rest.api.service.common.ExecutionContext;
import java.util.Optional;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public class FakeAnalyticsQueryService implements AnalyticsQueryService {

    public RequestsCount requestsCount;
    public AverageMessagesPerRequest averageMessagesPerRequest;
    public AverageConnectionDuration averageConnectionDuration;
    public Map<String, TopHitsAnalytics> topHitsAnalyticsByEntrypoint;

    @Override
    public Optional<RequestsCount> searchRequestsCount(ExecutionContext executionContext, String apiId) {
        return Optional.ofNullable(requestsCount);
    }

    @Override
    public Optional<AverageMessagesPerRequest> searchAverageMessagesPerRequest(ExecutionContext executionContext, String apiId) {
        return Optional.ofNullable(averageMessagesPerRequest);
    }

    @Override
    public Optional<AverageConnectionDuration> searchAverageConnectionDuration(ExecutionContext executionContext, String apiId) {
        return Optional.ofNullable(averageConnectionDuration);
    }

    public void reset() {
        requestsCount = null;
        averageMessagesPerRequest = null;
        averageConnectionDuration = null;
    }

    @Override
    public Optional<Map<String, TopHitsAnalytics>> searchTopHitsByEntrypoint(ExecutionContext executionContext, String apiId) {
        return Optional.ofNullable(topHitsAnalyticsByEntrypoint);
    }
}
