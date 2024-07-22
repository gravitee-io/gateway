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
package io.gravitee.repository.jdbc.management;

import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.jdbc.orm.JdbcObjectMapper;
import io.gravitee.repository.management.api.IntegrationJobRepository;
import io.gravitee.repository.management.model.IntegrationJob;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcIntegrationJobRepository extends JdbcAbstractCrudRepository<IntegrationJob, String> implements IntegrationJobRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcIntegrationJobRepository.class);

    JdbcIntegrationJobRepository(@Value("${management.jdbc.prefix:}") String prefix) {
        super(prefix, "integrationjobs");
    }

    @Override
    public Optional<IntegrationJob> findPendingJobFor(String sourceId) throws TechnicalException {
        LOGGER.debug("JdbcIntegrationJobRepository.findPendingJobFor({})", sourceId);
        final List<IntegrationJob> jobs;
        try {
            jobs =
                jdbcTemplate.query(
                    getOrm().getSelectAllSql() + " where source_id = ? and status = 'PENDING'",
                    getOrm().getRowMapper(),
                    sourceId
                );
            return jobs.stream().findFirst();
        } catch (final Exception ex) {
            final String message = "Failed to find pending IntegrationJob for: " + sourceId;
            LOGGER.error(message, ex);
            throw new TechnicalException(message, ex);
        }
    }

    @Override
    protected String getId(IntegrationJob item) {
        return item.getId();
    }

    @Override
    protected JdbcObjectMapper<IntegrationJob> buildOrm() {
        return JdbcObjectMapper
            .builder(IntegrationJob.class, this.tableName, "id")
            .addColumn("id", Types.NVARCHAR, String.class)
            .addColumn("source_id", Types.NVARCHAR, String.class)
            .addColumn("environment_id", Types.NVARCHAR, String.class)
            .addColumn("initiator_Id", Types.NVARCHAR, String.class)
            .addColumn("status", Types.NVARCHAR, String.class)
            .addColumn("error_message", Types.NVARCHAR, String.class)
            .addColumn("upper_limit", Types.INTEGER, Long.class)
            .addColumn("created_at", Types.TIMESTAMP, Date.class)
            .addColumn("updated_at", Types.TIMESTAMP, Date.class)
            .build();
    }
}
