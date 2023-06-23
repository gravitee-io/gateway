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

import { Component, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpTestingController } from '@angular/common/http/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatIconTestingModule } from '@angular/material/icon/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';

import { ApiEndpointComponent } from './api-endpoint.component';
import { ApiEndpointModule } from './api-endpoint.module';
import { ApiEndpointHarness } from './api-endpoint.harness';

import { CONSTANTS_TESTING, GioHttpTestingModule } from '../../../../../shared/testing';
import { ApiV4, fakeApiV4, fakeConnectorPlugin } from '../../../../../entities/management-api-v2';
import { UIRouterState, UIRouterStateParams } from '../../../../../ajs-upgraded-providers';

@Component({
  template: `<api-endpoint #apiEndpoint></api-endpoint>`,
})
class TestComponent {
  @ViewChild('apiEndpoint') apiEndpoint: ApiEndpointComponent;
  api?: ApiV4;
}

describe('ApiEndpointComponent', () => {
  const API_ID = 'apiId';
  const apiV4 = fakeApiV4({
    id: API_ID,
  });
  const fakeAjsState = {
    go: jest.fn(),
  };

  let fixture: ComponentFixture<TestComponent>;
  let httpTestingController: HttpTestingController;
  let loader: HarnessLoader;
  let componentHarness: ApiEndpointHarness;

  const initComponent = async (api: ApiV4, routerParams: unknown = { apiId: API_ID, groupIndex: 0 }) => {
    TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [NoopAnimationsModule, GioHttpTestingModule, ApiEndpointModule, MatIconTestingModule],
      providers: [
        { provide: UIRouterState, useValue: fakeAjsState },
        { provide: UIRouterStateParams, useValue: routerParams },
      ],
    });

    fixture = TestBed.createComponent(TestComponent);
    fixture.componentInstance.api = api;

    loader = TestbedHarnessEnvironment.loader(fixture);
    httpTestingController = TestBed.inject(HttpTestingController);
    fixture.detectChanges();

    componentHarness = await loader.getHarness(ApiEndpointHarness);

    expectApiGetRequest(apiV4);
    expectEndpointSchemaGetRequest(apiV4.endpointGroups[0].type);
    expectEndpointsSharedConfigurationSchemaGetRequest(apiV4.endpointGroups[0].type);
    expectEndpointPluginGetRequest(apiV4.endpointGroups[0].type);
  };

  afterEach(() => {
    jest.clearAllMocks();
    httpTestingController.verify();
  });

  describe('add endpoints in a group', () => {
    it('should load kafka endpoint form dynamically', async () => {
      await initComponent(apiV4);
    });

    it('should edit and save the endpoint', async () => {
      await initComponent(apiV4);

      expect(await componentHarness.isSaveButtonDisabled()).toBeTruthy();

      await componentHarness.fillInputName('endpoint-name');
      fixture.detectChanges();

      expect(await componentHarness.isSaveButtonDisabled()).toBeFalsy();

      await componentHarness.clickSaveButton();

      expectApiGetRequest(apiV4);

      const updatedApi: ApiV4 = {
        ...apiV4,
        endpointGroups: [
          {
            name: 'default-group',
            type: 'kafka',
            loadBalancer: {
              type: 'ROUND_ROBIN',
            },
            endpoints: [
              {
                name: 'default',
                type: 'kafka',
                weight: 1,
                inheritConfiguration: false,
                configuration: {
                  bootstrapServers: 'localhost:9092',
                },
              },
              {
                name: 'endpoint-name',
                type: 'kafka',
              },
            ],
          },
        ],
      };
      expectApiPutRequest(updatedApi);
      expect(fakeAjsState.go).toHaveBeenCalledWith('management.apis.ng.endpoints');
    });

    it('should edit and save an existing endpoint', async () => {
      await initComponent(apiV4, { apiId: API_ID, groupIndex: 0, endpointIndex: 0 });

      fixture.detectChanges();
      expect(await componentHarness.getEndpointName()).toStrictEqual('default');

      await componentHarness.fillInputName('endpoint-name updated');
      fixture.detectChanges();

      expect(await componentHarness.getEndpointName()).toStrictEqual('endpoint-name updated');

      await componentHarness.clickSaveButton();

      expectApiGetRequest(apiV4);

      const updatedApi: ApiV4 = {
        ...apiV4,
        endpointGroups: [
          {
            name: 'default-group',
            type: 'kafka',
            loadBalancer: {
              type: 'ROUND_ROBIN',
            },
            endpoints: [
              {
                name: 'default',
                type: 'kafka',
                weight: 1,
                inheritConfiguration: false,
                configuration: {
                  bootstrapServers: 'localhost:9092',
                },
              },
              {
                name: 'endpoint-name updated',
                type: 'kafka',
              },
            ],
          },
        ],
      };
      expectApiPutRequest(updatedApi);
      expect(fakeAjsState.go).toHaveBeenCalledWith('management.apis.ng.endpoints');
    });
  });

  describe('onPreviousClick', () => {
    it('should go back to endpoints groups list page', async () => {
      await initComponent(apiV4);

      await componentHarness.clickPreviousButton();

      expect(fakeAjsState.go).toHaveBeenCalledWith('management.apis.ng.endpoints');
    });
  });

  function expectEndpointsSharedConfigurationSchemaGetRequest(id: string) {
    httpTestingController
      .expectOne({ url: `${CONSTANTS_TESTING.v2BaseURL}/plugins/endpoints/${id}/shared-configuration-schema`, method: 'GET' })
      .flush(
        '{"$schema":"http://json-schema.org/draft-07/schema#","type":"object","definitions":{"security":{"type":"object","title":"Security configuration","oneOf":[{"title":"Protocol PLAINTEXT","properties":{"protocol":{"const":"PLAINTEXT"}},"additionalProperties":false},{"title":"Protocol SASL_PLAINTEXT","properties":{"protocol":{"const":"SASL_PLAINTEXT"},"sasl":{"$ref":"#/definitions/securityProtocolSasl"}},"additionalProperties":false,"required":["sasl"]},{"title":"Protocol SASL_SSL","properties":{"protocol":{"const":"SASL_SSL"},"sasl":{"$ref":"#/definitions/securityProtocolSasl"},"ssl":{"$ref":"#/definitions/securityProtocolSsl"}},"additionalProperties":false,"required":["sasl","ssl"]},{"title":"Protocol SSL","properties":{"protocol":{"const":"SSL"},"ssl":{"$ref":"#/definitions/securityProtocolSsl"}},"additionalProperties":false,"required":["ssl"]}]}},"oneOf":[{"title":"Use Consumer","properties":{"security":{"$ref":"#/definitions/security"},"consumer":{"$ref":"#/definitions/consumer"}},"required":["consumer"],"additionalProperties":false},{"title":"Use Producer","properties":{"security":{"$ref":"#/definitions/security"},"producer":{"$ref":"#/definitions/producer"}},"required":["producer"],"additionalProperties":false},{"title":"Use Consumer and Producer","properties":{"security":{"$ref":"#/definitions/security"},"producer":{"$ref":"#/definitions/producer"},"consumer":{"$ref":"#/definitions/consumer"}},"required":["producer","consumer"],"additionalProperties":false}]}',
      );
  }

  function expectEndpointSchemaGetRequest(id: string) {
    httpTestingController
      .expectOne({ url: `${CONSTANTS_TESTING.v2BaseURL}/plugins/endpoints/${id}/schema`, method: 'GET' })
      .flush(
        '{"$schema":"http://json-schema.org/draft-07/schema#","type":"object","definitions":{"bootstrapServers":{"title":"Bootstrap servers (bootstrap.servers)","description":"This list should be in the form host1:port1,host2:port2,...","type":"string","gioConfig":{"banner":{"title":"Bootstrap servers","text":"A list of host/port pairs, separated by a comma, to use for establishing the initial connection to the Kafka cluster. The client will make use of all servers irrespective of which servers are specified here for bootstrapping—this list only impacts the initial hosts used to discover the full set of servers. "}}}},"properties":{"bootstrapServers":{"$ref":"#/definitions/bootstrapServers"}},"required":["bootstrapServers"],"additionalProperties":false}',
      );
    fixture.detectChanges();
  }

  function expectApiGetRequest(api: ApiV4) {
    httpTestingController.expectOne({ url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${api.id}`, method: 'GET' }).flush(api);
    fixture.detectChanges();
  }

  function expectApiPutRequest(api: ApiV4) {
    httpTestingController.expectOne({ url: `${CONSTANTS_TESTING.env.v2BaseURL}/apis/${api.id}`, method: 'PUT' }).flush(api);
    fixture.detectChanges();
  }

  function expectEndpointPluginGetRequest(pluginId: string) {
    httpTestingController
      .expectOne({ url: `${CONSTANTS_TESTING.v2BaseURL}/plugins/endpoints/${pluginId}`, method: 'GET' })
      .flush([fakeConnectorPlugin({ id: pluginId, name: pluginId })]);
  }
});
