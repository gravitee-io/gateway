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
import { Component, Input, OnChanges } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

import { Page } from '../../../entities/page/page';
import { ConfigService } from '../../../services/config.service';
import { MarkdownService } from '../../../services/markdown.service';

@Component({
  selector: 'app-page-markdown',
  standalone: true,
  imports: [],
  template: `<div id="#markdown" [innerHTML]="markdownHtml"></div>`,
})
export class PageMarkdownComponent implements OnChanges {
  @Input() content!: string | undefined;
  @Input() apiPages: Page[] = [];
  @Input() apiId!: string;
  markdownHtml!: SafeHtml;

  constructor(
    private markdownService: MarkdownService,
    private configuration: ConfigService,
    private domSanitizer: DomSanitizer,
  ) {}

  ngOnChanges(): void {
    const renderedContent = this.content
      ? this.markdownService.render(this.content, this.configuration.baseURL, `/catalog/api/${this.apiId}/documentation`, this.apiPages)
      : '';
    const parser = new DOMParser();
    const document = parser.parseFromString(renderedContent, 'text/html');
    this.markdownHtml = this.domSanitizer.bypassSecurityTrustHtml(document.body.outerHTML);
  }
}
