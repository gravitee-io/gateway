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
import http, { RefinedParams } from 'k6/http';
import { k6Options } from '@env/environment';
import { HttpHelper } from '@helpers/http.helper';
import { OUT_OF_SCENARIO } from '@clients/GatewayClient';
import { randomString } from '@helpers/random.helper';

const baseUrl = k6Options.apim.managementBaseUrl;
const appsUrl = `${baseUrl}/organizations/${k6Options.apim.organization}/environments/${k6Options.apim.environment}/applications`;

export class ApplicationsV4Client {
  static createApplication(params: RefinedParams<any>) {
    const name = randomString();
    const app = {
      name: name,
      description: 'application',
      settings: {
        app: {
          type: 'test',
        },
      },
    };
    return http.post(appsUrl, JSON.stringify(app), {
      tags: { name: OUT_OF_SCENARIO },
      ...params,
    });
  }

  static deleteApplication(appId: string, params: RefinedParams<any>) {
    return http.del(
      HttpHelper.replacePathParams(`${appsUrl}/:appId`, [':appId'], [appId]),
      {},
      {
        tags: { name: OUT_OF_SCENARIO },
        ...params,
      },
    );
  }
}
