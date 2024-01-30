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
import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { MatLegacySlideToggleModule as MatSlideToggleModule } from '@angular/material/legacy-slide-toggle';
import { GioBannerModule, GioFormJsonSchemaModule, GioFormSlideToggleModule } from '@gravitee/ui-particles-angular';
import { ReactiveFormsModule } from '@angular/forms';

import { PolicyStudioConfigComponent } from './policy-studio-config.component';

@NgModule({
  imports: [CommonModule, ReactiveFormsModule, MatSlideToggleModule, GioFormSlideToggleModule, GioFormJsonSchemaModule, GioBannerModule],
  declarations: [PolicyStudioConfigComponent],
  exports: [PolicyStudioConfigComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PolicyStudioConfigModule {}
