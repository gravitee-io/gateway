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
import { UpdateBaseApi } from './updateBaseApi';

import { ExecutionMode, FlowV2, Proxy, ServicesV2 } from '../api/v2';
import { FlowMode } from '../api';

export interface UpdateApiV2 extends UpdateBaseApi {
  definitionVersion: 'V2';
  proxy?: Proxy;
  flowMode?: FlowMode;
  /** @description The list of flows associated with this API. */
  flows?: FlowV2[];
  services?: ServicesV2;
  /** @description The list of path mappings associated with this API. */
  pathMappings?: string[];
  executionMode?: ExecutionMode;
}
