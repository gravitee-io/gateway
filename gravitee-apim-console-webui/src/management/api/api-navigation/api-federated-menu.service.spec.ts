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

import { TestBed } from '@angular/core/testing';
import { LICENSE_CONFIGURATION_TESTING } from '@gravitee/ui-particles-angular';

import { ApiFederatedMenuService } from './api-federated-menu.service';

import { GioTestingModule } from '../../../shared/testing';
import { GioTestingPermission, GioTestingPermissionProvider } from '../../../shared/components/gio-permission/gio-permission.service';
import { EnvSettings } from '../../../entities/Constants';
import { fakeApiFederated } from '../../../entities/management-api-v2';
import { EnvironmentSettingsService } from '../../../services-ngx/environment-settings.service';

const DEFAULT_PERMISSIONS: GioTestingPermission = ['api-member-r', 'api-audit-r', 'api-documentation-r', 'api-metadata-r', 'api-plan-r'];
const DEFAULT_ENV_SETTINGS: Partial<EnvSettings> = { apiScore: { enabled: true } };

describe('ApiFederatedMenuService', () => {
  const init = async (
    { permissions, envSettings } = {
      permissions: DEFAULT_PERMISSIONS,
      envSettings: DEFAULT_ENV_SETTINGS,
    },
  ) => {
    await TestBed.configureTestingModule({
      imports: [GioTestingModule],
      providers: [
        { provide: ApiFederatedMenuService },
        { provide: EnvironmentSettingsService, useValue: { getSnapshot: () => envSettings } },
        {
          provide: GioTestingPermissionProvider,
          useValue: permissions,
        },
        { provide: 'LicenseConfiguration', useValue: LICENSE_CONFIGURATION_TESTING },
      ],
    }).compileComponents();
  };

  describe('getMenu', () => {
    it('should return items for created Federated API with expected permission and license', async () => {
      await init();
      const service = TestBed.inject(ApiFederatedMenuService);

      expect(service.getMenu(fakeApiFederated({ lifecycleState: 'CREATED' }))).toEqual({
        subMenuItems: [
          {
            displayName: 'Configuration',
            header: {
              subtitle: 'Manage general settings, user permissions, properties, and resources, and track changes to your API',
              title: 'Configuration',
            },
            icon: 'settings',
            routerLink: '',
            tabs: [
              {
                displayName: 'General',
                routerLink: '.',
                routerLinkActiveOptions: {
                  exact: true,
                },
              },
              {
                displayName: 'User Permissions',
                routerLink: 'members',
              },
              {
                displayName: 'Audit Logs',
                routerLink: 'v4/audit',
                license: {
                  context: 'api',
                  feature: 'apim-audit-trail',
                },
                iconRight$: expect.anything(),
              },
            ],
          },
          {
            displayName: 'API Score',
            icon: 'shield-check',
            routerLink: 'api-score',
            header: {
              title: 'API Score',
            },
          },
          {
            displayName: 'Consumers',
            header: {
              subtitle: 'Manage how your API is consumed',
              title: 'Consumers',
            },
            icon: 'cloud-consumers',
            routerLink: '',
            tabs: [
              {
                displayName: 'Plans',
                routerLink: 'plans',
              },
              {
                displayName: 'Broadcasts',
                routerLink: 'messages',
              },
            ],
          },
          {
            displayName: 'Documentation',
            header: {
              title: 'Documentation',
              subtitle: 'Documentation pages appear in the Developer Portal and inform API consumers how to use your API',
              action: {
                disabled: true,
                disabledTooltip: "Activate the Developer Portal by publishing your API under 'General > Info'",
                targetUrl: undefined,
                text: 'Open API in Developer Portal',
              },
            },
            icon: 'book',
            routerLink: '',
            tabs: [
              {
                displayName: 'Main Pages',
                routerLink: 'v4/documentation/main-pages',
                routerLinkActiveOptions: { exact: false },
              },
              {
                displayName: 'Documentation Pages',
                routerLink: 'v4/documentation/pages',
                routerLinkActiveOptions: { exact: false },
              },
              {
                displayName: 'Metadata',
                routerLink: 'v4/documentation/metadata',
                routerLinkActiveOptions: { exact: true },
              },
            ],
          },
        ],
        groupItems: [],
      });
    });

    it('should hide elements for published API when not enough permissions', async () => {
      await init({ permissions: [], envSettings: DEFAULT_ENV_SETTINGS });
      const service = TestBed.inject(ApiFederatedMenuService);

      expect(service.getMenu(fakeApiFederated({ lifecycleState: 'PUBLISHED' }))).toEqual({
        subMenuItems: [
          {
            displayName: 'Configuration',
            header: {
              subtitle: 'Manage general settings, user permissions, properties, and resources, and track changes to your API',
              title: 'Configuration',
            },
            icon: 'settings',
            routerLink: '',
            tabs: [
              {
                displayName: 'General',
                routerLink: '.',
                routerLinkActiveOptions: {
                  exact: true,
                },
              },
            ],
          },
          {
            displayName: 'API Score',
            icon: 'shield-check',
            routerLink: 'api-score',
            header: {
              title: 'API Score',
            },
          },
          {
            displayName: 'Consumers',
            header: {
              subtitle: 'Manage how your API is consumed',
              title: 'Consumers',
            },
            icon: 'cloud-consumers',
            routerLink: '',
            tabs: [
              {
                displayName: 'Broadcasts',
                routerLink: 'messages',
              },
            ],
          },
          {
            displayName: 'Documentation',
            header: {
              subtitle: 'Documentation pages appear in the Developer Portal and inform API consumers how to use your API',
              title: 'Documentation',
              action: {
                disabled: false,
                disabledTooltip: undefined,
                targetUrl: undefined,
                text: 'Open API in Developer Portal',
              },
            },
            icon: 'book',
            routerLink: '',
            tabs: [],
          },
        ],
        groupItems: [],
      });
    });

    it('should hide scoring elements when feature is disabled', async () => {
      await init({
        permissions: DEFAULT_PERMISSIONS,
        envSettings: { apiScore: { enabled: false } },
      });
      const service = TestBed.inject(ApiFederatedMenuService);

      const menu = service.getMenu(fakeApiFederated());

      expect(menu.subMenuItems.find((item) => item.displayName === 'API Score')).toBeUndefined();
    });
  });
});
