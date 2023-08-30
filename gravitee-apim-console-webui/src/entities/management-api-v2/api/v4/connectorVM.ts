import { ConnectorPlugin, ListenerType, Qos } from '../../index';

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
export type ConnectorVM = {
  id: string;
  name: string;
  description: string;
  /**
   * @deprecated since v4.0.0
   * Please use deployed instead of this field to check if a connector is licensed or not.
   */
  isEnterprise: boolean;
  supportedListenerType: ListenerType;
  supportedQos: Qos[];
  icon: string;
  deployed: boolean;
};

export const fromConnector: (iconService, connector: ConnectorPlugin) => ConnectorVM = (iconService, connector: ConnectorPlugin) => {
  const result: ConnectorVM = {
    id: connector.id,
    name: connector.name,
    description: connector.description,
    isEnterprise: connector.id.endsWith('-advanced'),
    supportedListenerType: connector.supportedListenerType,
    supportedQos: connector.supportedQos,
    icon: iconService.registerSvg(connector.id, connector.icon),
    deployed: connector.deployed,
  };
  return result;
};
