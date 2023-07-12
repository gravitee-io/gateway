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
package io.gravitee.rest.api.management.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.rest.api.model.MembershipMemberType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class TransferOwnership {

    /**
     * Member identifier
     */
    @Schema(name = "id", description = "User's technical identifier.")
    private String id;

    /**
     * Member reference
     */
    @Schema(name = "reference", description = "User's reference for user providing from an identity provider.")
    private String reference;

    /**
     * Member type
     */
    @Schema(name = "type", description = "Type's name", required = true, example = "USER")
    private MembershipMemberType type;

    @Schema(name = "role", description = "Role's name", required = true)
    @JsonProperty("role")
    private String poRole;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPoRole() {
        return poRole;
    }

    public void setPoRole(String poRole) {
        this.poRole = poRole;
    }

    public MembershipMemberType getType() {
        return type;
    }

    public void setType(MembershipMemberType type) {
        this.type = type;
    }
}
