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
import type { Condition } from './Condition';
import {
    ConditionFromJSON,
    ConditionFromJSONTyped,
    ConditionToJSON,
} from './Condition';
import type { Projection } from './Projection';
import {
    ProjectionFromJSON,
    ProjectionFromJSONTyped,
    ProjectionToJSON,
} from './Projection';
import type { ThresholdConditionAllOf } from './ThresholdConditionAllOf';
import {
    ThresholdConditionAllOfFromJSON,
    ThresholdConditionAllOfFromJSONTyped,
    ThresholdConditionAllOfToJSON,
} from './ThresholdConditionAllOf';

/**
 * 
 * @export
 * @interface ThresholdCondition
 */
export interface ThresholdCondition extends Condition {
    /**
     * 
     * @type {string}
     * @memberof ThresholdCondition
     */
    property: string;
    /**
     * 
     * @type {string}
     * @memberof ThresholdCondition
     */
    operator: ThresholdConditionOperatorEnum;
    /**
     * 
     * @type {number}
     * @memberof ThresholdCondition
     */
    threshold: number;
}


/**
 * @export
 */
export const ThresholdConditionOperatorEnum = {
    LT: 'LT',
    LTE: 'LTE',
    GTE: 'GTE',
    GT: 'GT'
} as const;
export type ThresholdConditionOperatorEnum = typeof ThresholdConditionOperatorEnum[keyof typeof ThresholdConditionOperatorEnum];


/**
 * Check if a given object implements the ThresholdCondition interface.
 */
export function instanceOfThresholdCondition(value: object): boolean {
    let isInstance = true;
    isInstance = isInstance && "property" in value;
    isInstance = isInstance && "operator" in value;
    isInstance = isInstance && "threshold" in value;

    return isInstance;
}

export function ThresholdConditionFromJSON(json: any): ThresholdCondition {
    return ThresholdConditionFromJSONTyped(json, false);
}

export function ThresholdConditionFromJSONTyped(json: any, ignoreDiscriminator: boolean): ThresholdCondition {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        ...ConditionFromJSONTyped(json, ignoreDiscriminator),
        'property': json['property'],
        'operator': json['operator'],
        'threshold': json['threshold'],
    };
}

export function ThresholdConditionToJSON(value?: ThresholdCondition | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        ...ConditionToJSON(value),
        'property': value.property,
        'operator': value.operator,
        'threshold': value.threshold,
    };
}

