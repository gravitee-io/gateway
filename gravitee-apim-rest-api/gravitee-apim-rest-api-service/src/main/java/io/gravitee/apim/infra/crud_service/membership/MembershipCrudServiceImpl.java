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
package io.gravitee.apim.infra.crud_service.membership;

import io.gravitee.apim.core.exception.TechnicalDomainException;
import io.gravitee.apim.core.membership.crud_service.MembershipCrudService;
import io.gravitee.apim.core.membership.model.Membership;
import io.gravitee.apim.infra.adapter.MembershipAdapter;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.MembershipRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MembershipCrudServiceImpl implements MembershipCrudService {

    private final MembershipRepository membershipRepository;

    public MembershipCrudServiceImpl(@Lazy MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    public Membership create(Membership membership) {
        try {
            var result = membershipRepository.create(MembershipAdapter.INSTANCE.toRepository(membership));
            return MembershipAdapter.INSTANCE.toEntity(result);
        } catch (TechnicalException e) {
            throw new TechnicalDomainException("An error occurs while trying to create the membership: " + membership.getId(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            membershipRepository.delete(id);
        } catch (TechnicalException e) {
            throw new TechnicalDomainException("An error occurs while trying to delete the membership: " + id, e);
        }
    }
}
