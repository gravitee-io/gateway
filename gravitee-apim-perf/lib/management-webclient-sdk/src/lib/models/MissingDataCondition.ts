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
import {
    Condition,
    ConditionFromJSON,
    ConditionFromJSONTyped,
    ConditionToJSON,
    MissingDataConditionAllOf,
    MissingDataConditionAllOfFromJSON,
    MissingDataConditionAllOfFromJSONTyped,
    MissingDataConditionAllOfToJSON,
    Projection,
    ProjectionFromJSON,
    ProjectionFromJSONTyped,
    ProjectionToJSON,
} from './';

/**
 * 
 * @export
 * @interface MissingDataCondition
 */
export interface MissingDataCondition extends Condition {
    /**
     * 
     * @type {number}
     * @memberof MissingDataCondition
     */
    duration: number;
    /**
     * 
     * @type {string}
     * @memberof MissingDataCondition
     */
    timeUnit?: MissingDataConditionTimeUnitEnum;
}

export function MissingDataConditionFromJSON(json: any): MissingDataCondition {
    return MissingDataConditionFromJSONTyped(json, false);
}

export function MissingDataConditionFromJSONTyped(json: any, ignoreDiscriminator: boolean): MissingDataCondition {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        ...ConditionFromJSONTyped(json, ignoreDiscriminator),
        'duration': json['duration'],
        'timeUnit': !exists(json, 'timeUnit') ? undefined : json['timeUnit'],
    };
}

export function MissingDataConditionToJSON(value?: MissingDataCondition | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        ...ConditionToJSON(value),
        'duration': value.duration,
        'timeUnit': value.timeUnit,
    };
}

/**
* @export
* @enum {string}
*/
export enum MissingDataConditionTimeUnitEnum {
    NANOSECONDS = 'NANOSECONDS',
    MICROSECONDS = 'MICROSECONDS',
    MILLISECONDS = 'MILLISECONDS',
    SECONDS = 'SECONDS',
    MINUTES = 'MINUTES',
    HOURS = 'HOURS',
    DAYS = 'DAYS'
}


