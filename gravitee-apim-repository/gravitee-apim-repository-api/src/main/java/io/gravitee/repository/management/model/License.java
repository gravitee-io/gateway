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
import java.util.Objects;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class License {

    private String referenceId;
    private ReferenceType referenceType;
    private String license;
    private Date createdAt;
    private Date updatedAt;

    public enum ReferenceType {
        ORGANIZATION,
        PLATFORM,
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        License license1 = (License) o;
        return (
            Objects.equals(referenceId, license1.referenceId) &&
            referenceType == license1.referenceType &&
            Objects.equals(license, license1.license)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId, referenceType, license);
    }
}
