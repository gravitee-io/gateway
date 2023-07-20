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
import { Inject, Injectable } from '@angular/core';

import { MenuGroupItem, MenuItem } from './MenuGroupItem';
import { ApiMenuService } from './ApiMenuService';

import { GioPermissionService } from '../../../shared/components/gio-permission/gio-permission.service';
import { Constants } from '../../../entities/Constants';

@Injectable()
export class ApiNgV4MenuService implements ApiMenuService {
  constructor(private readonly permissionService: GioPermissionService, @Inject('Constants') private readonly constants: Constants) {}

  public getMenu(): {
    subMenuItems: MenuItem[];
    groupItems: MenuGroupItem[];
  } {
    const subMenuItems: MenuItem[] = [
      {
        displayName: 'Policy Studio',
        targetRoute: 'management.apis.ng.policyStudio',
        baseRoute: 'management.apis.ng.policyStudio',
        tabs: undefined,
      },
      {
        displayName: 'Messages',
        targetRoute: 'DISABLED',
      },
    ];
    const groupItems: MenuGroupItem[] = [
      this.getGeneralGroup(),
      this.getEntrypointsGroup(),
      this.getEndpointsGroup(),
      this.getAnalyticsGroup(),
      this.getAuditGroup(),
      this.getNotificationsGroup(),
    ].filter((group) => !!group);

    return { subMenuItems, groupItems };
  }

  private getGeneralGroup(): MenuGroupItem {
    const generalGroup: MenuGroupItem = {
      title: 'General',
      items: [
        {
          displayName: 'Info',
          targetRoute: 'management.apis.ng.general',
          baseRoute: 'management.apis.ng.general',
        },
      ],
    };
    // Plans
    const plansMenuItem: MenuItem = {
      displayName: 'Plans',
      tabs: [],
    };

    if (this.permissionService.hasAnyMatching(['api-plan-r'])) {
      plansMenuItem.tabs.push({
        displayName: 'Plans',
        targetRoute: 'management.apis.ng.plans',
        baseRoute: ['management.apis.ng.plans', 'management.apis.ng.plan'],
      });
    }
    if (this.permissionService.hasAnyMatching(['api-subscription-r'])) {
      plansMenuItem.tabs.push({
        displayName: 'Subscriptions',
        targetRoute: 'management.apis.ng.subscriptions',
        baseRoute: ['management.apis.ng.subscriptions', 'management.apis.ng.subscription'],
      });
    }
    if (plansMenuItem.tabs.length > 0) {
      generalGroup.items.push(plansMenuItem);
    }

    if (this.permissionService.hasAnyMatching(['api-definition-r'])) {
      generalGroup.items.push(
        {
          displayName: 'Properties',
          targetRoute: 'management.apis.ng.properties',
          baseRoute: 'management.apis.ng.properties',
        },
        {
          displayName: 'Resources',
          targetRoute: 'management.apis.ng.resources',
          baseRoute: 'management.apis.ng.resources',
        },
      );
    }

    if (this.permissionService.hasAnyMatching(['api-documentation-r'])) {
      generalGroup.items.push({
        displayName: 'Documentation',
        targetRoute: 'DISABLED',
      });
    }

    if (this.permissionService.hasAnyMatching(['api-member-r'])) {
      generalGroup.items.push({
        displayName: 'User and group access',
        targetRoute: 'DISABLED',
      });
    }

    return generalGroup;
  }

  private getEntrypointsGroup(): MenuGroupItem {
    if (this.permissionService.hasAnyMatching(['api-definition-r', 'api-health-r'])) {
      const entrypointsGroup: MenuGroupItem = {
        title: 'Entrypoints',
        items: [
          {
            displayName: 'General',
            targetRoute: 'management.apis.ng.entrypoints',
            baseRoute: 'management.apis.ng.entrypoints',
          },
        ],
      };
      return entrypointsGroup;
    }
    return undefined;
  }

  private getEndpointsGroup(): MenuGroupItem {
    const endpointsGroup: MenuGroupItem = {
      title: 'Endpoints',
      items: [],
    };

    if (this.permissionService.hasAnyMatching(['api-definition-r'])) {
      endpointsGroup.items.push({
        displayName: 'Backend services',
        targetRoute: 'management.apis.ng.endpoints',
        baseRoute: ['management.apis.ng.endpoints', 'management.apis.ng.endpoint'],
      });
    }

    return endpointsGroup;
  }

  private getAnalyticsGroup(): MenuGroupItem {
    const analyticsGroup: MenuGroupItem = {
      title: 'Analytics',
      items: [],
    };

    if (this.permissionService.hasAnyMatching(['api-analytics-r'])) {
      analyticsGroup.items.push({
        displayName: 'Overview',
        targetRoute: 'DISABLED',
      });
    }
    if (this.permissionService.hasAnyMatching(['api-log-r'])) {
      analyticsGroup.items.push({
        displayName: 'Logs',
        targetRoute: 'DISABLED',
      });
    }
    if (this.permissionService.hasAnyMatching(['api-definition-u'])) {
      analyticsGroup.items.push({
        displayName: 'Path mappings',
        targetRoute: 'DISABLED',
      });
    }
    if (this.constants.org.settings.alert?.enabled && this.permissionService.hasAnyMatching(['api-alert-r'])) {
      analyticsGroup.items.push({
        displayName: 'Alerts',
        targetRoute: 'DISABLED',
      });
    }

    return analyticsGroup;
  }

  private getAuditGroup(): MenuGroupItem {
    const auditGroup: MenuGroupItem = {
      title: 'Audit',
      items: [],
    };

    if (this.permissionService.hasAnyMatching(['api-audit-r'])) {
      auditGroup.items.push({
        displayName: 'Audit',
        targetRoute: 'DISABLED',
      });
    }
    if (this.permissionService.hasAnyMatching(['api-event-r'])) {
      auditGroup.items.push({
        displayName: 'History',
        targetRoute: 'DISABLED',
      });
    }
    if (this.permissionService.hasAnyMatching(['api-event-u'])) {
      auditGroup.items.push({
        displayName: 'Events',
        targetRoute: 'DISABLED',
      });
    }

    return auditGroup;
  }

  private getNotificationsGroup(): MenuGroupItem {
    const notificationsGroup: MenuGroupItem = {
      title: 'Notifications',
      items: [],
    };

    if (this.permissionService.hasAnyMatching(['api-notification-r'])) {
      notificationsGroup.items.push({
        displayName: 'Notifications',
        targetRoute: 'DISABLED',
      });
    }

    if (this.constants.org.settings.alert?.enabled && this.permissionService.hasAnyMatching(['api-alert-r'])) {
      notificationsGroup.items.push({
        displayName: 'Alerts',
        targetRoute: 'DISABLED',
      });
    }

    return notificationsGroup;
  }
}
