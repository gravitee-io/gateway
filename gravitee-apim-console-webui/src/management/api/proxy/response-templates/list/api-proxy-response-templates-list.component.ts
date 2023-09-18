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
import { MatDialog } from '@angular/material/dialog';
import { GioConfirmDialogComponent, GioConfirmDialogData } from '@gravitee/ui-particles-angular';
import { StateService } from '@uirouter/angular';
import { EMPTY, of, Subject } from 'rxjs';
import { catchError, filter, switchMap, takeUntil, tap } from 'rxjs/operators';

import { UIRouterState, UIRouterStateParams } from '../../../../../ajs-upgraded-providers';
import { SnackBarService } from '../../../../../services-ngx/snack-bar.service';
import { GioPermissionService } from '../../../../../shared/components/gio-permission/gio-permission.service';
import { ResponseTemplate, toResponseTemplates } from '../response-templates.adapter';
import { onlyApiV1V2Filter, onlyApiV2Filter } from '../../../../../util/apiFilter.operator';
import { ApiV2Service } from '../../../../../services-ngx/api-v2.service';

@Component({
  selector: 'api-proxy-response-templates-list',
  template: require('./api-proxy-response-templates-list.component.html'),
  styles: [require('./api-proxy-response-templates-list.component.scss')],
})
export class ApiProxyResponseTemplatesListComponent implements OnInit, OnDestroy {
  private unsubscribe$: Subject<boolean> = new Subject<boolean>();

  public responseTemplateTableDisplayedColumns = ['key', 'contentType', 'statusCode', 'actions'];
  public responseTemplateTableData: ResponseTemplate[];
  public isReadOnly = false;
  public apiId: string;

  constructor(
    @Inject(UIRouterStateParams) private readonly ajsStateParams,
    @Inject(UIRouterState) private readonly ajsState: StateService,
    private readonly apiService: ApiV2Service,
    private readonly permissionService: GioPermissionService,
    private readonly matDialog: MatDialog,
    private readonly snackBarService: SnackBarService,
  ) {}

  ngOnInit(): void {
    this.apiService
      .get(this.ajsStateParams.apiId)
      .pipe(
        onlyApiV1V2Filter(this.snackBarService),
        tap((api) => {
          this.apiId = api.id;
          this.responseTemplateTableData = toResponseTemplates(api.responseTemplates);

          this.isReadOnly =
            !this.permissionService.hasAnyMatching(['api-response_templates-u']) || api.definitionContext?.origin === 'KUBERNETES';
        }),
        takeUntil(this.unsubscribe$),
      )
      .subscribe();
  }

  ngOnDestroy() {
    this.unsubscribe$.next(true);
    this.unsubscribe$.unsubscribe();
  }

  onAddResponseTemplateClicked() {
    this.ajsState.go('management.apis.responseTemplateNew', { apiId: this.apiId });
  }

  onEditResponseTemplateClicked(element: ResponseTemplate) {
    this.ajsState.go('management.apis.responseTemplateEdit', { apiId: this.apiId, responseTemplateId: element.id });
  }

  onDeleteResponseTemplateClicked(element: ResponseTemplate) {
    this.matDialog
      .open<GioConfirmDialogComponent, GioConfirmDialogData>(GioConfirmDialogComponent, {
        width: '500px',
        data: {
          title: 'Delete a Response Template',
          content: `Are you sure you want to delete the Response Template <strong>${element.key} - ${element.contentType}</strong>?`,
          confirmButton: 'Delete',
        },
        role: 'alertdialog',
        id: 'deleteResponseTemplateConfirmDialog',
      })
      .afterClosed()
      .pipe(
        filter((confirm) => confirm === true),
        switchMap(() => this.apiService.get(this.ajsStateParams.apiId)),
        onlyApiV2Filter(this.snackBarService),
        switchMap((api) => {
          if (api.responseTemplates[element.key] && api.responseTemplates[element.key][element.contentType]) {
            delete api.responseTemplates[element.key][element.contentType];
            return this.apiService.update(api.id, api);
          }
          return of({});
        }),
        catchError(({ error }) => {
          this.snackBarService.error(error.message);
          return EMPTY;
        }),
        tap(() => this.snackBarService.success(`Response Template ${element.key} - ${element.contentType} successfully deleted!`)),

        takeUntil(this.unsubscribe$),
      )
      .subscribe(() => this.ngOnInit());
  }
}
