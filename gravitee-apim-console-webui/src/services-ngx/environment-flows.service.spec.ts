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
import { HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { EnvironmentFlowsService } from './environment-flows.service';

import { GioTestingModule } from '../shared/testing';

describe('EnvironmentFlowsService', () => {
  let _httpTestingController: HttpTestingController;
  let service: EnvironmentFlowsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [GioTestingModule],
    });

    _httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject<EnvironmentFlowsService>(EnvironmentFlowsService);
  });

  describe('list', () => {
    it('should call the API', (done) => {
      service.list().subscribe((flows) => {
        expect(flows.data.length).toEqual(1);
        done();
      });
    });
  });
});
