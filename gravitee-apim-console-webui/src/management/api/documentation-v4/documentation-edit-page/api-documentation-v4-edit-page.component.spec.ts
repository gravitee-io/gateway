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
import { GioConfirmDialogHarness, GioMonacoEditorHarness } from '@gravitee/ui-particles-angular';
import { MatButtonHarness } from '@angular/material/button/testing';
import { HttpTestingController } from '@angular/common/http/testing';
import { InteractivityChecker } from '@angular/cdk/a11y';
import { MatSnackBarHarness } from '@angular/material/snack-bar/testing';
import { set } from 'lodash';

import { ApiDocumentationV4EditPageHarness } from './api-documentation-v4-edit-page.harness';
import { ApiDocumentationV4EditPageComponent } from './api-documentation-v4-edit-page.component';

import { GioUiRouterTestingModule } from '../../../../shared/testing/gio-uirouter-testing-module';
import { ApiDocumentationV4Module } from '../api-documentation-v4.module';
import { CurrentUserService, UIRouterState, UIRouterStateParams } from '../../../../ajs-upgraded-providers';
import { CONSTANTS_TESTING, GioHttpTestingModule } from '../../../../shared/testing';
import { Breadcrumb, Page } from '../../../../entities/management-api-v2/documentation/page';
import { ApiDocumentationV4ContentEditorHarness } from '../components/api-documentation-v4-content-editor/api-documentation-v4-content-editor.harness';
import { ApiDocumentationV4BreadcrumbHarness } from '../components/api-documentation-v4-breadcrumb/api-documentation-v4-breadcrumb.harness';
import { fakeFolder, fakeMarkdown } from '../../../../entities/management-api-v2/documentation/page.fixture';
import { ApiDocumentationV4PageTitleHarness } from '../components/api-documentation-v4-page-title/api-documentation-v4-page-title.harness';
import { fakeApiV4 } from '../../../../entities/management-api-v2';
import { User } from '../../../../entities/user';

interface InitInput {
  pages?: Page[];
  breadcrumb?: Breadcrumb[];
  parentId?: string;
  mode?: 'create' | 'edit';
}

describe('ApiDocumentationV4EditPageComponent', () => {
  let fixture: ComponentFixture<ApiDocumentationV4EditPageComponent>;
  let harnessLoader: HarnessLoader;
  const fakeUiRouter = { go: jest.fn() };
  const API_ID = 'api-id';
  let httpTestingController: HttpTestingController;

  const init = async (
    parentId: string,
    pageId: string,
    portalUrl = 'portal.url',
    userPermissions = ['api-documentation-u', 'api-documentation-c', 'api-documentation-r', 'api-documentation-d'],
  ) => {
    const currentUser = new User();
    currentUser.userPermissions = userPermissions;

    await TestBed.configureTestingModule({
      declarations: [ApiDocumentationV4EditPageComponent],
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
        { provide: UIRouterStateParams, useValue: { apiId: API_ID, parentId, pageId } },
        { provide: CurrentUserService, useValue: { currentUser } },
        {
          provide: 'Constants',
          useFactory: () => {
            const constants = CONSTANTS_TESTING;
            set(constants, 'env.settings.portal', {
              get url() {
                return portalUrl;
              },
            });
            return constants;
          },
        },
      ],
    })
      .overrideProvider(InteractivityChecker, {
        useValue: {
          isFocusable: () => true, // This traps focus checks and so avoid warnings when dealing with
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(ApiDocumentationV4EditPageComponent);
    harnessLoader = TestbedHarnessEnvironment.loader(fixture);
    httpTestingController = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  };

  const initPageServiceRequests = (input: InitInput, page: Page = {}) => {
    httpTestingController
      .expectOne({
        method: 'GET',
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}`,
      })
      .flush(fakeApiV4({ id: API_ID, lifecycleState: 'PUBLISHED' }));

    if (input.mode === 'edit') {
      expectGetPage(page);
    }
    expectGetPages(input.pages, input.breadcrumb, input.parentId);
    fixture.detectChanges();
  };

  afterEach(() => {
    httpTestingController.verify();
  });

  describe('Create page', () => {
    describe('In the root folder of the API', () => {
      const EXISTING_PAGE = fakeMarkdown({ id: 'page-id', name: 'page-name' });

      beforeEach(async () => {
        await init('ROOT', undefined);
        initPageServiceRequests({ pages: [EXISTING_PAGE], breadcrumb: [], parentId: 'ROOT' });
      });

      it('should have 2 steps', async () => {
        const stepper = await harnessLoader.getHarness(MatStepperHarness);
        const steps = await Promise.all(await stepper.getSteps());
        expect(steps.length).toEqual(2);
        expect(await steps[0].getLabel()).toEqual('Configure page');
        expect(await steps[1].getLabel()).toEqual('Add content');
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
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);

          expect(getPageTitle()).toEqual('Add new page');

          const nextBtn = await harness.getNextButton();
          expect(await nextBtn.isDisabled()).toEqual(true);

          // In creation mode, delete button should not be present
          expect(await harness.getDeleteButton()).toBeUndefined();

          await harness.setName('New page');
          await harness.checkVisibility('PRIVATE');

          expect(await nextBtn.isDisabled()).toEqual(false);
          expect(await nextBtn.click());
          expect(fixture.componentInstance.form.getRawValue()).toEqual({
            stepOne: {
              name: 'New page',
              visibility: 'PRIVATE',
            },
            content: '',
          });

          expect(getPageTitle()).toEqual('New page');
        });

        it('should not allow duplicate name', async () => {
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
          await harness.setName(EXISTING_PAGE.name);
          expect(await harness.getNextButton().then((btn) => btn.isDisabled())).toEqual(true);
        });
      });

      describe('step 2 - Fill content ', () => {
        beforeEach(async () => {
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
          await harness.setName('New page');

          await harness.getNextButton().then(async (btn) => {
            expect(await btn.isDisabled()).toEqual(false);
            return btn.click();
          });
        });

        it('should show markdown editor', async () => {
          const editor = await harnessLoader
            .getHarness(ApiDocumentationV4ContentEditorHarness)
            .then((harness) => harness.getContentEditor());
          expect(editor).toBeDefined();

          await editor.setValue('#TITLE \n This is the file content');
          expect(fixture.componentInstance.form.getRawValue().content).toEqual('#TITLE  This is the file content');
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
        await init('parent-folder-id', undefined);
        initPageServiceRequests({
          pages: [],
          breadcrumb: [{ name: 'Parent Folder', id: 'parent-folder-id', position: 1 }],
          parentId: 'parent-folder-id',
        });
      });

      it('should show breadcrumb', async () => {
        const harness = await harnessLoader.getHarness(ApiDocumentationV4BreadcrumbHarness);
        expect(await harness.getContent()).toEqual('Home > Parent Folder');
      });

      it('should save page in the correct folder', async () => {
        const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
        await harness.setName('New page');

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
  });

  describe('Edit page', () => {
    describe('In the root folder', () => {
      describe('with published page', () => {
        const PAGE = fakeMarkdown({ id: 'page-id', name: 'page-name', content: 'my content', visibility: 'PUBLIC', published: true });
        const OTHER_PAGE = fakeMarkdown({
          id: 'other-page',
          name: 'other-page-name',
          content: 'my other content',
          visibility: 'PUBLIC',
          published: true,
        });

        beforeEach(async () => {
          await init(undefined, PAGE.id);
          initPageServiceRequests({ pages: [PAGE, OTHER_PAGE], breadcrumb: [], parentId: undefined, mode: 'edit' }, PAGE);
        });

        it('should load step one with existing name and visibility', async () => {
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
          expect(await harness.getName()).toEqual(PAGE.name);
          expect(await harness.getVisibility()).toEqual(PAGE.visibility);
        });

        it('should load step one and have Next button clickable without any changes', async () => {
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
          expect(await harness.getNextButton().then((btn) => btn.isDisabled())).toEqual(false);
        });

        it('should not have Next button clickable with name blank', async () => {
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
          await harness.setName('');
          expect(await harness.getNextButton().then((btn) => btn.isDisabled())).toEqual(true);
        });

        it('should not have Next button clickable with duplicate name', async () => {
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
          await harness.setName(' Other-page-Name  ');
          expect(await harness.getNextButton().then((btn) => btn.isDisabled())).toEqual(true);
        });

        it('should show markdown editor with existing content', async () => {
          const editor = await harnessLoader
            .getHarness(ApiDocumentationV4ContentEditorHarness)
            .then((harness) => harness.getContentEditor());
          expect(editor).toBeDefined();
          expect(await editor.getValue()).toEqual(PAGE.content);
        });

        it('should not save content if no changes', async () => {
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);

          await harness.getNextButton().then(async (btn) => btn.click());

          const saveBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Publish changes' }));
          expect(await saveBtn.isDisabled()).toEqual(true);
        });

        it('should save content', async () => {
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
          await harness.setName('New name');
          await harness.checkVisibility('PRIVATE');

          await harness.getNextButton().then(async (btn) => btn.click());

          await harnessLoader
            .getHarness(ApiDocumentationV4ContentEditorHarness)
            .then((harness) => harness.getContentEditor())
            .then((editor) => editor.setValue('New content'));

          const saveBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Publish changes' }));
          expect(await saveBtn.isDisabled()).toEqual(false);
          await saveBtn.click();

          expectGetPage(PAGE);

          const req = httpTestingController.expectOne({
            method: 'PUT',
            url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${PAGE.id}`,
          });
          expect(req.request.body).toEqual({
            ...PAGE,
            name: 'New name',
            visibility: 'PRIVATE',
            content: 'New content',
          });
          req.flush(PAGE);

          expect(fakeUiRouter.go).toHaveBeenCalledWith('management.apis.documentationV4', { apiId: API_ID, parentId: 'ROOT' });
        });
      });
      describe('with unpublished page', () => {
        const PAGE = fakeMarkdown({ id: 'page-id', content: 'my content', visibility: 'PUBLIC', published: false });

        beforeEach(async () => {
          await init(undefined, PAGE.id);
          initPageServiceRequests({ pages: [PAGE], breadcrumb: [], parentId: undefined, mode: 'edit' }, PAGE);
        });

        describe('and no changes', () => {
          beforeEach(async () => {
            const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
            await harness.getNextButton().then(async (btn) => btn.click());
          });

          it('should not update page', async () => {
            const publishBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Save' }));
            expect(await publishBtn.isDisabled()).toEqual(true);
          });

          it('should not update and publish page', async () => {
            const publishBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Save and publish' }));
            expect(await publishBtn.isDisabled()).toEqual(true);
          });
        });
        describe('and with changes', () => {
          beforeEach(async () => {
            const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
            await harness.setName('New name');
            await harness.getNextButton().then(async (btn) => btn.click());
          });

          it('should update page', async () => {
            const saveBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Save' }));
            expect(await saveBtn.isDisabled()).toEqual(false);
            await saveBtn.click();

            expectGetPage(PAGE);

            const req = httpTestingController.expectOne({
              method: 'PUT',
              url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${PAGE.id}`,
            });

            expect(req.request.body).toEqual({
              ...PAGE,
              name: 'New name',
              visibility: 'PUBLIC',
              content: PAGE.content,
            });
            req.flush({ ...PAGE, name: 'New name' });
          });

          it('should update and publish page', async () => {
            const saveBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Save and publish' }));
            expect(await saveBtn.isDisabled()).toEqual(false);
            await saveBtn.click();

            expectGetPage(PAGE);

            const updatedPage = { ...PAGE, name: 'New name' };
            const req = httpTestingController.expectOne({
              method: 'PUT',
              url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${PAGE.id}`,
            });

            expect(req.request.body).toEqual({
              ...PAGE,
              name: 'New name',
              visibility: 'PUBLIC',
              content: PAGE.content,
            });
            req.flush(updatedPage);

            httpTestingController
              .expectOne({
                method: 'POST',
                url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${PAGE.id}/_publish`,
              })
              .flush({ updatedPage, published: true });
          });
        });
      });
    });
    describe('In parent folder', () => {
      const PAGE = fakeMarkdown({ id: 'page-id', content: 'my content', visibility: 'PUBLIC', parentId: 'parent-folder-id' });

      beforeEach(async () => {
        await init('parent-folder-id', PAGE.id);
        initPageServiceRequests(
          {
            pages: [],
            breadcrumb: [{ name: 'Parent Folder', id: 'parent-folder-id', position: 1 }],
            parentId: 'parent-folder-id',
            mode: 'edit',
          },
          PAGE,
        );
      });

      it('should show breadcrumb', async () => {
        const harness = await harnessLoader.getHarness(ApiDocumentationV4BreadcrumbHarness);
        expect(await harness.getContent()).toEqual('Home > Parent Folder');
      });

      it('should exit without saving', async () => {
        const exitBtn = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Exit without saving' }));
        await exitBtn.click();
        expect(fakeUiRouter.go).toHaveBeenCalledWith('management.apis.documentationV4', {
          apiId: API_ID,
          parentId: PAGE.parentId,
        });
      });
    });
    describe('In read-only mode', () => {
      const PAGE = fakeMarkdown({ id: 'page-id', name: 'page-name', content: 'my content', visibility: 'PUBLIC', published: true });

      beforeEach(async () => {
        await init(undefined, PAGE.id, 'portal.url', ['api-documentation-r']);
        initPageServiceRequests({ pages: [PAGE], breadcrumb: [], parentId: undefined, mode: 'edit' }, PAGE);
      });

      it('should not be able to delete page and form is disabled', async () => {
        // No delete button
        const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
        expect(await harness.getDeleteButton()).toBeUndefined();

        // All fields disabled
        expect(await harness.nameIsDisabled()).toEqual(true);
        expect(await harness.visibilityIsDisabled()).toEqual(true);

        const editor = await harnessLoader.getHarness(GioMonacoEditorHarness);
        expect(await editor.isDisabled()).toEqual(true);

        // At end, no Publish Changes button
        const publishChangesButtons = await harnessLoader.getAllHarnesses(MatButtonHarness.with({ text: 'Publish changes' }));
        expect(publishChangesButtons.length).toEqual(0);
      });
    });
  });

  describe('Delete page', () => {
    describe('In the root folder', () => {
      describe('with published page', () => {
        const PAGE = fakeMarkdown({ id: 'page-id', name: 'page-name', content: 'my content', visibility: 'PUBLIC', published: true });
        const OTHER_PAGE = fakeMarkdown({
          id: 'other-page',
          name: 'other-page-name',
          content: 'my other content',
          visibility: 'PUBLIC',
          published: true,
        });

        it('should not delete page used as general condition', async () => {
          const GENERAL_CONDITION_PAGE = fakeMarkdown({
            id: 'general-condition-page',
            name: 'general-condition-page-name',
            content: 'my other content',
            visibility: 'PUBLIC',
            published: true,
            generalConditions: true,
          });

          await init(undefined, GENERAL_CONDITION_PAGE.id);
          initPageServiceRequests(
            { pages: [GENERAL_CONDITION_PAGE, PAGE, OTHER_PAGE], breadcrumb: [], parentId: undefined, mode: 'edit' },
            GENERAL_CONDITION_PAGE,
          );

          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
          expect(await harness.getName()).toEqual(GENERAL_CONDITION_PAGE.name);
          expect(await harness.getVisibility()).toEqual(GENERAL_CONDITION_PAGE.visibility);
          const deleteButton = await harness.getDeleteButton();
          expect(await deleteButton.isDisabled()).toBeTruthy();
        });

        it('should delete page and navigate to root list', async () => {
          await init(undefined, PAGE.id);
          initPageServiceRequests({ pages: [PAGE, OTHER_PAGE], breadcrumb: [], parentId: undefined, mode: 'edit' }, PAGE);
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
          expect(await harness.getName()).toEqual(PAGE.name);
          expect(await harness.getVisibility()).toEqual(PAGE.visibility);
          const deleteButton = await harness.getDeleteButton();
          await deleteButton.click();

          const dialogHarness = await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(GioConfirmDialogHarness);
          await dialogHarness.confirm();

          httpTestingController
            .expectOne({
              method: 'DELETE',
              url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${PAGE.id}`,
            })
            .flush(null);

          expect(fakeUiRouter.go).toHaveBeenCalledWith('management.apis.documentationV4', { apiId: API_ID, parentId: 'ROOT' });
        });
      });
    });
    describe('Under another folder', () => {
      describe('with published page', () => {
        const FOLDER = fakeFolder({
          id: 'folder',
          name: 'folder-name',
          visibility: 'PUBLIC',
          published: true,
        });
        const SUB_FOLDER_PAGE = fakeMarkdown({
          id: 'folder-page',
          name: 'folder-page-name',
          content: 'my other content',
          visibility: 'PUBLIC',
          published: true,
          parentId: FOLDER.id,
        });

        it('should delete page and navigate to parent folder', async () => {
          await init(undefined, SUB_FOLDER_PAGE.id);
          initPageServiceRequests(
            {
              pages: [FOLDER, SUB_FOLDER_PAGE],
              parentId: FOLDER.id,
              mode: 'edit',
            },
            SUB_FOLDER_PAGE,
          );
          const harness = await TestbedHarnessEnvironment.harnessForFixture(fixture, ApiDocumentationV4EditPageHarness);
          expect(await harness.getName()).toEqual(SUB_FOLDER_PAGE.name);
          expect(await harness.getVisibility()).toEqual(SUB_FOLDER_PAGE.visibility);
          const deleteButton = await harness.getDeleteButton();
          await deleteButton.click();

          const dialogHarness = await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(GioConfirmDialogHarness);
          await dialogHarness.confirm();

          httpTestingController
            .expectOne({
              method: 'DELETE',
              url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${SUB_FOLDER_PAGE.id}`,
            })
            .flush(null);

          expect(fakeUiRouter.go).toHaveBeenCalledWith('management.apis.documentationV4', {
            apiId: API_ID,
            parentId: FOLDER.id,
          });
        });
      });
    });
  });

  describe('Header', () => {
    const PAGE = fakeMarkdown({ id: 'page-id', name: 'page-name', content: 'my content', visibility: 'PUBLIC', published: true });

    it('should display Open in Portal button', async () => {
      await init(undefined, PAGE.id);
      initPageServiceRequests({ pages: [PAGE], breadcrumb: [], parentId: undefined, mode: 'edit' }, PAGE);

      const header = await harnessLoader.getHarness(ApiDocumentationV4PageTitleHarness);
      expect(header).toBeDefined();
      const openInPortalBtn = await header.getOpenInPortalBtn();
      expect(openInPortalBtn).toBeDefined();
      expect(await openInPortalBtn.isDisabled()).toEqual(false);
    });
    it('should not display Open in Portal button if Portal url not defined', async () => {
      await init(undefined, PAGE.id, null);
      initPageServiceRequests({ pages: [PAGE], breadcrumb: [], parentId: undefined, mode: 'edit' }, PAGE);

      const header = await harnessLoader.getHarness(ApiDocumentationV4PageTitleHarness);
      expect(await header.getOpenInPortalBtn()).toEqual(null);
    });
  });

  const expectGetPages = (pages: Page[], breadcrumb: Breadcrumb[], parentId = 'ROOT') => {
    const req = httpTestingController.expectOne({
      method: 'GET',
      url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages?parentId=${parentId}`,
    });

    req.flush({ pages, breadcrumb });
  };
  const expectGetPage = (page: Page) => {
    const req = httpTestingController.expectOne({
      method: 'GET',
      url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${page.id}`,
    });

    req.flush(page);
  };

  const getPageTitle = () => {
    return fixture.nativeElement.querySelector('h3').innerHTML;
  };
});
