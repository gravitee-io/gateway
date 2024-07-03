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
package io.gravitee.apim.core.api.domain_service;

import static org.mockito.Mockito.*;

import fixtures.core.model.ApiCRDFixtures;
import io.gravitee.apim.core.audit.model.AuditInfo;
import io.gravitee.apim.core.category.domain_service.ValidateCategoryIdsDomainService;
import io.gravitee.apim.core.validation.Validator;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ValidateCRDDomainServiceTest {

    private static final String ENV_ID = "TEST";

    ValidateCategoryIdsDomainService categoryIdsValidator = mock(ValidateCategoryIdsDomainService.class);

    ValidateCRDDomainService cut = new ValidateCRDDomainService(categoryIdsValidator);

    @BeforeEach
    void setUp() {
        reset(categoryIdsValidator);
    }

    @Test
    void should_return_input_with_categories_and_no_warnings() {
        var spec = ApiCRDFixtures.BASE_SPEC.categories(Set.of("key-1", "id-2")).build();
        var input = new ValidateCRDDomainService.Input(AuditInfo.builder().environmentId(ENV_ID).build(), spec);

        when(categoryIdsValidator.validateAndSanitize(new ValidateCategoryIdsDomainService.Input(ENV_ID, spec.getCategories())))
            .thenReturn(Validator.Result.ofValue(new ValidateCategoryIdsDomainService.Input(ENV_ID, Set.of("id-1", "id-2"))));

        var expected = spec.toBuilder().categories(Set.of("id-1", "id-2")).build();

        cut
            .validateAndSanitize(input)
            .peek(
                sanitized -> Assertions.assertThat(sanitized.spec()).isEqualTo(expected),
                errors -> Assertions.assertThat(errors).isEmpty()
            );
    }
}
