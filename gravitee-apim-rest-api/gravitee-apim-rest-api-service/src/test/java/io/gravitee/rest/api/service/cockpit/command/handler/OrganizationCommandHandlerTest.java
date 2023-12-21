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
package io.gravitee.rest.api.service.cockpit.command.handler;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.gravitee.apim.core.access_point.crud_service.AccessPointCrudService;
import io.gravitee.cockpit.api.command.Command;
import io.gravitee.cockpit.api.command.CommandStatus;
import io.gravitee.cockpit.api.command.accesspoint.AccessPoint;
import io.gravitee.cockpit.api.command.organization.OrganizationCommand;
import io.gravitee.cockpit.api.command.organization.OrganizationPayload;
import io.gravitee.cockpit.api.command.organization.OrganizationReply;
import io.gravitee.rest.api.model.OrganizationEntity;
import io.gravitee.rest.api.model.UpdateOrganizationEntity;
import io.gravitee.rest.api.service.OrganizationService;
import io.gravitee.rest.api.service.exceptions.EnvironmentNotFoundException;
import io.gravitee.rest.api.service.exceptions.OrganizationNotFoundException;
import io.reactivex.rxjava3.observers.TestObserver;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizationCommandHandlerTest {

    @Mock
    private OrganizationService organizationService;

    @Mock
    private AccessPointCrudService accessPointService;

    public OrganizationCommandHandler cut;

    @Before
    public void before() {
        cut = new OrganizationCommandHandler(organizationService, accessPointService);
    }

    @Test
    public void handleType() {
        assertEquals(Command.Type.ORGANIZATION_COMMAND, cut.handleType());
    }

    @Test
    public void handle() throws InterruptedException {
        OrganizationPayload organizationPayload = new OrganizationPayload();
        OrganizationCommand command = new OrganizationCommand(organizationPayload);

        organizationPayload.setId("orga#1");
        organizationPayload.setCockpitId("org#cockpit-1");
        organizationPayload.setHrids(Collections.singletonList("orga-1"));
        organizationPayload.setDescription("Organization description");
        organizationPayload.setName("Organization name");
        organizationPayload.setAccessPoints(
            List.of(
                AccessPoint.builder().target(AccessPoint.Target.CONSOLE).host("domain.restriction1.io").build(),
                AccessPoint.builder().target(AccessPoint.Target.CONSOLE).host("domain.restriction2.io").build()
            )
        );
        when(organizationService.findByCockpitId(any())).thenThrow(new OrganizationNotFoundException("Org not found"));
        when(
            organizationService.createOrUpdate(
                argThat(orgaId -> orgaId.equals("orga#1")),
                argThat(newOrganization ->
                    newOrganization.getCockpitId().equals(organizationPayload.getCockpitId()) &&
                    newOrganization.getHrids().equals(organizationPayload.getHrids()) &&
                    newOrganization.getDescription().equals(organizationPayload.getDescription()) &&
                    newOrganization.getName().equals(organizationPayload.getName())
                )
            )
        )
            .thenReturn(new OrganizationEntity());

        TestObserver<OrganizationReply> obs = cut.handle(command).test();

        obs.await();
        obs.assertValue(reply -> reply.getCommandId().equals(command.getId()) && reply.getCommandStatus().equals(CommandStatus.SUCCEEDED));
    }

    @Test
    public void handleWithException() throws InterruptedException {
        OrganizationPayload organizationPayload = new OrganizationPayload();
        OrganizationCommand command = new OrganizationCommand(organizationPayload);

        organizationPayload.setId("orga#1");
        organizationPayload.setDescription("Organization description");
        organizationPayload.setName("Organization name");
        organizationPayload.setAccessPoints(
            List.of(
                AccessPoint.builder().target(AccessPoint.Target.CONSOLE).host("domain.restriction1.io").build(),
                AccessPoint.builder().target(AccessPoint.Target.CONSOLE).host("domain.restriction2.io").build()
            )
        );

        when(organizationService.findByCockpitId(any())).thenThrow(new OrganizationNotFoundException("Org not found"));
        when(organizationService.createOrUpdate(argThat(orgaId -> orgaId.equals("orga#1")), any(UpdateOrganizationEntity.class)))
            .thenThrow(new RuntimeException("fake error"));

        TestObserver<OrganizationReply> obs = cut.handle(command).test();

        obs.await();
        obs.assertNoErrors();
        obs.assertValue(reply -> reply.getCommandId().equals(command.getId()) && reply.getCommandStatus().equals(CommandStatus.ERROR));
    }

    @Test
    public void handleWithExistingCockpitId() throws InterruptedException {
        OrganizationPayload organizationPayload = new OrganizationPayload();
        OrganizationCommand command = new OrganizationCommand(organizationPayload);

        organizationPayload.setId("orga#1");
        organizationPayload.setCockpitId("org#cockpit-1");
        organizationPayload.setHrids(Collections.singletonList("orga-1"));
        organizationPayload.setDescription("Organization description");
        organizationPayload.setName("Organization name");
        organizationPayload.setAccessPoints(
            List.of(
                AccessPoint.builder().target(AccessPoint.Target.CONSOLE).host("domain.restriction1.io").build(),
                AccessPoint.builder().target(AccessPoint.Target.CONSOLE).host("domain.restriction2.io").build()
            )
        );
        OrganizationEntity existingOrganization = mock(OrganizationEntity.class);
        when(existingOrganization.getId()).thenReturn("DEFAULT");
        when(organizationService.findByCockpitId(any())).thenReturn(existingOrganization);
        when(
            organizationService.createOrUpdate(
                argThat(orgaId -> orgaId.equals("DEFAULT")),
                argThat(newOrganization ->
                    newOrganization.getCockpitId().equals(organizationPayload.getCockpitId()) &&
                    newOrganization.getHrids().equals(organizationPayload.getHrids()) &&
                    newOrganization.getDescription().equals(organizationPayload.getDescription()) &&
                    newOrganization.getName().equals(organizationPayload.getName())
                )
            )
        )
            .thenReturn(new OrganizationEntity());

        TestObserver<OrganizationReply> obs = cut.handle(command).test();

        obs.await();
        obs.assertValue(reply -> reply.getCommandId().equals(command.getId()) && reply.getCommandStatus().equals(CommandStatus.SUCCEEDED));
    }
}
