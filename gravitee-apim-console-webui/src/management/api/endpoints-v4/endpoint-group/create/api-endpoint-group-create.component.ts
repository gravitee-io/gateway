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
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { combineLatest, of, Subject } from 'rxjs';
import { GioJsonSchema } from '@gravitee/ui-particles-angular';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { distinctUntilChanged, switchMap, takeUntil, tap } from 'rxjs/operators';
import { StateService } from '@uirouter/angular';

import { ApiEndpointGroupSelectionComponent } from '../selection/api-endpoint-group-selection.component';
import { ApiEndpointGroupConfigurationComponent } from '../configuration/api-endpoint-group-configuration.component';
import { ApiEndpointGroupGeneralComponent } from '../general/api-endpoint-group-general.component';
import { ApiV2Service } from '../../../../../services-ngx/api-v2.service';
import { ApiType, ApiV4, EndpointGroupV4, EndpointV4Default, UpdateApiV4 } from '../../../../../entities/management-api-v2';
import { UIRouterState, UIRouterStateParams } from '../../../../../ajs-upgraded-providers';
import { SnackBarService } from '../../../../../services-ngx/snack-bar.service';
import { ConnectorPluginsV2Service } from '../../../../../services-ngx/connector-plugins-v2.service';
import { isEndpointNameUnique } from '../../api-endpoint-v4-unique-name';

@Component({
  selector: 'api-endpoints-group-create',
  template: require('./api-endpoint-group-create.component.html'),
  styles: [require('./api-endpoint-group-create.component.scss')],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false, showError: true },
    },
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApiEndpointGroupCreateComponent implements OnInit {
  private unsubscribe$: Subject<boolean> = new Subject<boolean>();

  @ViewChild(ApiEndpointGroupSelectionComponent)
  private apiEndpointGroupsSelectionComponent: ApiEndpointGroupSelectionComponent;

  @ViewChild(ApiEndpointGroupGeneralComponent)
  private apiEndpointGroupGeneralComponent: ApiEndpointGroupGeneralComponent;

  @ViewChild(ApiEndpointGroupConfigurationComponent)
  private apiEndpointGroupConfigurationComponent: ApiEndpointGroupConfigurationComponent;

  public createForm: FormGroup;
  public endpointGroupTypeForm: FormGroup;
  public generalForm: FormGroup;
  public configuration: FormControl;
  public sharedConfiguration: FormControl;
  public configurationSchema: GioJsonSchema;
  public sharedConfigurationSchema: GioJsonSchema;
  public apiType: ApiType;

  constructor(
    @Inject(UIRouterStateParams) private readonly ajsStateParams,
    @Inject(UIRouterState) readonly ajsState: StateService,
    private readonly apiService: ApiV2Service,
    private readonly connectorPluginsV2Service: ConnectorPluginsV2Service,
    private readonly snackBarService: SnackBarService,
    private readonly changeDetectorRef: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.initCreateForm();

    this.apiService
      .get(this.ajsStateParams.apiId)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe({
        next: (api) => {
          const apiV4 = api as ApiV4;
          this.generalForm.get('name').addValidators([isEndpointNameUnique(apiV4)]);
          this.apiType = apiV4.type;
          if (this.apiType === 'PROXY') {
            this.endpointGroupTypeForm.get('endpointGroupType').setValue('http-proxy');
          }
          this.changeDetectorRef.detectChanges();
        },
      });

    this.endpointGroupTypeForm
      .get('endpointGroupType')
      .valueChanges.pipe(
        distinctUntilChanged(),
        switchMap((type) => {
          // Hide GioJsonSchemaForm to reset
          this.configurationSchema = undefined;
          this.sharedConfigurationSchema = undefined;

          this.createForm.setControl('configuration', new FormControl({}, Validators.required));
          this.createForm.setControl('sharedConfiguration', new FormControl({}, Validators.required));

          if (!type || type === 'mock') {
            this.createForm.removeControl('configuration');
            return of([undefined, undefined]);
          }

          return combineLatest([
            this.connectorPluginsV2Service.getEndpointPluginSchema(type),
            this.connectorPluginsV2Service.getEndpointPluginSharedConfigurationSchema(type),
          ]);
        }),
        tap(([schema, sharedSchema]) => {
          this.configurationSchema = schema;
          this.sharedConfigurationSchema = sharedSchema;
        }),
        takeUntil(this.unsubscribe$),
      )
      .subscribe();
  }

  createEndpointGroup() {
    const formValue = this.createForm.getRawValue();
    const configuration = formValue.configuration;
    const sharedConfiguration = formValue.sharedConfiguration;
    const cleanName = formValue.general.name.trim();

    const newEndpointGroup: EndpointGroupV4 = {
      name: cleanName,
      loadBalancer: { type: formValue.general.loadBalancerType },
      type: formValue.type.endpointGroupType,
      endpoints: [
        {
          ...EndpointV4Default.byTypeAndGroupName(formValue.type.endpointGroupType, cleanName),
          ...(configuration ? { configuration } : {}),
        },
      ],
      ...(sharedConfiguration ? { sharedConfiguration } : {}),
    };

    this.apiService
      .get(this.ajsStateParams.apiId)
      .pipe(
        switchMap((api) => {
          const updatedApi: UpdateApiV4 = { ...(api as ApiV4) };
          updatedApi.endpointGroups.push(newEndpointGroup);
          return this.apiService.update(api.id, updatedApi);
        }),
        takeUntil(this.unsubscribe$),
      )
      .subscribe({
        next: (_) => {
          this.snackBarService.success(`Endpoint group ${newEndpointGroup.name} created!`);
          this.goBackToEndpointGroups();
        },
        error: ({ err }) => this.snackBarService.error(err.message ?? 'An error occurred.'),
      });
  }

  goBackToEndpointGroups() {
    this.ajsState.go('management.apis.endpoint-groups');
  }

  private initCreateForm(): void {
    this.endpointGroupTypeForm = new FormGroup({
      endpointGroupType: new FormControl('', Validators.required),
    });

    this.generalForm = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.pattern(/^[^:]*$/)]),
      loadBalancerType: new FormControl('', [Validators.required]),
    });

    this.configuration = new FormControl({}, Validators.required);
    this.sharedConfiguration = new FormControl({}, Validators.required);

    this.createForm = new FormGroup({
      type: this.endpointGroupTypeForm,
      general: this.generalForm,
      configuration: this.configuration,
      sharedConfiguration: this.sharedConfiguration,
    });
  }
}
