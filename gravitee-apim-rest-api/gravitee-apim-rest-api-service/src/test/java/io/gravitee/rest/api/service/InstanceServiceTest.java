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
package io.gravitee.rest.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.repository.management.model.Event;
import io.gravitee.rest.api.model.*;
import io.gravitee.rest.api.service.impl.InstanceServiceImpl;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author Eric LELEU (eric.leleu at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class InstanceServiceTest {

    @Mock
    private EventService eventService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private InstanceService cut;

    @Before
    public void setUp() throws Exception {
        cut = new InstanceServiceImpl(eventService, objectMapper);
    }

    @Test
    public void shouldFindByEventIfNoEnvOrOrgProperty() {
        final EventEntity evt = new EventEntity();
        evt.setProperties(Map.of("id", "evt-id"));

        when(eventService.findById("evt-id")).thenReturn(evt);

        final InstanceEntity result = cut.findByEvent("evt-id");

        assertThat(result).isNotNull();
        assertThat(result.getEnvironmentsHrids()).isEmpty();
        assertThat(result.getOrganizationsHrids()).isEmpty();
    }

    @Test
    public void shouldFindByEventIfNoOrgProperty() {
        final EventEntity evt = new EventEntity();
        evt.setProperties(Map.of("id", "evt-id", Event.EventProperties.ENVIRONMENTS_HRIDS_PROPERTY.getValue(), "evt-env"));

        when(eventService.findById("evt-id")).thenReturn(evt);

        final InstanceEntity result = cut.findByEvent("evt-id");

        assertThat(result).isNotNull();
        assertThat(result.getEnvironmentsHrids()).hasSize(1);
        assertThat(result.getOrganizationsHrids()).isEmpty();
    }

    @Test
    public void shouldFindByEventIfNoEnvProperty() {
        final EventEntity evt = new EventEntity();
        evt.setProperties(Map.of("id", "evt-id", Event.EventProperties.ORGANIZATIONS_HRIDS_PROPERTY.getValue(), "evt-org"));

        when(eventService.findById("evt-id")).thenReturn(evt);

        final InstanceEntity result = cut.findByEvent("evt-id");

        assertThat(result).isNotNull();
        assertThat(result.getEnvironmentsHrids()).isEmpty();
        assertThat(result.getOrganizationsHrids()).hasSize(1);
    }

    @Test
    public void shouldFindByEvent() {
        final EventEntity evt = new EventEntity();
        Instant aMinAgo = Instant.now().minus(1, ChronoUnit.MINUTES);
        Instant twoMinAgo = Instant.now().minus(2, ChronoUnit.MINUTES);
        evt.setProperties(
            Map.of(
                "id",
                "evt-id",
                Event.EventProperties.ENVIRONMENTS_HRIDS_PROPERTY.getValue(),
                "evt-env",
                Event.EventProperties.ORGANIZATIONS_HRIDS_PROPERTY.getValue(),
                "evt-org",
                "last_heartbeat_at",
                String.valueOf(aMinAgo.toEpochMilli()),
                "started_at",
                String.valueOf(twoMinAgo.toEpochMilli())
            )
        );
        evt.setType(EventType.GATEWAY_STARTED);
        evt.setPayload("{\"hostname\":\"myhost\"}");

        when(eventService.findById("evt-id")).thenReturn(evt);

        final InstanceEntity result = cut.findByEvent("evt-id");

        assertThat(result.getLastHeartbeatAt()).isEqualTo(Date.from(aMinAgo));
        assertThat(result.getStartedAt()).isEqualTo(Date.from(twoMinAgo));
        assertThat(result.getState()).isEqualTo(InstanceState.STARTED);
        assertThat(result.getEnvironmentsHrids()).hasSize(1);
        assertThat(result.getOrganizationsHrids()).hasSize(1);

        assertThat(result.getHostname()).isEqualTo("myhost");
    }

    @Test
    public void shouldFindByEventWithoutDateProperties() {
        final EventEntity evt = new EventEntity();
        evt.setProperties(
            Map.of(
                "id",
                "evt-id",
                Event.EventProperties.ENVIRONMENTS_HRIDS_PROPERTY.getValue(),
                "evt-env",
                Event.EventProperties.ORGANIZATIONS_HRIDS_PROPERTY.getValue(),
                "evt-org"
            )
        );
        evt.setType(EventType.GATEWAY_STARTED);

        when(eventService.findById("evt-id")).thenReturn(evt);

        final InstanceEntity result = cut.findByEvent("evt-id");

        assertThat(result.getLastHeartbeatAt()).isNull();
        assertThat(result.getState()).isEqualTo(InstanceState.UNKNOWN);
    }

    @Test
    public void shouldFindByEventWithoutGatewayStartedType() {
        Instant aMinAgo = Instant.now().minus(1, ChronoUnit.MINUTES);

        final EventEntity evt = new EventEntity();
        evt.setProperties(
            Map.of(
                "id",
                "evt-id",
                Event.EventProperties.ENVIRONMENTS_HRIDS_PROPERTY.getValue(),
                "evt-env",
                Event.EventProperties.ORGANIZATIONS_HRIDS_PROPERTY.getValue(),
                "evt-org",
                "stopped_at",
                String.valueOf(aMinAgo.toEpochMilli())
            )
        );

        when(eventService.findById("evt-id")).thenReturn(evt);

        final InstanceEntity result = cut.findByEvent("evt-id");

        assertThat(result.getState()).isEqualTo(InstanceState.STOPPED);
        assertThat(result.getStoppedAt()).isEqualTo(Date.from(aMinAgo));
    }

    @Test
    public void shouldFindAllStartedEvenIfNoEnvOrOrgProperty() {
        final EventEntity evt = new EventEntity();
        evt.setProperties(Map.of("id", "evt-id"));

        final EventEntity evtWithEnv = new EventEntity();
        evtWithEnv.setProperties(Map.of("id", "evt-id", Event.EventProperties.ENVIRONMENTS_HRIDS_PROPERTY.getValue(), "evt-env"));

        final EventEntity evtWithOrg = new EventEntity();
        evtWithOrg.setProperties(Map.of("id", "evt-id", Event.EventProperties.ORGANIZATIONS_HRIDS_PROPERTY.getValue(), "evt-org"));

        final EventEntity evtWithEnvAndOrg = new EventEntity();
        evtWithEnvAndOrg.setProperties(
            Map.of(
                "id",
                "evt-id",
                Event.EventProperties.ENVIRONMENTS_HRIDS_PROPERTY.getValue(),
                "evt-env",
                Event.EventProperties.ORGANIZATIONS_HRIDS_PROPERTY.getValue(),
                "evt-org"
            )
        );

        when(eventService.search(any(EventQuery.class))).thenReturn(List.of(evt, evtWithEnv, evtWithOrg, evtWithEnvAndOrg));

        final List<InstanceEntity> result = cut.findAllStarted();

        assertThat(result).hasSize(4);
    }

    @Test
    public void expirePredicateShouldFilterOldUnknownState() {
        InstanceServiceImpl.ExpiredPredicate predicateDays = new InstanceServiceImpl.ExpiredPredicate(Duration.ofDays(7));
        InstanceServiceImpl.ExpiredPredicate predicateSeconds = new InstanceServiceImpl.ExpiredPredicate(Duration.ofSeconds(7 * 24 * 3600));

        InstanceListItem itemStarted = new InstanceListItem();
        itemStarted.setId("ok-1");
        itemStarted.setState(InstanceState.STARTED);
        itemStarted.setLastHeartbeatAt(new Date(Instant.now().toEpochMilli()));
        InstanceListItem itemStopped = new InstanceListItem();
        itemStopped.setId("ok-2");
        itemStopped.setState(InstanceState.STOPPED);
        itemStopped.setLastHeartbeatAt(new Date(Instant.now().minus(8, ChronoUnit.DAYS).toEpochMilli()));
        InstanceListItem itemUnknownVisible = new InstanceListItem();
        itemUnknownVisible.setId("ok-3");
        itemUnknownVisible.setState(InstanceState.UNKNOWN);
        itemUnknownVisible.setLastHeartbeatAt(new Date(Instant.now().minus(6, ChronoUnit.DAYS).toEpochMilli()));
        InstanceListItem itemUnknownNotVisible = new InstanceListItem();
        itemUnknownNotVisible.setId("ko-4");
        itemUnknownNotVisible.setState(InstanceState.UNKNOWN);
        itemUnknownNotVisible.setLastHeartbeatAt(new Date(Instant.now().minus(8, ChronoUnit.DAYS).toEpochMilli()));

        execFiltering(predicateDays, Stream.of(itemStarted, itemUnknownNotVisible, itemStopped, itemUnknownVisible));
        execFiltering(predicateSeconds, Stream.of(itemStarted, itemUnknownNotVisible, itemStopped, itemUnknownVisible));
    }

    private void execFiltering(InstanceServiceImpl.ExpiredPredicate predicateDays, Stream<InstanceListItem> stream) {
        List<InstanceListItem> items = stream.filter(predicateDays).collect(Collectors.toList());
        assertNotNull(items);
        assertEquals(3, items.size());
        for (InstanceListItem item : items) {
            assertNotEquals("ko-4", item.getId());
        }
    }
}
