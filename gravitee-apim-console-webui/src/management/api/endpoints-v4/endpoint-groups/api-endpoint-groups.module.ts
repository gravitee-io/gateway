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

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { MatLegacyTooltipModule as MatTooltipModule } from '@angular/material/legacy-tooltip';
import { GioBannerModule, GioIconsModule } from '@gravitee/ui-particles-angular';
import { MatDialogModule } from '@angular/material/dialog';
import { MatLegacySnackBarModule as MatSnackBarModule } from '@angular/material/legacy-snack-bar';
import { RouterModule } from '@angular/router';

import { ApiEndpointGroupsComponent } from './api-endpoint-groups.component';

import { ApiEndpointModule } from '../endpoint/api-endpoint.module';
import { GioPermissionModule } from '../../../../shared/components/gio-permission/gio-permission.module';
import { GioLicenseBannerModule } from '../../../../shared/components/gio-license-banner/gio-license-banner.module';

@NgModule({
  declarations: [ApiEndpointGroupsComponent],
  exports: [ApiEndpointGroupsComponent],
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatDialogModule,
    MatTableModule,
    MatTooltipModule,
    MatSnackBarModule,
    GioIconsModule,

    ApiEndpointModule,
    GioPermissionModule,
    GioBannerModule,
    GioLicenseBannerModule,
  ],
})
export class ApiEndpointGroupsModule {}
