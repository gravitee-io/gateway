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
import { GioPolicyGroupStudioHarness } from '@gravitee/ui-policy-studio-angular/testing';
import { MatButtonHarness } from '@angular/material/button/testing';

export class SharedPolicyGroupsStudioHarness extends ComponentHarness {
  static readonly hostSelector = 'shared-policy-groups-studio';

  public getEditButton = this.locatorFor(MatButtonHarness.with({ text: /edit/ }));

  public getSaveButton = this.locatorFor(MatButtonHarness.with({ text: /Save/ }));

  public getPolicyGroupStudio = this.locatorFor(GioPolicyGroupStudioHarness);

  public async clickEditButton() {
    const editButton = await this.getEditButton();
    await editButton.click();
  }

  public async save() {
    const saveButton = await this.getSaveButton();
    await saveButton.click();
  }
}
