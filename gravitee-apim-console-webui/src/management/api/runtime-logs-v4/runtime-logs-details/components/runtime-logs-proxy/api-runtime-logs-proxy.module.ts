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
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { UIRouterModule } from '@uirouter/angular';

import { ApiRuntimeLogsProxyComponent } from './api-runtime-logs-proxy.component';

import { ApiRuntimeLogsConnectionLogDetailsModule, ApiRuntimeLogsDetailsEmptyStateModule } from '../components';

@NgModule({
  declarations: [ApiRuntimeLogsProxyComponent],
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    UIRouterModule,

    ApiRuntimeLogsConnectionLogDetailsModule,
    ApiRuntimeLogsDetailsEmptyStateModule,
  ],
  exports: [ApiRuntimeLogsProxyComponent],
})
export class ApiRuntimeLogsProxyModule {}
