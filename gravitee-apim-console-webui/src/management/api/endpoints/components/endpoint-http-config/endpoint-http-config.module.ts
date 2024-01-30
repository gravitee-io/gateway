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
import { NgModule } from '@angular/core';
import { GioFormHeadersModule, GioFormSlideToggleModule } from '@gravitee/ui-particles-angular';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { MatLegacySelectModule as MatSelectModule } from '@angular/material/legacy-select';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { MatLegacySlideToggleModule as MatSlideToggleModule } from '@angular/material/legacy-slide-toggle';

import { EndpointHttpConfigComponent } from './endpoint-http-config.component';

import { SslTrustStoreFormModule } from '../ssl-truststore-form/ssl-truststore-form.module';
import { SslKeyStoreFormModule } from '../ssl-keystore-form/ssl-keystore-form.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatSlideToggleModule,

    GioFormSlideToggleModule,
    GioFormHeadersModule,
    SslTrustStoreFormModule,
    SslKeyStoreFormModule,
  ],
  declarations: [EndpointHttpConfigComponent],
  exports: [EndpointHttpConfigComponent],
})
export class EndpointHttpConfigModule {}
