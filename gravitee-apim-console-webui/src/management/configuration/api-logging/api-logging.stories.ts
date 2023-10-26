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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { ApiLoggingComponent } from './api-logging.component';
import { ApiLoggingModule } from './api-logging.module';

import { ConsoleSettingsService } from '../../../services-ngx/console-settings.service';
import { User } from '../../../entities/user';

const currentUser = new User();
currentUser.userPermissions = ['organization-settings-r'];

export default {
  title: 'Environment / Settings / API Logging ',
  component: ApiLoggingComponent,
  decorators: [
    moduleMetadata({
      imports: [ApiLoggingModule, BrowserAnimationsModule],
      providers: [
        {
          provide: ConsoleSettingsService,
          useValue: {
            get: () =>
              of({
                metadata: {
                  readonly: ['logging.messageSampling.count.default'],
                },
                logging: {
                  maxDurationMillis: 10,
                  audit: {
                    enabled: true,
                    trail: {
                      enabled: false,
                    },
                  },
                  user: {
                    displayed: true,
                  },
                  messageSampling: {
                    probabilistic: {
                      limit: 0.52,
                      default: 0.33322,
                    },
                    count: {
                      limit: 40,
                      default: 666,
                    },
                    temporal: {
                      limit: 'PT10S',
                      default: 'PT10S',
                    },
                  },
                },
              }),
          },
        },
      ],
    }),
  ],
  argTypes: {},
  render: (args) => ({
    template: `
      <div style="width: 870px">
        <api-logging></api-logging>
      </div>
    `,
    props: args,
  }),
} as Meta;

export const Default: Story = {};
Default.args = {};
