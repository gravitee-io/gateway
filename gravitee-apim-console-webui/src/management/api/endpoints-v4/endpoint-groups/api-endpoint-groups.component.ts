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
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { GioConfirmDialogComponent, GioConfirmDialogData } from '@gravitee/ui-particles-angular';
import { catchError, filter, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { find, remove } from 'lodash';
import { combineLatest, EMPTY, Subject } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { StateService } from '@uirouter/angular';

import { EndpointGroup, toEndpoints } from './api-endpoint-groups.adapter';

import { ApiV2Service } from '../../../../services-ngx/api-v2.service';
import { SnackBarService } from '../../../../services-ngx/snack-bar.service';
import { ApiV4, ConnectorPlugin, UpdateApi } from '../../../../entities/management-api-v2';
import { ConnectorPluginsV2Service } from '../../../../services-ngx/connector-plugins-v2.service';
import { IconService } from '../../../../services-ngx/icon.service';
import { UIRouterState, UIRouterStateParams } from '../../../../ajs-upgraded-providers';

@Component({
  selector: 'api-endpoint-groups',
  template: require('./api-endpoint-groups.component.html'),
  styles: [require('./api-endpoint-groups.component.scss')],
})
export class ApiEndpointGroupsComponent implements OnInit, OnDestroy {
  public endpointsDisplayedColumns = ['name', 'options', 'weight', 'actions'];
  public groupsTableData: EndpointGroup[];
  public plugins: Map<string, ConnectorPlugin>;
  private unsubscribe$: Subject<boolean> = new Subject<boolean>();
  public isReordering = false;

  constructor(
    @Inject(UIRouterState) private readonly ajsState: StateService,
    @Inject(UIRouterStateParams) private readonly ajsStateParams,
    private readonly matDialog: MatDialog,
    private readonly apiService: ApiV2Service,
    private readonly snackBarService: SnackBarService,
    private readonly connectorPluginsV2Service: ConnectorPluginsV2Service,
    private readonly iconService: IconService,
  ) {}

  public ngOnInit() {
    combineLatest([this.apiService.get(this.ajsStateParams.apiId), this.connectorPluginsV2Service.listEndpointPlugins()])
      .pipe(
        tap(([apiV4, plugins]: [ApiV4, ConnectorPlugin[]]) => {
          this.groupsTableData = toEndpoints(apiV4);

          this.plugins = new Map(
            plugins.map((plugin) => [plugin.id, { ...plugin, icon: this.iconService.registerSvg(plugin.id, plugin.icon) }]),
          );
        }),
        takeUntil(this.unsubscribe$),
      )
      .subscribe();
  }

  public ngOnDestroy() {
    this.unsubscribe$.next(true);
    this.unsubscribe$.complete();
  }

  public deleteGroup(groupName: string): void {
    this.matDialog
      .open<GioConfirmDialogComponent, GioConfirmDialogData>(GioConfirmDialogComponent, {
        width: '500px',
        data: {
          title: 'Delete Endpoint Group',
          content: `Are you sure you want to delete the Group <strong>${groupName}</strong>?`,
          confirmButton: 'Delete',
        },
        role: 'alertdialog',
        id: 'deleteEndpointGroupConfirmDialog',
      })
      .afterClosed()
      .pipe(
        filter((confirm) => confirm === true),
        switchMap(() => this.apiService.get(this.ajsStateParams.apiId)),
        switchMap((api: ApiV4) => {
          remove(api.endpointGroups, (g) => g.name === groupName);
          return this.apiService.update(api.id, { ...api } as UpdateApi);
        }),
        catchError(({ error }) => {
          this.snackBarService.error(
            error.message === 'Validation error' ? `${error.message}: ${error.details[0].message}` : error.message,
          );
          return EMPTY;
        }),
        tap(() => this.ngOnInit()),
        map(() => this.snackBarService.success(`Endpoint group ${groupName} successfully deleted!`)),
        takeUntil(this.unsubscribe$),
      )
      .subscribe();
  }

  public deleteEndpoint(groupName: string, endpointName: string): void {
    this.matDialog
      .open<GioConfirmDialogComponent, GioConfirmDialogData>(GioConfirmDialogComponent, {
        width: '500px',
        data: {
          title: 'Delete Endpoint',
          content: `Are you sure you want to delete the Endpoint <strong>${endpointName}</strong>?`,
          confirmButton: 'Delete',
        },
        role: 'alertdialog',
        id: 'deleteEndpointConfirmDialog',
      })
      .afterClosed()
      .pipe(
        filter((confirm) => confirm === true),
        switchMap(() => this.apiService.get(this.ajsStateParams.apiId)),
        switchMap((api: ApiV4) => {
          remove(find(api.endpointGroups, (g) => g.name === groupName).endpoints, (e) => e.name === endpointName);
          return this.apiService.update(api.id, { ...api } as UpdateApi);
        }),
        catchError(({ error }) => {
          this.snackBarService.error(
            error.message === 'Validation error' ? `${error.message}: ${error.details[0].message}` : error.message,
          );
          return EMPTY;
        }),
        tap(() => this.ngOnInit()),
        map(() => this.snackBarService.success(`Endpoint ${endpointName} successfully deleted!`)),
        takeUntil(this.unsubscribe$),
      )
      .subscribe();
  }

  public addEndpoint(groupIndex: number): void {
    this.ajsState.go('management.apis.ng.endpoint-new', { groupIndex });
  }

  public editEndpoint(groupIndex: number, endpointIndex: number): void {
    this.ajsState.go('management.apis.ng.endpoint-edit', { groupIndex, endpointIndex });
  }

  public reorderEndpointGroup(oldIndex: number, newIndex: number): void {
    this.isReordering = true;
    this.apiService
      .get(this.ajsStateParams.apiId)
      .pipe(
        switchMap((api: ApiV4) => {
          api.endpointGroups.splice(newIndex, 0, api.endpointGroups.splice(oldIndex, 1)[0]);
          return this.apiService.update(api.id, { ...api } as UpdateApi);
        }),
        tap(() => this.ngOnInit()),
        takeUntil(this.unsubscribe$),
      )
      .subscribe({
        next: () => {
          this.isReordering = false;
        },
        error: ({ error }) => {
          this.isReordering = false;
          this.snackBarService.error(error.message);
        },
      });
  }

  public editEndpointGroup(groupIndex: number): void {
    this.ajsState.go('management.apis.ng.endpoint-group', { groupIndex });
  }

  addEndpointGroup() {
    this.ajsState.go('management.apis.ng.endpoint-group-new');
  }
}
