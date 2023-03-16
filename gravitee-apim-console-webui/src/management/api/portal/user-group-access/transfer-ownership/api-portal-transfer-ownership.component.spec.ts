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
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { HttpTestingController } from '@angular/common/http/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { InteractivityChecker } from '@angular/cdk/a11y';
import { MatIconTestingModule } from '@angular/material/icon/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { MatRadioGroupHarness } from '@angular/material/radio/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { set } from 'lodash';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDialogHarness } from '@angular/material/dialog/testing';

import { ApiPortalTransferOwnershipComponent } from './api-portal-transfer-ownership.component';

import { CONSTANTS_TESTING, GioHttpTestingModule } from '../../../../../shared/testing';
import { ApiPortalUserGroupModule } from '../api-portal-user-group.module';
import { fakeApi } from '../../../../../entities/api/Api.fixture';
import { Api, ApiMember } from '../../../../../entities/api';
import { UIRouterStateParams } from '../../../../../ajs-upgraded-providers';
import { Role } from '../../../../../entities/role/role';
import { fakeRole } from '../../../../../entities/role/role.fixture';
import { Group } from '../../../../../entities/group/group';
import { fakeGroup } from '../../../../../entities/group/group.fixture';
import { GioFormUserAutocompleteHarness } from '../../../../../shared/components/gio-user-autocomplete/gio-form-user-autocomplete.harness';
import { fakeSearchableUser } from '../../../../../entities/user/searchableUser.fixture';
import { SearchableUser } from '../../../../../entities/user/searchableUser';

describe('ApiPortalTransferOwnershipComponent', () => {
  let fixture: ComponentFixture<ApiPortalTransferOwnershipComponent>;
  let httpTestingController: HttpTestingController;
  let loader: HarnessLoader;
  let rootLoader: HarnessLoader;

  const apiId = 'apiId';

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [NoopAnimationsModule, GioHttpTestingModule, MatIconTestingModule, ApiPortalUserGroupModule],
      providers: [
        { provide: UIRouterStateParams, useValue: { apiId } },
        {
          provide: 'Constants',
          useFactory: () => {
            const constants = CONSTANTS_TESTING;
            set(constants, 'env.settings.api.primaryOwnerMode', 'HYBRID');
            return constants;
          },
        },
      ],
    }).overrideProvider(InteractivityChecker, {
      useValue: {
        isFocusable: () => true, // This checks focus trap, set it to true to  avoid the warning
      },
    });

    fixture = TestBed.createComponent(ApiPortalTransferOwnershipComponent);
    httpTestingController = TestBed.inject(HttpTestingController);

    loader = await TestbedHarnessEnvironment.loader(fixture);
    rootLoader = TestbedHarnessEnvironment.documentRootLoader(fixture);

    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
    httpTestingController.verify();
  });

  it('should transfer ownership to user', async () => {
    const api = fakeApi({ id: apiId });
    expectApiGetRequest(api);
    expectGroupsGetRequest([]);
    expectApiRoleGetRequest([
      fakeRole({ name: 'ROLE_1', default: false }),
      fakeRole({ name: 'DEFAULT_ROLE', default: true }),
      fakeRole({ name: 'PRIMARY_OWNER' }),
    ]);
    expectApiMembersGetRequest();

    // Select User mode
    const userOrGroupRadio = await loader.getHarness(MatRadioGroupHarness);
    await userOrGroupRadio.checkRadioButton({ label: 'Other user' });

    // Search and select user
    const userSelect = await loader.getHarness(GioFormUserAutocompleteHarness);
    await userSelect.setSearchText('Joe');
    respondToUserSearchRequest('Joe', [fakeSearchableUser({ displayName: 'Joe' })]);
    await userSelect.selectOption({ text: 'Joe' });
    respondToUserSearchRequest('Joe', [fakeSearchableUser({ displayName: 'Joe' })]);

    // Select role
    const roleSelect = await loader.getHarness(MatSelectHarness.with({ selector: '[formControlName="roleId"]' }));
    await roleSelect.open();
    const roleOptions = await roleSelect.getOptions();
    expect(roleOptions.length).toBe(2);
    // Check that the default role is selected
    expect(await roleSelect.getValueText()).toBe('DEFAULT_ROLE');
    await roleSelect.clickOptions({ text: 'ROLE_1' });

    // Submit
    const transferBtn = await loader.getHarness(MatButtonHarness.with({ text: /Transfer/ }));
    await transferBtn.click();

    // Confirm dialog
    const dialog = await rootLoader.getHarness(MatDialogHarness);
    await (await dialog.getHarness(MatButtonHarness.with({ text: /^Transfer/ }))).click();

    // Check request
    const req = httpTestingController.expectOne({
      url: `${CONSTANTS_TESTING.env.baseURL}/apis/${apiId}/members/transfer_ownership`,
      method: 'POST',
    });
    expect(req.request.body).toEqual({
      id: '1d4fae8c-3705-43ab-8fae-8c370543abf3',
      reference: expect.any(String),
      role: 'ROLE_1',
      type: 'USER',
    });
  });

  it('should transfer ownership to api members', async () => {
    const api = fakeApi({ id: apiId });
    expectApiGetRequest(api);
    expectGroupsGetRequest([]);
    expectApiRoleGetRequest([
      fakeRole({ name: 'ROLE_1', default: false }),
      fakeRole({ name: 'DEFAULT_ROLE', default: true }),
      fakeRole({ name: 'PRIMARY_OWNER' }),
    ]);
    const members: ApiMember[] = [{ id: '1', displayName: 'Joe', role: 'USER' }];
    expectApiMembersGetRequest(members);

    // Select User mode
    const userOrGroupRadio = await loader.getHarness(MatRadioGroupHarness);
    await userOrGroupRadio.checkRadioButton({ label: 'API member' });

    // Search and select user
    const userSelect = await loader.getHarness(MatSelectHarness.with({ selector: '[formControlName="user"]' }));
    await userSelect.clickOptions({ text: 'Joe' });

    // Select role
    const roleSelect = await loader.getHarness(MatSelectHarness.with({ selector: '[formControlName="roleId"]' }));
    await roleSelect.open();
    const roleOptions = await roleSelect.getOptions();
    expect(roleOptions.length).toBe(2);
    // Check that the default role is selected
    expect(await roleSelect.getValueText()).toBe('DEFAULT_ROLE');
    await roleSelect.clickOptions({ text: 'ROLE_1' });

    // Submit
    const transferBtn = await loader.getHarness(MatButtonHarness.with({ text: /Transfer/ }));
    await transferBtn.click();

    // Confirm dialog
    const dialog = await rootLoader.getHarness(MatDialogHarness);
    await (await dialog.getHarness(MatButtonHarness.with({ text: /^Transfer/ }))).click();

    // Check request
    const req = httpTestingController.expectOne({
      url: `${CONSTANTS_TESTING.env.baseURL}/apis/${apiId}/members/transfer_ownership`,
      method: 'POST',
    });
    expect(req.request.body).toEqual({
      id: '1',
      reference: undefined,
      role: 'ROLE_1',
      type: 'USER',
    });
  });

  it('should transfer ownership to group', async () => {
    const api = fakeApi({ id: apiId });
    expectApiGetRequest(api);
    expectGroupsGetRequest([
      fakeGroup({ id: 'group1', name: 'Group 1', apiPrimaryOwner: true }),
      fakeGroup({ id: 'group2', name: 'Group null', apiPrimaryOwner: null }),
    ]);
    expectApiRoleGetRequest([
      fakeRole({ name: 'ROLE_1', default: false }),
      fakeRole({ name: 'DEFAULT_ROLE', default: true }),
      fakeRole({ name: 'PRIMARY_OWNER' }),
    ]);
    expectApiMembersGetRequest();

    // Select Group mode
    const userOrGroupRadio = await loader.getHarness(MatRadioGroupHarness);
    await userOrGroupRadio.checkRadioButton({ label: /Group/ });

    // Select group
    const groupSelect = await loader.getHarness(MatSelectHarness.with({ selector: '[formControlName="groupId"]' }));
    await groupSelect.open();
    const options = await groupSelect.getOptions();
    expect(options.length).toBe(1);
    await groupSelect.clickOptions({ text: 'Group 1' });

    // Not select role -> use default role

    // Submit
    const transferBtn = await loader.getHarness(MatButtonHarness.with({ text: /Transfer/ }));
    await transferBtn.click();

    // Confirm dialog
    const dialog = await rootLoader.getHarness(MatDialogHarness);
    await (await dialog.getHarness(MatButtonHarness.with({ text: /^Transfer/ }))).click();

    // Check request
    const req = httpTestingController.expectOne({
      url: `${CONSTANTS_TESTING.env.baseURL}/apis/${apiId}/members/transfer_ownership`,
      method: 'POST',
    });
    expect(req.request.body).toEqual({
      id: 'group1',
      reference: null,
      role: 'DEFAULT_ROLE',
      type: 'GROUP',
    });
  });

  function expectApiGetRequest(api: Api) {
    httpTestingController.expectOne({ url: `${CONSTANTS_TESTING.env.baseURL}/apis/${apiId}`, method: 'GET' }).flush(api);
    fixture.detectChanges();
  }

  function expectGroupsGetRequest(groups: Group[] = []) {
    httpTestingController.expectOne({ url: `${CONSTANTS_TESTING.env.baseURL}/configuration/groups`, method: 'GET' }).flush(groups);
  }
  function expectApiRoleGetRequest(roles: Role[] = []) {
    httpTestingController
      .expectOne({ url: `${CONSTANTS_TESTING.org.baseURL}/configuration/rolescopes/API/roles`, method: 'GET' })
      .flush(roles);
  }

  function respondToUserSearchRequest(searchTerm: string, searchableUsers: SearchableUser[]) {
    httpTestingController
      .expectOne({
        method: 'GET',
        url: `${CONSTANTS_TESTING.org.baseURL}/search/users?q=${searchTerm}`,
      })
      .flush(searchableUsers);
  }

  function expectApiMembersGetRequest(members: ApiMember[] = []) {
    httpTestingController.expectOne({ url: `${CONSTANTS_TESTING.env.baseURL}/apis/${apiId}/members`, method: 'GET' }).flush(members);
    fixture.detectChanges();
  }
});
