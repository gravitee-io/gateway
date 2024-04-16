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

import { ApplicationSubscriptionService } from './application-subscription.service';

import { CONSTANTS_TESTING, GioTestingModule } from '../shared/testing';

describe('ApplicationSubscriptionService', () => {
  let httpTestingController: HttpTestingController;
  let service: ApplicationSubscriptionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [GioTestingModule],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(ApplicationSubscriptionService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  describe('closeSubscription', () => {
    it('should call the API', (done) => {
      service.closeSubscription('applicationId', 'subscriptionId').subscribe(() => {
        done();
      });

      httpTestingController
        .expectOne({
          method: 'DELETE',
          url: `${CONSTANTS_TESTING.env.baseURL}/applications/applicationId/subscriptions/subscriptionId`,
        })
        .flush({});
    });
  });
});
