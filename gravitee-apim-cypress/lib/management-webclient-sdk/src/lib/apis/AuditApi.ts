/* tslint:disable */
/* eslint-disable */
/**
 * Gravitee.io - Management API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 3.18.0-SNAPSHOT
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


import * as runtime from '../runtime';
import {
    AuditEntityMetadataPage,
    AuditEntityMetadataPageFromJSON,
    AuditEntityMetadataPageToJSON,
} from '../models';

export interface GetAuditEventsRequest {
    envId: string;
    orgId: string;
}

export interface GetAuditsRequest {
    envLog?: boolean;
    orgLog?: boolean;
    api?: string;
    application?: string;
    event?: string;
    from?: number;
    to?: number;
    size?: number;
    page?: number;
    envId: string;
    orgId: string;
}

/**
 * 
 */
export class AuditApi extends runtime.BaseAPI {

    /**
     * User must have the MANAGEMENT_AUDIT[READ] permission to use this service
     * List available audit event type for platform
     */
    async getAuditEventsRaw(requestParameters: GetAuditEventsRequest): Promise<runtime.ApiResponse<Array<any>>> {
        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling getAuditEvents.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling getAuditEvents.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/audit/events`.replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse<any>(response);
    }

    /**
     * User must have the MANAGEMENT_AUDIT[READ] permission to use this service
     * List available audit event type for platform
     */
    async getAuditEvents(requestParameters: GetAuditEventsRequest): Promise<Array<any>> {
        const response = await this.getAuditEventsRaw(requestParameters);
        return await response.value();
    }

    /**
     * User must have the MANAGEMENT_AUDIT[READ] permission to use this service
     * Retrieve audit logs for the platform
     */
    async getAuditsRaw(requestParameters: GetAuditsRequest): Promise<runtime.ApiResponse<AuditEntityMetadataPage>> {
        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling getAudits.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling getAudits.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        if (requestParameters.envLog !== undefined) {
            queryParameters['envLog'] = requestParameters.envLog;
        }

        if (requestParameters.orgLog !== undefined) {
            queryParameters['orgLog'] = requestParameters.orgLog;
        }

        if (requestParameters.api !== undefined) {
            queryParameters['api'] = requestParameters.api;
        }

        if (requestParameters.application !== undefined) {
            queryParameters['application'] = requestParameters.application;
        }

        if (requestParameters.event !== undefined) {
            queryParameters['event'] = requestParameters.event;
        }

        if (requestParameters.from !== undefined) {
            queryParameters['from'] = requestParameters.from;
        }

        if (requestParameters.to !== undefined) {
            queryParameters['to'] = requestParameters.to;
        }

        if (requestParameters.size !== undefined) {
            queryParameters['size'] = requestParameters.size;
        }

        if (requestParameters.page !== undefined) {
            queryParameters['page'] = requestParameters.page;
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/audit`.replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => AuditEntityMetadataPageFromJSON(jsonValue));
    }

    /**
     * User must have the MANAGEMENT_AUDIT[READ] permission to use this service
     * Retrieve audit logs for the platform
     */
    async getAudits(requestParameters: GetAuditsRequest): Promise<AuditEntityMetadataPage> {
        const response = await this.getAuditsRaw(requestParameters);
        return await response.value();
    }

}
