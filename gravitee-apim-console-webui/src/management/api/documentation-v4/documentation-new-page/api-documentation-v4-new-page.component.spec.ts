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
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatStepperHarness } from '@angular/material/stepper/testing';
import { UIRouterModule } from '@uirouter/angular';
import { MatIconTestingModule } from '@angular/material/icon/testing';
import { FormsModule } from '@angular/forms';
import { GioMonacoEditorHarness } from '@gravitee/ui-particles-angular';
import { MatButtonHarness } from '@angular/material/button/testing';
import { HttpTestingController } from '@angular/common/http/testing';
import { InteractivityChecker } from '@angular/cdk/a11y';
import { MatSnackBarHarness } from '@angular/material/snack-bar/testing';

import { ApiDocumentationV4NewPageHarness } from './api-documentation-v4-new-page.harness';
import { ApiDocumentationV4NewPageComponent } from './api-documentation-v4-new-page.component';

import { GioUiRouterTestingModule } from '../../../../shared/testing/gio-uirouter-testing-module';
import { ApiDocumentationV4Module } from '../api-documentation-v4.module';
import { UIRouterState, UIRouterStateParams } from '../../../../ajs-upgraded-providers';
import { CONSTANTS_TESTING, GioHttpTestingModule } from '../../../../shared/testing';
import { Breadcrumb, Page } from '../../../../entities/management-api-v2/documentation/page';
import { ApiDocumentationV4ContentEditorHarness } from '../components/api-documentation-v4-content-editor/api-documentation-v4-content-editor.harness';
import { ApiDocumentationV4BreadcrumbHarness } from '../components/api-documentation-v4-breadcrumb/api-documentation-v4-breadcrumb.harness';
import { fakeMarkdown } from '../../../../entities/management-api-v2/documentation/page.fixture';

describe('ApiDocumentationV4NewPageComponent', () => {
  let fixture: ComponentFixture<ApiDocumentationV4NewPageComponent>;
  let harnessLoader: HarnessLoader;
  const fakeUiRouter = { go: jest.fn() };
  const API_ID = 'api-id';
  let httpTestingController: HttpTestingController;

  const init = async (pages: Page[] = [], breadcrumb: Breadcrumb[] = [], parentId = 'ROOT') => {
    await TestBed.configureTestingModule({
      declarations: [ApiDocumentationV4NewPageComponent],
      imports: [
        NoopAnimationsModule,
        ApiDocumentationV4Module,
        GioUiRouterTestingModule,
        UIRouterModule.forRoot({ useHash: true }),
        MatIconTestingModule,
        FormsModule,
        GioHttpTestingModule,
      ],
      providers: [
        { provide: UIRouterState, useValue: fakeUiRouter },
        { provide: UIRouterStateParams, useValue: { apiId: API_ID, parentId: parentId } },
      ],
    })
      .overrideProvider(InteractivityChecker, {
        useValue: {
          isFocusable: () => true, // This traps focus checks and so avoid warnings when dealing with
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(ApiDocumentationV4NewPageComponent);
    harnessLoader = TestbedHarnessEnvironment.loader(fixture);
    httpTestingController = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
    expectGetPages(pages, breadcrumb, parentId);
  };

  afterEach(() => {
    httpTestingController.verify();
  });

  describe('In the root folder of the API', () => {
    beforeEach(async () => await init());

    it('should have 3 steps', async () => {
      const stepper = await harnessLoader.getHarness(MatStepperHarness);
      const steps = await Promise.all(await stepper.getSteps());
      expect(steps.length).toEqual(3);
      expect(await steps[0].getLabel()).toEqual('Configure page');
      expect(await steps[1].getLabel()).toEqual('Determine source');
      expect(await steps[2].getLabel()).toEqual('Add content');
    });

    it('should exit without saving', async () => {
      const exitBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Exit without saving' }));
      await exitBtn.click();
      expect(fakeUiRouter.go).toHaveBeenCalledWith('management.apis.documentationV4', {
        apiId: API_ID,
        parentId: 'ROOT',
      });
    });

    describe('step 1 - Configure page', () => {
      it('should set name and visibility', async () => {
        const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4NewPageHarness);

        expect(getPageTitle()).toEqual('Add new page');

        const nextBtn = await harness.getNextButton();
        expect(await nextBtn.isDisabled()).toEqual(true);

        await harness.setName('New page');
        await harness.checkVisibility('PRIVATE');

        expect(await nextBtn.isDisabled()).toEqual(false);
        expect(await nextBtn.click());
        expect(fixture.componentInstance.form.getRawValue()).toEqual({ name: 'New page', visibility: 'PRIVATE' });

        expect(getPageTitle()).toEqual('New page');
      });
    });

    describe('step 2 - Determine source', () => {
      beforeEach(async () => {
        const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4NewPageHarness);
        await harness.setName('New page');

        await harness.getNextButton().then(async (btn) => {
          expect(await btn.isDisabled()).toEqual(false);
          return btn.click();
        });
        fixture.detectChanges();
      });

      it('should select source', async () => {
        const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4NewPageHarness);
        const options = await harness.getSourceOptions();

        expect(options.length).toEqual(3);
        const sourceOptions = await Promise.all(options.map(async (opt) => await opt.getLabelText()));
        expect(sourceOptions).toEqual(['Fill in the content myself', 'Import from fileComing soon', 'Import from fileComing soon']);
        expect(await options[1].isDisabled()).toEqual(true);
        expect(await options[2].isDisabled()).toEqual(true);

        await options[0].check();
      });
    });

    describe('step 3 - Fill content ', () => {
      beforeEach(async () => {
        const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4NewPageHarness);
        await harness.setName('New page');

        await harness.getNextButton().then(async (btn) => {
          expect(await btn.isDisabled()).toEqual(false);
          return btn.click();
        });

        await harness.getNextButton().then(async (btn) => {
          expect(await btn.isDisabled()).toEqual(false);
          return btn.click();
        });
      });

      it('should show markdown editor', async () => {
        const editor = await harnessLoader.getHarness(ApiDocumentationV4ContentEditorHarness).then((harness) => harness.getContentEditor());
        expect(editor).toBeDefined();

        await editor.setValue('#TITLE \n This is the file content');
        expect(fixture.componentInstance.content).toEqual('#TITLE  This is the file content');
      });

      it('should show markdown preview', async () => {
        const preview = await harnessLoader
          .getHarness(ApiDocumentationV4ContentEditorHarness)
          .then((editor) => editor.getMarkdownPreview());
        expect(preview).toBeTruthy();

        const togglePreviewButton = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Toggle preview' }));
        await togglePreviewButton.click();

        const previewAfter = await harnessLoader
          .getHarness(ApiDocumentationV4ContentEditorHarness)
          .then((editor) => editor.getMarkdownPreview());

        expect(previewAfter).toBeFalsy();
      });

      it('should save content', async () => {
        const editor = await harnessLoader.getHarness(GioMonacoEditorHarness);
        await editor.setValue('#TITLE \n This is the file content');

        const saveBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Save' }));
        expect(await saveBtn.isDisabled()).toEqual(false);
        await saveBtn.click();

        const req = httpTestingController.expectOne({
          method: 'POST',
          url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages`,
        });

        req.flush({});
        expect(req.request.body).toEqual({
          type: 'MARKDOWN',
          name: 'New page',
          visibility: 'PUBLIC',
          content: '#TITLE  This is the file content', // TODO: check why \n is removed
          parentId: 'ROOT',
        });
      });

      it('should save and publish content', async () => {
        const editor = await harnessLoader.getHarness(GioMonacoEditorHarness);
        await editor.setValue('#TITLE \n This is the file content');

        const saveBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Save and publish' }));
        expect(await saveBtn.isDisabled()).toEqual(false);
        await saveBtn.click();

        const req = httpTestingController.expectOne({
          method: 'POST',
          url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages`,
        });

        const page = fakeMarkdown({ id: 'page-id' });
        req.flush(page);
        expect(req.request.body).toEqual({
          type: 'MARKDOWN',
          name: 'New page',
          visibility: 'PUBLIC',
          content: '#TITLE  This is the file content', // TODO: check why \n is removed
          parentId: 'ROOT',
        });

        const publishReq = httpTestingController.expectOne({
          method: 'POST',
          url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${page.id}/_publish`,
        });
        publishReq.flush({ ...page, published: true });
      });

      it('should show error if raised on save', async () => {
        const editor = await harnessLoader.getHarness(GioMonacoEditorHarness);
        await editor.setValue('unsafe content');

        const saveBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Save' }));
        expect(await saveBtn.isDisabled()).toEqual(false);
        await saveBtn.click();

        const req = httpTestingController.expectOne({
          method: 'POST',
          url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages`,
        });

        req.flush({ message: 'Cannot save unsafe content' }, { status: 400, statusText: 'Cannot save unsafe content' });
        fixture.detectChanges();

        const snackBar = await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(MatSnackBarHarness);
        expect(await snackBar.getMessage()).toEqual('Cannot save unsafe content');
      });

      it('should show error if raised on publish', async () => {
        const editor = await harnessLoader.getHarness(GioMonacoEditorHarness);
        await editor.setValue('unsafe content');

        const saveBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Save and publish' }));
        expect(await saveBtn.isDisabled()).toEqual(false);
        await saveBtn.click();

        const page = fakeMarkdown({ id: 'page-id' });
        const req = httpTestingController.expectOne({
          method: 'POST',
          url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages`,
        });

        req.flush(page);

        const publishReq = httpTestingController.expectOne({
          method: 'POST',
          url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${page.id}/_publish`,
        });
        publishReq.flush({ message: 'Error on publish' }, { status: 400, statusText: 'Error on publish' });

        fixture.detectChanges();

        const snackBar = await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(MatSnackBarHarness);
        expect(await snackBar.getMessage()).toEqual('Error on publish');
      });
    });
  });
  describe('Under another folder', () => {
    beforeEach(async () => {
      await init([], [{ name: 'Parent Folder', id: 'parent-folder-id', position: 1 }], 'parent-folder-id');
    });

    it('should show breadcrumb', async () => {
      const harness = await harnessLoader.getHarness(ApiDocumentationV4BreadcrumbHarness);
      expect(await harness.getContent()).toEqual('Home>Parent Folder');
    });

    it('should save page in the correct folder', async () => {
      const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4NewPageHarness);
      await harness.setName('New page');

      await harness.getNextButton().then(async (btn) => btn.click());
      await harness.getNextButton().then(async (btn) => btn.click());

      const editor = await harnessLoader.getHarness(GioMonacoEditorHarness);
      await editor.setValue('File content');

      const saveBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Save' }));
      expect(await saveBtn.isDisabled()).toEqual(false);
      await saveBtn.click();

      const req = httpTestingController.expectOne({
        method: 'POST',
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages`,
      });

      req.flush({});
      expect(req.request.body).toEqual({
        type: 'MARKDOWN',
        name: 'New page',
        visibility: 'PUBLIC',
        content: 'File content',
        parentId: 'parent-folder-id',
      });
    });
  });

  const expectGetPages = (pages: Page[], breadcrumb: Breadcrumb[], parentId = 'ROOT') => {
    const req = httpTestingController.expectOne({
      method: 'GET',
      url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages?parentId=${parentId}`,
    });

    req.flush({ pages, breadcrumb });
  };

  const getPageTitle = () => {
    return fixture.nativeElement.querySelector('h3').innerHTML;
  };
});
