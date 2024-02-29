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
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, tap } from 'rxjs/operators';
import { ActivatedRoute } from '@angular/router';

import { ApiService } from '../../../../services-ngx/api.service';
import { Event, EventType } from '../../../../entities/event/event';
import { User } from '../../../../entities/user/user';

type EventsTableDS = {
  type: EventType;
  createdAt: Date;
  user: User;
};

@Component({
  selector: 'api-events',
  templateUrl: './api-events.component.html',
  styleUrls: ['./api-events.component.scss'],
})
export class ApiEventsComponent implements OnInit, OnDestroy {
  public displayedColumns = ['icon', 'type', 'createdAt', 'user'];
  public eventsTableDS: EventsTableDS[] = [];
  private unsubscribe$: Subject<boolean> = new Subject<boolean>();
  public isLoadingData = true;
  constructor(
    public readonly activatedRoute: ActivatedRoute,
    private readonly apiService: ApiService,
  ) {}

  public ngOnInit(): void {
    this.apiService
      .getApiEvents(this.activatedRoute.snapshot.params.apiId, ['START_API', 'STOP_API', 'PUBLISH_API'])
      .pipe(
        tap((events) => {
          this.eventsTableDS = events.map((event: Event) => {
            return {
              type: event.type,
              createdAt: event.created_at,
              user: event.user,
            };
          });
        }),
        takeUntil(this.unsubscribe$),
      )
      .subscribe(() => {
        this.isLoadingData = false;
      });
  }

  ngOnDestroy() {
    this.unsubscribe$.next(true);
    this.unsubscribe$.unsubscribe();
  }
}
