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
 * @interface MissingDataConditionAllOf
 */
export interface MissingDataConditionAllOf {
    /**
     * 
     * @type {number}
     * @memberof MissingDataConditionAllOf
     */
    duration?: number;
    /**
     * 
     * @type {string}
     * @memberof MissingDataConditionAllOf
     */
    timeUnit?: MissingDataConditionAllOfTimeUnitEnum;
}


/**
 * @export
 */
export const MissingDataConditionAllOfTimeUnitEnum = {
    NANOSECONDS: 'NANOSECONDS',
    MICROSECONDS: 'MICROSECONDS',
    MILLISECONDS: 'MILLISECONDS',
    SECONDS: 'SECONDS',
    MINUTES: 'MINUTES',
    HOURS: 'HOURS',
    DAYS: 'DAYS'
} as const;
export type MissingDataConditionAllOfTimeUnitEnum = typeof MissingDataConditionAllOfTimeUnitEnum[keyof typeof MissingDataConditionAllOfTimeUnitEnum];


/**
 * Check if a given object implements the MissingDataConditionAllOf interface.
 */
export function instanceOfMissingDataConditionAllOf(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function MissingDataConditionAllOfFromJSON(json: any): MissingDataConditionAllOf {
    return MissingDataConditionAllOfFromJSONTyped(json, false);
}

export function MissingDataConditionAllOfFromJSONTyped(json: any, ignoreDiscriminator: boolean): MissingDataConditionAllOf {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'duration': !exists(json, 'duration') ? undefined : json['duration'],
        'timeUnit': !exists(json, 'timeUnit') ? undefined : json['timeUnit'],
    };
}

export function MissingDataConditionAllOfToJSON(value?: MissingDataConditionAllOf | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'duration': value.duration,
        'timeUnit': value.timeUnit,
    };
}

