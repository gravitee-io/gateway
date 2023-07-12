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
package io.gravitee.gateway.services.sync.synchronizer;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.definition.model.DefinitionVersion;
import io.gravitee.gateway.handlers.api.definition.Api;
import io.gravitee.gateway.handlers.api.manager.ApiManager;
import io.gravitee.gateway.reactor.ReactableApi;
import io.gravitee.gateway.services.sync.builder.RepositoryApiBuilder;
import io.gravitee.gateway.services.sync.cache.ApiKeysCacheService;
import io.gravitee.gateway.services.sync.cache.SubscriptionsCacheService;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.EnvironmentRepository;
import io.gravitee.repository.management.api.EventRepository;
import io.gravitee.repository.management.api.OrganizationRepository;
import io.gravitee.repository.management.api.PlanRepository;
import io.gravitee.repository.management.model.Environment;
import io.gravitee.repository.management.model.Event;
import io.gravitee.repository.management.model.EventType;
import io.gravitee.repository.management.model.Organization;
import io.gravitee.repository.management.model.Plan;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiSynchronizerTest extends TestCase {

    static final List<String> ENVIRONMENTS = Arrays.asList("DEFAULT", "OTHER_ENV");
    private static final String ENVIRONMENT_ID = "env#1";
    private static final String ORGANIZATION_ID = "orga#1";
    private static final String ENVIRONMENT_HRID = "default-env";
    private static final String ORGANIZATION_HRID = "default-org";
    private static final String API_ID = "api-test";

    @InjectMocks
    private ApiSynchronizer apiSynchronizer = new ApiSynchronizer();

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private ApiManager apiManager;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private ApiKeysCacheService apiKeysCacheService;

    @Mock
    private SubscriptionsCacheService subscriptionsCacheService;

    @Mock
    private ObjectMapper objectMapper;

    private ThreadPoolExecutor executor;

    @Before
    public void setUp() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        apiSynchronizer.setExecutor(executor);
    }

    public void cleanup() {
        executor.shutdown();
    }

    @Test
    public void initialSynchronize() throws Exception {
        io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
            .id(API_ID)
            .updatedAt(new Date())
            .definition("test")
            .environment(ENVIRONMENT_ID)
            .build();

        final io.gravitee.definition.model.Api mockApi = mockApi(api);

        final Event mockEvent = mockEvent(api, EventType.PUBLISH_API);
        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria.getTypes().containsAll(asList(EventType.PUBLISH_API, EventType.START_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                anyLong(),
                anyLong()
            )
        )
            .thenReturn(singletonList(mockEvent));

        mockEnvironmentAndOrganization();

        apiSynchronizer.synchronize(-1L, System.currentTimeMillis(), ENVIRONMENTS);

        ArgumentCaptor<Api> apiCaptor = ArgumentCaptor.forClass(Api.class);

        verify(apiManager).register(apiCaptor.capture());

        Api verifyApi = apiCaptor.getValue();
        assertEquals(API_ID, verifyApi.getId());
        assertEquals(ENVIRONMENT_ID, verifyApi.getEnvironmentId());
        assertEquals(ENVIRONMENT_HRID, verifyApi.getEnvironmentHrid());
        assertEquals(ORGANIZATION_ID, verifyApi.getOrganizationId());
        assertEquals(ORGANIZATION_HRID, verifyApi.getOrganizationHrid());

        verify(apiManager, never()).unregister(any(String.class));
        verify(planRepository, never()).findByApis(anyList());
        verify(apiKeysCacheService).register(singletonList(new Api(mockApi)));
        verify(subscriptionsCacheService).register(singletonList(new Api(mockApi)));
    }

    @Test
    public void initialSynchronize_waitForActiveTasksToComplete() throws TechnicalException {
        // Simulate a long running background task.
        final ThreadPoolExecutor executor = apiSynchronizer.executor;

        executor.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        });

        apiSynchronizer.synchronize(-1L, System.currentTimeMillis(), ENVIRONMENTS);
    }

    @Test
    public void initialSynchronizeWithNoEnvironment() throws Exception {
        io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
            .id(API_ID)
            .updatedAt(new Date())
            .definition("test")
            .build();

        final io.gravitee.definition.model.Api mockApi = mockApi(api);

        final Event mockEvent = mockEvent(api, EventType.PUBLISH_API);
        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria.getTypes().containsAll(asList(EventType.PUBLISH_API, EventType.START_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                anyLong(),
                anyLong()
            )
        )
            .thenReturn(singletonList(mockEvent));

        apiSynchronizer.synchronize(-1L, System.currentTimeMillis(), ENVIRONMENTS);

        ArgumentCaptor<Api> apiCaptor = ArgumentCaptor.forClass(Api.class);

        verify(apiManager).register(apiCaptor.capture());

        Api verifyApi = apiCaptor.getValue();
        assertEquals(API_ID, verifyApi.getId());
        assertNull(verifyApi.getEnvironmentId());
        assertNull(verifyApi.getEnvironmentHrid());
        assertNull(verifyApi.getOrganizationId());
        assertNull(verifyApi.getOrganizationHrid());

        verify(apiManager, never()).unregister(any(String.class));
        verify(planRepository, never()).findByApis(anyList());
        verify(apiKeysCacheService)
            .register(
                argThat(
                    new ArgumentMatcher<List<ReactableApi<?>>>() {
                        @Override
                        public boolean matches(List<ReactableApi<?>> reactableApis) {
                            return reactableApis.size() == 1 && reactableApis.get(0).equals(new Api(mockApi));
                        }
                    }
                )
            );
        verify(subscriptionsCacheService)
            .register(
                argThat(
                    new ArgumentMatcher<List<ReactableApi<?>>>() {
                        @Override
                        public boolean matches(List<ReactableApi<?>> reactableApis) {
                            return reactableApis.size() == 1 && reactableApis.get(0).equals(new Api(mockApi));
                        }
                    }
                )
            );
    }

    @Test
    public void initialSynchronize_withEnvironmentAndOrganization_withoutHrId_shouldSetHrIdToNull() throws Exception {
        io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
            .id(API_ID)
            .updatedAt(new Date())
            .definition("test")
            .environment(ENVIRONMENT_ID)
            .build();

        final io.gravitee.definition.model.Api mockApi = mockApi(api);

        final Event mockEvent = mockEvent(api, EventType.PUBLISH_API);
        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria.getTypes().containsAll(asList(EventType.PUBLISH_API, EventType.START_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                anyLong(),
                anyLong()
            )
        )
            .thenReturn(singletonList(mockEvent));

        mockEnvironmentAndOrganizationWithoutHrIds();

        apiSynchronizer.synchronize(-1L, System.currentTimeMillis(), ENVIRONMENTS);

        ArgumentCaptor<Api> apiCaptor = ArgumentCaptor.forClass(Api.class);

        verify(apiManager).register(apiCaptor.capture());

        Api verifyApi = apiCaptor.getValue();
        assertEquals(API_ID, verifyApi.getId());
        assertEquals(ENVIRONMENT_ID, verifyApi.getEnvironmentId());
        assertNull(verifyApi.getEnvironmentHrid());
        assertEquals(ORGANIZATION_ID, verifyApi.getOrganizationId());
        assertNull(verifyApi.getOrganizationHrid());
    }

    @Test
    public void initialSynchronizeWithNoOrganization() throws Exception {
        io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
            .id(API_ID)
            .updatedAt(new Date())
            .definition("test")
            .environment(ENVIRONMENT_ID)
            .build();

        final io.gravitee.definition.model.Api mockApi = mockApi(api);

        final Event mockEvent = mockEvent(api, EventType.PUBLISH_API);
        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria.getTypes().containsAll(asList(EventType.PUBLISH_API, EventType.START_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                anyLong(),
                anyLong()
            )
        )
            .thenReturn(singletonList(mockEvent));

        final Environment environment = new Environment();
        environment.setId(ENVIRONMENT_ID);
        environment.setOrganizationId(ORGANIZATION_ID);
        environment.setHrids(asList(ENVIRONMENT_HRID));
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.of(environment));
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.empty());

        apiSynchronizer.synchronize(-1L, System.currentTimeMillis(), ENVIRONMENTS);

        ArgumentCaptor<Api> apiCaptor = ArgumentCaptor.forClass(Api.class);

        verify(apiManager).register(apiCaptor.capture());

        Api verifyApi = apiCaptor.getValue();
        assertEquals(API_ID, verifyApi.getId());
        assertEquals(ENVIRONMENT_ID, verifyApi.getEnvironmentId());
        assertEquals(ENVIRONMENT_HRID, verifyApi.getEnvironmentHrid());
        assertNull(verifyApi.getOrganizationId());
        assertNull(verifyApi.getOrganizationHrid());

        verify(apiManager, never()).unregister(any(String.class));
        verify(planRepository, never()).findByApis(anyList());
        verify(apiKeysCacheService).register(singletonList(new Api(mockApi)));
        verify(subscriptionsCacheService).register(singletonList(new Api(mockApi)));
    }

    @Test
    public void publishWithDefinitionV2() throws Exception {
        io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
            .id(API_ID)
            .updatedAt(new Date())
            .definition("test")
            .environment(ENVIRONMENT_ID)
            .build();

        final io.gravitee.definition.model.Api mockApi = mockApi(api);

        final Event mockEvent = mockEvent(api, EventType.PUBLISH_API);
        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria
                        .getTypes()
                        .containsAll(asList(EventType.PUBLISH_API, EventType.START_API, EventType.UNPUBLISH_API, EventType.STOP_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                anyLong(),
                anyLong()
            )
        )
            .thenReturn(singletonList(mockEvent));

        mockEnvironmentAndOrganization();

        apiSynchronizer.synchronize(System.currentTimeMillis() - 5000, System.currentTimeMillis(), ENVIRONMENTS);

        verify(apiManager).register(new Api(mockApi));
        verify(apiManager, never()).unregister(any(String.class));
        verify(planRepository, never()).findByApis(anyList());
        verify(apiKeysCacheService).register(singletonList(new Api(mockApi)));
        verify(subscriptionsCacheService).register(singletonList(new Api(mockApi)));
    }

    @Test
    public void publishWithDefinitionV1() throws Exception {
        io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
            .id(API_ID)
            .updatedAt(new Date())
            .definition("test")
            .environment(ENVIRONMENT_ID)
            .build();

        final io.gravitee.definition.model.Api mockApi = mockApi(api);
        mockApi.setDefinitionVersion(DefinitionVersion.V1);

        final Event mockEvent = mockEvent(api, EventType.PUBLISH_API);
        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria
                        .getTypes()
                        .containsAll(asList(EventType.PUBLISH_API, EventType.START_API, EventType.UNPUBLISH_API, EventType.STOP_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                anyLong(),
                anyLong()
            )
        )
            .thenReturn(singletonList(mockEvent));

        final Plan plan = new Plan();
        plan.setId("planId");
        plan.setApi(mockApi.getId());
        plan.setStatus(Plan.Status.PUBLISHED);
        when(planRepository.findByApis(anyList())).thenReturn(singletonList(plan));
        mockEnvironmentAndOrganization();

        apiSynchronizer.synchronize(System.currentTimeMillis() - 5000, System.currentTimeMillis(), ENVIRONMENTS);

        ArgumentCaptor<Api> apiCaptor = ArgumentCaptor.forClass(Api.class);
        verify(apiManager).register(apiCaptor.capture());
        Api verifyApi = apiCaptor.getValue();
        assertEquals(API_ID, verifyApi.getId());
        assertEquals(verifyApi.getDefinition().getPlan("planId").getApi(), mockApi.getId());

        verify(apiManager, never()).unregister(any(String.class));
        verify(planRepository, times(1)).findByApis(anyList());
        verify(apiKeysCacheService).register(singletonList(new Api(mockApi)));
        verify(subscriptionsCacheService).register(singletonList(new Api(mockApi)));
    }

    @Test
    public void publishWithPagination() throws Exception {
        io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
            .id(API_ID)
            .updatedAt(new Date())
            .definition(API_ID)
            .environment(ENVIRONMENT_ID)
            .build();

        io.gravitee.repository.management.model.Api api2 = new RepositoryApiBuilder()
            .id("api2-test")
            .updatedAt(new Date())
            .definition("api2-test")
            .environment(ENVIRONMENT_ID)
            .build();

        final io.gravitee.definition.model.Api mockApi = mockApi(api);
        final io.gravitee.definition.model.Api mockApi2 = mockApi(api2);

        // Force bulk size to 1.
        apiSynchronizer.bulkItems = 1;

        final Event mockEvent = mockEvent(api, EventType.PUBLISH_API);
        final Event mockEvent2 = mockEvent(api2, EventType.PUBLISH_API);
        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria
                        .getTypes()
                        .containsAll(asList(EventType.PUBLISH_API, EventType.START_API, EventType.UNPUBLISH_API, EventType.STOP_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                eq(0L),
                eq(1L)
            )
        )
            .thenReturn(singletonList(mockEvent));

        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria
                        .getTypes()
                        .containsAll(asList(EventType.PUBLISH_API, EventType.START_API, EventType.UNPUBLISH_API, EventType.STOP_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                eq(1L),
                eq(1L)
            )
        )
            .thenReturn(singletonList(mockEvent2));

        mockEnvironmentAndOrganization();

        apiSynchronizer.synchronize(System.currentTimeMillis() - 5000, System.currentTimeMillis(), ENVIRONMENTS);

        verify(apiManager, times(1)).register(new Api(mockApi));
        verify(apiManager, times(1)).register(new Api(mockApi2));
        verify(apiManager, never()).unregister(any(String.class));
        verify(planRepository, never()).findByApis(anyList());
        verify(apiKeysCacheService).register(singletonList(new Api(mockApi)));
        verify(apiKeysCacheService).register(singletonList(new Api(mockApi2)));
        verify(subscriptionsCacheService).register(singletonList(new Api(mockApi)));
        verify(subscriptionsCacheService).register(singletonList(new Api(mockApi2)));
    }

    @Test
    public void publishWithPaginationAndDefinitionV1AndV2() throws Exception {
        io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
            .id(API_ID)
            .updatedAt(new Date())
            .definition(API_ID)
            .environment(ENVIRONMENT_ID)
            .build();

        io.gravitee.repository.management.model.Api api2 = new RepositoryApiBuilder()
            .id("api2-test")
            .updatedAt(new Date())
            .definition("api2-test")
            .environment(ENVIRONMENT_ID)
            .build();

        final io.gravitee.definition.model.Api mockApi = mockApi(api);
        mockApi.setDefinitionVersion(DefinitionVersion.V1);
        final io.gravitee.definition.model.Api mockApi2 = mockApi(api2);
        mockApi2.setDefinitionVersion(DefinitionVersion.V2);

        // Force bulk size to 1.
        apiSynchronizer.bulkItems = 1;

        final Event mockEvent = mockEvent(api, EventType.PUBLISH_API);
        final Event mockEvent2 = mockEvent(api2, EventType.PUBLISH_API);
        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria
                        .getTypes()
                        .containsAll(asList(EventType.PUBLISH_API, EventType.START_API, EventType.UNPUBLISH_API, EventType.STOP_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                eq(0L),
                eq(1L)
            )
        )
            .thenReturn(singletonList(mockEvent));

        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria
                        .getTypes()
                        .containsAll(asList(EventType.PUBLISH_API, EventType.START_API, EventType.UNPUBLISH_API, EventType.STOP_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                eq(1L),
                eq(1L)
            )
        )
            .thenReturn(singletonList(mockEvent2));

        final Plan plan = new Plan();
        plan.setApi(mockApi.getId());
        when(planRepository.findByApis(singletonList(plan.getApi()))).thenReturn(singletonList(plan));
        mockEnvironmentAndOrganization();

        apiSynchronizer.synchronize(System.currentTimeMillis() - 5000, System.currentTimeMillis(), ENVIRONMENTS);

        verify(apiManager, times(1)).register(new Api(mockApi));
        verify(apiManager, times(1)).register(new Api(mockApi2));
        verify(apiManager, never()).unregister(any(String.class));
        verify(planRepository, times(1)).findByApis(singletonList(plan.getApi()));
        verify(apiKeysCacheService).register(singletonList(new Api(mockApi)));
        verify(apiKeysCacheService).register(singletonList(new Api(mockApi2)));
        verify(subscriptionsCacheService).register(singletonList(new Api(mockApi)));
        verify(subscriptionsCacheService).register(singletonList(new Api(mockApi2)));
    }

    @Test
    public void unpublish() throws Exception {
        io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
            .id(API_ID)
            .updatedAt(new Date())
            .definition("test")
            .build();

        final io.gravitee.definition.model.Api mockApi = mockApi(api);

        final Event mockEvent = mockEvent(api, EventType.UNPUBLISH_API);
        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria
                        .getTypes()
                        .containsAll(asList(EventType.PUBLISH_API, EventType.START_API, EventType.UNPUBLISH_API, EventType.STOP_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                anyLong(),
                anyLong()
            )
        )
            .thenReturn(singletonList(mockEvent));

        apiSynchronizer.synchronize(System.currentTimeMillis() - 5000, System.currentTimeMillis(), ENVIRONMENTS);

        verify(apiManager, never()).register(new Api(mockApi));
        verify(apiManager).unregister(mockApi.getId());
        verify(planRepository, never()).findByApis(anyList());
        verify(apiKeysCacheService, never()).register(singletonList(new Api(mockApi)));
        verify(subscriptionsCacheService, never()).register(singletonList(new Api(mockApi)));
    }

    @Test
    public void unpublishWithPagination() throws Exception {
        io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
            .id(API_ID)
            .updatedAt(new Date())
            .definition(API_ID)
            .build();

        io.gravitee.repository.management.model.Api api2 = new RepositoryApiBuilder()
            .id("api2-test")
            .updatedAt(new Date())
            .definition("api2-test")
            .build();

        final io.gravitee.definition.model.Api mockApi = mockApi(api);
        mockApi.setDefinitionVersion(DefinitionVersion.V1);
        final io.gravitee.definition.model.Api mockApi2 = mockApi(api2);
        mockApi2.setDefinitionVersion(DefinitionVersion.V2);

        // Force bulk size to 1.
        apiSynchronizer.bulkItems = 1;

        final Event mockEvent = mockEvent(api, EventType.UNPUBLISH_API);
        final Event mockEvent2 = mockEvent(api2, EventType.UNPUBLISH_API);
        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria
                        .getTypes()
                        .containsAll(asList(EventType.PUBLISH_API, EventType.START_API, EventType.UNPUBLISH_API, EventType.STOP_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                eq(0L),
                eq(1L)
            )
        )
            .thenReturn(singletonList(mockEvent));

        when(
            eventRepository.searchLatest(
                argThat(criteria ->
                    criteria != null &&
                    criteria
                        .getTypes()
                        .containsAll(asList(EventType.PUBLISH_API, EventType.START_API, EventType.UNPUBLISH_API, EventType.STOP_API)) &&
                    criteria.getEnvironments().containsAll(ENVIRONMENTS)
                ),
                eq(Event.EventProperties.API_ID),
                eq(1L),
                eq(1L)
            )
        )
            .thenReturn(singletonList(mockEvent2));

        apiSynchronizer.synchronize(System.currentTimeMillis() - 5000, System.currentTimeMillis(), ENVIRONMENTS);

        verify(apiManager, never()).register(new Api(mockApi));
        verify(apiManager).unregister(mockApi.getId());
        verify(apiManager).unregister(mockApi2.getId());
        verify(planRepository, never()).findByApis(anyList());
        verify(apiKeysCacheService, never()).register(singletonList(new Api(mockApi)));
        verify(subscriptionsCacheService, never()).register(singletonList(new Api(mockApi)));
    }

    @Test
    public void synchronizeWithLotOfApiEvents() throws Exception {
        long page = 0;
        List<Event> eventAccumulator = new ArrayList<>(100);

        for (int i = 1; i <= 500; i++) {
            io.gravitee.repository.management.model.Api api = new RepositoryApiBuilder()
                .id("api" + i + "-test")
                .updatedAt(new Date())
                .definition("api" + i + "-test")
                .environment(ENVIRONMENT_ID)
                .build();

            final io.gravitee.definition.model.Api mockApi = mockApi(api);
            mockApi.setDefinitionVersion(DefinitionVersion.V1);

            if (i % 2 == 0) {
                eventAccumulator.add(mockEvent(api, EventType.START_API));
            } else {
                eventAccumulator.add(mockEvent(api, EventType.STOP_API));
            }

            if (i % 100 == 0) {
                when(
                    eventRepository.searchLatest(
                        argThat(criteria ->
                            criteria != null &&
                            criteria
                                .getTypes()
                                .containsAll(
                                    asList(EventType.PUBLISH_API, EventType.START_API, EventType.UNPUBLISH_API, EventType.STOP_API)
                                ) &&
                            criteria.getEnvironments().containsAll(ENVIRONMENTS)
                        ),
                        eq(Event.EventProperties.API_ID),
                        eq(page),
                        eq(100L)
                    )
                )
                    .thenReturn(eventAccumulator);

                page++;
                eventAccumulator = new ArrayList<>();
            }
        }

        mockEnvironmentAndOrganization();
        apiSynchronizer.synchronize(System.currentTimeMillis() - 5000, System.currentTimeMillis(), ENVIRONMENTS);

        verify(planRepository, times(3)).findByApis(anyList());
        verify(apiKeysCacheService, times(3)).register(anyList());
        verify(subscriptionsCacheService, times(3)).register(anyList());
        verify(apiManager, times(250)).register(any(Api.class));
        verify(apiManager, times(250)).unregister(anyString());

        // Check that only one call to env and org repositories have been made, others should hit the cache.
        verify(environmentRepository, times(1)).findById(ENVIRONMENT_ID);
        verify(organizationRepository, times(1)).findById(ORGANIZATION_ID);
    }

    private io.gravitee.definition.model.Api mockApi(final io.gravitee.repository.management.model.Api api) throws Exception {
        return mockApi(api, new String[] {});
    }

    private io.gravitee.definition.model.Api mockApi(final io.gravitee.repository.management.model.Api api, final String[] tags)
        throws Exception {
        final io.gravitee.definition.model.Api mockApi = new io.gravitee.definition.model.Api();
        mockApi.setId(api.getId());
        mockApi.setTags(new HashSet<>(asList(tags)));
        mockApi.setDefinitionVersion(DefinitionVersion.V2);
        when(objectMapper.readValue(api.getDefinition(), io.gravitee.definition.model.Api.class)).thenReturn(mockApi);
        return mockApi;
    }

    private Event mockEvent(final io.gravitee.repository.management.model.Api api, EventType eventType) throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put(Event.EventProperties.API_ID.getValue(), api.getId());

        Event event = new Event();
        event.setType(eventType);
        event.setCreatedAt(new Date());
        event.setProperties(properties);
        event.setPayload(api.getId());
        event.setEnvironments(singleton(ENVIRONMENT_ID));

        when(objectMapper.readValue(event.getPayload(), io.gravitee.repository.management.model.Api.class)).thenReturn(api);

        return event;
    }

    private void mockEnvironmentAndOrganizationWithoutHrIds() throws io.gravitee.repository.exceptions.TechnicalException {
        mockEnvironmentAndOrganization(asList(), asList());
    }

    private void mockEnvironmentAndOrganization() throws io.gravitee.repository.exceptions.TechnicalException {
        mockEnvironmentAndOrganization(asList(ENVIRONMENT_HRID), asList(ORGANIZATION_HRID));
    }

    private void mockEnvironmentAndOrganization(List<String> envHrIds, List<String> orgHrIds)
        throws io.gravitee.repository.exceptions.TechnicalException {
        final Environment environment = new Environment();
        environment.setId(ENVIRONMENT_ID);
        environment.setOrganizationId(ORGANIZATION_ID);
        environment.setHrids(envHrIds);
        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Optional.of(environment));
        final Organization organization = new Organization();
        organization.setId(ORGANIZATION_ID);
        organization.setHrids(orgHrIds);
        when(organizationRepository.findById(ORGANIZATION_ID)).thenReturn(Optional.of(organization));
    }
}
