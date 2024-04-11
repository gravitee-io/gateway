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
import { HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { BrowserAnimationsModule, NoopAnimationsModule } from '@angular/platform-browser/animations';
import { InteractivityChecker } from '@angular/cdk/a11y';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { TestElement } from '@angular/cdk/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { GioConfirmDialogHarness } from '@gravitee/ui-particles-angular';

import { IntegrationOverviewComponent } from './integration-overview.component';
import { IntegrationOverviewHarness } from './integration-overview.harness';

import { IntegrationsModule } from '../integrations.module';
import { Integration } from '../integrations.model';
import { CONSTANTS_TESTING, GioTestingModule } from '../../../shared/testing';
import { fakeIntegration } from '../../../entities/integrations/integration.fixture';
import { SnackBarService } from '../../../services-ngx/snack-bar.service';

describe('IntegrationOverviewComponent', () => {
  let fixture: ComponentFixture<IntegrationOverviewComponent>;
  let componentHarness: IntegrationOverviewHarness;
  let httpTestingController: HttpTestingController;
  const integrationId: string = 'asd123';

  const fakeSnackBarService = {
    error: jest.fn(),
    success: jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [IntegrationOverviewComponent],
      imports: [GioTestingModule, IntegrationsModule, BrowserAnimationsModule, NoopAnimationsModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ integrationId }) } },
        },
        {
          provide: SnackBarService,
          useValue: fakeSnackBarService,
        },
      ],
    })
      .overrideProvider(InteractivityChecker, {
        useValue: {
          isFocusable: () => true, // This traps focus checks and so avoid warnings when dealing with
          isTabbable: () => true, // This traps focus checks and so avoid warnings when dealing with
        },
      })
      .compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(IntegrationOverviewComponent);
    httpTestingController = TestBed.inject(HttpTestingController);
    componentHarness = await TestbedHarnessEnvironment.harnessForFixture(fixture, IntegrationOverviewHarness);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should call backend with proper integration id', () => {
    const integrationMock: Integration = fakeIntegration({ id: integrationId });
    expectIntegrationGetRequest(integrationMock);
  });

  it('should display error badge', async (): Promise<void> => {
    expectIntegrationGetRequest(fakeIntegration({ id: integrationId, agentStatus: 'DISCONNECTED' }));

    const errorBadge: TestElement = await componentHarness.getErrorBadge();
    expect(errorBadge).toBeTruthy();

    const errorBanner = await componentHarness.getErrorBanner().then((e) => e.text());
    expect(errorBanner).toEqual(
      'Check your agent status and ensure connectivity with the provider to start importing your APIs in Gravitee.',
    );
  });

  it('should display success badge', async (): Promise<void> => {
    expectIntegrationGetRequest(fakeIntegration({ id: integrationId, agentStatus: 'CONNECTED' }));

    const successBadge: TestElement = await componentHarness.getSuccessBadge();
    expect(successBadge).toBeTruthy();

    expect(await componentHarness.getErrorBanner()).toBeNull();
  });

  describe('discover', () => {
    it('should call _ingest endpoint on confirm', async () => {
      expectIntegrationGetRequest(fakeIntegration({ id: integrationId }));

      const discoverBtn: MatButtonHarness = await componentHarness.getDiscoverButton();
      await discoverBtn.click();

      const dialogHarness: GioConfirmDialogHarness =
        await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(GioConfirmDialogHarness);
      await dialogHarness.confirm();

      expectIngestPostRequest({
        status: 'SUCCESS',
        message: 'Integration APIs have been ingested successfully',
      });
      expect(fakeSnackBarService.success).toHaveBeenCalledWith('APIs successfully created and ready for use!');
    });

    it('should not call _ingest endpoint on cancel', async () => {
      expectIntegrationGetRequest(fakeIntegration({ id: integrationId }));

      const discoverBtn: MatButtonHarness = await componentHarness.getDiscoverButton();
      await discoverBtn.click();

      const dialogHarness: GioConfirmDialogHarness =
        await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(GioConfirmDialogHarness);
      await dialogHarness.cancel();

      httpTestingController.expectNone(`${CONSTANTS_TESTING.env.v2BaseURL}/integrations/${integrationId}/_ingest`);
    });

    it('should handle error with message', async () => {
      expectIntegrationGetRequest(fakeIntegration({ id: integrationId }));

      const discoverBtn: MatButtonHarness = await componentHarness.getDiscoverButton();
      await discoverBtn.click();

      const dialogHarness: GioConfirmDialogHarness =
        await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(GioConfirmDialogHarness);
      await dialogHarness.confirm();

      const req: TestRequest = httpTestingController.expectOne(`${CONSTANTS_TESTING.env.v2BaseURL}/integrations/${integrationId}/_ingest`);
      req.flush({}, { status: 400, statusText: 'Bad Request' });

      fixture.detectChanges();

      expect(fakeSnackBarService.error).toHaveBeenCalledWith('An error occurred while we were importing assets from the provider');
    });
  });

  function expectIntegrationGetRequest(integrationMock: Integration): void {
    const req: TestRequest = httpTestingController.expectOne(`${CONSTANTS_TESTING.env.v2BaseURL}/integrations/${integrationId}`);
    req.flush(integrationMock);
    expect(req.request.method).toEqual('GET');
  }

  function expectIngestPostRequest(res): void {
    const req: TestRequest = httpTestingController.expectOne(`${CONSTANTS_TESTING.env.v2BaseURL}/integrations/${integrationId}/_ingest`);
    req.flush(res);
    expect(req.request.method).toEqual('POST');
  }
});
