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
import { FormGroup } from '@angular/forms';
import { StateService } from '@uirouter/angular';
import { EMPTY, Subject } from 'rxjs';
import { catchError, switchMap, takeUntil, tap } from 'rxjs/operators';

import { UIRouterState, UIRouterStateParams } from '../../../../ajs-upgraded-providers';
import { ApiService } from '../../../../services-ngx/api.service';
import { SnackBarService } from '../../../../services-ngx/snack-bar.service';
import { GioPermissionService } from '../../../../shared/components/gio-permission/gio-permission.service';
import { ApiProxyHealthCheckFormComponent } from '../components/health-check-form/api-proxy-health-check-form.component';

@Component({
  selector: 'api-proxy-health-check',
  template: require('./api-proxy-health-check.component.html'),
  styles: [require('./api-proxy-health-check.component.scss')],
})
export class ApiProxyHealthCheckComponent implements OnInit, OnDestroy {
  private unsubscribe$: Subject<boolean> = new Subject<boolean>();

  public healthCheckForm: FormGroup;

  public isReadOnly = false;

  constructor(
    @Inject(UIRouterStateParams) private readonly ajsStateParams,
    @Inject(UIRouterState) private readonly ajsState: StateService,
    private readonly apiService: ApiService,
    private readonly snackBarService: SnackBarService,
    private readonly permissionService: GioPermissionService,
  ) {}

  ngOnInit(): void {
    this.apiService
      .get(this.ajsStateParams.apiId)
      .pipe(
        takeUntil(this.unsubscribe$),
        tap((api) => {
          const isReadOnly =
            !this.permissionService.hasAnyMatching(['api-health-c', 'api-health-u']) || api.definition_context?.origin === 'kubernetes';

          this.healthCheckForm = ApiProxyHealthCheckFormComponent.NewHealthCheckFormGroup(api.services['health-check'], isReadOnly);
        }),
      )
      .subscribe();
  }

  ngOnDestroy() {
    this.unsubscribe$.next(true);
    this.unsubscribe$.unsubscribe();
  }

  onSubmit() {
    return this.apiService
      .get(this.ajsStateParams.apiId)
      .pipe(
        takeUntil(this.unsubscribe$),
        switchMap((api) =>
          this.apiService.update({
            ...api,
            services: {
              ...api.services,
              'health-check': ApiProxyHealthCheckFormComponent.HealthCheckFromFormGroup(this.healthCheckForm),
            },
          }),
        ),
        tap(() => this.snackBarService.success('Configuration successfully saved!')),
        catchError(({ error }) => {
          this.snackBarService.error(error.message);
          return EMPTY;
        }),
        tap(() => this.ngOnInit()),
      )
      .subscribe();
  }

  gotToHealthCheckDashboard() {
    this.ajsState.go('management.apis.detail.proxy.healthCheckDashboard.visualize');
  }
}
