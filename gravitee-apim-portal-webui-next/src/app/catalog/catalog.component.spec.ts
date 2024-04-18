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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardHarness } from '@angular/material/card/testing';
import { RouterModule } from '@angular/router';

import { CatalogComponent } from './catalog.component';
import { ApiCardHarness } from '../../components/api-card/api-card.harness';
import { fakeApi, fakeApisResponse } from '../../entities/api/api.fixtures';
import { ApisResponse } from '../../entities/api/apis-response';
import { AppTestingModule, TESTING_BASE_URL } from '../../testing/app-testing.module';

describe('CatalogComponent', () => {
  let fixture: ComponentFixture<CatalogComponent>;
  let harnessLoader: HarnessLoader;
  let httpTestingController: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CatalogComponent, AppTestingModule, RouterModule.forRoot([])],
    }).compileComponents();

    fixture = TestBed.createComponent(CatalogComponent);
    httpTestingController = TestBed.inject(HttpTestingController);
    harnessLoader = TestbedHarnessEnvironment.loader(fixture);

    fixture.detectChanges();
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  describe('populated api list', () => {
    beforeEach(() => {
      expectApiList(
        fakeApisResponse({
          data: [
            fakeApi({
              id: '1',
              name: 'Test title',
              version: 'v.1.2',
              description:
                'Get real-time weather updates, forecasts, and historical data to enhance your applications with accurate weather information.',
            }),
          ],
          metadata: {
            pagination: {
              current_page: 1,
              total_pages: 2,
            },
          },
        }),
      );
    });

    it('should render banner text', () => {
      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.querySelector('h1')?.textContent).toContain('Welcome to Gravitee Developer Portal!');
    });

    it('should show API list', async () => {
      const apiCard = await harnessLoader.getHarness(ApiCardHarness);
      expect(apiCard).toBeDefined();
      expect(await apiCard.getTitle()).toEqual('Test title');
      expect(await apiCard.getDescription()).toEqual(
        'Get real-time weather updates, forecasts, and historical data to enhance your applications with accurate weather information.',
      );
      expect(await apiCard.getVersion()).toEqual('v.1.2');
    });
    it('should call second page after scrolled event', async () => {
      const apiCard = await harnessLoader.getAllHarnesses(ApiCardHarness);
      expect(apiCard).toBeDefined();
      expect(apiCard.length).toEqual(1);
      expect(await apiCard[0].getTitle()).toEqual('Test title');

      document.getElementsByClassName('api-list__container')[0].dispatchEvent(new Event('scrolled'));
      expectApiList(
        fakeApisResponse({
          data: [fakeApi({ id: 'second-page-api', name: 'second page api', version: '24' })],
          metadata: {
            pagination: {
              current_page: 2,
              total_pages: 2,
            },
          },
        }),
        2,
      );
      fixture.detectChanges();

      const allHarnesses = await harnessLoader.getAllHarnesses(ApiCardHarness);
      expect(allHarnesses.length).toEqual(2);

      const secondPageApi = await harnessLoader.getHarnessOrNull(ApiCardHarness.with({ selector: '[ng-reflect-id="second-page-api"]' }));
      expect(secondPageApi).toBeTruthy();
    });

    it('should not call page if on last page', async () => {
      const apiCard = await harnessLoader.getAllHarnesses(ApiCardHarness);
      expect(apiCard.length).toEqual(1);

      document.getElementsByClassName('api-list__container')[0].dispatchEvent(new Event('scrolled'));
      expectApiList(
        fakeApisResponse({
          data: [fakeApi({ id: 'second-page-api' })],
          metadata: {
            pagination: {
              current_page: 2,
              total_pages: 2,
            },
          },
        }),
        2,
      );
      fixture.detectChanges();

      const allHarnesses = await harnessLoader.getAllHarnesses(ApiCardHarness);
      expect(allHarnesses.length).toEqual(2);

      document.getElementsByClassName('api-list__container')[0].dispatchEvent(new Event('scrolled'));
      httpTestingController.expectNone(`${TESTING_BASE_URL}/apis?page=3&size=9`);
    });
  });

  describe('empty component', () => {
    it('should show empty API list', async () => {
      expectApiList(fakeApisResponse({ data: [] }));
      const noApiCard = await harnessLoader.getHarness(MatCardHarness.with({ selector: '#no-apis' }));
      expect(noApiCard).toBeTruthy();
      expect(await noApiCard.getText()).toContain('Sorry, there are no APIs listed yet.');
    });
  });

  function expectApiList(apisResponse: ApisResponse = fakeApisResponse(), page: number = 1, size: number = 9) {
    httpTestingController.expectOne(`${TESTING_BASE_URL}/apis?page=${page}&size=${size}`).flush(apisResponse);
  }
});
