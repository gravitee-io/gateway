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
package io.gravitee.rest.api.management.v2.rest.mapper;

import io.gravitee.definition.model.v4.analytics.sampling.SamplingType;
import io.gravitee.rest.api.management.v2.rest.model.Analytics;
import io.gravitee.rest.api.management.v2.rest.model.Sampling;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;

@Mapper
public interface AnalyticsMapper {
    @Mapping(target = "sampling", source = "messageSampling")
    Analytics toAnalytics(io.gravitee.definition.model.v4.analytics.Analytics analytics);

    @Mapping(target = "messageSampling", source = "sampling")
    io.gravitee.definition.model.v4.analytics.Analytics fromAnalytics(Analytics analytics);
}
