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
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageAsciidocHarness } from './page-asciidoc/page-asciidoc.harness';
import { PageMarkdownHarness } from './page-markdown/page-markdown.harness';
import { PageSwaggerHarness } from './page-swagger/page-swagger.harness';
import { PageComponent } from './page.component';
import { fakePage } from '../../entities/page/page.fixtures';
import { AppTestingModule } from '../../testing/app-testing.module';

describe('PageComponent', () => {
  let component: PageComponent;
  let fixture: ComponentFixture<PageComponent>;
  let harnessLoader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PageComponent, AppTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(PageComponent);
    harnessLoader = TestbedHarnessEnvironment.loader(fixture);
    component = fixture.componentInstance;
    component.apiId = 'api-id';
    component.apiPages = [];
  });

  describe('swagger', () => {
    beforeEach(() => {
      component.page = fakePage({ type: 'SWAGGER' });
      fixture.detectChanges();
    });

    it('should show swagger content', async () => {
      const swagger = await harnessLoader.getHarnessOrNull(PageSwaggerHarness);
      expect(swagger).toBeTruthy();
      expect(await swagger?.getSwagger()).toBeTruthy();
    });
  });

  describe('markdown', () => {
    beforeEach(() => {
      component.page = fakePage({ type: 'MARKDOWN' });
      fixture.detectChanges();
    });

    it('should show markdown content', async () => {
      const markdown = await harnessLoader.getHarnessOrNull(PageMarkdownHarness);
      expect(markdown).toBeTruthy();
      expect(markdown?.getMarkdownHtml()).toContain(`<p>markdown content</p>`);
    });
  });

  describe('asciidoc', () => {
    beforeEach(() => {
      component.page = fakePage({ type: 'ASCIIDOC' });
      fixture.detectChanges();
    });

    it('should show asciidoc content', async () => {
      const asciidoc = await harnessLoader.getHarnessOrNull(PageAsciidocHarness);
      expect(asciidoc).toBeTruthy();
    });
  });
});
