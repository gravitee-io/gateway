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

import io.gravitee.apim.core.DomainService;
import io.gravitee.apim.core.api.model.crd.ApiCRDSpec;
import io.gravitee.apim.core.audit.model.AuditInfo;
import io.gravitee.apim.core.category.domain_service.ValidateCategoryIdsDomainService;
import io.gravitee.apim.core.documentation.domain_service.ValidatePagesDomainService;
import io.gravitee.apim.core.group.domain_service.ValidateGroupsDomainService;
import io.gravitee.apim.core.member.domain_service.ValidateCRDMembersDomainService;
import io.gravitee.apim.core.member.model.MembershipReferenceType;
import io.gravitee.apim.core.resource.domain_service.ValidateResourceDomainService;
import io.gravitee.apim.core.validation.Validator;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;

/**
 * @author Antoine CORDIER (antoine.cordier at graviteesource.com)
 * @author GraviteeSource Team
 */
@DomainService
@RequiredArgsConstructor
public class ValidateApiCRDDomainService implements Validator<ValidateApiCRDDomainService.Input> {

    public record Input(AuditInfo auditInfo, ApiCRDSpec spec) implements Validator.Input {}

    private final ValidateCategoryIdsDomainService categoryIdsValidator;

    private final VerifyApiPathDomainService apiPathValidator;

    private final ValidateCRDMembersDomainService membersValidator;

    private final ValidateGroupsDomainService groupsValidator;

    private final ValidateResourceDomainService resourceValidator;

    private final ValidatePagesDomainService pagesValidator;

    @Override
    public Validator.Result<ValidateApiCRDDomainService.Input> validateAndSanitize(ValidateApiCRDDomainService.Input input) {
        var errors = new ArrayList<Error>();
        var sanitizedBuilder = input.spec().toBuilder();

        if (!input.spec.isNative()) {
            validateAndSanitizeHttpV4ForCreation(input, sanitizedBuilder, errors);
        } else {
            validateAndSanitizeNativeV4ForCreation(input, sanitizedBuilder, errors);
        }

        categoryIdsValidator
            .validateAndSanitize(
                new ValidateCategoryIdsDomainService.Input(input.auditInfo().environmentId(), input.spec().getCategories())
            )
            .peek(sanitized -> sanitizedBuilder.categories(sanitized.idOrKeys()), errors::addAll);

        membersValidator
            .validateAndSanitize(
                new ValidateCRDMembersDomainService.Input(
                    input.auditInfo(),
                    input.spec.getId(),
                    MembershipReferenceType.API,
                    input.spec().getMembers()
                )
            )
            .peek(sanitized -> sanitizedBuilder.members(sanitized.members()), errors::addAll);

        groupsValidator
            .validateAndSanitize(
                new ValidateGroupsDomainService.Input(
                    input.auditInfo.environmentId(),
                    input.spec().getGroups(),
                    input.spec().getDefinitionVersion()
                )
            )
            .peek(sanitized -> sanitizedBuilder.groups(sanitized.groups()), errors::addAll);

        resourceValidator
            .validateAndSanitize(new ValidateResourceDomainService.Input(input.auditInfo.environmentId(), input.spec().getResources()))
            .peek(sanitized -> sanitizedBuilder.resources(sanitized.resources()), errors::addAll);

        pagesValidator
            .validateAndSanitize(new ValidatePagesDomainService.Input(input.auditInfo, input.spec.getId(), input.spec.getPages()))
            .peek(sanitized -> sanitizedBuilder.pages(sanitized.pages()), errors::addAll);

        return Validator.Result.ofBoth(new ValidateApiCRDDomainService.Input(input.auditInfo(), sanitizedBuilder.build()), errors);
    }

    private void validateAndSanitizeHttpV4ForCreation(Input input, ApiCRDSpec.ApiCRDSpecBuilder sanitizedBuilder, ArrayList<Error> errors) {
        apiPathValidator
            .validateAndSanitize(
                new VerifyApiPathDomainService.Input(input.auditInfo.environmentId(), input.spec.getId(), input.spec.getPaths())
            )
            .peek(sanitized -> sanitizedBuilder.paths(sanitized.paths()), errors::addAll);
    }

    private void validateAndSanitizeNativeV4ForCreation(
        Input input,
        ApiCRDSpec.ApiCRDSpecBuilder sanitizedBuilder,
        ArrayList<Error> errors
    ) {}
}
