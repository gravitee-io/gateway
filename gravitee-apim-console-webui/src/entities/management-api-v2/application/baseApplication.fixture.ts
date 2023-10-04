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
import { BaseApplication } from './baseApplication';

export function fakeBaseApplication(modifier?: Partial<BaseApplication>): BaseApplication {
  const base: BaseApplication = {
    id: 'my-application-1',
    name: 'My first application',
    description: 'My first application with a description',
    domain: 'https://my-domain.com',
    type: 'My special type',
    primaryOwner: {
      id: 'owner-id',
      type: 'USER',
      displayName: 'Primary Owner',
    },
    apiKeyMode: 'UNSPECIFIED',
  };

  return {
    ...base,
    ...modifier,
  };
}
