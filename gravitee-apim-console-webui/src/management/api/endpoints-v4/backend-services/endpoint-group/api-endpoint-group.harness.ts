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
import { MatTabHarness } from '@angular/material/tabs/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { GioSaveBarHarness } from '@gravitee/ui-particles-angular';
import {MatSelectHarness} from "@angular/material/select/testing";

export class ApiEndpointGroupHarness extends ComponentHarness {
  static hostSelector = 'api-endpoint-group';

  private readInputValue = (input: MatInputHarness) => input.getValue();
  private setInputValue = (inputValue: string) => (input: MatInputHarness) => input.setValue(inputValue);

  private setSelectorValue = (selectorValue: string) => (input: MatSelectHarness) => input.clickOptions({text: selectorValue });

  private clickButton = (button: MatButtonHarness) => button.click();

  private clickSaveButton = (saveButton: GioSaveBarHarness) => saveButton.clickSubmit();

  private clickResetButton = (saveButton: GioSaveBarHarness) => saveButton.clickReset();

  private selectTab = (tab: MatTabHarness) => tab.select();

  private readSelectorValue = (selector: MatSelectHarness) => selector.getValueText();

  private isEndpointGroupSubmitButtonInvalid = (gioSaveBar: GioSaveBarHarness) => gioSaveBar.isSubmitButtonInvalid();

  private getBackButton = this.locatorFor(MatButtonHarness.with({ selector: '[mattooltip="Go back"]' }));
  private getGeneralTab = this.locatorFor(MatTabHarness.with({ label: 'General' }));
  private getEndpointGroupNameInput = this.locatorFor(MatInputHarness.with({ selector: '[aria-label="Endpoint group name input"]' }));
  private getEndpointGroupSubmissionBar = this.locatorFor(GioSaveBarHarness);
  private getEndpointGroupLoadBalancerSelector = this.locatorFor(MatSelectHarness.with({ selector: '[aria-label="Load balancing algorithm"]' }));

  public async clickBackButton() {
    return this.getBackButton().then(this.clickButton);
  }

  public async clickGeneralTab() {
    return this.getGeneralTab().then(this.selectTab);
  }

  public async readEndpointGroupNameInput() {
    return this.getEndpointGroupNameInput().then(this.readInputValue);
  }

  public writeToEndpointGroupNameInput(inputValue) {
    return this.getEndpointGroupNameInput().then(this.setInputValue(inputValue));
  }

  public async readEndpointGroupLoadBalancerSelector() {
    return this.getEndpointGroupLoadBalancerSelector().then(this.readSelectorValue)
  }

  public async writeToEndpointGroupLoadBalancerSelector(selectorValue) {
    return this.getEndpointGroupLoadBalancerSelector().then(this.setSelectorValue(selectorValue));
  }

  public isGeneralTabSaveButtonInvalid() {
    return this.getEndpointGroupSubmissionBar().then(this.isEndpointGroupSubmitButtonInvalid);
  }

  public clickEndpointGroupSaveButton() {
    return this.getEndpointGroupSubmissionBar().then(this.clickSaveButton);
  }

  public clickEndpointGroupDismissButton() {
    return this.getEndpointGroupSubmissionBar().then(this.clickResetButton);
  }
}
