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

import { ApiRuntimeLogsListHarness } from './components';

export class ApiRuntimeLogsHarness extends ComponentHarness {
  static hostSelector = 'api-runtime-logs';

  public listHarness = this.locatorFor(ApiRuntimeLogsListHarness);

  async isEmptyPanelDisplayed(): Promise<boolean> {
    return this.listHarness().then((list) => list.isEmptyPanelDisplayed());
  }

  async isImpactBannerDisplayed(): Promise<boolean> {
    return this.listHarness().then((list) => list.isImpactBannerDisplayed());
  }

  async clickOpenSettings(): Promise<void> {
    return this.listHarness().then((list) => list.clickOpenSettings());
  }

  async getRows() {
    return this.listHarness().then((list) => list.rows());
  }

  async getPaginator() {
    return this.listHarness().then((list) => list.paginator());
  }
}
