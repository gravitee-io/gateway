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
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { DeveloperPortalApiComponent } from './developer-portal-api.component';
import { DeveloperPortalApiHarness } from './developer-portal-api.harness';

describe('DeveloperPortalApiComponent', () => {
  // let component: DeveloperPortalApiComponent;
  let fixture: ComponentFixture<DeveloperPortalApiComponent>;
  let componentHarness: DeveloperPortalApiHarness;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule, DeveloperPortalApiComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DeveloperPortalApiComponent);
    // component = fixture.componentInstance;
    componentHarness = await TestbedHarnessEnvironment.harnessForFixture(fixture, DeveloperPortalApiHarness);
    fixture.detectChanges();
  });

  it('should fill form and submit', async () => {
    await componentHarness.setName('name');
    await componentHarness.reset();
    expect(await componentHarness.getName()).toStrictEqual('');

    await componentHarness.setName('name');
    await componentHarness.submit();
    expect(await componentHarness.getName()).toStrictEqual('name');
  });

  it('should not be able to save', async () => {
    await componentHarness.setName('name');
    await componentHarness.setName(null);
    expect(await componentHarness.isSubmitInvalid()).toBeTruthy();
  });
});
