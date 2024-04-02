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

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCard, MatCardContent, MatCardHeader } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatError, MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { MatTooltip } from '@angular/material/tooltip';
import { MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatTableModule } from '@angular/material/table';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import {
  GioBannerModule,
  GioBreadcrumbModule,
  GioClipboardModule,
  GioRadioButtonModule,
  GioSubmenuModule,
} from '@gravitee/ui-particles-angular';
import { MatStep, MatStepLabel, MatStepper, MatStepperIcon, MatStepperNext, MatStepperPrevious } from '@angular/material/stepper';

import { IntegrationsComponent } from './integrations.component';
import { CreateIntegrationComponent } from './create-integration/create-integration.component';
import { IntegrationsRoutingModule } from './integrations-routing.module';
import { IntegrationOverviewComponent } from './integration-overview/integration-overview.component';
import { IntegrationsNavigationComponent } from './integrations-navigation/integrations-navigation.component';

import { GioTableWrapperModule } from '../../shared/components/gio-table-wrapper/gio-table-wrapper.module';

@NgModule({
  declarations: [IntegrationsComponent, CreateIntegrationComponent, IntegrationOverviewComponent, IntegrationsNavigationComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatIcon,
    MatButton,
    MatError,
    MatFormField,
    MatHint,
    MatInput,
    MatLabel,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatIconButton,
    MatTooltip,
    MatTableModule,
    GioTableWrapperModule,
    IntegrationsRoutingModule,
    MatRadioGroup,
    MatRadioButton,
    GioRadioButtonModule,
    MatStepper,
    MatStep,
    MatStepLabel,
    MatStepperPrevious,
    MatStepperNext,
    MatStepperIcon,
    GioBreadcrumbModule,
    GioSubmenuModule,
    GioBannerModule,
    GioClipboardModule,
  ],
})
export class IntegrationsModule {}
