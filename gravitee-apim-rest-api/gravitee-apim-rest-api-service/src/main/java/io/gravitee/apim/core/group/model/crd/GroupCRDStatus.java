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
package io.gravitee.apim.core.group.model.crd;

import io.gravitee.apim.core.validation.Validator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Antoine CORDIER (antoine.cordier at graviteesource.com)
 * @author GraviteeSource Team
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class GroupCRDStatus {

    @Builder
    public record Errors(List<String> severe, List<String> warning) {
        public static final Errors EMPTY = new GroupCRDStatus.Errors(List.of(), List.of());

        public static Errors fromErrorList(List<Validator.Error> errors) {
            return errors == null
                ? null
                : new Errors(
                    errors.stream().filter(Validator.Error::isSevere).map(Validator.Error::getMessage).sorted().distinct().toList(),
                    errors.stream().filter(Validator.Error::isWarning).map(Validator.Error::getMessage).sorted().distinct().toList()
                );
        }
    }

    private String id;

    private long members;

    private Errors errors;
}
