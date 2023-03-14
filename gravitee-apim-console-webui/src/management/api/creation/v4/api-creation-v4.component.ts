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

import { Component, HostBinding, Inject, Injector, OnDestroy, OnInit } from '@angular/core';
import { catchError, map, switchMap, takeUntil } from 'rxjs/operators';
import { forkJoin, Observable, of, Subject } from 'rxjs';
import { StateService } from '@uirouter/angular';

import { ApiCreationStep, ApiCreationStepperService } from './services/api-creation-stepper.service';
import { Step1ApiDetailsComponent } from './steps/step-1-api-details/step-1-api-details.component';
import { ApiCreationStepService } from './services/api-creation-step.service';
import { ApiCreationPayload } from './models/ApiCreationPayload';
import { MenuStepItem } from './components/api-creation-stepper-menu/api-creation-stepper-menu.component';
import { Step1MenuItemComponent } from './steps/step-1-menu-item/step-1-menu-item.component';
import { StepEntrypointMenuItemComponent } from './steps/step-connector-menu-item/step-entrypoint-menu-item.component';
import { StepEndpointMenuItemComponent } from './steps/step-connector-menu-item/step-endpoint-menu-item.component';
import { Step4MenuItemComponent } from './steps/step-4-menu-item/step-4-menu-item.component';

import { ApiV4Service } from '../../../../services-ngx/api-v4.service';
import { SnackBarService } from '../../../../services-ngx/snack-bar.service';
import { UIRouterState } from '../../../../ajs-upgraded-providers';
import { ApiEntity, EndpointGroup, Listener } from '../../../../entities/api-v4';
import { PlanV4Service } from '../../../../services-ngx/plan-v4.service';
import { Plan, PlanType, PlanValidation } from '../../../../entities/plan-v4';

// TODO: Make better... Add apiId as req ?
export interface Result {
  status: 'success' | 'failure';
  apiCreationPayload: ApiCreationPayload;
  message?: string;
  result?: {
    api?: ApiEntity;
    plans?: Plan[];
  };
}

@Component({
  selector: 'api-creation-v4',
  template: require('./api-creation-v4.component.html'),
  styles: [require('./api-creation-v4.component.scss')],
})
export class ApiCreationV4Component implements OnInit, OnDestroy {
  private unsubscribe$: Subject<void> = new Subject<void>();

  public currentStep: ApiCreationStep & { injector: Injector; payload: ApiCreationPayload };

  public stepper = new ApiCreationStepperService([
    {
      groupNumber: 1,
      label: 'API details',
      menuItemComponent: Step1MenuItemComponent,
    },
    {
      groupNumber: 2,
      label: 'Entrypoints',
      menuItemComponent: StepEntrypointMenuItemComponent,
    },
    {
      groupNumber: 3,
      label: 'Endpoints',
      menuItemComponent: StepEndpointMenuItemComponent,
    },
    {
      groupNumber: 4,
      label: 'Security',
      menuItemComponent: Step4MenuItemComponent,
    },
    {
      groupNumber: 5,
      label: 'Documentation',
    },
    {
      groupNumber: 6,
      label: 'Summary',
    },
  ]);

  @HostBinding('class.creating-api')
  public isCreatingApi = false;

  menuSteps$: Observable<MenuStepItem[]> = this.stepper.steps$.pipe(
    map((steps) => {
      // For each group, get the last step valid if present. To have the last state & full payload
      return this.stepper.groups.map((group) => {
        const stepsGroup = steps.filter((step) => step.group.groupNumber === group.groupNumber);
        const lastValidStep = stepsGroup.reverse().find((step) => step.state === 'valid');

        return {
          ...group,
          ...(lastValidStep
            ? { state: lastValidStep?.state ?? 'initial', payload: this.stepper.compileStepPayload(lastValidStep) }
            : { state: 'initial', payload: {} }),
        };
      });
    }),
  );

  constructor(
    private readonly injector: Injector,
    private readonly apiV4Service: ApiV4Service,
    private readonly planV4Service: PlanV4Service,
    private readonly snackBarService: SnackBarService,
    @Inject(UIRouterState) readonly ajsState: StateService,
  ) {}

  ngOnInit(): void {
    this.stepper.goToNextStep({
      groupNumber: 1,
      component: Step1ApiDetailsComponent,
    });

    // When the stepper change, update the current step
    this.stepper.currentStep$.pipe(takeUntil(this.unsubscribe$)).subscribe((apiCreationStep) => {
      const apiCreationStepService = new ApiCreationStepService(this.stepper, apiCreationStep);

      this.currentStep = {
        ...apiCreationStep,
        payload: apiCreationStepService.payload,
        injector: Injector.create({
          providers: [{ provide: ApiCreationStepService, useValue: apiCreationStepService }],
          parent: this.injector,
        }),
      };
    });

    // When then stepper is finished, create the API
    this.stepper.finished$
      .pipe(
        takeUntil(this.unsubscribe$),
        switchMap((p) => this.createApi$(p)),
        switchMap((apiCreationResult) => {
          if (apiCreationResult.apiCreationPayload.plans && apiCreationResult.status === 'success') {
            return this.createPlans$(apiCreationResult);
          }
          return of(apiCreationResult);
        }),
        switchMap((planCreationResult) => {
          if (planCreationResult.apiCreationPayload.deploy && planCreationResult.status === 'success') {
            return this.publishPlans$(planCreationResult);
          }
          return of(planCreationResult);
        }),
        switchMap((planPublishResult) => {
          if (planPublishResult.apiCreationPayload.deploy && planPublishResult.status === 'success') {
            return this.startApi$(planPublishResult);
          }
          return of(planPublishResult);
        }),
      )
      .subscribe((result) => {
        // TODO: Improve handling various errors non-related to creating API
        if (result.status === 'failure') {
          this.snackBarService.error(result.message);
        } else {
          this.snackBarService.success(`API ${this.currentStep.payload.deploy ? 'deployed' : 'created'} successfully!`);
        }
        if (result.result?.api?.id) {
          this.ajsState.go('management.apis.create-v4-confirmation', { apiId: result.result.api.id });
        }
      });
  }

  ngOnDestroy() {
    this.unsubscribe$.next();
    this.unsubscribe$.unsubscribe();
  }

  goToStep(label: string) {
    this.stepper.goToStepLabel(label);
  }

  private createApi$(apiCreationPayload: ApiCreationPayload): Observable<Result> {
    this.isCreatingApi = true;

    // Get distinct listener types
    const listenersType = [...new Set(apiCreationPayload.selectedEntrypoints.map(({ supportedListenerType }) => supportedListenerType))];

    // Create one listener per supportedListenerType and add all supported entrypoints
    const listeners: Listener[] = listenersType.reduce((listeners, listenersType) => {
      const entrypoints = apiCreationPayload.selectedEntrypoints
        .filter((e) => e.supportedListenerType === listenersType)
        .map(({ id, configuration }) => ({
          type: id,
          configuration: configuration,
        }));

      const listenerConfig = {
        type: listenersType,
        ...(listenersType === 'http' ? { paths: apiCreationPayload.paths } : {}),
        entrypoints,
      };
      return [...listeners, listenerConfig];
    }, []);

    return this.apiV4Service
      .create({
        definitionVersion: '4.0.0',
        name: apiCreationPayload.name,
        apiVersion: apiCreationPayload.version,
        description: apiCreationPayload.description ?? '',
        listeners: listeners,
        type: apiCreationPayload.type,
        endpointGroups: apiCreationPayload.selectedEndpoints.map(
          (endpoint) =>
            ({
              name: `Default ${endpoint.name} group`,
              type: endpoint.id,
              endpoints: [
                {
                  name: `Default ${endpoint.name}`,
                  type: endpoint.id,
                  weight: 1,
                  inheritConfiguration: false,
                  configuration: endpoint.configuration,
                },
              ],
            } as EndpointGroup),
        ),
      })
      .pipe(
        map((apiEntity) => ({ apiCreationPayload, result: { api: apiEntity }, status: 'success' as const })),
        catchError((err) => {
          return of({ apiCreationPayload, status: 'failure' as const, message: err.error?.message ?? `Error occurred when creating API` });
        }),
      );
  }

  private createPlans$(apiCreationStatus: Result): Observable<Result> {
    const api = apiCreationStatus.result.api;
    return forkJoin(
      apiCreationStatus.apiCreationPayload.plans.map((plan) =>
        this.planV4Service.create({
          apiId: api.id,
          description: plan.description,
          flows: [],
          name: plan.name,
          security: { configuration: {}, type: plan.type },
          status: api.lifecycleState === 'PUBLISHED' && api.state === 'STARTED' ? 'published' : 'staging',
          type: PlanType.API,
          validation: PlanValidation.AUTO,
        }),
      ),
    ).pipe(
      map((plans: Plan[]) => ({ ...apiCreationStatus, result: { ...apiCreationStatus.result, plans }, status: 'success' as const })),
      catchError((err) => {
        return of({
          ...apiCreationStatus,
          result: { ...apiCreationStatus.result, plans: [] },
          status: 'failure' as const,
          message: `Error while creating security plans: ${err.error?.message}`,
        });
      }),
    );
  }

  private publishPlans$(apiCreationStatus: Result): Observable<Result> {
    return forkJoin(
      apiCreationStatus.result.plans.map((p: Plan) => {
        return this.planV4Service.publish(apiCreationStatus.result.api.id, p.id);
      }),
    ).pipe(
      map(() => apiCreationStatus),
      catchError((err) => {
        return of({
          ...apiCreationStatus,
          status: 'failure' as const,
          message: `Error while publishing plans: ${err.error?.message}`,
        });
      }),
    );
  }

  private startApi$(planPublishResult: Result): Observable<Result> {
    return this.apiV4Service.start(planPublishResult.result.api.id).pipe(
      map(() => planPublishResult),
      catchError((err) => {
        return of({
          ...planPublishResult,
          status: 'failure' as const,
          message: `Error while starting API: ${err.error?.message}`,
        });
      }),
    );
  }
}
