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

import { ApiDocumentationV2Service } from './api-documentation-v2.service';

import { CONSTANTS_TESTING, GioHttpTestingModule } from '../shared/testing';
import { CreateDocumentation, CreateDocumentationFolder } from '../entities/management-api-v2/documentation/createDocumentation';
import { Page } from '../entities/management-api-v2/documentation/page';
import { EditDocumentationMarkdown } from '../entities/management-api-v2/documentation/editDocumentation';
import { fakeFolder, fakeMarkdown, fakePage } from '../entities/management-api-v2/documentation/page.fixture';

describe('ApiDocumentationV2Service', () => {
  let httpTestingController: HttpTestingController;
  let service: ApiDocumentationV2Service;
  const API_ID = 'api-id';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [GioHttpTestingModule],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject<ApiDocumentationV2Service>(ApiDocumentationV2Service);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  describe('getPage', () => {
    const PAGE_ID = 'page-id';
    it('should call the API', (done) => {
      const fakeResponse = fakeMarkdown();

      service.getApiPage(API_ID, PAGE_ID).subscribe((response) => {
        expect(response).toEqual(fakeResponse);
        done();
      });

      const req = httpTestingController.expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${PAGE_ID}`,
        method: 'GET',
      });

      req.flush(fakeResponse);
    });
  });

  describe('getApiPages', () => {
    it('should call the API', (done) => {
      const fakeResponse = { pages: [], breadcrumb: [] };

      service.getApiPages(API_ID, 'ROOT').subscribe((response) => {
        expect(response).toEqual(fakeResponse);
        done();
      });

      const req = httpTestingController.expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages?parentId=ROOT`,
        method: 'GET',
      });

      req.flush(fakeResponse);
    });

    it('should filter the non supported page type', (done) => {
      const markdown = fakeMarkdown();
      const folder = fakeFolder();
      const fakeResponse = {
        pages: [
          markdown,
          folder,
          fakePage({ type: 'ASCIIDOC' }),
          fakePage({ type: 'ASYNCAPI' }),
          fakePage({ type: 'MARKDOWN_TEMPLATE' }),
          fakePage({ type: 'SYSTEM_FOLDER' }),
          fakePage({ type: 'SWAGGER' }),
          fakePage({ type: 'LINK' }),
          fakePage({ type: 'TRANSLATION' }),
        ],
        breadcrumb: [],
      };

      service.getApiPages(API_ID, 'ROOT').subscribe((response) => {
        expect(response).toEqual({
          pages: [markdown, folder],
          breadcrumb: [],
        });
        done();
      });

      const req = httpTestingController.expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages?parentId=ROOT`,
        method: 'GET',
      });

      req.flush(fakeResponse);
    });

    it('should sort breadcrumb by position', (done) => {
      const fakeResponse = {
        pages: [],
        breadcrumb: [
          { name: 'folder 2', position: 2, id: 'folder-2' },
          { name: 'folder 3', position: 3, id: 'folder-3' },
          { name: 'folder 1', position: 1, id: 'folder-1' },
        ],
      };

      service.getApiPages(API_ID, 'ROOT').subscribe((response) => {
        expect(response.breadcrumb).toEqual([
          { name: 'folder 1', position: 1, id: 'folder-1' },
          { name: 'folder 2', position: 2, id: 'folder-2' },
          { name: 'folder 3', position: 3, id: 'folder-3' },
        ]);
        done();
      });

      const req = httpTestingController.expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages?parentId=ROOT`,
        method: 'GET',
      });

      req.flush(fakeResponse);
    });
  });

  describe('createDocumentation', () => {
    it('should call the API to create folder', (done) => {
      const fakeResponse: Page = {};
      const createFolder: CreateDocumentationFolder = {
        type: 'FOLDER',
        name: 'folder',
        visibility: 'PUBLIC',
      };
      service.createDocumentationPage(API_ID, createFolder).subscribe((response) => {
        expect(response).toEqual(fakeResponse);
        done();
      });

      const req = httpTestingController.expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages`,
        method: 'POST',
      });

      req.flush(fakeResponse);
    });

    it('should call the API to create page', (done) => {
      const fakeResponse: Page = {};
      const createPage: CreateDocumentation = {
        type: 'MARKDOWN',
        visibility: 'PRIVATE',
        name: 'page',
        content: '#TITLE \n content',
      };
      service.createDocumentationPage(API_ID, createPage).subscribe((response) => {
        expect(response).toEqual(fakeResponse);
        done();
      });

      const req = httpTestingController.expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages`,
        method: 'POST',
      });

      req.flush(fakeResponse);
    });
  });

  describe('updateDocumentation', () => {
    const PAGE_ID = 'page-id';
    it('should call the API to update markdown page', (done) => {
      const fakeResponse: Page = {};
      const editPage: EditDocumentationMarkdown = {
        type: 'FOLDER',
        name: 'folder',
        visibility: 'PUBLIC',
        content: 'new content',
      };
      service.updateDocumentationPage(API_ID, PAGE_ID, editPage).subscribe((response) => {
        expect(response).toEqual(fakeResponse);
        done();
      });

      const req = httpTestingController.expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${PAGE_ID}`,
        method: 'PUT',
      });
      req.flush(fakeResponse);
    });
  });

  describe('publishPage', () => {
    const PAGE_ID = 'page-id';
    it('should call the API to publish page', (done) => {
      const fakeResponse: Page = fakeMarkdown();
      service.publishDocumentationPage(API_ID, PAGE_ID).subscribe((response) => {
        expect(response).toEqual(fakeResponse);
        done();
      });

      const req = httpTestingController.expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${PAGE_ID}/_publish`,
        method: 'POST',
      });
      req.flush(fakeResponse);
    });
  });

  describe('unpublishPage', () => {
    const PAGE_ID = 'page-id';
    it('should call the API to unpublish page', (done) => {
      const fakeResponse: Page = fakeMarkdown();
      service.unpublishDocumentationPage(API_ID, PAGE_ID).subscribe((response) => {
        expect(response).toEqual(fakeResponse);
        done();
      });

      const req = httpTestingController.expectOne({
        url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${API_ID}/pages/${PAGE_ID}/_unpublish`,
        method: 'POST',
      });
      req.flush(fakeResponse);
    });
  });
});
