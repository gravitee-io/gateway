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
import {
    Analytics,
    AnalyticsFromJSON,
    AnalyticsToJSON,
    AnalyticsType,
    AnalyticsTypeFromJSON,
    AnalyticsTypeToJSON,
} from '../models';

export interface GetPlatformAnalyticsRequest {
    from?: number;
    to?: number;
    interval?: number;
    query?: string;
    field?: string;
    size?: number;
    type: AnalyticsType;
    ranges?: Array<string>;
    aggs?: Array<string>;
    envId: string;
    orgId: string;
}

/**
 * 
 */
export class EnvironmentAnalyticsApi extends runtime.BaseAPI {

    /**
     * Get environment analytics
     */
    async getPlatformAnalyticsRaw(requestParameters: GetPlatformAnalyticsRequest): Promise<runtime.ApiResponse<Analytics>> {
        if (requestParameters.type === null || requestParameters.type === undefined) {
            throw new runtime.RequiredError('type','Required parameter requestParameters.type was null or undefined when calling getPlatformAnalytics.');
        }

        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling getPlatformAnalytics.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling getPlatformAnalytics.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        if (requestParameters.from !== undefined) {
            queryParameters['from'] = requestParameters.from;
        }

        if (requestParameters.to !== undefined) {
            queryParameters['to'] = requestParameters.to;
        }

        if (requestParameters.interval !== undefined) {
            queryParameters['interval'] = requestParameters.interval;
        }

        if (requestParameters.query !== undefined) {
            queryParameters['query'] = requestParameters.query;
        }

        if (requestParameters.field !== undefined) {
            queryParameters['field'] = requestParameters.field;
        }

        if (requestParameters.size !== undefined) {
            queryParameters['size'] = requestParameters.size;
        }

        if (requestParameters.type !== undefined) {
            queryParameters['type'] = requestParameters.type;
        }

        if (requestParameters.ranges) {
            queryParameters['ranges'] = requestParameters.ranges.join(runtime.COLLECTION_FORMATS["csv"]);
        }

        if (requestParameters.aggs) {
            queryParameters['aggs'] = requestParameters.aggs.join(runtime.COLLECTION_FORMATS["csv"]);
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/analytics`.replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => AnalyticsFromJSON(jsonValue));
    }

    /**
     * Get environment analytics
     */
    async getPlatformAnalytics(requestParameters: GetPlatformAnalyticsRequest): Promise<Analytics> {
        const response = await this.getPlatformAnalyticsRaw(requestParameters);
        return await response.value();
    }

}
