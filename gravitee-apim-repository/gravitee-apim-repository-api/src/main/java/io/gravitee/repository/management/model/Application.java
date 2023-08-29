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

import io.gravitee.definition.model.Origin;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Application {

    public static final String METADATA_CLIENT_ID = "client_id";
    public static final String METADATA_TYPE = "type";
    public static final String METADATA_REGISTRATION_PAYLOAD = "registration_payload";

    public enum AuditEvent implements Audit.AuditEvent {
        APPLICATION_CREATED,
        APPLICATION_UPDATED,
        APPLICATION_ARCHIVED,
        APPLICATION_RESTORED,
    }

    /**
     * The application ID.
     */
    private String id;

    /**
     * The ID of the environment the application is attached to
     */
    private String environmentId;

    /**
     * The application name
     */
    private String name;

    /**
     * The application description
     */
    private String description;

    /**
     * The application picture
     */
    private String picture;

    /**
     * Domain used by the application, if relevant
     */
    private String domain;

    /**
     * The application creation date
     */
    private Date createdAt;

    /**
     * The application last updated date
     */
    private Date updatedAt;

    /**
     * the application group
     */
    private Set<String> groups;

    private ApplicationStatus status;

    private ApplicationType type;

    private Map<String, String> metadata;

    private boolean disableMembershipNotifications;

    private String background;

    @Builder.Default
    private ApiKeyMode apiKeyMode = ApiKeyMode.UNSPECIFIED;

    private Origin origin;

    public Application(Application cloned) {
        this.id = cloned.id;
        this.environmentId = cloned.environmentId;
        this.name = cloned.name;
        this.description = cloned.description;
        this.createdAt = cloned.createdAt;
        this.updatedAt = cloned.updatedAt;
        this.groups = cloned.groups;
        this.status = cloned.status;
        this.disableMembershipNotifications = cloned.disableMembershipNotifications;
        this.background = cloned.background;
        this.domain = cloned.domain;
        this.apiKeyMode = cloned.apiKeyMode;
        this.picture = cloned.picture;
        this.type = cloned.type;
        this.origin = cloned.origin;
        this.metadata = cloned.metadata != null ? new HashMap<>(cloned.metadata) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return (
            "Application{" +
            "id='" +
            id +
            '\'' +
            ", environmentId='" +
            environmentId +
            '\'' +
            ", name='" +
            name +
            '\'' +
            ", description='" +
            description +
            '\'' +
            ", groups='" +
            groups +
            '\'' +
            ", status='" +
            status +
            '\'' +
            ", createdAt=" +
            createdAt +
            '\'' +
            ", updatedAt=" +
            updatedAt +
            '\'' +
            ", disableMembershipNotifications=" +
            disableMembershipNotifications +
            '}'
        );
    }
}
