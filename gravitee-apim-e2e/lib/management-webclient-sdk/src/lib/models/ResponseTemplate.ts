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
/**
 * A map that allows you to configure the output of a request based on the event throws by the gateway. Example : Quota exceeded, api-ky is missing, ...
 * @export
 * @interface ResponseTemplate
 */
export interface ResponseTemplate {
    /**
     * 
     * @type {string}
     * @memberof ResponseTemplate
     */
    body?: string;
    /**
     * 
     * @type {{ [key: string]: string; }}
     * @memberof ResponseTemplate
     */
    headers?: { [key: string]: string; };
    /**
     * 
     * @type {number}
     * @memberof ResponseTemplate
     */
    status?: number;
}

/**
 * Check if a given object implements the ResponseTemplate interface.
 */
export function instanceOfResponseTemplate(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function ResponseTemplateFromJSON(json: any): ResponseTemplate {
    return ResponseTemplateFromJSONTyped(json, false);
}

export function ResponseTemplateFromJSONTyped(json: any, ignoreDiscriminator: boolean): ResponseTemplate {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'body': !exists(json, 'body') ? undefined : json['body'],
        'headers': !exists(json, 'headers') ? undefined : json['headers'],
        'status': !exists(json, 'status') ? undefined : json['status'],
    };
}

export function ResponseTemplateToJSON(value?: ResponseTemplate | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'body': value.body,
        'headers': value.headers,
        'status': value.status,
    };
}

