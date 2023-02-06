/*
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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpTestingController } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatIconTestingModule } from '@angular/material/icon/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { InteractivityChecker } from '@angular/cdk/a11y';

import { ApiPortalGroupsComponent } from './api-portal-groups.component';
import { ApiPortalGroupsHarness } from './api-portal-groups.harness';

import { CONSTANTS_TESTING, GioHttpTestingModule } from '../../../../../shared/testing';
import { ApiPortalUserGroupModule } from '../api-portal-user-group.module';
import { CurrentUserService, UIRouterState, UIRouterStateParams } from '../../../../../ajs-upgraded-providers';
import { fakeApi } from '../../../../../entities/api/Api.fixture';
import { Api } from '../../../../../entities/api';
import { fakeGroup } from '../../../../../entities/group/group.fixture';
import { Group } from '../../../../../entities/group/group';
import { User } from '../../../../../entities/user';

describe('ApiPortalGroupsComponent', () => {
  const API_ID = 'api-id';

  const fakeAjsState = {
    go: jest.fn(),
  };

  const currentUser = new User();

  let fixture: ComponentFixture<ApiPortalGroupsComponent>;
  let harness: ApiPortalGroupsHarness;
  let httpTestingController: HttpTestingController;

  const init = async (userPermissions: string[]) => {
    currentUser.userPermissions = userPermissions;
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule, GioHttpTestingModule, ApiPortalUserGroupModule, MatIconTestingModule],
      providers: [
        { provide: UIRouterState, useValue: fakeAjsState },
        { provide: UIRouterStateParams, useValue: { apiId: API_ID } },
        { provide: CurrentUserService, useValue: { currentUser } },
      ],
    })
      .overrideProvider(InteractivityChecker, {
        useValue: {
          isFocusable: () => true, // This traps focus checks and so avoid warnings when dealing with
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(ApiPortalGroupsComponent);

    httpTestingController = TestBed.inject(HttpTestingController);
    harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiPortalGroupsHarness);

    fixture.detectChanges();
  };

  afterEach(() => {
    httpTestingController.verify();
  });

  describe('Groups tab for user with writing rights', () => {
    beforeEach(async () => await init(['api-definition-u', 'api-gateway_definition-u']));

    it('should show groups', async () => {
      expectOneGroupList([fakeGroup({ id: 'my_group', name: 'My Group' })]);
      expectOneApiGet(fakeApi({ id: API_ID }));

      expect(await harness.getFillFormLabel()).toEqual('Groups');
      await harness.selectGroups({ text: 'My Group' });
      expect(await harness.getGroupsListValueText()).toEqual('My Group');
      expect(await harness.isReadOnlyGroupsPresent()).toEqual(false);
    });

    it('should pre-select groups found in user + save new groups', async () => {
      expectOneGroupList([fakeGroup({ id: 'my_group', name: 'My Group' }), fakeGroup({ id: 'new_group', name: 'New Group' })]);
      expectOneApiGet(fakeApi({ id: API_ID, groups: ['my_group'] }));

      expect(await harness.isFillFormControlDirty()).toEqual(false);

      const allGroups = await harness.getGroups();
      expect(allGroups.length).toEqual(2);

      const selectedGroups = await harness.getSelectedGroups();
      expect(selectedGroups.length).toEqual(1);
      expect(await selectedGroups[0].getText()).toEqual('My Group');

      await harness.selectGroups({ text: 'New Group' });
      expect(await harness.getGroupsListValueText()).toEqual('My Group, New Group');
      await harness.closeGroupsList();

      expect(await harness.isFillFormControlDirty()).toEqual(true);
      expect(await harness.isSaveBarVisible()).toEqual(true);
      await harness.clickSubmit();

      expectOneApiGet(fakeApi({ id: API_ID, groups: ['my_group'] }));
      expectOneApiPut(fakeApi({ id: API_ID, groups: ['my_group', 'new_group'] }));

      // expect reloaded component
      expectOneGroupList([fakeGroup({ id: 'my_group', name: 'My Group' }), fakeGroup({ id: 'new_group', name: 'New Group' })]);
      expectOneApiGet(fakeApi({ id: API_ID, groups: ['my_group', 'new_group'] }));

      expect(await harness.isFillFormControlDirty()).toEqual(false);

      const newSelectedGroups = await harness.getSelectedGroups();
      expect(newSelectedGroups.length).toEqual(2);
      expect(await harness.isSaveBarVisible()).toEqual(false);
    });

    it("should reset to user's original groups after clicking reset button", async () => {
      expectOneGroupList([fakeGroup({ id: 'my_group', name: 'My Group' }), fakeGroup({ id: 'new_group', name: 'New Group' })]);
      expectOneApiGet(fakeApi({ id: API_ID, groups: ['my_group'] }));

      await harness.selectGroups({ text: 'New Group' });
      let selectedGroups = await harness.getSelectedGroups();

      expect(selectedGroups.length).toEqual(2);
      expect(await harness.getGroupsListValueText()).toEqual('My Group, New Group');
      await harness.closeGroupsList();

      expect(await harness.isResetButtonVisible()).toEqual(true);
      await harness.clickReset();

      selectedGroups = await harness.getSelectedGroups();
      expect(selectedGroups.length).toEqual(1);
      expect(await harness.isSaveBarVisible()).toEqual(false);
    });
  });

  describe('Groups tab for user with read-only rights', () => {
    beforeEach(async () => {
      await init(['api-definition-r', 'api-gateway_definition-u']);
    });

    it('should display list of groups', async () => {
      expectOneGroupList([fakeGroup({ id: 'my_group', name: 'My Group' }), fakeGroup({ id: 'my_other_group', name: 'My Other Group' })]);
      expectOneApiGet(fakeApi({ id: API_ID, groups: ['my_group', 'my_other_group'] }));

      expect(await harness.isReadOnlyGroupsPresent()).toEqual(true);
      expect(await harness.getReadOnlyGroupsText()).toContain('My Group, My Other Group');
      expect(await harness.isFillFormPresent()).toEqual(false);
      expect(await harness.isSaveBarVisible()).toEqual(false);
    });
  });

  function expectOneApiGet(api: Api) {
    httpTestingController.expectOne({ url: `${CONSTANTS_TESTING.env.baseURL}/apis/${api.id}`, method: 'GET' }).flush(api);
  }

  function expectOneApiPut(api: Api) {
    const httpCall = httpTestingController.expectOne({ url: `${CONSTANTS_TESTING.env.baseURL}/apis/${api.id}`, method: 'PUT' });
    const requestBody = httpCall.request.body;
    expect(requestBody.groups).toEqual(api.groups);

    httpCall.flush(api);
  }

  function expectOneGroupList(groups: Group[]) {
    httpTestingController.expectOne({ url: `${CONSTANTS_TESTING.env.baseURL}/configuration/groups`, method: 'GET' }).flush(groups);
  }
});
