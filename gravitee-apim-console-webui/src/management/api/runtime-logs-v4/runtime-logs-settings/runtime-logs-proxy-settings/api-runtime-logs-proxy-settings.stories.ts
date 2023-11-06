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
import { Meta, moduleMetadata } from '@storybook/angular';
import { Story } from '@storybook/angular/dist/ts3.9/client/preview/types-7-0';
import { of } from 'rxjs';

import { ApiRuntimeLogsProxySettingsModule } from './api-runtime-logs-proxy-settings.module';
import { ApiRuntimeLogsProxySettingsComponent } from './api-runtime-logs-proxy-settings.component';

import { UIRouterStateParams } from '../../../../../ajs-upgraded-providers';
import { ApiV2Service } from '../../../../../services-ngx/api-v2.service';
import { ApiV4, fakeProxyApiV4 } from '../../../../../entities/management-api-v2';

const api = fakeProxyApiV4();

export default {
  title: 'API / Logs / Settings / Proxy',
  component: ApiRuntimeLogsProxySettingsComponent,
  decorators: [
    moduleMetadata({
      imports: [ApiRuntimeLogsProxySettingsModule],
      providers: [
        { provide: UIRouterStateParams, useValue: { apiId: api.id } },
        {
          provide: ApiV2Service,
          useValue: {
            get: (_: string) => {
              return of(api);
            },
            update: (_: ApiV4) => {
              return of(api);
            },
          },
        },
      ],
    }),
  ],
  argTypes: {},
  render: (args) => ({
    template: `
      <div style="width: 870px">
        <api-runtime-logs-proxy-settings></api-runtime-logs-proxy-settings>
      </div>
    `,
    props: args,
  }),
} as Meta;

export const Default: Story = {};
Default.args = {};
