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
package io.gravitee.rest.api.management.v2.rest.model;

import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Antoine CORDIER (antoine.cordier at graviteesource.com)
 * @author GraviteeSource Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCRDSpec {

    private String id;

    private String name;

    private Set<Member> members;

    private boolean notifyMembers;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder(toBuilder = true)
    public static class Member {

        private String id;

        private String source;

        private String sourceId;

        private Map<RoleScope, String> roles;
    }
}
