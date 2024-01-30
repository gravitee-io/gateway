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
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { ReactiveFormsModule } from '@angular/forms';
import { MatLegacySnackBarModule as MatSnackBarModule } from '@angular/material/legacy-snack-bar';
import { GioSaveBarModule, GioConfirmDialogModule, GioIconsModule, GioLicenseModule } from '@gravitee/ui-particles-angular';
import { RouterModule } from '@angular/router';

import { GioPolicyStudioLayoutComponent } from './gio-policy-studio-layout.component';
import { PolicyStudioDesignModule } from './design/policy-studio-design.module';
import { PolicyStudioDebugModule } from './debug/policy-studio-debug.module';
import { PolicyStudioConfigModule } from './config/policy-studio-config.module';

import { GioPermissionModule } from '../../../shared/components/gio-permission/gio-permission.module';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,

    MatTabsModule,
    MatSnackBarModule,

    GioSaveBarModule,
    GioPermissionModule,
    GioConfirmDialogModule,

    PolicyStudioDesignModule,
    PolicyStudioConfigModule,
    PolicyStudioDebugModule,
    GioLicenseModule,
    GioIconsModule,
  ],
  declarations: [GioPolicyStudioLayoutComponent],
})
export class GioPolicyStudioRoutingModule {}
