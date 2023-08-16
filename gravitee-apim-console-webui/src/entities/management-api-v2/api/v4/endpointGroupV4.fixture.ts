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

import { EndpointGroupV4 } from './endpointGroupV4';

export function fakeEndpointGroupV4(
  modifier?: Partial<EndpointGroupV4> | ((baseApi: EndpointGroupV4) => EndpointGroupV4),
): EndpointGroupV4 {
  const base: EndpointGroupV4 = {
    name: 'default-group',
    loadBalancer: { type: 'ROUND_ROBIN' },
    type: 'kafka',
    endpoints: [],
    sharedConfiguration: {
      security: {
        protocol: 'PLAINTEXT',
      },
      consumer: {
        encodeMessageId: true,
        topics: ['kafka_topic_1'],
        enabled: true,
        autoOffsetReset: 'latest',
      },
    },
  };

  if (isFunction(modifier)) {
    return modifier(base);
  }

  return {
    ...base,
    ...modifier,
  };
}
