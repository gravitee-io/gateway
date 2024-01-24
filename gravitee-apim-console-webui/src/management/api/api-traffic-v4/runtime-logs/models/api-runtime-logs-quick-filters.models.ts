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

import { Moment } from 'moment';

export type SimpleFilter = FilterVM;
export type MultiFilter = FilterVM[];

export type FilterVM = {
  label: string;
  value: string;
};

export type LogFilters = {
  from?: number;
  to?: number;
  period?: SimpleFilter;
  applications?: MultiFilter;
  plans?: MultiFilter;
  methods?: string[];
  statuses?: Set<number>;
};

export type LogFiltersForm = { period: SimpleFilter; plans: string[]; methods: string[] };

export type MoreFiltersForm = { period: SimpleFilter; from: Moment; to: Moment; statuses: Set<number>; applications?: MultiFilter };

export type LogFiltersInitialValues = {
  applications?: MultiFilter;
  plans?: MultiFilter;
  from: Moment;
  to: Moment;
  methods: string[];
  statuses: Set<number>;
};

export const DEFAULT_PERIOD = { label: 'None', value: '0' };

export const PERIODS = [
  DEFAULT_PERIOD,
  { label: 'Last 5 Minutes', value: '-5m' },
  { label: 'Last 30 Minutes', value: '-30m' },
  { label: 'Last 1 Hour', value: '-1h' },
  { label: 'Last 3 Hours', value: '-3h' },
  { label: 'Last 6 Hours', value: '-6h' },
  { label: 'Last 12 Hours', value: '-12h' },
  { label: 'Last 1 Day', value: '-1d' },
  { label: 'Last 3 Days', value: '-3d' },
  { label: 'Last 7 Days', value: '-7d' },
];

export const DEFAULT_FILTERS: LogFilters = {
  period: DEFAULT_PERIOD,
  applications: undefined,
  plans: undefined,
  from: undefined,
  to: undefined,
  methods: undefined,
};

export const DATE_TIME_FORMATS = {
  parseInput: 'Y-M-DD HH:mm:ss',
  fullPickerInput: 'Y-M-DD HH:mm:ss',
  datePickerInput: 'Y-M-D',
  timePickerInput: 'HH:mm:ss',
  monthYearLabel: 'MMM y',
  dateA11yLabel: 'Y-M-D',
  monthYearA11yLabel: 'Y MMMM',
};
