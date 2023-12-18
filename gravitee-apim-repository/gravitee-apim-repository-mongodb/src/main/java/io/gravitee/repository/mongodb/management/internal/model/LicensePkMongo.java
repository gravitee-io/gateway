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
package io.gravitee.repository.mongodb.management.internal.model;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Eric LELEU (eric.leleu at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicensePkMongo implements Serializable {

    private String referenceId;
    private String referenceType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LicensePkMongo)) return false;
        LicensePkMongo that = (LicensePkMongo) o;
        return Objects.equals(referenceId, that.referenceId) && Objects.equals(referenceType, that.referenceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId, referenceType);
    }

    @Override
    public String toString() {
        return "LicensePkMongo{" + "referenceId='" + referenceId + '\'' + ", referenceType=" + referenceType + '}';
    }
}
