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

import { GioConnectorSelectionListHarness } from '../../../../../shared/components/gio-connector-list-option/gio-connector-selection-list.harness';

export class Step2Entrypoints1ListHarness extends ComponentHarness {
  static hostSelector = 'step-2-entrypoints-1-list';

  private readonly selectionList = this.locatorFor(GioConnectorSelectionListHarness);

  protected getButtonByText = (text: string) =>
    this.locatorFor(
      MatButtonHarness.with({
        text: text,
      }),
    )();

  async getEntrypoints(): Promise<GioConnectorSelectionListHarness> {
    return this.selectionList();
  }

  async clickValidate(): Promise<void> {
    return this.getButtonByText('Select my entrypoints').then((button) => button.click());
  }

  async clickPrevious(): Promise<void> {
    return this.getButtonByText('Previous').then((button) => button.click());
  }

  async fillAndValidate(entrypoints: string[]): Promise<void> {
    await this.getEntrypoints().then((f) => f.selectOptionsByIds(entrypoints));
    return this.clickValidate();
  }
}
