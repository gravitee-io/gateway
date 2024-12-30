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
import { SpanHarness } from '@gravitee/ui-particles-angular/testing';

export class AvailabilityHarness extends ComponentHarness {
  static hostSelector = 'global-availability';

  private widget = this.locatorForOptional(SpanHarness.with({ selector: '[data-testid=globalAvailability]' }));

  public async getWidgetValue() {
    return await this.widget().then((value) => value.getText());
  }
}
