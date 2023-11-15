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
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

import { ConsoleCustomization } from '../entities/management-api-v2/consoleCustomization';
import { Constants } from '../entities/Constants';

@Injectable({ providedIn: 'root' })
export class UiCustomizationService {
  constructor(private readonly http: HttpClient, @Inject('Constants') private readonly constants: Constants) {}

  getConsoleCustomization(): Observable<ConsoleCustomization> {
    return this.http.get<ConsoleCustomization>(`${this.constants.v2BaseURL}/ui/customization`);
  }
}
