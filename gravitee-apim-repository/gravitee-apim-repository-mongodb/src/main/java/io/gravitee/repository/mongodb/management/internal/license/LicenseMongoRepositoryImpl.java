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
package io.gravitee.repository.mongodb.management.internal.license;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.util.CollectionUtils.isEmpty;

import io.gravitee.common.data.domain.Page;
import io.gravitee.repository.management.api.search.LicenseCriteria;
import io.gravitee.repository.management.api.search.Pageable;
import io.gravitee.repository.mongodb.management.internal.model.LicenseMongo;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class LicenseMongoRepositoryImpl implements LicenseMongoRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<LicenseMongo> search(LicenseCriteria filter, Pageable pageable) {
        final Query query = new Query();

        if (filter.getReferenceType() != null) {
            query.addCriteria(where("_id.referenceType").is(filter.getReferenceType().name()));
        }

        if (!isEmpty(filter.getReferenceIds())) {
            query.addCriteria(where("_id.referenceId").in(filter.getReferenceIds()));
        }

        if (filter.getFrom() > 0 && filter.getTo() > 0) {
            query.addCriteria(where("updatedAt").gte(new Date(filter.getFrom())).lte(new Date(filter.getTo())));
        } else {
            if (filter.getFrom() > 0) {
                query.addCriteria(where("updatedAt").gte(new Date(filter.getFrom())));
            }
            if (filter.getTo() > 0) {
                query.addCriteria(where("updatedAt").lte(new Date(filter.getTo())));
            }
        }

        long total = mongoTemplate.count(query, LicenseMongo.class);
        List<LicenseMongo> license = mongoTemplate.find(query, LicenseMongo.class);

        return new Page<>(license, pageable != null ? pageable.pageNumber() : 0, pageable != null ? pageable.pageSize() : 0, total);
    }
}
