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

import io.gravitee.apim.core.scoring.model.ScoringReport;
import io.gravitee.apim.core.scoring.query_service.ScoringReportQueryService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ScoringReportQueryServiceInMemory implements ScoringReportQueryService, InMemoryAlternative<ScoringReport> {

    private final List<ScoringReport> storage;

    public ScoringReportQueryServiceInMemory() {
        storage = new ArrayList<>();
    }

    public ScoringReportQueryServiceInMemory(ScoringReportCrudServiceInMemory integrationCrudServiceInMemory) {
        storage = integrationCrudServiceInMemory.storage;
    }

    @Override
    public Optional<ScoringReport> findLatestByApiId(String apiId) {
        return storage.stream().filter(report -> report.apiId().equals(apiId)).max(Comparator.comparing(ScoringReport::createdAt));
    }

    @Override
    public Stream<ScoringReport> findLatestReportsByApiId(Collection<String> apiIds) {
        return storage.stream().filter(report -> apiIds.contains(report.apiId()));
    }

    @Override
    public void initWith(List<ScoringReport> items) {
        storage.clear();
        storage.addAll(items);
    }

    @Override
    public void reset() {
        storage.clear();
    }

    @Override
    public List<ScoringReport> storage() {
        return Collections.unmodifiableList(storage);
    }
}
