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
package io.gravitee.repository.management.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public final class ScoringEnvironmentApi {

    private String apiId;
    private String apiName;
    private Date apiUpdatedAt;

    private String reportId;
    private Date reportCreatedAt;

    private Double score;
    private Long errors;
    private Long warnings;
    private Long infos;
    private Long hints;

    public ScoringEnvironmentApi(String apiId, String apiName, Date apiUpdatedAt) {
        this.apiId = apiId;
        this.apiName = apiName;
        this.apiUpdatedAt = apiUpdatedAt;
    }
}
