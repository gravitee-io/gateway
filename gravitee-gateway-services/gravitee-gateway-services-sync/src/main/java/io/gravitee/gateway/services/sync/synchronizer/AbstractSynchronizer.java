/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.services.sync.synchronizer;

import static io.gravitee.gateway.services.sync.SyncManager.TIMEFRAME_AFTER_DELAY;
import static io.gravitee.gateway.services.sync.SyncManager.TIMEFRAME_BEFORE_DELAY;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.common.service.AbstractService;
import io.gravitee.repository.management.api.EventRepository;
import io.gravitee.repository.management.api.search.EventCriteria;
import io.gravitee.repository.management.model.Event;
import io.gravitee.repository.management.model.EventType;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class AbstractSynchronizer extends AbstractService<AbstractSynchronizer> {

    public static final int DEFAULT_BULK_SIZE = 100;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ExecutorService executor;

    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }

    /**
     * Synchronize the elements retrieving events from the datasource.
     *
     * @param lastRefreshAt the last timestamp the synchronization has been made. If -1 an initial synchronization will be perform, then incremental synchronization will be made.
     * @param nextLastRefreshAt the timestamp of the next synchronization planed.
     * @param environments the list of environments to filter events
     */
    public abstract void synchronize(long lastRefreshAt, long nextLastRefreshAt, List<String> environments);

    protected Flowable<Event> searchLatestEvents(
        int bulkSize,
        Long from,
        Long to,
        Event.EventProperties group,
        List<String> environments,
        EventType... eventTypes
    ) {
        return Flowable.create(
            emitter -> {
                try {
                    int size = bulkSize > 0 ? bulkSize : DEFAULT_BULK_SIZE;
                    long page = 0;
                    EventCriteria.Builder criteriaBuilder = new EventCriteria.Builder()
                        .types(eventTypes)
                        .from(from == null ? 0 : from - TIMEFRAME_BEFORE_DELAY)
                        .to(to == null ? 0 : to + TIMEFRAME_AFTER_DELAY)
                        .environments(environments);

                    EventCriteria criteria = appendCriteria(criteriaBuilder);
                    List<Event> events;

                    do {
                        events = eventRepository.searchLatest(criteria, group, page, (long) size);
                        events.forEach(emitter::onNext);
                        page++;
                    } while (!events.isEmpty() && events.size() == size);

                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            },
            BackpressureStrategy.BUFFER
        );
    }

    protected EventCriteria appendCriteria(EventCriteria.Builder criteriaBuilder) {
        return criteriaBuilder.build();
    }
}
