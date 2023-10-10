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
package io.gravitee.rest.api.services.sync;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.definition.model.DefinitionVersion;
import io.gravitee.repository.management.api.EventLatestRepository;
import io.gravitee.repository.management.api.search.EventCriteria;
import io.gravitee.repository.management.model.Api;
import io.gravitee.repository.management.model.Event;
import io.gravitee.repository.management.model.EventType;
import io.gravitee.rest.api.model.EnvironmentEntity;
import io.gravitee.rest.api.model.PrimaryOwnerEntity;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.model.v4.api.GenericApiEntity;
import io.gravitee.rest.api.service.EnvironmentService;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.converter.ApiConverter;
import io.gravitee.rest.api.service.exceptions.PrimaryOwnerNotFoundException;
import io.gravitee.rest.api.service.v4.PrimaryOwnerService;
import io.gravitee.rest.api.service.v4.mapper.ApiMapper;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
public class SyncManager {

    private static final int TIMEFRAME_BEFORE_DELAY = 10 * 60 * 1000;
    private static final int TIMEFRAME_AFTER_DELAY = 1 * 60 * 1000;
    private final AtomicLong counter = new AtomicLong(0);
    private final Map<String, String> organizationByEnvironment = new ConcurrentHashMap<>();

    @Autowired
    private DictionaryManager dictionaryManager;

    @Lazy
    @Autowired
    private EventLatestRepository eventLatestRepository;

    @Autowired
    private ApiManager apiManager;

    @Autowired
    private ApiConverter apiConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EnvironmentService environmentService;

    @Autowired
    private PrimaryOwnerService primaryOwnerService;

    @Autowired
    private ApiMapper apiMapper;

    private long lastRefreshAt = -1;

    public void refresh() {
        log.debug("Synchronization #{} started at {}", counter.incrementAndGet(), Instant.now());
        log.debug("Refreshing state...");

        long nextLastRefreshAt = System.currentTimeMillis();

        try {
            synchronizeApis(nextLastRefreshAt);
        } catch (Exception ex) {
            log.error("An error occurs while synchronizing APIs", ex);
        }

        try {
            synchronizeDictionaries(nextLastRefreshAt);
        } catch (Exception ex) {
            log.error("An error occurs while synchronizing dictionaries", ex);
        }

        lastRefreshAt = nextLastRefreshAt;
        log.debug("Synchronization #{} ended at {}", counter.get(), Instant.now());
    }

    private void synchronizeApis(long nextLastRefreshAt) {
        final EventCriteria eventCriteria = EventCriteria
            .builder()
            .types(Set.of(EventType.PUBLISH_API, EventType.UNPUBLISH_API, EventType.START_API, EventType.STOP_API))
            .from(lastRefreshAt - TIMEFRAME_BEFORE_DELAY)
            .to(nextLastRefreshAt + TIMEFRAME_AFTER_DELAY)
            .build();

        Map<String, Event> apiEvents = eventLatestRepository
            .search(eventCriteria, Event.EventProperties.API_ID, null, null)
            .stream()
            .collect(toMap(event -> event.getProperties().get(Event.EventProperties.API_ID.getValue()), event -> event));

        // Then, compute events
        computeApiEvents(apiEvents);
    }

    private void synchronizeDictionaries(long nextLastRefreshAt) throws Exception {
        final EventCriteria eventCriteria = EventCriteria
            .builder()
            .types(Set.of(EventType.START_DICTIONARY, EventType.STOP_DICTIONARY))
            .from(lastRefreshAt - TIMEFRAME_BEFORE_DELAY)
            .to(nextLastRefreshAt + TIMEFRAME_AFTER_DELAY)
            .build();

        Map<String, Event> dictionaryEvents = eventLatestRepository
            .search(eventCriteria, Event.EventProperties.DICTIONARY_ID, null, null)
            .stream()
            .collect(toMap(event -> event.getProperties().get(Event.EventProperties.DICTIONARY_ID.getValue()), event -> event));

        computeDictionaryEvents(dictionaryEvents);
    }

    private void computeDictionaryEvents(Map<String, Event> dictionaryEvents) {
        dictionaryEvents.forEach((id, event) -> {
            switch (event.getType()) {
                case START_DICTIONARY:
                    dictionaryManager.start(id);
                    break;
                case STOP_DICTIONARY:
                    dictionaryManager.stop(id);
                    break;
                default:
                    break;
            }
        });
    }

    private void computeApiEvents(Map<String, Event> apiEvents) {
        final int parallelism = Runtime.getRuntime().availableProcessors() * 2;

        if (apiEvents.size() > parallelism) {
            final ForkJoinPool.ForkJoinWorkerThreadFactory factory = pool -> {
                final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                worker.setName("gio.sync-" + worker.getPoolIndex());
                return worker;
            };

            ForkJoinPool customThreadPool = new ForkJoinPool(parallelism, factory, null, false);
            customThreadPool.submit(() -> apiEvents.entrySet().parallelStream().forEach(e -> processApiEvent(e.getKey(), e.getValue())));
            customThreadPool.shutdown();
        } else {
            apiEvents.forEach(this::processApiEvent);
        }
    }

    protected void processApiEvent(String apiId, Event apiEvent) {
        switch (apiEvent.getType()) {
            case UNPUBLISH_API:
            case STOP_API:
                apiManager.undeploy(apiId);
                break;
            case START_API:
            case PUBLISH_API:
                try {
                    // Read API definition from event
                    Api payloadApi = objectMapper.readValue(apiEvent.getPayload(), Api.class);

                    // API to deploy
                    GenericApiEntity indexableApiToDeploy;
                    if (payloadApi.getDefinitionVersion() == null || payloadApi.getDefinitionVersion() != DefinitionVersion.V4) {
                        indexableApiToDeploy = convert(payloadApi);
                    } else {
                        indexableApiToDeploy = convertV4(payloadApi);
                    }

                    if (indexableApiToDeploy != null) {
                        // Get deployed API
                        GenericApiEntity deployedApi = apiManager.get(indexableApiToDeploy.getId());

                        // API is not yet deployed, so let's do it !
                        if (deployedApi == null) {
                            apiManager.deploy(indexableApiToDeploy);
                        } else {
                            if (deployedApi.getDeployedAt().before(indexableApiToDeploy.getDeployedAt())) {
                                apiManager.update(indexableApiToDeploy);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Unable to handle event [" + apiEvent.getType() + "]  for API [" + apiId + "]", e);
                }
                break;
            default:
                break;
        }
    }

    private ApiEntity convert(Api api) {
        // When event was created with APIM < 3.x, the api doesn't have environmentId, we must use default.
        if (api.getEnvironmentId() == null) {
            api.setEnvironmentId(GraviteeContext.getDefaultEnvironment());
        }
        PrimaryOwnerEntity primaryOwnerEntity = null;
        try {
            primaryOwnerEntity = primaryOwnerService.getPrimaryOwner(getOrganizationId(api.getEnvironmentId()), api.getId());
        } catch (PrimaryOwnerNotFoundException e) {
            log.error(e.getMessage());
        }
        return apiConverter.toApiEntity(api, primaryOwnerEntity);
    }

    private io.gravitee.rest.api.model.v4.api.ApiEntity convertV4(Api api) {
        PrimaryOwnerEntity primaryOwner = primaryOwnerService.getPrimaryOwner(getOrganizationId(api.getEnvironmentId()), api.getId());
        return apiMapper.toEntity(api, primaryOwner);
    }

    private String getOrganizationId(String environmentId) {
        return organizationByEnvironment.computeIfAbsent(
            environmentId,
            envId -> {
                EnvironmentEntity environmentEntity = environmentService.findById(environmentId);
                return environmentEntity.getOrganizationId();
            }
        );
    }
}
