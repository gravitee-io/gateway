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
  OrganizationFlowConfiguration,
} from '../models';
import {
    OrganizationFlowConfigurationFromJSON,
    OrganizationFlowConfigurationToJSON,
} from '../models';

export interface GetConfigurationSchemaFormRequest {
    orgId: string;
}

export interface GetPlatformFlowSchemaFormRequest {
    orgId: string;
}

export interface HasPoliciesRequest {
    orgId: string;
}

/**
 * 
 */
export class FlowsApi extends runtime.BaseAPI {

    /**
     */
    async getConfigurationSchemaFormRaw(requestParameters: GetConfigurationSchemaFormRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<void>> {
        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling getConfigurationSchemaForm.');
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/configuration/flows/configuration-schema`.replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.VoidApiResponse(response);
    }

    /**
     */
    async getConfigurationSchemaForm(requestParameters: GetConfigurationSchemaFormRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<void> {
        await this.getConfigurationSchemaFormRaw(requestParameters, initOverrides);
    }

    /**
     */
    async getPlatformFlowSchemaFormRaw(requestParameters: GetPlatformFlowSchemaFormRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<void>> {
        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling getPlatformFlowSchemaForm.');
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/configuration/flows/flow-schema`.replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.VoidApiResponse(response);
    }

    /**
     */
    async getPlatformFlowSchemaForm(requestParameters: GetPlatformFlowSchemaFormRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<void> {
        await this.getPlatformFlowSchemaFormRaw(requestParameters, initOverrides);
    }

    /**
     * Get the global flow configuration of the organization
     */
    async hasPoliciesRaw(requestParameters: HasPoliciesRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<OrganizationFlowConfiguration>> {
        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling hasPolicies.');
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/configuration/flows`.replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => OrganizationFlowConfigurationFromJSON(jsonValue));
    }

    /**
     * Get the global flow configuration of the organization
     */
    async hasPolicies(requestParameters: HasPoliciesRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<OrganizationFlowConfiguration> {
        const response = await this.hasPoliciesRaw(requestParameters, initOverrides);
        return await response.value();
    }

}
