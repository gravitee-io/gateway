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
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { AsyncPipe, NgIf } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import {
  GioBannerModule,
  GioCardEmptyStateModule,
  GioFormFilePickerModule,
  GioFormSelectionInlineModule,
  GioLoaderModule,
  GioSaveBarModule,
} from '@gravitee/ui-particles-angular';
import { MatTooltipModule } from '@angular/material/tooltip';

import { ApiScoreRulesetsComponent } from './api-score-rulesets.component';
import { ImportApiScoreRulesetComponent } from './import/import-api-score-ruleset.component';
import { EditApiScoreRulesetComponent } from './edit/edit-api-score-ruleset.component';
import { ImportScoringFunctionComponent } from './import-function/import-scoring-function.component';
import { RulesetFormatPipe } from './ruleset-format-mapper.pipe';

import { ApiImportFilePickerComponent } from '../../api/component/api-import-file-picker/api-import-file-picker.component';
import { GioPermissionModule } from '../../../shared/components/gio-permission/gio-permission.module';
import { FilePreviewComponent } from '../../../shared/components/file-preview/file-preview.component';

@NgModule({
  declarations: [ApiScoreRulesetsComponent, ImportApiScoreRulesetComponent, EditApiScoreRulesetComponent, ImportScoringFunctionComponent],
  imports: [
    ReactiveFormsModule,
    RouterLink,
    AsyncPipe,
    NgIf,

    MatCardModule,
    MatButtonModule,
    MatMenuModule,
    MatExpansionModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,

    GioFormFilePickerModule,
    GioFormSelectionInlineModule,
    GioCardEmptyStateModule,
    GioBannerModule,
    GioSaveBarModule,
    GioLoaderModule,
    GioPermissionModule,

    FilePreviewComponent,
    ApiImportFilePickerComponent,

    RulesetFormatPipe,
  ],
  exports: [ApiScoreRulesetsComponent, ImportApiScoreRulesetComponent, EditApiScoreRulesetComponent, ImportScoringFunctionComponent],
})
export class ApiScoreRulesetsModule {}
