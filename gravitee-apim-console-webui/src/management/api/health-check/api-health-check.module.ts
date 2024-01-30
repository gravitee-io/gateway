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
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { GioBannerModule, GioFormFocusInvalidModule, GioIconsModule, GioSaveBarModule } from '@gravitee/ui-particles-angular';
import { MatLegacySnackBarModule as MatSnackBarModule } from '@angular/material/legacy-snack-bar';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { RouterModule } from '@angular/router';

import { ApiHealthCheckComponent } from './api-health-check.component';

import { ApiHealthCheckFormModule } from '../component/health-check-form/api-health-check-form.module';

@NgModule({
  declarations: [ApiHealthCheckComponent],
  exports: [ApiHealthCheckComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,

    MatSnackBarModule,
    MatButtonModule,

    GioSaveBarModule,
    ApiHealthCheckFormModule,
    GioIconsModule,
    GioBannerModule,
    GioFormFocusInvalidModule,
  ],
})
export class ApiHealthCheckModule {}
