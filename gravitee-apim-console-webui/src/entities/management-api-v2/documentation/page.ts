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
import { PageType } from './pageType';
import { Visibility } from './visibility';
import { PageSource } from './pageSource';

export interface Revision {
  id?: string;
  revision?: number;
}

export interface Page {
  id?: string;
  crossId?: string;
  name?: string;
  type?: PageType;
  content?: string;
  order?: number;
  lastContributor?: string;
  published?: boolean;
  visibility?: Visibility;
  updatedAt?: Date;
  contentType?: string;
  source?: PageSource;
  configuration?: { [key: string]: string };
  homepage?: boolean;
  parentId?: string;
  parentPath?: string;
  contentRevision?: Revision;
  hidden?: boolean;
  generalConditions?: boolean;
}

export interface Breadcrumb {
  id: string;
  name: string;
  position: number;
}
