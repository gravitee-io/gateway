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
import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatChipHarness } from '@angular/material/chips/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatTableHarness } from '@angular/material/table/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

import { ApplicationTabLogsComponent } from './application-tab-logs.component';
import { fakeApplication } from '../../../../entities/application/application.fixture';
import { LogsResponse } from '../../../../entities/log/log';
import { fakeLog, fakeLogsResponse } from '../../../../entities/log/log.fixture';
import { fakeSubscription, fakeSubscriptionResponse } from '../../../../entities/subscription/subscription.fixture';
import { SubscriptionsResponse } from '../../../../entities/subscription/subscriptions-response';
import { AppTestingModule, TESTING_BASE_URL } from '../../../../testing/app-testing.module';

/* eslint-disable no-useless-escape */

describe('ApplicationTabLogsComponent', () => {
  let component: ApplicationTabLogsComponent;
  let fixture: ComponentFixture<ApplicationTabLogsComponent>;
  let httpTestingController: HttpTestingController;
  let harnessLoader: HarnessLoader;

  const APP_ID = 'app-id';
  const MOCK_DATE = new Date(1466424490000);

  const init = async (queryParams: unknown) => {
    const asBehaviorSubject = new BehaviorSubject(queryParams);

    await TestBed.configureTestingModule({
      imports: [ApplicationTabLogsComponent, AppTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { queryParams: asBehaviorSubject.asObservable() },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ApplicationTabLogsComponent);
    httpTestingController = TestBed.inject(HttpTestingController);
    harnessLoader = TestbedHarnessEnvironment.loader(fixture);
    component = fixture.componentInstance;
    component.application = fakeApplication({ id: APP_ID });

    const router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockImplementation((_, configuration) => {
      asBehaviorSubject.next(configuration?.queryParams ?? {});
      return new Promise(_ => true);
    });

    jest.useFakeTimers().setSystemTime(MOCK_DATE);
    fixture.detectChanges();
  };
  afterEach(() => {
    httpTestingController.verify();
  });

  describe('No page in query params', () => {
    beforeEach(async () => {
      await init({});
    });

    it('should show empty message when no logs', async () => {
      expectGetApplicationLogs(fakeLogsResponse({ data: [], metadata: { data: { total: 0 } } }));
      expectGetSubscriptions(fakeSubscriptionResponse());
      fixture.detectChanges();

      expect(getNoLogsMessageSection()).toBeTruthy();
    });

    it('should show log data', async () => {
      expectGetApplicationLogs(
        fakeLogsResponse({
          data: [
            fakeLog({ api: 'my-api', plan: 'my-plan', status: 201, timestamp: 1466424490000 }),
            fakeLog({ api: 'my-api-2', plan: 'my-plan', status: 204, timestamp: 1566424490000 }),
          ],
          metadata: {
            'my-api': { name: 'My API', version: '1.0' },
            'my-api-2': { name: 'My API 2', version: '2.0' },
            'my-plan': { name: 'My Plan' },
            data: { total: 3 },
          },
        }),
      );
      expectGetSubscriptions(fakeSubscriptionResponse());

      fixture.detectChanges();

      expect(getNoLogsMessageSection()).toBeFalsy();

      const apiCellRowOne = await getTextByRowIndexAndColumnName(0, 'api');
      expect(apiCellRowOne).toContain('My API');
      expect(apiCellRowOne).toContain('Version: 1.0');

      expect(await getTextByRowIndexAndColumnName(0, 'timestamp')).toContain('2016-06-20');
      expect(await getTextByRowIndexAndColumnName(0, 'responseStatus')).toEqual('201');
      expect(await getTextByRowIndexAndColumnName(0, 'httpMethod')).toEqual('GET');

      const apiCellRowTwo = await getTextByRowIndexAndColumnName(1, 'api');
      expect(apiCellRowTwo).toContain('My API 2');
      expect(apiCellRowTwo).toContain('Version: 2.0');

      expect(await getTextByRowIndexAndColumnName(1, 'timestamp')).toContain('2019-08-21');
      expect(await getTextByRowIndexAndColumnName(1, 'responseStatus')).toEqual('204');
      expect(await getTextByRowIndexAndColumnName(1, 'httpMethod')).toEqual('GET');
    });

    describe('Pagination', () => {
      describe('Only one page of results', () => {
        beforeEach(async () => {
          expectGetApplicationLogs(
            fakeLogsResponse({
              data: [
                fakeLog({ api: 'my-api', plan: 'my-plan', status: 201, timestamp: 1466424490000 }),
                fakeLog({ api: 'my-api-2', plan: 'my-plan', status: 204, timestamp: 1566424490000 }),
              ],
              metadata: {
                'my-api': { name: 'My API', version: '1.0' },
                'my-api-2': { name: 'My API 2', version: '2.0' },
                'my-plan': { name: 'My Plan' },
                data: { total: 3 },
              },
            }),
          );
          expectGetSubscriptions(fakeSubscriptionResponse());
          fixture.detectChanges();
        });
        it('should not allow previous page on load', async () => {
          const previousPageButton = await getPreviousPageButton();
          expect(previousPageButton).toBeTruthy();
          expect(await previousPageButton.isDisabled()).toEqual(true);
        });
        it('should not allow next page when on last page', async () => {
          const nextPageButton = await getNextPageButton();
          expect(nextPageButton).toBeTruthy();
          expect(await nextPageButton.isDisabled()).toEqual(true);
        });
        it('should highlight current page', async () => {
          const currentPaginationPage = await getCurrentPaginationPage();
          expect(currentPaginationPage).toBeTruthy();
          expect(await currentPaginationPage.getText()).toEqual('1');
        });
      });
      describe('First of many pages of results', () => {
        beforeEach(async () => {
          expectGetApplicationLogs(
            fakeLogsResponse({
              data: [
                fakeLog({ api: 'my-api', plan: 'my-plan', status: 201, timestamp: 1466424490000 }),
                fakeLog({ api: 'my-api-2', plan: 'my-plan', status: 204, timestamp: 1566424490000 }),
              ],
              metadata: {
                'my-api': { name: 'My API', version: '1.0' },
                'my-api-2': { name: 'My API 2', version: '2.0' },
                'my-plan': { name: 'My Plan' },
                data: { total: 79 },
              },
            }),
          );
          expectGetSubscriptions(fakeSubscriptionResponse());
          fixture.detectChanges();
        });
        it('should not allow previous page on load', async () => {
          const previousPageButton = await getPreviousPageButton();
          expect(previousPageButton).toBeTruthy();
          expect(await previousPageButton.isDisabled()).toEqual(true);
        });
        it('should allow next page', async () => {
          const nextPageButton = await getNextPageButton();
          expect(nextPageButton).toBeTruthy();
          expect(await nextPageButton.isDisabled()).toEqual(false);
        });
        it('should show "2" for next page', async () => {
          const secondPageButton = await getPageButtonByLabel('2');
          expect(secondPageButton).toBeTruthy();
          expect(await secondPageButton.isDisabled()).toEqual(false);
        });
        it('should show "3" for page option', async () => {
          const thirdPageButton = await getPageButtonByLabel('3');
          expect(thirdPageButton).toBeTruthy();
          expect(await thirdPageButton.isDisabled()).toEqual(false);
        });

        it('should show "8" for last page', async () => {
          const lastPageButton = await getPageButtonByLabel('8');
          expect(lastPageButton).toBeTruthy();
          expect(await lastPageButton.isDisabled()).toEqual(false);
        });

        it('should highlight current page', async () => {
          const currentPaginationPage = await getCurrentPaginationPage();
          expect(currentPaginationPage).toBeTruthy();
          expect(await currentPaginationPage.getText()).toEqual('1');
        });
        it('should go to next page via page number button', async () => {
          const secondPageButton = await getPageButtonByLabel('2');
          await secondPageButton.click();
          fixture.detectChanges();

          expectGetApplicationLogs(
            fakeLogsResponse({
              data: [
                fakeLog({ api: 'my-api', plan: 'my-plan', status: 201, timestamp: 1466424490000 }),
                fakeLog({ api: 'my-api-2', plan: 'my-plan', status: 204, timestamp: 1566424490000 }),
              ],
              metadata: {
                'my-api': { name: 'My API', version: '1.0' },
                'my-api-2': { name: 'My API 2', version: '2.0' },
                'my-plan': { name: 'My Plan' },
                data: { total: 79 },
              },
            }),
            2,
          );
        });
        it('should go to last page', async () => {
          const lastPageButton = await getPageButtonByLabel('8');
          await lastPageButton.click();

          expectGetApplicationLogs(
            fakeLogsResponse({
              data: [
                fakeLog({ api: 'my-api', plan: 'my-plan', status: 201, timestamp: 1466424490000 }),
                fakeLog({ api: 'my-api-2', plan: 'my-plan', status: 204, timestamp: 1566424490000 }),
              ],
              metadata: {
                'my-api': { name: 'My API', version: '1.0' },
                'my-api-2': { name: 'My API 2', version: '2.0' },
                'my-plan': { name: 'My Plan' },
                data: { total: 79 },
              },
            }),
            8,
          );
          fixture.detectChanges();
        });
      });
    });
  });

  describe('Page 3 in query params', () => {
    beforeEach(async () => {
      await init({ page: 3 });
    });

    describe('Third page of many pages of results', () => {
      beforeEach(async () => {
        expectGetApplicationLogs(
          fakeLogsResponse({
            data: [
              fakeLog({ api: 'my-api', plan: 'my-plan', status: 201, timestamp: 1466424490000 }),
              fakeLog({ api: 'my-api-2', plan: 'my-plan', status: 204, timestamp: 1566424490000 }),
            ],
            metadata: {
              'my-api': { name: 'My API', version: '1.0' },
              'my-api-2': { name: 'My API 2', version: '2.0' },
              'my-plan': { name: 'My Plan' },
              data: { total: 79 },
            },
          }),
          3,
        );
        expectGetSubscriptions(fakeSubscriptionResponse());
      });
      it('should allow previous page', async () => {
        const previousPageButton = await getPreviousPageButton();
        expect(previousPageButton).toBeTruthy();
        expect(await previousPageButton.isDisabled()).toEqual(false);
      });
      it('should allow next page', async () => {
        const nextPageButton = await getNextPageButton();
        expect(nextPageButton).toBeTruthy();
        expect(await nextPageButton.isDisabled()).toEqual(false);
      });
      it('should show "1" for first page', async () => {
        const firstPageButton = await getPageButtonByLabel('1');
        expect(firstPageButton).toBeTruthy();
        expect(await firstPageButton.isDisabled()).toEqual(false);
      });
      it('should show "2" for previous page', async () => {
        const secondPageButton = await getPageButtonByLabel('2');
        expect(secondPageButton).toBeTruthy();
        expect(await secondPageButton.isDisabled()).toEqual(false);
      });
      it('should show "8" for last page', async () => {
        const lastPageButton = await getPageButtonByLabel('8');
        expect(lastPageButton).toBeTruthy();
        expect(await lastPageButton.isDisabled()).toEqual(false);
      });
      it('should highlight current page', async () => {
        const currentPaginationPage = await getCurrentPaginationPage();
        expect(currentPaginationPage).toBeTruthy();
        expect(await currentPaginationPage.getText()).toEqual('3');
      });
      it('should go to previous page via arrow', async () => {
        const previousPageButton = await getPreviousPageButton();
        await previousPageButton.click();

        expectGetApplicationLogs(
          fakeLogsResponse({
            data: [
              fakeLog({ api: 'my-api', plan: 'my-plan', status: 201, timestamp: 1466424490000 }),
              fakeLog({ api: 'my-api-2', plan: 'my-plan', status: 204, timestamp: 1566424490000 }),
            ],
            metadata: {
              'my-api': { name: 'My API', version: '1.0' },
              'my-api-2': { name: 'My API 2', version: '2.0' },
              'my-plan': { name: 'My Plan' },
              data: { total: 79 },
            },
          }),
          2,
        );
        fixture.detectChanges();
      });
    });
  });

  describe('Filters', () => {
    describe('No filters in query params', () => {
      beforeEach(async () => {
        await init({});

        expectGetApplicationLogs(
          fakeLogsResponse({
            data: [
              fakeLog({ api: 'my-api', plan: 'my-plan', status: 201, timestamp: 1466424490000 }),
              fakeLog({ api: 'my-api-2', plan: 'my-plan', status: 204, timestamp: 1566424490000 }),
            ],
            metadata: {
              'my-api': { name: 'My API', version: '1.0' },
              'my-api-2': { name: 'My API 2', version: '2.0' },
              'my-plan': { name: 'My Plan' },
              data: { total: 3 },
            },
          }),
        );
        expectGetSubscriptions(fakeSubscriptionResponse());
        fixture.detectChanges();
      });

      it('should have no filters selected', async () => {
        expect(await noChipFiltersDisplayed()).toEqual(true);
      });
      it('should have search and reset buttons disabled', async () => {
        const resetButton = await getResetFilterButton();
        expect(await resetButton.isDisabled()).toEqual(true);

        const searchButton = await getSearchButton();
        expect(await searchButton.isDisabled()).toEqual(true);
      });
    });
    describe('APIs', () => {
      describe('One API in query params', () => {
        const API_ID = 'api-id';
        beforeEach(async () => {
          await init({ apis: [API_ID] });

          expectGetApplicationLogs(
            fakeLogsResponse({
              data: [
                fakeLog({ api: 'my-api', plan: 'my-plan', status: 201, timestamp: 1466424490000 }),
                fakeLog({ api: 'my-api-2', plan: 'my-plan', status: 204, timestamp: 1566424490000 }),
              ],
              metadata: {
                'my-api': { name: 'My API', version: '1.0' },
                'my-api-2': { name: 'My API 2', version: '2.0' },
                'my-plan': { name: 'My Plan' },
                data: { total: 3 },
              },
            }),
            1,
            `(api:\\"${API_ID}\\")`,
          );
          expectGetSubscriptions(
            fakeSubscriptionResponse({
              data: [fakeSubscription({ api: API_ID })],
              metadata: { [API_ID]: { name: 'API 1', apiVersion: '99' } },
            }),
          );
          fixture.detectChanges();
        });

        it('should have api pre-selected', async () => {
          expect(await noChipFiltersDisplayed()).toEqual(false);
          const apiFilter = await getApiSelection();
          expect(await apiFilter.isEmpty()).toEqual(false);
          expect(await apiFilter.getValueText()).toEqual('API 1 (99)');
        });

        it('should have search button disabled on load', async () => {
          const searchButton = await getSearchButton();
          expect(await searchButton.isDisabled()).toEqual(true);
        });

        it('should reset filter', async () => {
          const resetButton = await getResetFilterButton();
          expect(await resetButton.isDisabled()).toEqual(false);
          await resetButton.click();

          expect(await noChipFiltersDisplayed()).toEqual(true);
        });
      });
      describe('Two APIs in query params', () => {
        const API_ID_1 = 'api-id-1';
        const API_ID_2 = 'api-id-2';
        const API_ID_3 = 'api-id-3';
        beforeEach(async () => {
          await init({ apis: [API_ID_1, API_ID_2] });

          expectGetApplicationLogs(
            fakeLogsResponse({
              data: [
                fakeLog({ api: 'my-api', plan: 'my-plan', status: 201, timestamp: 1466424490000 }),
                fakeLog({ api: 'my-api-2', plan: 'my-plan', status: 204, timestamp: 1566424490000 }),
              ],
              metadata: {
                'my-api': { name: 'My API', version: '1.0' },
                'my-api-2': { name: 'My API 2', version: '2.0' },
                'my-plan': { name: 'My Plan' },
                data: { total: 3 },
              },
            }),
            1,
            `(api:\\"${API_ID_1}\\" OR \\"${API_ID_2}\\")`,
          );
          expectGetSubscriptions(
            fakeSubscriptionResponse({
              data: [fakeSubscription({ api: API_ID_1 }), fakeSubscription({ api: API_ID_2 }), fakeSubscription({ api: API_ID_3 })],
              metadata: {
                [API_ID_1]: { name: 'API 1', apiVersion: '99' },
                [API_ID_2]: { name: 'API 2', apiVersion: '1' },
                [API_ID_3]: { name: 'API 3', apiVersion: '24' },
              },
            }),
          );
          fixture.detectChanges();
        });

        it('should have two apis pre-selected', async () => {
          expect(await harnessLoader.getAllHarnesses(MatChipHarness)).toHaveLength(2);

          const api1Chip = await getChipFilter('API 1 (99)');
          expect(api1Chip).toBeTruthy();

          const api2Chip = await getChipFilter('API 2 (1)');
          expect(api2Chip).toBeTruthy();

          const apiFilter = await getApiSelection();
          expect(await apiFilter.isEmpty()).toEqual(false);
          expect(await apiFilter.getValueText()).toEqual('API 1 (99), API 2 (1)');
        });
      });
    });

    describe('HTTP Methods', () => {
      describe('One method in query params', () => {
        const GET_METHOD = '3';
        beforeEach(async () => {
          await init({ methods: [GET_METHOD] });

          expectGetApplicationLogs(fakeLogsResponse(), 1, `(method:\\"${GET_METHOD}\\")`);
          expectGetSubscriptions(fakeSubscriptionResponse());
          fixture.detectChanges();
        });

        it('should have method pre-selected', async () => {
          expect(await noChipFiltersDisplayed()).toEqual(false);
          const httpMethodFilter = await getHttpMethodSelection();
          expect(await httpMethodFilter.isEmpty()).toEqual(false);
          expect(await httpMethodFilter.getValueText()).toEqual('GET');

          const filterChips = await harnessLoader.getAllHarnesses(MatChipHarness);
          expect(filterChips).toHaveLength(1);

          expect(await filterChips[0].getText()).toEqual('GET');
        });

        it('should select POST HTTP Method filter', async () => {
          const httpMethodFilter = await getHttpMethodSelection();
          await httpMethodFilter.open();
          await httpMethodFilter.clickOptions({ text: 'POST' });
          await httpMethodFilter.close();
          expect(await httpMethodFilter.getValueText()).toEqual('GET, POST');

          const filterChips = await harnessLoader.getAllHarnesses(MatChipHarness);
          expect(filterChips).toHaveLength(2);

          expect(await filterChips[1].getText()).toEqual('POST');

          const searchButton = await getSearchButton();
          expect(await searchButton.isDisabled()).toEqual(false);

          await searchButton.click();
          expectGetApplicationLogs(fakeLogsResponse(), 1, `(method:\\"${GET_METHOD}\\" OR \\"7\\")`);
        });
        it('should reset filter', async () => {
          const resetButton = await getResetFilterButton();
          expect(await resetButton.isDisabled()).toEqual(false);
          await resetButton.click();

          expect(await noChipFiltersDisplayed()).toEqual(true);
        });
      });
    });

    describe('Response Times', () => {
      describe('One method in query params', () => {
        const RESPONSE_TIME = '0 TO 100';
        beforeEach(async () => {
          await init({ responseTimes: RESPONSE_TIME });

          expectGetApplicationLogs(fakeLogsResponse(), 1, `(response-time:[${RESPONSE_TIME}])`);
          expectGetSubscriptions(fakeSubscriptionResponse());
          fixture.detectChanges();
        });

        it('should have method pre-selected', async () => {
          expect(await noChipFiltersDisplayed()).toEqual(false);
          const responseTimeSelection = await getResponseTimesSelection();
          expect(await responseTimeSelection.isEmpty()).toEqual(false);
          expect(await responseTimeSelection.getValueText()).toEqual('< 100 ms');

          const filterChips = await harnessLoader.getAllHarnesses(MatChipHarness);
          expect(filterChips).toHaveLength(1);

          expect(await filterChips[0].getText()).toEqual('Response time < 100 ms');
        });

        it('should select 100 - 200 response time filter', async () => {
          const responseTimeSelection = await getResponseTimesSelection();
          await responseTimeSelection.open();
          await responseTimeSelection.clickOptions({ text: '100 to 200 ms' });
          await responseTimeSelection.close();
          expect(await responseTimeSelection.getValueText()).toEqual('< 100 ms, 100 to 200 ms');

          const filterChips = await harnessLoader.getAllHarnesses(MatChipHarness);
          expect(filterChips).toHaveLength(2);

          expect(await filterChips[1].getText()).toEqual('Response time 100 - 200 ms');

          const searchButton = await getSearchButton();
          expect(await searchButton.isDisabled()).toEqual(false);

          await searchButton.click();
          expectGetApplicationLogs(fakeLogsResponse(), 1, `(response-time:[${RESPONSE_TIME}] OR [100 TO 200])`);
        });
        it('should reset filter', async () => {
          const resetButton = await getResetFilterButton();
          expect(await resetButton.isDisabled()).toEqual(false);
          await resetButton.click();

          expect(await noChipFiltersDisplayed()).toEqual(true);
        });
      });
    });

    describe('Period', () => {
      describe('No period in query params', () => {
        beforeEach(async () => {
          await init({});

          expectGetApplicationLogs(fakeLogsResponse());
          expectGetSubscriptions(fakeSubscriptionResponse());
          fixture.detectChanges();
        });

        it('should have "Last day" pre-selected + empty filters', async () => {
          expect(await noChipFiltersDisplayed()).toEqual(true);

          const periodSelection = await getPeriodSelection();
          expect(await periodSelection.getValueText()).toEqual('Last day');
        });
      });
      describe('One period in query params', () => {
        const LAST_3_DAYS = '3d';
        beforeEach(async () => {
          await init({ period: LAST_3_DAYS });

          const dateMinusThreeDays = MOCK_DATE.getTime() - 86400000 * 3;

          expectGetApplicationLogs(fakeLogsResponse(), 1, undefined, undefined, dateMinusThreeDays);
          expectGetSubscriptions(fakeSubscriptionResponse());
          fixture.detectChanges();
        });

        it('should have period pre-selected', async () => {
          const periodSelection = await getPeriodSelection();
          expect(await periodSelection.isEmpty()).toEqual(false);
          expect(await periodSelection.getValueText()).toEqual('Last 3 days');

          expect(await noChipFiltersDisplayed()).toEqual(true);
        });

        it('should select "Last 6 hours"', async () => {
          const periodSelection = await getPeriodSelection();
          await periodSelection.open();
          expect(await periodSelection.isOpen()).toEqual(true);
          await periodSelection.clickOptions({ text: 'Last day' });

          expect(await periodSelection.getValueText()).toEqual('Last day');

          expect(await noChipFiltersDisplayed()).toEqual(true);

          const searchButton = await getSearchButton();
          expect(await searchButton.isDisabled()).toEqual(false);

          await searchButton.click();
          expectGetApplicationLogs(fakeLogsResponse());
        });
        it('should reset filter but keep same period', async () => {
          const httpMethodsSelection = await getHttpMethodSelection();
          await httpMethodsSelection.open();
          await httpMethodsSelection.clickOptions({ text: 'GET' });

          const resetButton = await getResetFilterButton();
          expect(await resetButton.isDisabled()).toEqual(false);

          await resetButton.click();

          expect(await noChipFiltersDisplayed()).toEqual(true);
          const periodSelection = await getPeriodSelection();
          expect(await periodSelection.isEmpty()).toEqual(false);
          expect(await periodSelection.getValueText()).toEqual('Last 3 days');
        });
      });
    });
  });

  function expectGetApplicationLogs(logsResponse: LogsResponse, page: number = 1, query?: string, to?: number, from?: number) {
    const toInMilliseconds = to ?? MOCK_DATE.getTime();
    const fromInMilliseconds = from ?? toInMilliseconds - 86400000;
    httpTestingController
      .expectOne(
        `${TESTING_BASE_URL}/applications/${APP_ID}/logs?page=${page}&size=10&from=${fromInMilliseconds}&to=${toInMilliseconds}&order=DESC&field=@timestamp` +
          `${query ? '&query=' + query : ''}`,
      )
      .flush(logsResponse);
  }

  function expectGetSubscriptions(subscriptionsResponse: SubscriptionsResponse) {
    httpTestingController
      .expectOne(`${TESTING_BASE_URL}/subscriptions?applicationId=${APP_ID}&statuses=ACCEPTED&statuses=PAUSED&size=-1`)
      .flush(subscriptionsResponse);
  }

  function getNoLogsMessageSection(): DebugElement {
    return fixture.debugElement.query(By.css('.no-logs'));
  }

  async function getTextByRowIndexAndColumnName(i: number, columnName: string): Promise<string> {
    return await harnessLoader
      .getHarness(MatTableHarness)
      .then(table => table.getRows())
      .then(rows => rows[i].getCells({ columnName }))
      .then(cells => cells[0].getText());
  }

  async function getPreviousPageButton(): Promise<MatButtonHarness> {
    return await harnessLoader.getHarness(MatButtonHarness.with({ selector: '[aria-label="Previous page of logs"]', variant: 'icon' }));
  }

  async function getNextPageButton(): Promise<MatButtonHarness> {
    return await harnessLoader.getHarness(MatButtonHarness.with({ selector: '[aria-label="Next page of logs"]', variant: 'icon' }));
  }

  async function getPageButtonByLabel(label: string): Promise<MatButtonHarness> {
    return await harnessLoader.getHarness(MatButtonHarness.with({ text: label }));
  }

  async function getCurrentPaginationPage(): Promise<MatButtonHarness> {
    return await harnessLoader.getHarness(MatButtonHarness.with({ selector: '[aria-label="Current page of logs"]' }));
  }

  async function noChipFiltersDisplayed(): Promise<boolean> {
    return await harnessLoader.getAllHarnesses(MatChipHarness).then(harnesses => harnesses.length === 0);
  }

  async function getChipFilter(text: string): Promise<MatChipHarness> {
    return await harnessLoader.getHarness(MatChipHarness.with({ text }));
  }

  async function getApiSelection(): Promise<MatSelectHarness> {
    return await harnessLoader.getHarness(MatSelectHarness.with({ selector: '[aria-label="Filter by API"]' }));
  }

  async function getHttpMethodSelection(): Promise<MatSelectHarness> {
    return await harnessLoader.getHarness(MatSelectHarness.with({ selector: '[aria-label="Filter by HTTP Method"]' }));
  }

  async function getResponseTimesSelection(): Promise<MatSelectHarness> {
    return await harnessLoader.getHarness(MatSelectHarness.with({ selector: '[aria-label="Filter by Response Time"]' }));
  }

  async function getPeriodSelection(): Promise<MatSelectHarness> {
    return await harnessLoader.getHarness(MatSelectHarness.with({ selector: '[aria-label="Filter by Period"]' }));
  }

  async function getResetFilterButton(): Promise<MatButtonHarness> {
    return await harnessLoader.getHarness(MatButtonHarness.with({ selector: '[aria-label="Reset filters"]' }));
  }
  async function getSearchButton(): Promise<MatButtonHarness> {
    return await harnessLoader.getHarness(MatButtonHarness.with({ selector: '[aria-label="Apply filters"]' }));
  }
});
