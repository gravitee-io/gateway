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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { HttpTestingController } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatIconTestingModule } from '@angular/material/icon/testing';
import { InteractivityChecker } from '@angular/cdk/a11y';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatRadioGroupHarness } from '@angular/material/radio/testing';
import { MatDialogHarness } from '@angular/material/dialog/testing';
import { MatButtonHarness } from '@angular/material/button/testing';

import { ApiPortalSubscriptionEditComponent } from './api-portal-subscription-edit.component';
import { ApiPortalSubscriptionEditHarness } from './api-portal-subscription-edit.harness';

import { CurrentUserService, UIRouterState, UIRouterStateParams } from '../../../../../ajs-upgraded-providers';
import { CONSTANTS_TESTING, GioHttpTestingModule } from '../../../../../shared/testing';
import { ApiPortalSubscriptionsModule } from '../api-portal-subscriptions.module';
import { User as DeprecatedUser } from '../../../../../entities/user';
import { fakeBasePlan, fakePlanV4, fakeSubscription, Plan, PlanMode, Subscription } from '../../../../../entities/management-api-v2';
import { fakeApplication } from '../../../../../entities/application/Application.fixture';
import { ApiKeyMode } from '../../../../../entities/application/application';

const SUBSCRIPTION_ID = 'my-nice-subscription';
const API_ID = 'api_1';
const APP_ID = 'my-application';
const PLAN_ID = 'a-nice-plan-id';
const BASIC_SUBSCRIPTION = () =>
  fakeSubscription({
    id: SUBSCRIPTION_ID,
    plan: fakeBasePlan({ id: PLAN_ID }),
    status: 'ACCEPTED',
    application: {
      id: APP_ID,
      name: 'My Application',
      domain: 'https://my-domain.com',
      type: 'My special type',
      primaryOwner: {
        id: 'my-primary-owner',
        displayName: 'Primary Owner',
      },
    },
  });

describe('ApiPortalSubscriptionEditComponent', () => {
  const fakeUiRouter = { go: jest.fn() };
  const currentUser = new DeprecatedUser();
  currentUser.userPermissions = ['api-subscription-u', 'api-subscription-r', 'api-subscription-d'];

  let fixture: ComponentFixture<ApiPortalSubscriptionEditComponent>;
  let loader: HarnessLoader;
  let httpTestingController: HttpTestingController;

  const init = async () => {
    await TestBed.configureTestingModule({
      imports: [ApiPortalSubscriptionsModule, NoopAnimationsModule, GioHttpTestingModule, MatIconTestingModule],
      providers: [
        { provide: UIRouterState, useValue: fakeUiRouter },
        { provide: CurrentUserService, useValue: { currentUser } },
        {
          provide: InteractivityChecker,
          useValue: {
            isFocusable: () => true, // This traps focus checks and so avoid warnings when dealing with
            isTabbable: () => true, // Allows tabbing and avoids warnings
          },
        },
      ],
    }).compileComponents();
  };

  beforeEach(async () => {
    await init();
  });

  afterEach(() => {
    jest.clearAllMocks();
    httpTestingController.verify();
  });

  describe('details', () => {
    it('should load accepted subscription', async () => {
      await initComponent();
      expectApplicationGet();

      const harness = await loader.getHarness(ApiPortalSubscriptionEditHarness);

      expect(await harness.getId()).toEqual(SUBSCRIPTION_ID);
      expect(await harness.getPlan()).toEqual('Default plan (API_KEY)');
      expect(await harness.getStatus()).toEqual('ACCEPTED');
      expect(await harness.getApplication()).toEqual('My Application (Primary Owner) - Type: My special type');
      expect(await harness.getSubscribedBy()).toEqual('My subscriber');
      expect(await harness.getSubscriberMessage()).toEqual('My consumer message');
      expect(await harness.getPublisherMessage()).toEqual('My publisher message');
      expect(await harness.getCreatedAt()).toEqual('Jan 1, 2020 12:00:00.000 AM');
      expect(await harness.getProcessedAt()).toEqual('Jan 1, 2020 12:00:00.000 AM');
      expect(await harness.getClosedAt()).toEqual('-');
      expect(await harness.getPausedAt()).toEqual('-');
      expect(await harness.getStartingAt()).toEqual('Jan 1, 2020 12:00:00.000 AM');
      expect(await harness.getEndingAt()).toEqual('-');
      expect(await harness.getDomain()).toEqual('https://my-domain.com');

      expect(await harness.footerIsVisible()).toEqual(true);

      expect(await harness.transferBtnIsVisible()).toEqual(true);
      expect(await harness.pauseBtnIsVisible()).toEqual(true);
      expect(await harness.resumeBtnIsVisible()).toEqual(false);
      expect(await harness.changeEndDateBtnIsVisible()).toEqual(true);
      expect(await harness.closeBtnIsVisible()).toEqual(true);

      expect(await harness.validateBtnIsVisible()).toEqual(false);
      expect(await harness.rejectBtnIsVisible()).toEqual(false);

      await harness.goBackToSubscriptionsList();
      expect(fakeUiRouter.go).toHaveBeenCalledWith('management.apis.ng.subscriptions');
    });

    it('should load pending subscription', async () => {
      const pendingSubscription = BASIC_SUBSCRIPTION();
      pendingSubscription.status = 'PENDING';
      await initComponent(pendingSubscription);
      expectApplicationGet();

      const harness = await loader.getHarness(ApiPortalSubscriptionEditHarness);

      expect(await harness.getStatus()).toEqual('PENDING');

      expect(await harness.footerIsVisible()).toEqual(true);

      expect(await harness.transferBtnIsVisible()).toEqual(false);
      expect(await harness.pauseBtnIsVisible()).toEqual(false);
      expect(await harness.changeEndDateBtnIsVisible()).toEqual(false);
      expect(await harness.closeBtnIsVisible()).toEqual(false);

      expect(await harness.validateBtnIsVisible()).toEqual(true);
      expect(await harness.rejectBtnIsVisible()).toEqual(true);
    });

    it('should not load footer in read-only mode', async () => {
      await initComponent(BASIC_SUBSCRIPTION(), ['api-subscription-r']);
      expectApplicationGet(ApiKeyMode.SHARED);

      const harness = await loader.getHarness(ApiPortalSubscriptionEditHarness);
      expect(await harness.footerIsVisible()).toEqual(false);
    });
  });

  describe('transfer subscription', () => {
    it('should transfer subscription to new push plan', async () => {
      const pushPlanSubscription = BASIC_SUBSCRIPTION();
      pushPlanSubscription.plan = fakeBasePlan({ id: PLAN_ID, security: { type: undefined, configuration: {} } });
      await initComponent(pushPlanSubscription);

      const harness = await loader.getHarness(ApiPortalSubscriptionEditHarness);
      expect(await harness.transferBtnIsVisible()).toEqual(true);

      await harness.openTransferDialog();

      const transferDialog = await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(
        MatDialogHarness.with({ selector: '#transferSubscriptionDialog' }),
      );
      expectApiPlansList(
        [
          fakePlanV4({ id: PLAN_ID, name: 'original', mode: 'PUSH', security: { type: undefined, configuration: {} } }),
          fakePlanV4({ id: 'new-id', name: 'new', generalConditions: '', mode: 'PUSH', security: { type: undefined, configuration: {} } }),
          fakePlanV4({ id: 'other-id', name: 'other', mode: 'PUSH', security: { type: undefined, configuration: {} } }),
        ],
        [],
        'PUSH',
      );
      expect(await transferDialog.getTitleText()).toEqual('Transfer your subscription');

      const radioGroup = await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(MatRadioGroupHarness);
      expect(await radioGroup.getRadioButtons().then((buttons) => buttons.length)).toEqual(2);
      expect(await radioGroup.getRadioButtons({ label: 'other' }).then((btn) => btn[0].isDisabled())).toEqual(true);
      expect(await radioGroup.getRadioButtons({ label: 'new' }).then((btn) => btn[0].isDisabled())).toEqual(false);

      const transferBtn = await transferDialog.getHarness(MatButtonHarness.with({ text: 'Transfer' }));
      expect(await transferBtn.isDisabled()).toEqual(true);

      await radioGroup.checkRadioButton({ label: 'new' });
      expect(await radioGroup.getCheckedValue()).toEqual('new-id');

      expect(await transferBtn.isDisabled()).toEqual(false);
      await transferBtn.click();

      expectApiSubscriptionTransfer(
        SUBSCRIPTION_ID,
        'new-id',
        fakeSubscription({ id: SUBSCRIPTION_ID, plan: fakeBasePlan({ id: 'new-id', name: 'new' }) }),
      );
      const newSubscription = BASIC_SUBSCRIPTION();
      newSubscription.plan = fakePlanV4({
        id: 'new-id',
        name: 'new',
        generalConditions: '',
        security: { type: undefined, configuration: {} },
      });
      expectApiSubscriptionGet(newSubscription);

      expect(await harness.getPlan()).toEqual('new');
    });

    it('should not transfer subscription on cancel', async () => {
      await initComponent();
      expectApplicationGet();

      const harness = await loader.getHarness(ApiPortalSubscriptionEditHarness);
      expect(await harness.transferBtnIsVisible()).toEqual(true);

      await harness.openTransferDialog();

      const transferDialog = await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(
        MatDialogHarness.with({ selector: '#transferSubscriptionDialog' }),
      );
      expectApiPlansList(
        [
          fakePlanV4({ id: PLAN_ID, name: 'original' }),
          fakePlanV4({ id: 'new-id', name: 'new', generalConditions: '' }),
          fakePlanV4({ id: 'other-id', name: 'other' }),
        ],
        ['API_KEY'],
        'STANDARD',
      );

      await transferDialog.getHarness(MatButtonHarness.with({ text: 'Cancel' })).then((btn) => btn.click());

      expect(await harness.getPlan()).toEqual('Default plan (API_KEY)');
    });
  });

  describe('pause subscription', () => {
    const API_KEYS_DIALOG_TXT = 'All Api-keys associated to this subscription will be paused and unusable.';

    it('should pause subscription', async () => {
      await initComponent(BASIC_SUBSCRIPTION());
      expectApplicationGet(ApiKeyMode.EXCLUSIVE);

      const harness = await loader.getHarness(ApiPortalSubscriptionEditHarness);
      expect(await harness.pauseBtnIsVisible()).toEqual(true);

      await harness.openPauseDialog();

      const pauseDialog = await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(
        MatDialogHarness.with({ selector: '#confirmPauseSubscriptionDialog' }),
      );
      expect(await pauseDialog.getTitleText()).toEqual('Pause your subscription');
      // expect the dialog specific to sharedApiKeyMode to be present
      expect(await pauseDialog.getContentText().then((txt) => txt.indexOf(API_KEYS_DIALOG_TXT) !== -1)).toEqual(true);

      const pauseBtn = await pauseDialog.getHarness(MatButtonHarness.with({ text: 'Pause' }));
      expect(await pauseBtn.isDisabled()).toEqual(false);
      await pauseBtn.click();

      const pausedSubscription = BASIC_SUBSCRIPTION();
      pausedSubscription.status = 'PAUSED';

      expectApiSubscriptionPause(SUBSCRIPTION_ID, pausedSubscription);
      expectApiSubscriptionGet(pausedSubscription);
      expectApplicationGet();

      expect(await harness.getStatus()).toEqual('PAUSED');
      expect(await harness.pauseBtnIsVisible()).toEqual(false);
      expect(await harness.resumeBtnIsVisible()).toEqual(true);
    });
    it('should not pause subscription on cancel', async () => {
      await initComponent();
      expectApplicationGet();

      const harness = await loader.getHarness(ApiPortalSubscriptionEditHarness);
      await harness.openPauseDialog();

      const pauseDialog = await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(
        MatDialogHarness.with({ selector: '#confirmPauseSubscriptionDialog' }),
      );
      const cancelBtn = await pauseDialog.getHarness(MatButtonHarness.with({ text: 'Cancel' }));
      await cancelBtn.click();

      expect(await harness.getStatus()).toEqual('ACCEPTED');
    });
    it('should not contain info about shared api key', async () => {
      const keylessSubscription = BASIC_SUBSCRIPTION();
      keylessSubscription.plan = fakeBasePlan({ security: { type: 'KEY_LESS' } });
      await initComponent(keylessSubscription);

      const harness = await loader.getHarness(ApiPortalSubscriptionEditHarness);
      await harness.openPauseDialog();

      const pauseDialog = await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(
        MatDialogHarness.with({ selector: '#confirmPauseSubscriptionDialog' }),
      );
      expect(await pauseDialog.getContentText().then((txt) => txt.indexOf(API_KEYS_DIALOG_TXT))).toEqual(-1);
    });
  });

  async function initComponent(
    subscription: Subscription = BASIC_SUBSCRIPTION(),
    permissions: string[] = ['api-subscription-r', 'api-subscription-u', 'api-subscription-d'],
  ) {
    await TestBed.overrideProvider(UIRouterStateParams, {
      useValue: { apiId: API_ID, subscriptionId: SUBSCRIPTION_ID },
    }).compileComponents();
    if (permissions) {
      const overrideUser = currentUser;
      overrideUser.userPermissions = permissions;
      await TestBed.overrideProvider(CurrentUserService, { useValue: { currentUser: overrideUser } });
    }
    fixture = TestBed.createComponent(ApiPortalSubscriptionEditComponent);
    httpTestingController = TestBed.inject(HttpTestingController);
    fixture.detectChanges();

    expectApiSubscriptionGet(subscription);

    loader = TestbedHarnessEnvironment.loader(fixture);
    fixture.detectChanges();
  }

  function expectApiSubscriptionGet(subscription: Subscription): void {
    httpTestingController
      .expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/subscriptions/${subscription.id}?expands=plan,application,subscribedBy`,
        method: 'GET',
      })
      .flush(subscription);
  }

  function expectApiPlansList(plans: Plan[], securities: string[], mode: PlanMode): void {
    httpTestingController
      .expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/plans?page=1&perPage=9999&securities=${securities}&statuses=PUBLISHED&mode=${mode}`,
        method: 'GET',
      })
      .flush({ data: plans });
  }

  function expectApiSubscriptionTransfer(subscriptionId: string, planId: string, subscription: Subscription): void {
    const req = httpTestingController.expectOne({
      url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/subscriptions/${subscriptionId}/_transfer`,
      method: 'POST',
    });
    expect(req.request.body.planId).toEqual(planId);
    req.flush(subscription);
  }

  function expectApiSubscriptionPause(subscriptionId: string, subscription: Subscription): void {
    const req = httpTestingController.expectOne({
      url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/subscriptions/${subscriptionId}/_pause`,
      method: 'POST',
    });
    expect(req.request.body).toEqual({});
    req.flush(subscription);
  }

  function expectApplicationGet(apiKeyMode: ApiKeyMode = ApiKeyMode.UNSPECIFIED): void {
    httpTestingController
      .expectOne({
        url: `${CONSTANTS_TESTING.env.baseURL}/applications/${APP_ID}`,
        method: 'GET',
      })
      .flush(fakeApplication({ id: APP_ID, api_key_mode: apiKeyMode }));
  }
});
