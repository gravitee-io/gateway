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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { ApiCreationStepService } from '../../services/api-creation-step.service';
import { ConstantsService, PlanMenuItemVM } from '../../../../../services-ngx/constants.service';
import { CreatePlanV4 } from '../../../../../entities/management-api-v2';
import { Step5SummaryComponent } from '../step-5-summary/step-5-summary.component';

@Component({
  selector: 'step-4-security-1-plans-list',
  template: require('./step-4-security-1-plans-list.component.html'),
  styles: [require('./step-4-security-1-plans-list.component.scss'), require('../api-creation-steps-common.component.scss')],
})
export class Step4Security1PlansListComponent implements OnInit {
  @Input()
  plans: CreatePlanV4[] = [];

  @Output()
  addPlanClicked = new EventEmitter<PlanMenuItemVM>();

  @Output()
  editPlanClicked = new EventEmitter<CreatePlanV4>();

  @Output()
  removePlanClicked = new EventEmitter<CreatePlanV4>();

  planMenuItems: PlanMenuItemVM[];

  public form = new FormGroup({});
  displayedColumns: string[] = ['name', 'mode', 'security', 'actions'];

  constructor(private readonly stepService: ApiCreationStepService, private readonly constantsService: ConstantsService) {}

  ngOnInit(): void {
    this.planMenuItems = this.constantsService.getEnabledPlanMenuItems();
    if (this.stepService?.payload?.selectedEntrypoints.every((entrypoint) => entrypoint.supportedListenerType === 'SUBSCRIPTION')) {
      this.planMenuItems = this.planMenuItems.filter((planMenuItem) => planMenuItem.planFormType === 'PUSH');
    }

    if (this.stepService?.payload?.selectedEntrypoints.every((entrypoint) => ['HTTP', 'TCP'].includes(entrypoint.supportedListenerType))) {
      this.planMenuItems = this.planMenuItems.filter((planMenuItem) => planMenuItem.planFormType !== 'PUSH');
    }
  }

  save(): void {
    this.stepService.validStep((previousPayload) => ({
      ...previousPayload,
      plans: this.plans,
    }));

    this.stepService.goToNextStep({ groupNumber: 5, component: Step5SummaryComponent });
  }

  goBack(): void {
    this.stepService.goToPreviousStep();
  }

  addPlan(selectedPlanMenuItem: PlanMenuItemVM) {
    this.addPlanClicked.emit(selectedPlanMenuItem);
  }

  editPlan(plan: CreatePlanV4) {
    this.editPlanClicked.emit(this.plans.find((listedPlan) => listedPlan === plan));
  }

  removePlan(plan: CreatePlanV4) {
    this.removePlanClicked.emit(plan);
  }
}
