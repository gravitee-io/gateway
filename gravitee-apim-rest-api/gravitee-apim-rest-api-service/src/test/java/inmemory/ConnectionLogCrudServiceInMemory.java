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
package inmemory;

import io.gravitee.apim.crud_service.analytics.log.ConnectionLogCrudService;
import io.gravitee.rest.api.model.common.Pageable;
import io.gravitee.rest.api.model.v4.log.SearchLogResponse;
import io.gravitee.rest.api.model.v4.log.connection.BaseConnectionLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class ConnectionLogCrudServiceInMemory implements ConnectionLogCrudService, InMemoryCrudService<BaseConnectionLog> {

    private final List<BaseConnectionLog> storage = new ArrayList<>();

    @Override
    public SearchLogResponse<BaseConnectionLog> searchApiConnectionLog(String apiId, Pageable pageable) {
        Predicate<BaseConnectionLog> predicate = connectionLog -> connectionLog.getApiId().equals(apiId);
        var pageNumber = pageable.getPageNumber();
        var pageSize = pageable.getPageSize();

        var matches = storage()
            .stream()
            .filter(predicate)
            .sorted(Comparator.comparing(BaseConnectionLog::getTimestamp).reversed())
            .toList();

        var page = matches.size() <= pageSize ? matches : matches.subList((pageNumber - 1) * pageSize, pageNumber * pageSize);

        return new SearchLogResponse<>(matches.size(), page);
    }

    @Override
    public void initWith(List<BaseConnectionLog> items) {
        storage.addAll(items);
    }

    @Override
    public void reset() {
        storage.clear();
    }

    @Override
    public List<BaseConnectionLog> storage() {
        return Collections.unmodifiableList(storage);
    }
}
