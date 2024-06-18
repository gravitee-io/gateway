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
import { isFunction } from 'lodash';

import { CategoryApi } from './categoryApi';

export function fakeCategoryApi(modifier?: Partial<CategoryApi> | ((baseApi: CategoryApi) => CategoryApi)): CategoryApi {
  const base: CategoryApi = {
    id: 'api-1',
    name: 'Api 1',
    description: 'The whole universe in your hand.',
    apiVersion: '1.0',
    definitionVersion: 'V4',
    order: 1,
  };

  if (isFunction(modifier)) {
    return modifier(base);
  }

  return {
    ...base,
    ...modifier,
  };
}
