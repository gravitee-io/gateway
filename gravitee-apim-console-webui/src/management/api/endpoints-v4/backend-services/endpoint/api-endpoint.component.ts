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
import { StateService } from '@uirouter/core';
import { catchError, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { combineLatest, EMPTY, Subject } from 'rxjs';
import { GioFormJsonSchemaComponent, GioJsonSchema } from '@gravitee/ui-particles-angular';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { UIRouterState, UIRouterStateParams } from '../../../../../ajs-upgraded-providers';
import { ApiV2Service } from '../../../../../services-ngx/api-v2.service';
import { ApiV4, ConnectorPlugin, EndpointGroupV4, EndpointV4 } from '../../../../../entities/management-api-v2';
import { ConnectorPluginsV2Service } from '../../../../../services-ngx/connector-plugins-v2.service';
import { SnackBarService } from '../../../../../services-ngx/snack-bar.service';
import { IconService } from '../../../../../services-ngx/icon.service';

@Component({
  selector: 'api-endpoint',
  template: require('./api-endpoint.component.html'),
  styles: [require('./api-endpoint.component.scss')],
})
export class ApiEndpointComponent implements OnInit, OnDestroy {
  private unsubscribe$: Subject<boolean> = new Subject<boolean>();
  private groupIndex: number;
  private endpointIndex: number;
  public endpointGroup: EndpointGroupV4;
  public formGroup: FormGroup;
  public endpointSchema: { config: GioJsonSchema; sharedConfig: GioJsonSchema };
  public connectorPlugin: ConnectorPlugin;
  public isLoading = false;

  constructor(
    @Inject(UIRouterState) private readonly ajsState: StateService,
    @Inject(UIRouterStateParams) private readonly ajsStateParams,
    private readonly apiService: ApiV2Service,
    private readonly connectorPluginsV2Service: ConnectorPluginsV2Service,
    private readonly snackBarService: SnackBarService,
    private readonly iconService: IconService,
  ) {}

  public ngOnInit(): void {
    this.isLoading = true;
    const apiId = this.ajsStateParams.apiId;
    this.groupIndex = +this.ajsStateParams.groupIndex;

    this.apiService
      .get(apiId)
      .pipe(
        switchMap((api: ApiV4) => {
          this.endpointGroup = api.endpointGroups[this.groupIndex];
          return combineLatest([
            this.connectorPluginsV2Service.getEndpointPluginSchema(this.endpointGroup.type),
            this.connectorPluginsV2Service.getEndpointPluginSharedConfigurationSchema(this.endpointGroup.type),
            this.connectorPluginsV2Service.getEndpointPlugin(this.endpointGroup.type),
          ]);
        }),
        tap(([config, sharedConfig, connectorPlugin]) => {
          this.endpointSchema = {
            config: GioFormJsonSchemaComponent.isDisplayable(config) ? config : null,
            sharedConfig: GioFormJsonSchemaComponent.isDisplayable(sharedConfig) ? sharedConfig : null,
          };
          this.connectorPlugin = { ...connectorPlugin, icon: this.iconService.registerSvg(connectorPlugin.id, connectorPlugin.icon) };
          this.initForm();
        }),
        takeUntil(this.unsubscribe$),
      )
      .subscribe(() => (this.isLoading = false));
  }

  public ngOnDestroy() {
    this.unsubscribe$.next(true);
    this.unsubscribe$.unsubscribe();
  }

  public onPrevious() {
    this.ajsState.go('management.apis.ng.endpoints');
  }

  public onSave() {
    const inheritConfiguration = this.formGroup.get('inheritConfiguration').value;

    const updatedEndpoint: EndpointV4 = {
      type: this.endpointGroup.type,
      name: this.formGroup.get('name').value,
      configuration: this.formGroup.get('configuration').value,
      sharedConfigurationOverride: inheritConfiguration ? {} : this.formGroup.get('sharedConfiguration').value,
      inheritConfiguration,
    };

    this.apiService
      .get(this.ajsStateParams.apiId)
      .pipe(
        switchMap((api: ApiV4) => {
          const endpointGroups = api.endpointGroups.map((group, i) => {
            if (i === this.groupIndex) {
              return {
                ...group,
                endpoints:
                  this.endpointIndex !== undefined
                    ? group.endpoints.map((endpoint, j) => (j === this.endpointIndex ? updatedEndpoint : endpoint))
                    : [...group.endpoints, updatedEndpoint],
              };
            }
            return group;
          });
          return this.apiService.update(api.id, { ...api, endpointGroups });
        }),
        catchError(({ error }) => {
          this.snackBarService.error(error.message);
          return EMPTY;
        }),
        map(() => {
          this.snackBarService.success(`Endpoint successfully created!`);
          this.ajsState.go('management.apis.ng.endpoints');
        }),
        takeUntil(this.unsubscribe$),
      )
      .subscribe();
  }

  public onInheritConfigurationChange() {
    if (this.formGroup.get('inheritConfiguration').value) {
      this.formGroup.get('sharedConfiguration').disable();
    } else {
      this.formGroup.get('sharedConfiguration').enable();
    }
  }

  private initForm() {
    let name = null;
    let inheritConfiguration = false;
    let configuration = null;
    let sharedConfiguration = null;

    if (this.ajsStateParams.endpointIndex !== undefined) {
      this.endpointIndex = +this.ajsStateParams.endpointIndex;
      const endpoint = this.endpointGroup.endpoints[this.endpointIndex];
      name = endpoint.name;
      inheritConfiguration = endpoint.inheritConfiguration;
      configuration = endpoint.configuration;
      sharedConfiguration = inheritConfiguration ? this.endpointGroup.sharedConfiguration : endpoint.sharedConfigurationOverride;
    }

    this.formGroup = new FormGroup({
      name: new FormControl(name, Validators.required),
      inheritConfiguration: new FormControl(inheritConfiguration),
      configuration: new FormControl(configuration),
      sharedConfiguration: new FormControl({ value: sharedConfiguration, disabled: inheritConfiguration }),
    });
  }
}
