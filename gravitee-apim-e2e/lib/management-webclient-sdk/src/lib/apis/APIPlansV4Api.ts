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
    NewPlanEntityV4,
    NewPlanEntityV4FromJSON,
    NewPlanEntityV4ToJSON,
    PlanEntityV4,
    PlanEntityV4FromJSON,
    PlanEntityV4ToJSON,
    PlanSecurityTypeV4,
    PlanSecurityTypeV4FromJSON,
    PlanSecurityTypeV4ToJSON,
    UpdatePlanEntityV4,
    UpdatePlanEntityV4FromJSON,
    UpdatePlanEntityV4ToJSON,
} from '../models';

export interface CloseApiPlan1Request {
    plan: string;
    api: string;
    envId: string;
    orgId: string;
}

export interface CreateApiPlan1Request {
    api: string;
    envId: string;
    orgId: string;
    newPlanEntityV4: NewPlanEntityV4;
}

export interface DeleteApiPlan1Request {
    plan: string;
    api: string;
    envId: string;
    orgId: string;
}

export interface DeprecateApiPlan1Request {
    plan: string;
    api: string;
    envId: string;
    orgId: string;
}

export interface DepreciateApiPlan1Request {
    plan: string;
    api: string;
    envId: string;
    orgId: string;
}

export interface GetApiPlan1Request {
    plan: string;
    api: string;
    envId: string;
    orgId: string;
}

export interface GetApiPlans1Request {
    status?: Array<GetApiPlans1StatusEnum>;
    security?: Array<PlanSecurityTypeV4>;
    api: string;
    envId: string;
    orgId: string;
}

export interface PublishApiPlan1Request {
    plan: string;
    api: string;
    envId: string;
    orgId: string;
}

export interface UpdateApiPlan1Request {
    plan: string;
    api: string;
    envId: string;
    orgId: string;
    updatePlanEntityV4: UpdatePlanEntityV4;
}

/**
 * 
 */
export class APIPlansV4Api extends runtime.BaseAPI {

    /**
     * User must have the MANAGE_PLANS permission to use this service
     * Close  a plan
     */
    async closeApiPlan1Raw(requestParameters: CloseApiPlan1Request): Promise<runtime.ApiResponse<PlanEntityV4>> {
        if (requestParameters.plan === null || requestParameters.plan === undefined) {
            throw new runtime.RequiredError('plan','Required parameter requestParameters.plan was null or undefined when calling closeApiPlan1.');
        }

        if (requestParameters.api === null || requestParameters.api === undefined) {
            throw new runtime.RequiredError('api','Required parameter requestParameters.api was null or undefined when calling closeApiPlan1.');
        }

        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling closeApiPlan1.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling closeApiPlan1.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/v4/apis/{api}/plans/{plan}/_close`.replace(`{${"plan"}}`, encodeURIComponent(String(requestParameters.plan))).replace(`{${"api"}}`, encodeURIComponent(String(requestParameters.api))).replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => PlanEntityV4FromJSON(jsonValue));
    }

    /**
     * User must have the MANAGE_PLANS permission to use this service
     * Close  a plan
     */
    async closeApiPlan1(requestParameters: CloseApiPlan1Request): Promise<PlanEntityV4> {
        const response = await this.closeApiPlan1Raw(requestParameters);
        return await response.value();
    }

    /**
     * User must have the MANAGE_PLANS permission to use this service
     * Create a plan
     */
    async createApiPlan1Raw(requestParameters: CreateApiPlan1Request): Promise<runtime.ApiResponse<PlanEntityV4>> {
        if (requestParameters.api === null || requestParameters.api === undefined) {
            throw new runtime.RequiredError('api','Required parameter requestParameters.api was null or undefined when calling createApiPlan1.');
        }

        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling createApiPlan1.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling createApiPlan1.');
        }

        if (requestParameters.newPlanEntityV4 === null || requestParameters.newPlanEntityV4 === undefined) {
            throw new runtime.RequiredError('newPlanEntityV4','Required parameter requestParameters.newPlanEntityV4 was null or undefined when calling createApiPlan1.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/v4/apis/{api}/plans`.replace(`{${"api"}}`, encodeURIComponent(String(requestParameters.api))).replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: NewPlanEntityV4ToJSON(requestParameters.newPlanEntityV4),
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => PlanEntityV4FromJSON(jsonValue));
    }

    /**
     * User must have the MANAGE_PLANS permission to use this service
     * Create a plan
     */
    async createApiPlan1(requestParameters: CreateApiPlan1Request): Promise<PlanEntityV4> {
        const response = await this.createApiPlan1Raw(requestParameters);
        return await response.value();
    }

    /**
     * User must have the MANAGE_PLANS permission to use this service
     * Delete a plan
     */
    async deleteApiPlan1Raw(requestParameters: DeleteApiPlan1Request): Promise<runtime.ApiResponse<void>> {
        if (requestParameters.plan === null || requestParameters.plan === undefined) {
            throw new runtime.RequiredError('plan','Required parameter requestParameters.plan was null or undefined when calling deleteApiPlan1.');
        }

        if (requestParameters.api === null || requestParameters.api === undefined) {
            throw new runtime.RequiredError('api','Required parameter requestParameters.api was null or undefined when calling deleteApiPlan1.');
        }

        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling deleteApiPlan1.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling deleteApiPlan1.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/v4/apis/{api}/plans/{plan}`.replace(`{${"plan"}}`, encodeURIComponent(String(requestParameters.plan))).replace(`{${"api"}}`, encodeURIComponent(String(requestParameters.api))).replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'DELETE',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.VoidApiResponse(response);
    }

    /**
     * User must have the MANAGE_PLANS permission to use this service
     * Delete a plan
     */
    async deleteApiPlan1(requestParameters: DeleteApiPlan1Request): Promise<void> {
        await this.deleteApiPlan1Raw(requestParameters);
    }

    /**
     * User must have the API_PLAN[UPDATE] permission to use this service
     * Deprecate a plan
     */
    async deprecateApiPlan1Raw(requestParameters: DeprecateApiPlan1Request): Promise<runtime.ApiResponse<PlanEntityV4>> {
        if (requestParameters.plan === null || requestParameters.plan === undefined) {
            throw new runtime.RequiredError('plan','Required parameter requestParameters.plan was null or undefined when calling deprecateApiPlan1.');
        }

        if (requestParameters.api === null || requestParameters.api === undefined) {
            throw new runtime.RequiredError('api','Required parameter requestParameters.api was null or undefined when calling deprecateApiPlan1.');
        }

        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling deprecateApiPlan1.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling deprecateApiPlan1.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/v4/apis/{api}/plans/{plan}/_deprecate`.replace(`{${"plan"}}`, encodeURIComponent(String(requestParameters.plan))).replace(`{${"api"}}`, encodeURIComponent(String(requestParameters.api))).replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => PlanEntityV4FromJSON(jsonValue));
    }

    /**
     * User must have the API_PLAN[UPDATE] permission to use this service
     * Deprecate a plan
     */
    async deprecateApiPlan1(requestParameters: DeprecateApiPlan1Request): Promise<PlanEntityV4> {
        const response = await this.deprecateApiPlan1Raw(requestParameters);
        return await response.value();
    }

    /**
     * User must have the API_PLAN[UPDATE] permission to use this service
     * Deprecated, use \'_deprecate\' instead. Deprecate a plan
     */
    async depreciateApiPlan1Raw(requestParameters: DepreciateApiPlan1Request): Promise<runtime.ApiResponse<PlanEntityV4>> {
        if (requestParameters.plan === null || requestParameters.plan === undefined) {
            throw new runtime.RequiredError('plan','Required parameter requestParameters.plan was null or undefined when calling depreciateApiPlan1.');
        }

        if (requestParameters.api === null || requestParameters.api === undefined) {
            throw new runtime.RequiredError('api','Required parameter requestParameters.api was null or undefined when calling depreciateApiPlan1.');
        }

        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling depreciateApiPlan1.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling depreciateApiPlan1.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/v4/apis/{api}/plans/{plan}/_depreciate`.replace(`{${"plan"}}`, encodeURIComponent(String(requestParameters.plan))).replace(`{${"api"}}`, encodeURIComponent(String(requestParameters.api))).replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => PlanEntityV4FromJSON(jsonValue));
    }

    /**
     * User must have the API_PLAN[UPDATE] permission to use this service
     * Deprecated, use \'_deprecate\' instead. Deprecate a plan
     */
    async depreciateApiPlan1(requestParameters: DepreciateApiPlan1Request): Promise<PlanEntityV4> {
        const response = await this.depreciateApiPlan1Raw(requestParameters);
        return await response.value();
    }

    /**
     * User must have the READ permission to use this service
     * Get a plan
     */
    async getApiPlan1Raw(requestParameters: GetApiPlan1Request): Promise<runtime.ApiResponse<PlanEntityV4>> {
        if (requestParameters.plan === null || requestParameters.plan === undefined) {
            throw new runtime.RequiredError('plan','Required parameter requestParameters.plan was null or undefined when calling getApiPlan1.');
        }

        if (requestParameters.api === null || requestParameters.api === undefined) {
            throw new runtime.RequiredError('api','Required parameter requestParameters.api was null or undefined when calling getApiPlan1.');
        }

        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling getApiPlan1.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling getApiPlan1.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/v4/apis/{api}/plans/{plan}`.replace(`{${"plan"}}`, encodeURIComponent(String(requestParameters.plan))).replace(`{${"api"}}`, encodeURIComponent(String(requestParameters.api))).replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => PlanEntityV4FromJSON(jsonValue));
    }

    /**
     * User must have the READ permission to use this service
     * Get a plan
     */
    async getApiPlan1(requestParameters: GetApiPlan1Request): Promise<PlanEntityV4> {
        const response = await this.getApiPlan1Raw(requestParameters);
        return await response.value();
    }

    /**
     * List all the plans accessible to the current user.
     * List plans for an API
     */
    async getApiPlans1Raw(requestParameters: GetApiPlans1Request): Promise<runtime.ApiResponse<Array<PlanEntityV4>>> {
        if (requestParameters.api === null || requestParameters.api === undefined) {
            throw new runtime.RequiredError('api','Required parameter requestParameters.api was null or undefined when calling getApiPlans1.');
        }

        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling getApiPlans1.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling getApiPlans1.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        if (requestParameters.status) {
            queryParameters['status'] = requestParameters.status.join(runtime.COLLECTION_FORMATS["csv"]);
        }

        if (requestParameters.security) {
            queryParameters['security'] = requestParameters.security.join(runtime.COLLECTION_FORMATS["csv"]);
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/v4/apis/{api}/plans`.replace(`{${"api"}}`, encodeURIComponent(String(requestParameters.api))).replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(PlanEntityV4FromJSON));
    }

    /**
     * List all the plans accessible to the current user.
     * List plans for an API
     */
    async getApiPlans1(requestParameters: GetApiPlans1Request): Promise<Array<PlanEntityV4>> {
        const response = await this.getApiPlans1Raw(requestParameters);
        return await response.value();
    }

    /**
     * User must have the MANAGE_PLANS permission to use this service
     * Publicly publish plan
     */
    async publishApiPlan1Raw(requestParameters: PublishApiPlan1Request): Promise<runtime.ApiResponse<PlanEntityV4>> {
        if (requestParameters.plan === null || requestParameters.plan === undefined) {
            throw new runtime.RequiredError('plan','Required parameter requestParameters.plan was null or undefined when calling publishApiPlan1.');
        }

        if (requestParameters.api === null || requestParameters.api === undefined) {
            throw new runtime.RequiredError('api','Required parameter requestParameters.api was null or undefined when calling publishApiPlan1.');
        }

        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling publishApiPlan1.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling publishApiPlan1.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/v4/apis/{api}/plans/{plan}/_publish`.replace(`{${"plan"}}`, encodeURIComponent(String(requestParameters.plan))).replace(`{${"api"}}`, encodeURIComponent(String(requestParameters.api))).replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => PlanEntityV4FromJSON(jsonValue));
    }

    /**
     * User must have the MANAGE_PLANS permission to use this service
     * Publicly publish plan
     */
    async publishApiPlan1(requestParameters: PublishApiPlan1Request): Promise<PlanEntityV4> {
        const response = await this.publishApiPlan1Raw(requestParameters);
        return await response.value();
    }

    /**
     * User must have the MANAGE_PLANS permission to use this service
     * Update a plan
     */
    async updateApiPlan1Raw(requestParameters: UpdateApiPlan1Request): Promise<runtime.ApiResponse<PlanEntityV4>> {
        if (requestParameters.plan === null || requestParameters.plan === undefined) {
            throw new runtime.RequiredError('plan','Required parameter requestParameters.plan was null or undefined when calling updateApiPlan1.');
        }

        if (requestParameters.api === null || requestParameters.api === undefined) {
            throw new runtime.RequiredError('api','Required parameter requestParameters.api was null or undefined when calling updateApiPlan1.');
        }

        if (requestParameters.envId === null || requestParameters.envId === undefined) {
            throw new runtime.RequiredError('envId','Required parameter requestParameters.envId was null or undefined when calling updateApiPlan1.');
        }

        if (requestParameters.orgId === null || requestParameters.orgId === undefined) {
            throw new runtime.RequiredError('orgId','Required parameter requestParameters.orgId was null or undefined when calling updateApiPlan1.');
        }

        if (requestParameters.updatePlanEntityV4 === null || requestParameters.updatePlanEntityV4 === undefined) {
            throw new runtime.RequiredError('updatePlanEntityV4','Required parameter requestParameters.updatePlanEntityV4 was null or undefined when calling updateApiPlan1.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && (this.configuration.username !== undefined || this.configuration.password !== undefined)) {
            headerParameters["Authorization"] = "Basic " + btoa(this.configuration.username + ":" + this.configuration.password);
        }
        const response = await this.request({
            path: `/organizations/{orgId}/environments/{envId}/v4/apis/{api}/plans/{plan}`.replace(`{${"plan"}}`, encodeURIComponent(String(requestParameters.plan))).replace(`{${"api"}}`, encodeURIComponent(String(requestParameters.api))).replace(`{${"envId"}}`, encodeURIComponent(String(requestParameters.envId))).replace(`{${"orgId"}}`, encodeURIComponent(String(requestParameters.orgId))),
            method: 'PUT',
            headers: headerParameters,
            query: queryParameters,
            body: UpdatePlanEntityV4ToJSON(requestParameters.updatePlanEntityV4),
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => PlanEntityV4FromJSON(jsonValue));
    }

    /**
     * User must have the MANAGE_PLANS permission to use this service
     * Update a plan
     */
    async updateApiPlan1(requestParameters: UpdateApiPlan1Request): Promise<PlanEntityV4> {
        const response = await this.updateApiPlan1Raw(requestParameters);
        return await response.value();
    }

}

/**
    * @export
    * @enum {string}
    */
export enum GetApiPlans1StatusEnum {
    STAGING = 'STAGING',
    PUBLISHED = 'PUBLISHED',
    DEPRECATED = 'DEPRECATED',
    CLOSED = 'CLOSED'
}
