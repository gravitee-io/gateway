/*
 * Copyright (C) 2024 The Gravitee team (http://gravitee.io)
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
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ConfigService } from './config.service';
import { Api } from '../entities/api/api';
import { ApisResponse } from '../entities/api/apis-response';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(
    private readonly http: HttpClient,
    private configService: ConfigService,
  ) {}

  list(page = 1, size = 9): Observable<ApisResponse> {
    return this.http.get<ApisResponse>(`${this.configService.baseURL}/apis`, {
      params: {
        page,
        size,
      },
    });
  }

  details(apiId: string): Observable<Api> {
    return this.http.get<Api>(`${this.configService.baseURL}/apis/${apiId}`);
  }
}
