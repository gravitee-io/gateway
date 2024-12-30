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

import { Constants } from '../entities/Constants';
import {
  CreateFunctionRequestData,
  CreateRulesetRequestData,
  EditRulesetRequestData,
  ScoringFunctionsResponse,
  ScoringRuleset,
  ScoringRulesetsResponse,
} from '../entities/management-api-v2/api/v4/ruleset';

@Injectable({
  providedIn: 'root',
})
export class RulesetV2Service {
  constructor(
    @Inject(Constants) private readonly constants: Constants,
    private httpClient: HttpClient,
  ) {}

  public listRulesets(): Observable<ScoringRulesetsResponse> {
    const url = `${this.constants.env.v2BaseURL}/scoring/rulesets`;
    return this.httpClient.get<ScoringRulesetsResponse>(url);
  }

  public getRuleset(id: string): Observable<ScoringRuleset> {
    const url = `${this.constants.env.v2BaseURL}/scoring/rulesets/${id}`;
    return this.httpClient.get<ScoringRuleset>(url);
  }

  public createRuleset(data: CreateRulesetRequestData) {
    const url = `${this.constants.env.v2BaseURL}/scoring/rulesets`;
    return this.httpClient.post(url, data);
  }

  public editRuleset(id: string, data: EditRulesetRequestData) {
    const url = `${this.constants.env.v2BaseURL}/scoring/rulesets/${id}`;
    return this.httpClient.put(url, data);
  }

  public deleteRuleset(id: string) {
    const url = `${this.constants.env.v2BaseURL}/scoring/rulesets/${id}`;
    return this.httpClient.delete(url);
  }

  public listFunctions(): Observable<ScoringFunctionsResponse> {
    const url = `${this.constants.env.v2BaseURL}/scoring/functions`;
    return this.httpClient.get<ScoringFunctionsResponse>(url);
  }

  public createFunction(data: CreateFunctionRequestData) {
    const url = `${this.constants.env.v2BaseURL}/scoring/functions`;
    return this.httpClient.post(url, data);
  }

  public deleteFunction(id: string) {
    const url = `${this.constants.env.v2BaseURL}/scoring/functions/${id}`;
    return this.httpClient.delete(url);
  }
}
