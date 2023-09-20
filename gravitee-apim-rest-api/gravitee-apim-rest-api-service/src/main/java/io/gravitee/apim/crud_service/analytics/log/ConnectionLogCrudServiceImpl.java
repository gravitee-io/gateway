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
package io.gravitee.apim.crud_service.analytics.log;

import io.gravitee.apim.infra.adapter.ConnectionLogAdapter;
import io.gravitee.repository.analytics.AnalyticsException;
import io.gravitee.repository.log.v4.api.LogRepository;
import io.gravitee.repository.log.v4.model.LogResponse;
import io.gravitee.repository.log.v4.model.connection.ConnectionLog;
import io.gravitee.repository.log.v4.model.connection.ConnectionLogQuery;
import io.gravitee.rest.api.model.common.Pageable;
import io.gravitee.rest.api.model.v4.log.SearchLogResponse;
import io.gravitee.rest.api.model.v4.log.connection.BaseConnectionLog;
import io.gravitee.rest.api.service.exceptions.TechnicalManagementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class ConnectionLogCrudServiceImpl implements ConnectionLogCrudService {

    private final LogRepository logRepository;

    public ConnectionLogCrudServiceImpl(@Lazy LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public SearchLogResponse<BaseConnectionLog> searchApiConnectionLog(String apiId, Pageable pageable) {
        try {
            var response = logRepository.searchConnectionLog(
                ConnectionLogQuery
                    .builder()
                    .filter(ConnectionLogQuery.Filter.builder().apiId(apiId).build())
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .build()
            );
            return mapToConnectionResponse(response);
        } catch (AnalyticsException e) {
            log.error("An error occurs while trying to search connection of api [apiId={}]", apiId, e);
            throw new TechnicalManagementException("Error while searching connection logs of api " + apiId, e);
        }
    }

    private SearchLogResponse<BaseConnectionLog> mapToConnectionResponse(LogResponse<ConnectionLog> logs) {
        var total = logs.total();
        var data = ConnectionLogAdapter.INSTANCE.toEntitiesList(logs.data());

        return new SearchLogResponse<>(total, data);
    }
}
