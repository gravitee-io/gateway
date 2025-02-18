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
package io.gravitee.repository.management.api;

import io.gravitee.common.data.domain.Page;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.search.Pageable;
import io.gravitee.repository.management.api.search.PromotionCriteria;
import io.gravitee.repository.management.api.search.Sortable;
import io.gravitee.repository.management.model.Promotion;
import java.util.List;

public interface PromotionRepository extends CrudRepository<Promotion, String> {
    Page<Promotion> search(PromotionCriteria criteria, Sortable sortable, Pageable pageable) throws TechnicalException;

    /**
     * Delete promotion by api ID
     * @param apiId The api ID
     * @return List of deleted IDs for promotions
     * @throws TechnicalException
     */
    List<String> deleteByApiId(String apiId) throws TechnicalException;
}
