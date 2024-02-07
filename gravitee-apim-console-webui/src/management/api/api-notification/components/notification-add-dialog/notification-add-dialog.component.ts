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
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { Notifier } from '../../../../../entities/notification/notifier';

export interface NotificationAddDialogData {
  notifiers: Notifier[];
}

export type NotificationAddDialogResult = {
  name: string;
  notifierId: string;
};

@Component({
  selector: 'notification-add-dialog',
  templateUrl: './notification-add-dialog.component.html',
  styleUrls: ['./notification-add-dialog.component.scss'],
})
export class NotificationAddDialogComponent {
  protected readonly notifiersList: Notifier[] = this.dialogData.notifiers;
  protected notificationForm = new FormGroup({
    name: new FormControl<string>('', [Validators.required]),
    notifierId: new FormControl<string>(this.notifiersList[0].id, [Validators.required]),
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) private readonly dialogData: NotificationAddDialogData,
    public dialogRef: MatDialogRef<NotificationAddDialogData, NotificationAddDialogResult>,
  ) {}

  onSubmit() {
    const { name, notifierId } = this.notificationForm.getRawValue();

    this.dialogRef.close({
      name,
      notifierId,
    });
  }
}
