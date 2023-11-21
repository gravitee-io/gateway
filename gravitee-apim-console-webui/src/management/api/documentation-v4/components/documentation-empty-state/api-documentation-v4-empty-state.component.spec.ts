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
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { InteractivityChecker } from '@angular/cdk/a11y';
import { DivHarness } from '@gravitee/ui-particles-angular/testing';
import { MatButtonHarness } from '@angular/material/button/testing';

import { ApiDocumentationV4EmptyStateComponent } from './api-documentation-v4-empty-state.component';

import { ApiDocumentationV4Module } from '../../api-documentation-v4.module';
import { GioHttpTestingModule } from '../../../../../shared/testing';
import { CurrentUserService } from '../../../../../ajs-upgraded-providers';
import { User } from '../../../../../entities/user';

describe('ApiDocumentationV4EmptyStateComponent', () => {
  let fixture: ComponentFixture<ApiDocumentationV4EmptyStateComponent>;
  let component: ApiDocumentationV4EmptyStateComponent;
  let harnessLoader: HarnessLoader;
  const currentUser = new User();
  currentUser.userPermissions = ['api-documentation-u', 'api-documentation-c', 'api-documentation-r'];

  const init = async () => {
    await TestBed.configureTestingModule({
      declarations: [ApiDocumentationV4EmptyStateComponent],
      imports: [NoopAnimationsModule, ApiDocumentationV4Module, GioHttpTestingModule],
      providers: [{ provide: CurrentUserService, useValue: { currentUser } }],
    })
      .overrideProvider(InteractivityChecker, {
        useValue: {
          isFocusable: () => true, // This traps focus checks and so avoid warnings when dealing with
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(ApiDocumentationV4EmptyStateComponent);
    component = fixture.componentInstance;
    harnessLoader = await TestbedHarnessEnvironment.loader(fixture);
  };

  beforeEach(async () => await init());

  it('should show empty state text', async () => {
    const title = await harnessLoader.getHarness(DivHarness.with({ selector: '.mat-h2' }));
    expect(await title.getText()).toEqual('No pages available yet');
    const subtitle = await harnessLoader.getHarness(DivHarness.with({ selector: '.mat-body-1' }));
    expect(await subtitle.getText()).toEqual('Start creating pages to fill up your folder.');
  });

  it('should emit event when clicking on add button', async () => {
    const spy = jest.spyOn(component.onAddPage, 'emit');
    const button = await harnessLoader.getHarness(MatButtonHarness.with({ text: 'Add new page' }));
    await button.click();

    expect(spy).toHaveBeenCalled();
  });
});
