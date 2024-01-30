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
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { MatLegacyTooltipModule as MatTooltipModule } from '@angular/material/legacy-tooltip';
import { GioAvatarModule, GioBannerModule, GioIconsModule, GioLoaderModule } from '@gravitee/ui-particles-angular';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { MatLegacySelectModule as MatSelectModule } from '@angular/material/legacy-select';
import { ReactiveFormsModule } from '@angular/forms';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { Route, RouterModule } from '@angular/router';

import { HomeApiHealthCheckComponent } from './home-api-health-check/home-api-health-check.component';
import { HomeLayoutComponent } from './home-layout/home-layout.component';
import { HomeOverviewComponent } from './home-overview/home-overview.component';
import { HealthAvailabilityTimeFrameModule } from './home-api-health-check/health-availability-time-frame/health-availability-time-frame.module';
import { GioQuickTimeRangeModule } from './components/gio-quick-time-range/gio-quick-time-range.module';
import { GioRequestStatsModule } from './components/gio-request-stats/gio-request-stats.module';
import { GioTopApisTableModule } from './components/gio-top-apis-table/gio-top-apis-table.module';
import { GioApiResponseStatusModule } from './components/gio-api-response-status/gio-api-response-status.module';
import { GioApiStateModule } from './components/gio-api-state/gio-api-state.module';
import { GioApiLifecycleStateModule } from './components/gio-api-lifecycle-state/gio-api-lifecycle-state.module';
import { GioApiEventsTableModule } from './components/gio-api-events-table/gio-api-events-table.module';

import { GioCircularPercentageModule } from '../../shared/components/gio-circular-percentage/gio-circular-percentage.module';
import { GioTableWrapperModule } from '../../shared/components/gio-table-wrapper/gio-table-wrapper.module';
import { TasksComponent } from '../../user/tasks/tasks.component';

const homeRoutes: Route[] = [
  {
    path: '',
    component: HomeLayoutComponent,
    children: [
      {
        path: 'overview',
        component: HomeOverviewComponent,
      },
      {
        path: 'apiHealthCheck',
        component: HomeApiHealthCheckComponent,
      },
      {
        path: 'tasks',
        component: TasksComponent,
      },
      {
        path: '',
        redirectTo: 'overview',
        pathMatch: 'full',
      },
    ],
  },
];

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(homeRoutes),

    MatTabsModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatSelectModule,
    MatCardModule,

    GioAvatarModule,
    GioTableWrapperModule,
    GioCircularPercentageModule,
    GioBannerModule,
    GioIconsModule,
    GioLoaderModule,
    GioRequestStatsModule,
    HealthAvailabilityTimeFrameModule,
    GioQuickTimeRangeModule,
    GioTopApisTableModule,
    GioApiResponseStatusModule,
    GioApiStateModule,
    GioApiLifecycleStateModule,
    GioApiEventsTableModule,
  ],
  declarations: [HomeLayoutComponent, HomeOverviewComponent, HomeApiHealthCheckComponent],
})
export class HomeModule {}
