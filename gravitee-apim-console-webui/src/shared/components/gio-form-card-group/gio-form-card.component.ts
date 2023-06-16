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
import { ChangeDetectorRef, Component, Input } from '@angular/core';

@Component({
  selector: 'gio-form-card',
  template: `
    <mat-card matRipple [matRippleDisabled]="disabled" class="card" [class.selected]="selected" [class.disabled]="disabled">
      <span *ngIf="!lock" class="selection-icon" [class.selection-icon__disabled]="disabled">
        <mat-icon>check_circle</mat-icon>
      </span>
      <span *ngIf="lock" class="lock-icon">
        <mat-icon svgIcon="gio:lock"></mat-icon>
      </span>
      <mat-card-content class="card__content"><ng-content></ng-content></mat-card-content>
    </mat-card>
  `,
  host: {
    '[class.disabled]': 'disabled',
    '[class.selected]': 'selected',
    '(click)': 'onSelectCard()',
  },
  styles: [require('./gio-form-card.component.scss')],
})
export class GioFormCardComponent {
  @Input()
  value: string;
  @Input()
  lock: boolean;
  disabled = false;
  selected = false;

  onSelectFn: (value: string) => void;

  constructor(private readonly changeDetector: ChangeDetectorRef) {}

  onSelectCard() {
    if (!this.disabled && !this.lock) {
      this.selected = !this.selected;

      if (this.onSelectFn) {
        this.onSelectFn(this.value);
      }
    }
  }

  _markForCheck() {
    // When group value changes, the button will not be notified. Use `markForCheck` to explicit
    // update radio button's status
    this.changeDetector.markForCheck();
  }
}
