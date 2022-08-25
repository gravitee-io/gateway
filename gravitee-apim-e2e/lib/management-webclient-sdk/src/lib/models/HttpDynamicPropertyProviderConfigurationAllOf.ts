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

import { exists, mapValues } from '../runtime';
import type { HttpHeader } from './HttpHeader';
import {
    HttpHeaderFromJSON,
    HttpHeaderFromJSONTyped,
    HttpHeaderToJSON,
} from './HttpHeader';

/**
 * 
 * @export
 * @interface HttpDynamicPropertyProviderConfigurationAllOf
 */
export interface HttpDynamicPropertyProviderConfigurationAllOf {
    /**
     * 
     * @type {string}
     * @memberof HttpDynamicPropertyProviderConfigurationAllOf
     */
    url?: string;
    /**
     * 
     * @type {string}
     * @memberof HttpDynamicPropertyProviderConfigurationAllOf
     */
    specification?: string;
    /**
     * 
     * @type {boolean}
     * @memberof HttpDynamicPropertyProviderConfigurationAllOf
     */
    useSystemProxy?: boolean;
    /**
     * 
     * @type {string}
     * @memberof HttpDynamicPropertyProviderConfigurationAllOf
     */
    method?: HttpDynamicPropertyProviderConfigurationAllOfMethodEnum;
    /**
     * 
     * @type {Array<HttpHeader>}
     * @memberof HttpDynamicPropertyProviderConfigurationAllOf
     */
    headers?: Array<HttpHeader>;
    /**
     * 
     * @type {string}
     * @memberof HttpDynamicPropertyProviderConfigurationAllOf
     */
    body?: string;
}


/**
 * @export
 */
export const HttpDynamicPropertyProviderConfigurationAllOfMethodEnum = {
    CONNECT: 'CONNECT',
    DELETE: 'DELETE',
    GET: 'GET',
    HEAD: 'HEAD',
    OPTIONS: 'OPTIONS',
    PATCH: 'PATCH',
    POST: 'POST',
    PUT: 'PUT',
    TRACE: 'TRACE',
    OTHER: 'OTHER'
} as const;
export type HttpDynamicPropertyProviderConfigurationAllOfMethodEnum = typeof HttpDynamicPropertyProviderConfigurationAllOfMethodEnum[keyof typeof HttpDynamicPropertyProviderConfigurationAllOfMethodEnum];


/**
 * Check if a given object implements the HttpDynamicPropertyProviderConfigurationAllOf interface.
 */
export function instanceOfHttpDynamicPropertyProviderConfigurationAllOf(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function HttpDynamicPropertyProviderConfigurationAllOfFromJSON(json: any): HttpDynamicPropertyProviderConfigurationAllOf {
    return HttpDynamicPropertyProviderConfigurationAllOfFromJSONTyped(json, false);
}

export function HttpDynamicPropertyProviderConfigurationAllOfFromJSONTyped(json: any, ignoreDiscriminator: boolean): HttpDynamicPropertyProviderConfigurationAllOf {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'url': !exists(json, 'url') ? undefined : json['url'],
        'specification': !exists(json, 'specification') ? undefined : json['specification'],
        'useSystemProxy': !exists(json, 'useSystemProxy') ? undefined : json['useSystemProxy'],
        'method': !exists(json, 'method') ? undefined : json['method'],
        'headers': !exists(json, 'headers') ? undefined : ((json['headers'] as Array<any>).map(HttpHeaderFromJSON)),
        'body': !exists(json, 'body') ? undefined : json['body'],
    };
}

export function HttpDynamicPropertyProviderConfigurationAllOfToJSON(value?: HttpDynamicPropertyProviderConfigurationAllOf | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'url': value.url,
        'specification': value.specification,
        'useSystemProxy': value.useSystemProxy,
        'method': value.method,
        'headers': value.headers === undefined ? undefined : ((value.headers as Array<any>).map(HttpHeaderToJSON)),
        'body': value.body,
    };
}

