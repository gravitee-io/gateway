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

import { EndpointServices } from './endpointServices';

export interface EndpointV4 {
  /**
   * The name of the endpoint
   */
  name?: string;
  /**
   * The type of the endpoint
   */
  type: string;
  /**
   * The weight of the endpoint
   */
  weight?: number;
  /**
   * Is the configuration of the endpoint inherited from the endpoint group it belongs to.
   */
  inheritConfiguration?: boolean;
  configuration?: any;
  sharedConfigurationOverride?: any;
  services?: EndpointServices;
  secondary?: boolean;
}

export const EndpointV4Default = {
  byTypeAndGroupName(type: string, groupName: string): EndpointV4 {
    if (type === 'mock') {
      return { name: `${groupName} default endpoint`, type, configuration: {} };
    }
    return { name: `${groupName} default endpoint`, type, inheritConfiguration: true };
  },
};
