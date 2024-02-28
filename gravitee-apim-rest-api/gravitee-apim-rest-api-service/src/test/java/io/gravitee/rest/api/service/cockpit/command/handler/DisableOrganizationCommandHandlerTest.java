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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.gravitee.apim.core.access_point.crud_service.AccessPointCrudService;
import io.gravitee.apim.core.access_point.model.AccessPoint;
import io.gravitee.cockpit.api.command.Command;
import io.gravitee.cockpit.api.command.CommandStatus;
import io.gravitee.cockpit.api.command.organization.DisableOrganizationCommand;
import io.gravitee.cockpit.api.command.organization.DisableOrganizationPayload;
import io.gravitee.rest.api.model.OrganizationEntity;
import io.gravitee.rest.api.service.OrganizationService;
import io.gravitee.rest.api.service.exceptions.OrganizationNotFoundException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DisableOrganizationCommandHandlerTest {

    private static final String ORG_COCKPIT_ID = "org#cockpit#id";
    private static final String ORG_APIM_ID = "org#apim#id";

    @Mock
    private OrganizationService organizationService;

    @Mock
    private AccessPointCrudService accessPointService;

    private DisableOrganizationCommandHandler cut;

    @BeforeEach
    void setUp() {
        cut = new DisableOrganizationCommandHandler(organizationService, accessPointService);
    }

    @Test
    void handleType() {
        assertEquals(Command.Type.DISABLE_ORGANIZATION_COMMAND, cut.handleType());
    }

    @Test
    void handleSuccessfulCommand() {
        var apimOrg = new OrganizationEntity();
        apimOrg.setId(ORG_APIM_ID);

        when(organizationService.findByCockpitId(ORG_COCKPIT_ID)).thenReturn(apimOrg);

        cut
            .handle(aDisableOrgCommand())
            .test()
            .awaitDone(1, TimeUnit.SECONDS)
            .assertValue(reply -> reply.getCommandStatus().equals(CommandStatus.SUCCEEDED));

        verify(accessPointService).deleteAccessPoints(AccessPoint.ReferenceType.ORGANIZATION, ORG_APIM_ID);
    }

    @Test
    void handleThrowsException() {
        when(organizationService.findByCockpitId(ORG_COCKPIT_ID)).thenThrow(new OrganizationNotFoundException(ORG_COCKPIT_ID));

        cut
            .handle(aDisableOrgCommand())
            .test()
            .awaitDone(1, TimeUnit.SECONDS)
            .assertValue(reply -> reply.getCommandStatus().equals(CommandStatus.ERROR));
    }

    private DisableOrganizationCommand aDisableOrgCommand() {
        var payload = new DisableOrganizationPayload();
        payload.setCockpitId(ORG_COCKPIT_ID);
        return new DisableOrganizationCommand(payload);
    }
}
