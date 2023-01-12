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
import { ApiService } from '../../../services/api.service';

export default apisProxyRouterConfig;

/* @ngInject */
function apisProxyRouterConfig($stateProvider) {
  $stateProvider
    .state('management.apis.detail.proxy', {})
    .state('management.apis.detail.proxy.entrypoints', {
      url: '/proxy',
      component: 'ngApiProxyEntrypoints',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-definition-r', 'api-health-r'],
        },
        docs: {
          page: 'management-api-proxy',
        },
      },
    })
    .state('management.apis.detail.proxy.cors', {
      url: '/cors',
      component: 'ngApiProxyCors',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-definition-r'],
        },
        docs: {
          page: 'management-api-proxy',
        },
      },
    })
    .state('management.apis.detail.proxy.deployments', {
      url: '/deployments',
      component: 'ngApiProxyDeployments',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-definition-r'],
        },
        docs: {
          page: 'management-api-proxy',
        },
      },
    })
    .state('management.apis.detail.proxy.failover', {
      url: '/failover',
      component: 'ngApiProxyFailover',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-definition-r'],
        },
        docs: {
          page: 'management-api-proxy-failover',
        },
      },
    })
    .state('management.apis.detail.proxy.endpoints', {
      url: '/endpoints',
      component: 'ngApiProxyEndpointList',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-definition-r'],
        },
        docs: {
          page: 'management-api-proxy-endpoint',
        },
      },
    })
    .state('management.apis.detail.proxy.endpoint', {
      url: '/groups/:groupName/endpoints/:endpointName',
      component: 'ngApiProxyGroupEndpointEdit',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-definition-r'],
        },
        docs: {
          page: 'management-api-proxy-endpoints',
        },
      },
    })
    .state('management.apis.detail.proxy.group', {
      url: '/groups/:groupName',
      component: 'ngApiProxyGroupEdit',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-definition-r'],
        },
        docs: {
          page: 'management-api-proxy-group',
        },
      },
    })
    .state('management.apis.detail.proxy.healthCheckDashboard', {
      abstract: true,
      url: '/healthcheck-dashboard',
      template: require('./health-check-dashboard/healthcheck.html'),
    })
    .state('management.apis.detail.proxy.healthCheckDashboard.visualize', {
      url: '?from&to&page&size',
      template: require('./health-check-dashboard/healthcheck-visualize.html'),
      controller: 'ApiHealthCheckController',
      controllerAs: 'healthCheckCtrl',
      data: {
        perms: {
          only: ['api-health-r'],
        },
        docs: {
          page: 'management-api-health-check',
        },
      },
      params: {
        from: {
          type: 'int',
          dynamic: true,
        },
        to: {
          type: 'int',
          dynamic: true,
        },
        page: {
          type: 'int',
          dynamic: true,
        },
        size: {
          type: 'int',
          dynamic: true,
        },
      },
    })
    .state('management.apis.detail.proxy.healthCheckDashboard.log', {
      url: '/logs/:log',
      template: require('./health-check-dashboard/healthcheck-log.html'),
      controller: 'ApiHealthCheckLogController',
      controllerAs: 'healthCheckLogCtrl',
      resolve: {
        resolvedLog: ($stateParams, ApiService: ApiService) => ApiService.getHealthLog($stateParams.apiId, $stateParams.log),
      },
      data: {
        perms: {
          only: ['api-health-r'],
        },
      },
    })
    .state('management.apis.detail.proxy.healthcheck', {
      url: '/healthcheck',
      component: 'ngApiProxyHealthCheck',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-health-c'],
        },
        docs: {
          page: 'management-api-health-check-configure',
        },
      },
    })
    .state('management.apis.detail.proxy.responsetemplates', {
      abstract: true,
      url: '/responsetemplates',
    })
    .state('management.apis.detail.proxy.responsetemplates.list', {
      url: '',
      component: 'ngApiProxyResponseTemplatesList',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-response_templates-r'],
        },
        docs: {
          page: 'management-api-proxy-response-templates',
        },
      },
    })
    .state('management.apis.detail.proxy.responsetemplates.new', {
      url: '/',
      component: 'ngApiProxyResponseTemplatesEdit',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-response_templates-c', 'api-response_templates-r', 'api-response_templates-u'],
        },
        docs: {
          page: 'management-api-proxy-response-template',
        },
      },
    })
    .state('management.apis.detail.proxy.responsetemplates.edit', {
      url: '/:responseTemplateId',
      component: 'ngApiProxyResponseTemplatesEdit',
      data: {
        useAngularMaterial: true,
        perms: {
          only: ['api-response_templates-c', 'api-response_templates-r', 'api-response_templates-u'],
        },
        docs: {
          page: 'management-api-proxy-response-template',
        },
      },
    });
}
