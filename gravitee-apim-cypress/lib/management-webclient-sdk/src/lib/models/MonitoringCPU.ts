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

import { exists, mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface MonitoringCPU
 */
export interface MonitoringCPU {
    /**
     * 
     * @type {number}
     * @memberof MonitoringCPU
     */
    percent_use?: number;
    /**
     * 
     * @type {{ [key: string]: any; }}
     * @memberof MonitoringCPU
     */
    load_average?: { [key: string]: any; };
}

export function MonitoringCPUFromJSON(json: any): MonitoringCPU {
    return MonitoringCPUFromJSONTyped(json, false);
}

export function MonitoringCPUFromJSONTyped(json: any, ignoreDiscriminator: boolean): MonitoringCPU {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'percent_use': !exists(json, 'percent_use') ? undefined : json['percent_use'],
        'load_average': !exists(json, 'load_average') ? undefined : json['load_average'],
    };
}

export function MonitoringCPUToJSON(value?: MonitoringCPU | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'percent_use': value.percent_use,
        'load_average': value.load_average,
    };
}


