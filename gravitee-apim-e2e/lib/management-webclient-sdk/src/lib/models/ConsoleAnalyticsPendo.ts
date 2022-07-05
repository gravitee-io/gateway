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
 * 
 * @export
 * @interface ConsoleAnalyticsPendo
 */
export interface ConsoleAnalyticsPendo {
    /**
     * 
     * @type {string}
     * @memberof ConsoleAnalyticsPendo
     */
    apiKey?: string;
    /**
     * 
     * @type {boolean}
     * @memberof ConsoleAnalyticsPendo
     */
    enabled?: boolean;
}

export function ConsoleAnalyticsPendoFromJSON(json: any): ConsoleAnalyticsPendo {
    return ConsoleAnalyticsPendoFromJSONTyped(json, false);
}

export function ConsoleAnalyticsPendoFromJSONTyped(json: any, ignoreDiscriminator: boolean): ConsoleAnalyticsPendo {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'apiKey': !exists(json, 'apiKey') ? undefined : json['apiKey'],
        'enabled': !exists(json, 'enabled') ? undefined : json['enabled'],
    };
}

export function ConsoleAnalyticsPendoToJSON(value?: ConsoleAnalyticsPendo | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'apiKey': value.apiKey,
        'enabled': value.enabled,
    };
}


