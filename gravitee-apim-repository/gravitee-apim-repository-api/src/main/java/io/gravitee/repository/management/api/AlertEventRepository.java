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
import io.gravitee.repository.management.api.search.AlertEventCriteria;
import io.gravitee.repository.management.api.search.Pageable;
import io.gravitee.repository.management.model.AlertEvent;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface AlertEventRepository extends CrudRepository<AlertEvent, String> {
    /**
     * Search for {@link AlertEvent} with {@link Pageable} feature.
     *
     * <p>
     *  Note that events must be ordered by created date in DESC mode.
     * </p>
     *
     * @param criteria A criteria to search for {@link AlertEvent}.
     * @param pageable If user wants a paginable result. Can be <code>null</code>.
     * @return
     */
    Page<AlertEvent> search(AlertEventCriteria criteria, Pageable pageable);

    /**
     * Counts the number of {@link AlertEvent}s that match the given criteria.
     *
     * @param criteria The criteria to match the {@link AlertEvent}s against.
     * @return The number of {@link AlertEvent}s that match the given criteria.
     */
    long count(AlertEventCriteria criteria);

    /**
     * delete all events of the provided alert
     * @param alertId
     */
    void deleteAll(String alertId);
}
