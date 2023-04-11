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
import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { EMPTY, of, Subject } from 'rxjs';
import { catchError, switchMap, takeUntil, tap } from 'rxjs/operators';
import { StateService } from '@uirouter/angularjs';

import { UIRouterState, UIRouterStateParams } from '../../../../../ajs-upgraded-providers';
import { Api } from '../../../../../entities/api';
import { ApiService } from '../../../../../services-ngx/api.service';
import { PlanService } from '../../../../../services-ngx/plan.service';
import { SnackBarService } from '../../../../../services-ngx/snack-bar.service';
import { GioPermissionService } from '../../../../../shared/components/gio-permission/gio-permission.service';
import { NewPlan, Plan } from '../../../../../entities/plan';
import { ApiPlanFormComponent } from '../../../component/plan/api-plan-form.component';

@Component({
  selector: 'api-portal-plan-edit',
  template: require('./api-portal-plan-edit.component.html'),
  styles: [require('./api-portal-plan-edit.component.scss')],
})
export class ApiPortalPlanEditComponent implements OnInit, OnDestroy {
  private unsubscribe$: Subject<boolean> = new Subject<boolean>();

  public mode: 'create' | 'edit' = 'create';

  public planForm: FormGroup;
  public initialPlanFormValue: unknown;
  public api: Api;
  public isReadOnly = false;
  public displaySubscriptionsSection = true;

  @ViewChild('apiPlanForm')
  private apiPlanForm: ApiPlanFormComponent;

  constructor(
    @Inject(UIRouterStateParams) private readonly ajsStateParams,
    @Inject(UIRouterState) private readonly ajsState: StateService,
    private readonly apiService: ApiService,
    private readonly planService: PlanService,
    private readonly snackBarService: SnackBarService,
    private readonly permissionService: GioPermissionService,
    private readonly changeDetectorRef: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.mode = this.ajsStateParams.planId ? 'edit' : 'create';

    this.apiService
      .get(this.ajsStateParams.apiId)
      .pipe(
        takeUntil(this.unsubscribe$),
        tap((api) => {
          this.api = api;
          this.isReadOnly = !this.permissionService.hasAnyMatching(['api-plan-u']) || this.api.definition_context?.origin === 'kubernetes';
        }),
        switchMap(() =>
          this.mode === 'edit' ? this.planService.get(this.ajsStateParams.apiId, this.ajsStateParams.planId) : of(undefined),
        ),
        tap((plan) => {
          this.planForm = new FormGroup({
            plan: new FormControl({
              value: plan,
              disabled: this.isReadOnly,
            }),
          });
          this.changeDetectorRef.detectChanges();
        }),
        catchError((error) => {
          this.snackBarService.error(error.error?.message ?? 'An ');
          return EMPTY;
        }),
      )
      .subscribe();
  }

  ngOnDestroy() {
    this.unsubscribe$.next(true);
    this.unsubscribe$.unsubscribe();
  }

  onSubmit() {
    const planToSave: NewPlan | Plan = {
      ...this.planForm.get('plan').value,
    };

    const savePlan$ =
      this.mode === 'edit'
        ? this.planService
            .get(this.ajsStateParams.apiId, this.ajsStateParams.planId)
            .pipe(switchMap((planToUpdate) => this.planService.update(this.api, { ...planToUpdate, ...planToSave })))
        : this.planService.create(this.api, { ...planToSave });

    savePlan$
      .pipe(
        takeUntil(this.unsubscribe$),
        tap(() => this.snackBarService.success('Configuration successfully saved!')),
        catchError((error) => {
          this.snackBarService.error(error.error?.message ?? 'An error occurs while saving configuration');
          return EMPTY;
        }),
      )
      .subscribe(() => this.ajsState.go('management.apis.detail.portal.plans'));
  }
}
