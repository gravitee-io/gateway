/* tslint:disable */
/* eslint-disable */
/**
 * Gravitee.io - Management API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


import * as runtime from '../runtime';
import type {
  AlertMetric,
} from '../models';
import {
    AlertMetricFromJSON,
    AlertMetricToJSON,
} from '../models';

export interface GetAlertMetricsRequest {
    envId: string;
    orgId: string;
}

/**
 * 
 */
export class AlertsApi extends runtime.BaseAPI {

    /**
     * List alert metrics
     */
    async getAlertMetricsRaw(requestParameters: GetAlertMetricsRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<Array<AlertMetric>>> {
        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling getAlertMetrics.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling getAlertMetrics.');
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/alerts/metrics`.replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(AlertMetricFromJSON));
    }

    /**
     * List alert metrics
     */
    async getAlertMetrics(requestParameters: GetAlertMetricsRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<Array<AlertMetric>> {
        const response = await this.getAlertMetricsRaw(requestParameters, initOverrides);
        return await response.value();
    }

}
