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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { GioMenuSearchService, GioMenuService, MenuSearchItem } from '@gravitee/ui-particles-angular';
import { Subject } from 'rxjs';
import { startWith, takeUntil } from 'rxjs/operators';
import { castArray, flatMap } from 'lodash';
import { ActivatedRoute, Router } from '@angular/router';

import { cleanRouterLink, getPathFromRoot } from '../../../util/router-link.util';
import { GioPermissionService } from '../../../shared/components/gio-permission/gio-permission.service';
import { Application } from '../../../entities/application/application';
import { ApplicationService } from '../../../services-ngx/application.service';

export interface MenuItem {
  displayName: string;
  permissions?: string[];
  tabs?: MenuItem[];
  header?: MenuItemHeader;
  routerLink?: string;
  routerLinkActiveOptions?: { exact: boolean };
}

export interface MenuItemHeader {
  title?: string;
  subtitle?: string;
}

@Component({
  selector: 'application-navigation',
  templateUrl: './application-navigation.component.html',
  styleUrls: ['./application-navigation.component.scss'],
})
export class ApplicationNavigationComponent implements OnInit, OnDestroy {
  public application: Application;
  public subMenuItems: MenuItem[] = [];
  public hasBreadcrumb = false;
  public selectedItemWithTabs: MenuItem = undefined;
  private unsubscribe$ = new Subject<void>();

  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private readonly permissionService: GioPermissionService,
    private readonly gioMenuService: GioMenuService,
    private readonly applicationService: ApplicationService,
    private readonly gioMenuSearchService: GioMenuSearchService,
  ) {}

  ngOnInit() {
    this.applicationService
      .getLastApplicationFetch(this.activatedRoute.snapshot.params.applicationId)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe({
        next: (application) => (this.application = application),
      });

    this.gioMenuService.reduced$.pipe(takeUntil(this.unsubscribe$)).subscribe((reduced) => {
      this.hasBreadcrumb = reduced;
    });
    this.subMenuItems = this.filterMenuByPermission([
      {
        displayName: 'Global settings',
        routerLink: 'general',
        permissions: ['application-definition-r'],
      },
      {
        displayName: 'User and group access',
        permissions: ['application-member-r'],
        routerLink: 'members',
        tabs: [
          {
            displayName: 'Members',
            routerLink: 'members',
          },
          {
            displayName: 'Groups',
            routerLink: 'groups',
          },
          {
            displayName: 'Transfer ownership',
            routerLink: 'transfer-ownership',
          },
        ],
      },
      {
        displayName: 'Metadata',
        routerLink: 'metadata',
        permissions: ['application-metadata-r'],
      },
      {
        displayName: 'Subscriptions',
        routerLink: 'subscriptions',
        permissions: ['application-subscription-r'],
      },
      {
        displayName: 'Analytics',
        routerLink: 'analytics',
        permissions: ['application-analytics-r'],
      },
      {
        displayName: 'Logs',
        routerLink: 'logs',
        permissions: ['application-log-r'],
      },
      {
        displayName: 'Notification settings',
        routerLink: 'notifications',
        permissions: ['application-notification-r', 'application-alert-r'],
      },
    ]);

    this.gioMenuSearchService.addMenuSearchItems(this.getApplicationNavigationSearchItems());

    this.router.events.pipe(startWith({}), takeUntil(this.unsubscribe$)).subscribe(() => {
      this.selectedItemWithTabs = this.subMenuItems.find((item) => item.tabs && this.isTabActive(item.tabs));
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.unsubscribe();
    this.gioMenuSearchService.removeMenuSearchItems([this.activatedRoute.snapshot.params.applicationId]);
  }

  isActive(item: MenuItem): boolean {
    if (!item.routerLink) {
      return false;
    }
    return [item.routerLink, ...castArray(item.routerLink)]
      .filter((r) => !!r)
      .some((routerLink) => {
        return this.router.isActive(this.router.createUrlTree([routerLink], { relativeTo: this.activatedRoute }), {
          paths: item.routerLinkActiveOptions?.exact ? 'exact' : 'subset',
          queryParams: 'subset',
          fragment: 'ignored',
          matrixParams: 'ignored',
        });
      });
  }

  computeBreadcrumbItems(): string[] {
    const breadcrumbItems: string[] = [];

    this.subMenuItems.forEach((item) => {
      if (this.isActive(item)) {
        breadcrumbItems.push(item.displayName);
      }
    });

    return breadcrumbItems;
  }

  isTabActive(tabs: MenuItem[]): boolean {
    return flatMap(tabs, (tab) => tab).some((tab) => this.isActive(tab));
  }

  private filterMenuByPermission(menuItems: MenuItem[]): MenuItem[] {
    if (menuItems) {
      return menuItems.filter((item) => !item.permissions || this.permissionService.hasAnyMatching(item.permissions));
    }
    return [];
  }

  private getApplicationNavigationSearchItems(): MenuSearchItem[] {
    const environmentId = this.activatedRoute.snapshot.params.envId;
    const applicationId = this.activatedRoute.snapshot.params.applicationId;
    const parentRouterLink = getPathFromRoot(this.activatedRoute);

    return this.subMenuItems.reduce((acc: MenuSearchItem[], item: MenuItem) => {
      acc.push({
        name: item.displayName,
        routerLink: `${parentRouterLink}/${cleanRouterLink(item.routerLink)}`,
        category: `Applications`,
        groupIds: [environmentId, applicationId],
      });

      item.tabs?.forEach((tab) => {
        acc.push({
          name: tab.displayName,
          routerLink: `${parentRouterLink}/${cleanRouterLink(tab.routerLink)}`,
          category: `Applications / ${item.displayName}`,
          groupIds: [environmentId, applicationId],
        });
      });

      return acc;
    }, []);
  }
}
