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
import {
  GioAvatarModule,
  GioBannerModule,
  GioConfirmDialogModule,
  GioIconsModule,
  GioSaveBarModule,
  GioFormSlideToggleModule,
} from '@gravitee/ui-particles-angular';
import { MatLegacySelectModule as MatSelectModule } from '@angular/material/legacy-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatLegacySnackBarModule as MatSnackBarModule } from '@angular/material/legacy-snack-bar';
import { MatLegacyCheckboxModule as MatCheckboxModule } from '@angular/material/legacy-checkbox';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { MatLegacyDialogModule as MatDialogModule } from '@angular/material/legacy-dialog';
import { MatLegacyRadioModule as MatRadioModule } from '@angular/material/legacy-radio';
import { MatLegacySlideToggleModule as MatSlideToggleModule } from '@angular/material/legacy-slide-toggle';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { RouterModule } from '@angular/router';

import { ApplicationGeneralGroupsComponent } from './groups/application-general-groups.component';
import { ApplicationGeneralMembersComponent } from './members/application-general-members.component';
import { ApplicationGeneralTransferOwnershipComponent } from './transfer-ownership/application-general-transfer-ownership.component';

import { GioPermissionModule } from '../../../../shared/components/gio-permission/gio-permission.module';
import { GioUsersSelectorModule } from '../../../../shared/components/gio-users-selector/gio-users-selector.module';
import { GioFormUserAutocompleteModule } from '../../../../shared/components/gio-user-autocomplete/gio-form-user-autocomplete.module';
import { GioTableWrapperModule } from '../../../../shared/components/gio-table-wrapper/gio-table-wrapper.module';
import { ApiGeneralUserGroupModule } from '../../../api/general/user-group-access/api-general-user-group.module';

@NgModule({
  declarations: [ApplicationGeneralGroupsComponent, ApplicationGeneralMembersComponent, ApplicationGeneralTransferOwnershipComponent],
  exports: [ApplicationGeneralGroupsComponent, ApplicationGeneralMembersComponent, ApplicationGeneralTransferOwnershipComponent],
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,

    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule,
    MatDialogModule,
    MatSlideToggleModule,
    MatRadioModule,
    MatButtonToggleModule,

    GioAvatarModule,
    GioIconsModule,
    GioPermissionModule,
    GioSaveBarModule,
    GioConfirmDialogModule,
    GioFormUserAutocompleteModule,
    GioBannerModule,
    GioFormSlideToggleModule,
    GioUsersSelectorModule,
    GioTableWrapperModule,
    ApiGeneralUserGroupModule,
    FormsModule,
  ],
})
export class ApplicationGeneralUserGroupModule {}
