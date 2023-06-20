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
import { ComponentHarness, HarnessLoader } from '@angular/cdk/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatTableHarness } from '@angular/material/table/testing';
import { MatDialogHarness } from '@angular/material/dialog/testing';

export class ApiProxyEndpointListHarness extends ComponentHarness {
  static hostSelector = 'api-proxy-endpoint-list';

  private getAddEndpointGroupButton = this.locatorFor(MatButtonHarness.with({ text: /Add new endpoint group/ }));
  private getEditEndpointGroupButton = this.locatorFor(MatButtonHarness.with({ selector: '[mattooltip="Edit group"]' }));
  private getDeleteEndpointGroupButton = this.locatorFor(MatButtonHarness.with({ selector: '[aria-label="Delete group"]' }));
  private getDeleteEndpointButtons = this.locatorForAll(MatButtonHarness.with({ selector: '[aria-label="Delete endpoint"]' }));

  public async getTableRows(index: number) {
    const table = this.locatorFor(MatTableHarness.with({ selector: `#endpointGroupsTable-${index}` }));
    return await table().then((t) => t.getCellTextByIndex());
  }

  public async addEndpointGroup() {
    const button = await this.getAddEndpointGroupButton();
    return button.click();
  }

  public async editEndpointGroup() {
    const button = await this.getEditEndpointGroupButton();
    return await button.click();
  }

  public async deleteEndpointGroup(rootLoader: HarnessLoader) {
    const button = await this.getDeleteEndpointGroupButton();
    await button.click();
    return await rootLoader
      .getHarness(MatDialogHarness)
      .then((dialog) => dialog.getHarness(MatButtonHarness.with({ text: /Delete/ })))
      .then((element) => element.click());
  }

  public async deleteEndpoint(index: number, rootLoader: HarnessLoader) {
    const buttons = await this.getDeleteEndpointButtons();
    await buttons[index].click();
    return await rootLoader
      .getHarness(MatDialogHarness)
      .then((dialog) => dialog.getHarness(MatButtonHarness.with({ text: /Delete/ })))
      .then((element) => element.click());
  }
}
