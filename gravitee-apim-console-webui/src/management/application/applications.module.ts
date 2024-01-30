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
import { FormsModule } from '@angular/forms';
import { GioAvatarModule, GioIconsModule, GioConfirmDialogModule } from '@gravitee/ui-particles-angular';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyDialogModule as MatDialogModule } from '@angular/material/legacy-dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { MatLegacyPaginatorModule as MatPaginatorModule } from '@angular/material/legacy-paginator';
import { MatLegacyRadioModule as MatRadioModule } from '@angular/material/legacy-radio';
import { MatLegacySnackBarModule as MatSnackBarModule } from '@angular/material/legacy-snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { MatLegacyTooltipModule as MatTooltipModule } from '@angular/material/legacy-tooltip';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { EnvApplicationListComponent } from './list/env-application-list.component';
import { ApplicationNavigationModule } from './application-navigation/application-navigation.module';
import { ApplicationNotificationSettingsModule } from './details/notifications/notification-settings/application-notification-settings.module';
import { ApplicationMetadataModule } from './details/metadata/application-metadata.module';
import { ApplicationGeneralUserGroupModule } from './details/user-group-access/application-general-user-group.module';
import { ApplicationGeneralNgModule } from './details/general/general-ng/application-general-ng.module';
import { ApplicationGeneralComponent } from './details/general/application-general.component';
import { ApplicationSubscriptionsComponent } from './details/subscriptions/application-subscriptions.component';
import { ApplicationSubscriptionComponent } from './details/subscriptions/application-subscription.component';
import { ApplicationAnalyticsComponent } from './details/analytics/application-analytics.component';
import { ApplicationLogsComponent } from './details/logs/application-logs.component';
import { ApplicationLogComponent } from './details/logs/application-log.component';
import { ApplicationCreationComponent } from './creation/steps/application-creation.component';
import { ApplicationSubscribeComponent } from './details/subscribe/application-subscribe.component';
import { ApplicationMembersComponent } from './details/members/application-members.component';

import { GioTableWrapperModule } from '../../shared/components/gio-table-wrapper/gio-table-wrapper.module';
import { GioRoleModule } from '../../shared/components/gio-role/gio-role.module';
import { GioPermissionModule } from '../../shared/components/gio-permission/gio-permission.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    GioAvatarModule,
    GioConfirmDialogModule,
    GioIconsModule,
    GioPermissionModule,
    GioRoleModule,
    GioTableWrapperModule,
    MatButtonModule,
    MatDialogModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatRadioModule,
    MatSnackBarModule,
    MatSortModule,
    MatTableModule,
    MatTooltipModule,
    ApplicationNavigationModule,
    ApplicationNotificationSettingsModule,
    ApplicationMetadataModule,
    ApplicationGeneralUserGroupModule,
    ApplicationGeneralNgModule,
  ],
  declarations: [
    EnvApplicationListComponent,
    ApplicationGeneralComponent,
    ApplicationSubscriptionsComponent,
    ApplicationSubscriptionComponent,
    ApplicationAnalyticsComponent,
    ApplicationLogsComponent,
    ApplicationLogComponent,
    ApplicationCreationComponent,
    ApplicationSubscribeComponent,
    ApplicationMembersComponent,
  ],
  exports: [EnvApplicationListComponent],
  providers: [],
})
export class ApplicationsModule {}
