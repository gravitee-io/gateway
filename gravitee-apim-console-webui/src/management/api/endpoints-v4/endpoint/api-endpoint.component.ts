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
import { catchError, filter, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { combineLatest, EMPTY, Observable, Subject } from 'rxjs';
import { GioConfirmDialogComponent, GioConfirmDialogData, GioFormJsonSchemaComponent, GioJsonSchema } from '@gravitee/ui-particles-angular';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';

import { UIRouterState, UIRouterStateParams } from '../../../../ajs-upgraded-providers';
import { ApiV2Service } from '../../../../services-ngx/api-v2.service';
import { Api, ApiV4, ConnectorPlugin, EndpointGroupV4, EndpointV4, Entrypoint } from '../../../../entities/management-api-v2';
import { ConnectorPluginsV2Service } from '../../../../services-ngx/connector-plugins-v2.service';
import { SnackBarService } from '../../../../services-ngx/snack-bar.service';
import { IconService } from '../../../../services-ngx/icon.service';
import { isEndpointNameUnique, isEndpointNameUniqueAndDoesNotMatchDefaultValue } from '../api-endpoint-v4-unique-name';
import { getMatchingDlqEntrypoints, updateDlqEntrypoint } from '../api-endpoint-v4-matching-dlq';

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
  private api: ApiV4;
  private endpoint: EndpointV4;
  private mode: 'edit' | 'create';

  constructor(
    @Inject(UIRouterState) private readonly ajsState: StateService,
    @Inject(UIRouterStateParams) private readonly ajsStateParams,
    private readonly apiService: ApiV2Service,
    private readonly connectorPluginsV2Service: ConnectorPluginsV2Service,
    private readonly snackBarService: SnackBarService,
    private readonly iconService: IconService,
    private readonly matDialog: MatDialog,
  ) {}

  public ngOnInit(): void {
    this.isLoading = true;
    const apiId = this.ajsStateParams.apiId;
    this.groupIndex = +this.ajsStateParams.groupIndex;
    this.mode = this.ajsStateParams.endpointIndex !== undefined ? 'edit' : 'create';

    this.apiService
      .get(apiId)
      .pipe(
        switchMap((api: ApiV4) => {
          this.api = api;
          this.endpointGroup = api.endpointGroups[this.groupIndex];

          if (!this.endpointGroup) {
            this.snackBarService.error(`Endpoint group at index [ ${this.groupIndex} ] does not exist.`);
            this.ajsState.go('management.apis.endpoint-groups');
            return EMPTY;
          }

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

  public onSave() {
    const inheritConfiguration = this.formGroup.get('inheritConfiguration').value;

    const updatedEndpoint: EndpointV4 = {
      type: this.endpointGroup.type,
      name: this.formGroup.get('name').value.trim(),
      weight: this.formGroup.get('weight').value,
      configuration: this.formGroup.get('configuration').value,
      sharedConfigurationOverride: inheritConfiguration ? {} : this.formGroup.get('sharedConfigurationOverride').value,
      inheritConfiguration,
    };

    let apiUpdate$: Observable<Api>;
    if (this.mode === 'edit') {
      const matchingDlqEntrypoint = getMatchingDlqEntrypoints(this.api, this.endpoint.name);
      if (updatedEndpoint.name !== this.endpoint.name && matchingDlqEntrypoint.length > 0) {
        apiUpdate$ = this.updateApiWithDlq(matchingDlqEntrypoint, updatedEndpoint);
      } else {
        apiUpdate$ = this.apiService.get(this.ajsStateParams.apiId).pipe(switchMap((api: ApiV4) => this.updateApi(api, updatedEndpoint)));
      }
    } else {
      apiUpdate$ = this.apiService.get(this.ajsStateParams.apiId).pipe(switchMap((api: ApiV4) => this.updateApi(api, updatedEndpoint)));
    }

    apiUpdate$
      .pipe(
        catchError(({ error }) => {
          this.snackBarService.error(error.message);
          return EMPTY;
        }),
        map(() => {
          this.snackBarService.success(`Endpoint successfully created!`);
          this.ajsState.go('management.apis.endpoint-groups');
        }),
        takeUntil(this.unsubscribe$),
      )
      .subscribe();
  }

  private updateApiWithDlq(matchingDlqEntrypoint: Entrypoint[], updatedEndpoint: EndpointV4): Observable<Api> {
    return this.matDialog
      .open<GioConfirmDialogComponent, GioConfirmDialogData>(GioConfirmDialogComponent, {
        width: '500px',
        data: {
          title: 'Rename Endpoint',
          content: `Some entrypoints use this endpoint as Dead letter queue. They will be modified to reference the new name.`,
          confirmButton: 'Update',
        },
        role: 'alertdialog',
        id: 'updateEndpointNameDlqConfirmDialog',
      })
      .afterClosed()
      .pipe(
        filter((confirm) => confirm === true),
        switchMap(() => this.apiService.get(this.api.id)),
        map((api: ApiV4) => {
          updateDlqEntrypoint(api, matchingDlqEntrypoint, updatedEndpoint.name);
          return api;
        }),
        switchMap((api: ApiV4) => this.updateApi(api, updatedEndpoint)),
      );
  }

  private updateApi(api: ApiV4, updatedEndpoint: EndpointV4): Observable<Api> {
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
  }

  public onInheritConfigurationChange() {
    if (this.formGroup.get('inheritConfiguration').value) {
      this.formGroup.get('sharedConfigurationOverride').disable();
    } else {
      this.formGroup.get('sharedConfigurationOverride').enable();
    }
  }

  private initForm() {
    let name = null;
    let configuration = null;
    let sharedConfigurationOverride = this.endpointGroup.sharedConfiguration;
    // Inherit configuration only if there is a sharedConfiguration (that is not the case of mock endppoint)
    let inheritConfiguration = !!sharedConfigurationOverride;
    let weight = null;

    if (this.mode === 'edit') {
      this.endpointIndex = +this.ajsStateParams.endpointIndex;
      this.endpoint = this.endpointGroup.endpoints[this.endpointIndex];

      if (!this.endpoint) {
        this.snackBarService.error(`Endpoint at index [ ${this.endpointIndex} ] does not exist.`);
        this.ajsState.go('management.apis.endpoint-groups');
        return;
      }

      name = this.endpoint.name;
      weight = this.endpoint.weight;
      inheritConfiguration = this.endpoint.inheritConfiguration;
      configuration = this.endpoint.configuration;
      if (!inheritConfiguration) {
        sharedConfigurationOverride = this.endpoint.sharedConfigurationOverride;
      }
    }

    this.formGroup = new FormGroup({
      name: new FormControl(name, [
        Validators.required,
        this.mode === 'edit'
          ? isEndpointNameUniqueAndDoesNotMatchDefaultValue(this.api, this.endpoint.name)
          : isEndpointNameUnique(this.api),
      ]),
      weight: new FormControl(weight, Validators.required),
      inheritConfiguration: new FormControl(inheritConfiguration),
      configuration: new FormControl(configuration),
      sharedConfigurationOverride: new FormControl({ value: sharedConfigurationOverride, disabled: inheritConfiguration }),
    });
  }
}
