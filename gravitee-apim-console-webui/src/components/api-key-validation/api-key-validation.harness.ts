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
import { MatInputHarness } from '@angular/material/input/testing';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';

export class ApiKeyValidationHarness extends ComponentHarness {
  static hostSelector = 'api-key-validation';

  protected getInput = this.locatorFor(MatInputHarness);
  protected getFormField = this.locatorFor(MatFormFieldHarness);

  public async getInputValue(): Promise<any> {
    return this.getInput().then((input) => input.getValue());
  }

  public async setInputValue(value: string): Promise<void> {
    return this.getInput().then((input) => input.setValue(value));
  }

  public async isRequired(): Promise<boolean> {
    return this.getFormField()
      .then((formField) => formField.getControl())
      .then((control) => control.isRequired());
  }

  public async isValid(): Promise<boolean> {
    return this.getFormField().then((formField) => formField.isControlValid());
  }
}
