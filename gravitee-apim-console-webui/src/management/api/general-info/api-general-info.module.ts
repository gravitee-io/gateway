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
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { MatLegacySnackBarModule as MatSnackBarModule } from '@angular/material/legacy-snack-bar';
import {
  GioAvatarModule,
  GioBannerModule,
  GioFormFilePickerModule,
  GioFormTagsInputModule,
  GioSaveBarModule,
  GioFormSlideToggleModule,
  GioFormFocusInvalidModule,
  GioClipboardModule,
} from '@gravitee/ui-particles-angular';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacySelectModule as MatSelectModule } from '@angular/material/legacy-select';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyDialogModule } from '@angular/material/legacy-dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { MatLegacySlideToggleModule as MatSlideToggleModule } from '@angular/material/legacy-slide-toggle';
import { MatLegacyCheckboxModule as MatCheckboxModule } from '@angular/material/legacy-checkbox';
import { MatLegacyTooltipModule as MatTooltipModule } from '@angular/material/legacy-tooltip';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { A11yModule } from '@angular/cdk/a11y';
import { MatLegacyProgressBarModule as MatProgressBarModule } from '@angular/material/legacy-progress-bar';

import { ApiGeneralInfoComponent } from './api-general-info.component';
import { ApiGeneralInfoQualityComponent } from './api-general-info-quality/api-general-info-quality.component';
import { ApiGeneralInfoDangerZoneComponent } from './api-general-info-danger-zone/api-general-info-danger-zone.component';
import { ApiGeneralInfoDuplicateDialogComponent } from './api-general-info-duplicate-dialog/api-general-info-duplicate-dialog.component';
import { ApiGeneralInfoExportDialogComponent } from './api-general-info-export-dialog/api-general-info-export-dialog.component';
import { ApiGeneralInfoPromoteDialogComponent } from './api-general-info-promote-dialog/api-general-info-promote-dialog.component';

import { GioPermissionModule } from '../../../shared/components/gio-permission/gio-permission.module';
import { GioCircularPercentageModule } from '../../../shared/components/gio-circular-percentage/gio-circular-percentage.module';
import { GioApiImportDialogModule } from '../../../shared/components/gio-api-import-dialog/gio-api-import-dialog.module';
import { GioLicenseBannerModule } from '../../../shared/components/gio-license-banner/gio-license-banner.module';

@NgModule({
  declarations: [
    ApiGeneralInfoComponent,
    ApiGeneralInfoQualityComponent,
    ApiGeneralInfoDangerZoneComponent,
    ApiGeneralInfoDuplicateDialogComponent,
    ApiGeneralInfoExportDialogComponent,
    ApiGeneralInfoPromoteDialogComponent,
  ],
  exports: [ApiGeneralInfoComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,

    A11yModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    MatIconModule,
    MatSelectModule,
    MatButtonModule,
    MatDialogModule,
    MatLegacyDialogModule,
    MatSlideToggleModule,
    MatCheckboxModule,
    MatTooltipModule,
    MatTabsModule,
    MatProgressBarModule,

    GioFormFocusInvalidModule,
    GioAvatarModule,
    GioFormFilePickerModule,
    GioSaveBarModule,
    GioFormTagsInputModule,
    GioClipboardModule,
    GioPermissionModule,
    GioFormSlideToggleModule,
    GioCircularPercentageModule,
    GioApiImportDialogModule,
    GioBannerModule,
    GioLicenseBannerModule,
  ],
})
export class ApiGeneralInfoModule {}
