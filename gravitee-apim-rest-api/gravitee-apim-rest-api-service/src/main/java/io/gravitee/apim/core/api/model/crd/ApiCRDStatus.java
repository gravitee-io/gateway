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
package io.gravitee.apim.core.api.model.crd;

import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * @author Antoine CORDIER (antoine.cordier at graviteesource.com)
 * @author GraviteeSource Team
 */
@Data
@Builder
public class ApiCRDStatus {

    private String organizationId;

    private String environmentId;
    /**
     * The API cross ID
     */
    private String crossId;

    /**
     * The API ID
     */
    private String id;

    /**
     * maps the name of the plan to its ID (not cross ID)
     */
    @Singular
    private Map<String, String> plans;

    private String state;
}
