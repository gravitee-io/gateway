/*
 * Copyright (C) 2024 The Gravitee team (http://gravitee.io)
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
import { BaseHarnessFilters, ContentContainerComponentHarness, HarnessPredicate } from '@angular/cdk/testing';

import { RadioCardHarness } from '../../radio-card/radio-card.harness';

export class PlanCardHarness extends ContentContainerComponentHarness {
  public static hostSelector = 'app-plan-card';
  protected locateRadioCard = this.locatorFor(RadioCardHarness);

  public static with(options: BaseHarnessFilters): HarnessPredicate<PlanCardHarness> {
    return new HarnessPredicate(PlanCardHarness, options);
  }

  public async isSelected(): Promise<boolean> {
    return await this.locateRadioCard().then(radioCard => radioCard.isSelected());
  }

  public async select(): Promise<void> {
    return await this.locateRadioCard().then(radioCard => radioCard.select());
  }

  public async isDisabled(): Promise<boolean> {
    return await this.locateRadioCard().then(radioCard => radioCard.isDisabled());
  }
}
