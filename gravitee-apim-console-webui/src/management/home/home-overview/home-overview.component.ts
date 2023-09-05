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
import { FormControl, Validators } from '@angular/forms';
import { switchMap, takeUntil, tap } from 'rxjs/operators';

import { AnalyticsService } from '../../../services-ngx/analytics.service';
import { GioQuickTimeRangeComponent, TimeRangeParams } from '../widgets/gio-quick-time-range/gio-quick-time-range.component';
import { RequestStats } from '../widgets/gio-request-stats/gio-request-stats.component';

@Component({
  selector: 'home-overview',
  template: require('./home-overview.component.html'),
  styles: [require('./home-overview.component.scss')],
})
export class HomeOverviewComponent implements OnInit, OnDestroy {
  loading = false;

  private fetchAnalyticsRequest$ = new Subject<TimeRangeParams>();
  private unsubscribe$: Subject<boolean> = new Subject<boolean>();
  constructor(private readonly statsService: AnalyticsService) {}

  requestStats?: RequestStats;

  timeRangeControl = new FormControl('1m', Validators.required);

  ngOnInit(): void {
    // Request Stats
    this.fetchAnalyticsRequest$
      .pipe(
        tap(() => (this.requestStats = undefined)),
        switchMap((val) => this.statsService.getStats({ field: 'response-time', interval: val.interval, from: val.from, to: val.to })),
        tap((data) => (this.requestStats = data)),
        takeUntil(this.unsubscribe$),
      )
      .subscribe();

    // Fetch Analytics when timeRange change
    this.timeRangeControl.valueChanges
      .pipe(
        tap(() => this.fetchAnalyticsRequest()),
        takeUntil(this.unsubscribe$),
      )
      .subscribe();

    // First fetch
    this.fetchAnalyticsRequest();
  }

  ngOnDestroy() {
    this.unsubscribe$.next(true);
    this.unsubscribe$.unsubscribe();
  }

  fetchAnalyticsRequest() {
    const timeRange = this.timeRangeControl.value;
    this.fetchAnalyticsRequest$.next(GioQuickTimeRangeComponent.getTimeFrameRangesParams(timeRange));
  }
}
