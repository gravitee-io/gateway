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
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { ReactiveFormsModule, Validators, FormControl, FormGroup } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { GioSaveBarModule } from '@gravitee/ui-particles-angular';

@Component({
  selector: 'developer-portal-api',
  templateUrl: './developer-portal-api.component.html',
  styleUrls: ['./developer-portal-api.component.scss'],
  imports: [CommonModule, GioSaveBarModule, MatCardModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule],
  standalone: true,
})
export class DeveloperPortalApiComponent {
  public developerPortalApiForm = new FormGroup({
    name: new FormControl(null, Validators.required),
  });

  reset() {
    this.developerPortalApiForm.reset();
  }

  submit() {
    // console.log("Form submitted 🚀");
  }
}
