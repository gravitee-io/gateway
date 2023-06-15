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
import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Constants } from '../entities/Constants';
import { PolicyDocumentation, PolicyListItem, PolicySchema } from '../entities/policy';
import { PolicyPlugin } from '../entities/management-api-v2';

@Injectable({
  providedIn: 'root',
})
export class PolicyV2Service {
  constructor(private readonly http: HttpClient, @Inject('Constants') private readonly constants: Constants) {}

  list(): Observable<PolicyPlugin[]> {
    return this.http.get<PolicyListItem[]>(`${this.constants.v2BaseURL}/plugins/policies`);
  }

  getSchema(policyId: string): Observable<PolicySchema> {
    return this.http.get<PolicySchema>(`${this.constants.v2BaseURL}/plugins/policies/${policyId}/schema`);
  }

  getDocumentation(policyId: string): Observable<PolicyDocumentation> {
    return this.http
      .get(`${this.constants.v2BaseURL}/plugins/policies/${policyId}/documentation`, {
        responseType: 'text',
      })
      .pipe(map((buffer) => buffer.toString()));
  }
}
