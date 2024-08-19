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
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { HttpTestingController } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { HarnessLoader } from '@angular/cdk/testing';
import { MatButtonHarness } from '@angular/material/button/testing';

import { SharedPolicyGroupHistoryComponent } from './shared-policy-group-history.component';
import { SharedPolicyGroupHistoryHarness } from './shared-policy-group-history.harness';
import { HistoryJsonDialogHarness } from './history-json-dialog/history-json-dialog.harness';
import { HistoryStudioDialogHarness } from './history-studio-dialog/history-studio-dialog.harness';

import {
  expectGetSharedPolicyGroupRequest,
  expectListSharedPolicyGroupHistoriesRequest,
} from '../../../../../services-ngx/shared-policy-groups.service.spec';
import { GioTestingModule } from '../../../../../shared/testing';
import { fakeSharedPolicyGroup } from '../../../../../entities/management-api-v2';

describe('SharedPolicyGroupHistoryComponent', () => {
  const SHARED_POLICY_GROUP_ID = 'sharedPolicyGroupId';
  let fixture: ComponentFixture<SharedPolicyGroupHistoryComponent>;
  let rootLoader: HarnessLoader;
  let componentHarness: SharedPolicyGroupHistoryHarness;
  let httpTestingController: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule, SharedPolicyGroupHistoryComponent, GioTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { sharedPolicyGroupId: SHARED_POLICY_GROUP_ID } } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SharedPolicyGroupHistoryComponent);
    httpTestingController = TestBed.inject(HttpTestingController);
    rootLoader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    componentHarness = await TestbedHarnessEnvironment.harnessForFixture(fixture, SharedPolicyGroupHistoryHarness);
    expectGetSharedPolicyGroupRequest(
      httpTestingController,
      fakeSharedPolicyGroup({
        id: SHARED_POLICY_GROUP_ID,
      }),
    );
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should display resources table', async () => {
    const table = await componentHarness.getTable();

    expect(await table.getCellTextByIndex()).toStrictEqual([['Loading...']]);
    expectListSharedPolicyGroupHistoriesRequest(httpTestingController, undefined, SHARED_POLICY_GROUP_ID);

    expect(await table.getCellTextByIndex()).toStrictEqual([['1', 'Shared policy group', expect.any(String), '']]);
  });

  it('should refresh the table when filters change', async () => {
    const table = await componentHarness.getTable();
    const getTableWrapper = await componentHarness.getTableWrapper();

    expect(await table.getCellTextByIndex()).toStrictEqual([['Loading...']]);
    expectListSharedPolicyGroupHistoriesRequest(httpTestingController, undefined, SHARED_POLICY_GROUP_ID);

    await (await getTableWrapper.getPaginator()).setPageSize(50);
    expect(await table.getCellTextByIndex()).toStrictEqual([['Loading...']]);

    expectListSharedPolicyGroupHistoriesRequest(httpTestingController, undefined, SHARED_POLICY_GROUP_ID, '?page=1&perPage=50');

    expect(await table.getCellTextByIndex()).toStrictEqual([['1', 'Shared policy group', expect.any(String), '']]);
  });

  it('should display the JSON dialog', async () => {
    expectListSharedPolicyGroupHistoriesRequest(httpTestingController, undefined, SHARED_POLICY_GROUP_ID);

    const table = await componentHarness.getTable();
    await table
      .getRows()
      .then((rows) =>
        rows[0]
          .getCells({ columnName: 'actions' })
          .then((cells) => cells[0].getHarness(MatButtonHarness.with({ text: 'code' })).then((button) => button.click())),
      );
    const dialog = await rootLoader.getHarness(HistoryJsonDialogHarness);

    expect(dialog).toBeTruthy();
  });

  it('should display the details dialog', async () => {
    expectListSharedPolicyGroupHistoriesRequest(httpTestingController, undefined, SHARED_POLICY_GROUP_ID);

    const table = await componentHarness.getTable();
    await table
      .getRows()
      .then((rows) =>
        rows[0]
          .getCells({ columnName: 'actions' })
          .then((cells) => cells[0].getHarness(MatButtonHarness.with({ text: 'eye' })).then((button) => button.click())),
      );
    const dialog = await rootLoader.getHarness(HistoryStudioDialogHarness);

    expect(await dialog.getTitleText()).toEqual('Version 1 details');
  });
});
