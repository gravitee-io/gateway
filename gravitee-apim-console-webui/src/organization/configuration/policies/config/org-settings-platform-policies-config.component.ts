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

import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { combineLatest, Subject } from 'rxjs';
import { takeUntil, tap } from 'rxjs/operators';
import { ComponentCustomEvent } from '@gravitee/ui-components/src/lib/events';

import { OrganizationService } from '../../../../services-ngx/organization.service';
import { OrgSettingsPlatformPoliciesService } from '../org-settings-platform-policies.service';
import { DefinitionVM } from '../org-settings-platform-policies.component';
import { FlowConfigurationSchema } from '../../../../entities/flow/configurationSchema';

@Component({
  selector: 'org-settings-platform-policies-config',
  template: require('./org-settings-platform-policies-config.component.html'),
  styles: [require('./org-settings-platform-policies-config.component.scss')],
})
export class OrgSettingsPlatformPoliciesConfigComponent implements OnInit, OnDestroy {
  flowConfigurationSchema: FlowConfigurationSchema;

  isLoading = true;

  fromValue: {
    flow_mode: DefinitionVM['flow_mode'];
  };

  @Output()
  change = new EventEmitter<DefinitionVM['flow_mode']>();

  private unsubscribe$ = new Subject<boolean>();

  constructor(
    private readonly organizationService: OrganizationService,
    private readonly orgSettingsPlatformPoliciesService: OrgSettingsPlatformPoliciesService,
  ) {}

  ngOnInit(): void {
    this.isLoading = true;

    combineLatest([this.orgSettingsPlatformPoliciesService.getConfigurationSchemaForm(), this.organizationService.get()])
      .pipe(
        takeUntil(this.unsubscribe$),
        tap(([flowSchema, organization]) => {
          this.flowConfigurationSchema = flowSchema;

          this.fromValue = {
            flow_mode: organization.flowMode,
          };

          this.isLoading = false;
        }),
      )
      .subscribe();
  }

  ngOnDestroy() {
    this.unsubscribe$.next(true);
    this.unsubscribe$.unsubscribe();
  }

  onChange(
    $event: ComponentCustomEvent<{
      values: {
        flow_mode: DefinitionVM['flow_mode'];
      };
    }>,
  ) {
    this.change.emit($event.detail?.values?.flow_mode);
  }
}
