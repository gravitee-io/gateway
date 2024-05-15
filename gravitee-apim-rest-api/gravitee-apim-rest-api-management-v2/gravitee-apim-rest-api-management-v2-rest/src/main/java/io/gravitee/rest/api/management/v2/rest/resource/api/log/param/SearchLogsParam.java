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
package io.gravitee.rest.api.management.v2.rest.resource.api.log.param;

import io.gravitee.common.http.HttpMethod;
import io.gravitee.rest.api.management.v2.rest.validation.IntervalParamConstraint;
import io.gravitee.rest.api.management.v2.rest.validation.TimeInterval;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.QueryParam;
import java.util.Set;
import lombok.Data;

@Data
@IntervalParamConstraint
public class SearchLogsParam implements TimeInterval {

    public static final String FROM_QUERY_PARAM_NAME = "from";
    public static final String TO_QUERY_PARAM_NAME = "to";
    public static final String APPLICATION_IDS_QUERY_PARAM_NAME = "applicationIds";
    public static final String PLAN_IDS_QUERY_PARAM_NAME = "planIds";
    public static final String METHODS_QUERY_PARAM_NAME = "methods";
    public static final String STATUSES_QUERY_PARAM_NAME = "statuses";
    public static final String ENTRYPOINT_IDS_QUERY_PARAM_NAME = "entrypointIds";

    @QueryParam(FROM_QUERY_PARAM_NAME)
    @Min(0)
    Long from;

    @QueryParam(TO_QUERY_PARAM_NAME)
    @Min(0)
    Long to;

    @QueryParam(APPLICATION_IDS_QUERY_PARAM_NAME)
    Set<String> applicationIds;

    @QueryParam(PLAN_IDS_QUERY_PARAM_NAME)
    Set<String> planIds;

    @QueryParam(METHODS_QUERY_PARAM_NAME)
    Set<HttpMethod> methods;

    @QueryParam(STATUSES_QUERY_PARAM_NAME)
    Set<Integer> statuses;

    @QueryParam(ENTRYPOINT_IDS_QUERY_PARAM_NAME)
    Set<String> entrypointIds;
}
