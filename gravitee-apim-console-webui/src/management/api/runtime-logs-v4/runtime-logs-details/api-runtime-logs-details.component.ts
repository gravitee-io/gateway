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
import { Component, Inject } from '@angular/core';
import { StateParams } from '@uirouter/core';

import { UIRouterStateParams } from '../../../../ajs-upgraded-providers';
import { ApiV2Service } from '../../../../services-ngx/api-v2.service';

@Component({
  selector: 'api-runtime-logs-details',
  template: require('./api-runtime-logs-details.component.html'),
})
export class ApiRuntimeLogsDetailsComponent {
  api$ = this.apiService.get(this.ajsStateParams.apiId);

  constructor(@Inject(UIRouterStateParams) private readonly ajsStateParams: StateParams, private readonly apiService: ApiV2Service) {}
}
