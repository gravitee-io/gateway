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
import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Constants } from '../entities/Constants';
import { MetadataResponse, SearchApiMetadataParam } from '../entities/management-api-v2';

@Injectable({
  providedIn: 'root',
})
export class ApiMetadataV2Service {
  constructor(private readonly http: HttpClient, @Inject(Constants) private readonly constants: Constants) {}

  search(apiId: string, queryParam?: SearchApiMetadataParam): Observable<MetadataResponse> {
    let params = new HttpParams();
    params = params.append('page', queryParam?.page ?? 1);
    params = params.append('perPage', queryParam?.perPage ?? 10);

    if (queryParam?.source) params = params.append('source', queryParam.source);
    if (queryParam?.sortBy) params = params.append('sortBy', queryParam.sortBy);

    return this.http.get<MetadataResponse>(`${this.constants.env.v2BaseURL}/apis/${apiId}/metadata`, { params });
  }
}
