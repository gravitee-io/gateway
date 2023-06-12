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
import { ComponentHarness } from '@angular/cdk/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatTableHarness } from '@angular/material/table/testing';
import { HttpTestingController } from '@angular/common/http/testing';
import { MatMenuHarness } from '@angular/material/menu/testing';

import { ApiPlanFormHarness } from '../../../../component/plan/api-plan-form.harness';
import { fakeGroup } from '../../../../../../entities/group/group.fixture';

export class Step4Security1PlansHarness extends ComponentHarness {
  static hostSelector = 'step-4-security-1-plans';

  private readonly matTable = this.locatorFor(MatTableHarness);

  protected getButtonBySelector = (selector: string) => this.locatorFor(MatButtonHarness.with({ selector }))();

  protected getButtonByText = (text: string) =>
    this.locatorFor(
      MatButtonHarness.with({
        text: text,
      }),
    )();

  async getColumnTextByRowIndex(index: number): Promise<{ name: string; security: string }> {
    return this.matTable()
      .then((x) => x.getRows())
      .then((rows) => rows[index])
      .then((row) => row.getCellTextByColumnName())
      .then((cell) => ({ name: cell.name, security: cell.security }));
  }

  async countNumberOfRows(): Promise<number> {
    return this.matTable()
      .then((table) => table.getRows())
      .then((rows) => rows.length);
  }

  async clickRemovePlanButton(): Promise<void> {
    return this.getButtonBySelector('[aria-label="Remove plan"]').then((btn) => btn.click());
  }

  async clickPrevious() {
    return (await this.getButtonByText('Previous')).click();
  }

  async clickValidate() {
    return (await this.getButtonByText('Validate my plans')).click();
  }

  async fillAndValidate(): Promise<void> {
    return this.clickValidate();
  }

  async addApiKeyPlan(planName: string, httpTestingController: HttpTestingController): Promise<void> {
    await this.getButtonBySelector('[aria-label="Add new plan"]').then((b) => b.click());
    const planSecurityDropdown = await this.locatorFor(MatMenuHarness)();
    expect(await planSecurityDropdown.getItems().then((items) => items.length)).toEqual(5);

    await planSecurityDropdown.clickItem({ text: 'API Key' });

    const apiPlanFormHarness = await this.locatorFor(ApiPlanFormHarness)();

    apiPlanFormHarness.httpRequest(httpTestingController).expectGroupLisRequest([fakeGroup({ id: 'group-a', name: 'Group A' })]);
    await apiPlanFormHarness.getNameInput().then((i) => i.setValue(planName));

    apiPlanFormHarness.httpRequest(httpTestingController).expectPlanSchemaGetRequest('api-key', {});

    await (await this.getButtonByText('Next')).click();
    await (await this.getButtonByText('Next')).click();
    await (await this.getButtonByText('Add plan')).click();
  }

  async editDefaultKeylessPlanName(newPlanName: string, httpTestingController: HttpTestingController): Promise<void> {
    // Find DefaultKeyless Row anf get actions cell
    const tableDefaultKeylessActionsCell = await this.matTable()
      .then((t) => t.getRows())
      .then((row) => row[0])
      .then((row) => row.getCells({ columnName: 'actions' }))
      .then((cell) => cell[0]);

    // Click on Edit plan button
    await tableDefaultKeylessActionsCell.getHarness(MatButtonHarness.with({ selector: '[aria-label="Edit plan"]' })).then((b) => b.click());

    const apiPlanFormHarness = await this.locatorFor(ApiPlanFormHarness)();

    apiPlanFormHarness.httpRequest(httpTestingController).expectGroupLisRequest([fakeGroup({ id: 'group-a', name: 'Group A' })]);
    // Change plan name
    await apiPlanFormHarness.getNameInput().then((i) => i.setValue(newPlanName));

    await (await this.getButtonByText('Next')).click();
    await (await this.getButtonByText('Save changes')).click();
  }

  async addRateLimitToPlan(httpTestingController: HttpTestingController): Promise<void> {
    const tableDefaultKeylessActionsCell = await this.matTable()
      .then((t) => t.getRows())
      .then((row) => row[0])
      .then((row) => row.getCells({ columnName: 'actions' }))
      .then((cell) => cell[0]);

    // Click on Edit plan button
    await tableDefaultKeylessActionsCell.getHarness(MatButtonHarness.with({ selector: '[aria-label="Edit plan"]' })).then((b) => b.click());

    const apiPlanFormHarness = await this.locatorFor(ApiPlanFormHarness)();

    apiPlanFormHarness.httpRequest(httpTestingController).expectGroupLisRequest([fakeGroup({ id: 'group-a', name: 'Group A' })]);

    await (await this.getButtonByText('Next')).click();

    const rateLimitToggle = await apiPlanFormHarness.getRateLimitEnabledInput();
    await rateLimitToggle.toggle();

    apiPlanFormHarness.httpRequest(httpTestingController).expectPolicySchemaGetRequest('rate-limit', {});

    await (await this.getButtonByText('Save changes')).click();
  }
}
