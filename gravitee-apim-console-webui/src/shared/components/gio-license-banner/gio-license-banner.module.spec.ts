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
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatIconTestingModule } from '@angular/material/icon/testing';

import { GioLicenseBannerModule } from './gio-license-banner.module';

const onRequestUpgrade = jest.fn();

@Component({
  template: `<gio-license-banner (onRequestUpgrade)="onRequestUpgrade()"></gio-license-banner>`,
})
class TestComponent {
  public onRequestUpgrade = onRequestUpgrade;
}

describe('GioLicenseBannerModule', () => {
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [NoopAnimationsModule, GioLicenseBannerModule, MatIconTestingModule],
    });
    fixture = TestBed.createComponent(TestComponent);
  });

  afterEach(() => {
    jest.resetAllMocks();
  });

  it('should call on request upgrade', async () => {
    fixture.detectChanges();
    fixture.nativeElement.querySelector('button').click();

    expect(onRequestUpgrade).toHaveBeenCalled();
  });
});
