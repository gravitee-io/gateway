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
package io.gravitee.repository.mongodb.management.internal.score;

import io.gravitee.repository.mongodb.management.internal.model.ScoringRulesetMongo;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoringRulesetMongoRepository extends MongoRepository<ScoringRulesetMongo, String> {
    @Query(value = "{ referenceId: ?0, referenceType: ?1 }", fields = "{ _id : 1 }", delete = true)
    List<ScoringRulesetMongo> deleteByReferenceIdAAndReferenceType(String referenceId, String referenceType);

    @Query(value = "{ referenceId: ?0, referenceType: ?1 }")
    List<ScoringRulesetMongo> findByReferenceIdAndReferenceType(String referenceId, String referenceType);
}
