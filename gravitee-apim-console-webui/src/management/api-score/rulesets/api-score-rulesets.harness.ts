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
import { MatExpansionPanelHarness } from '@angular/material/expansion/testing';
import { MatCardHarness } from '@angular/material/card/testing';

export class ApiScoreRulesetsHarness extends ComponentHarness {
  static hostSelector = 'app-api-score-rulesets';

  getMatExpansionPanelHarness = this.locatorForOptional(MatExpansionPanelHarness);

  getMatCardHarness = this.locatorForOptional(MatCardHarness);

  // gio-card-empty-state // rulesets-empty
  getRulesetsEmpty = this.locatorForOptional('.rulesets-empty');
}
