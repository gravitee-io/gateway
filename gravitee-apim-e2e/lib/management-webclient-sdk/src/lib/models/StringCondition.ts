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
import type { StringConditionAllOf } from './StringConditionAllOf';
import {
    StringConditionAllOfFromJSON,
    StringConditionAllOfFromJSONTyped,
    StringConditionAllOfToJSON,
} from './StringConditionAllOf';

/**
 * 
 * @export
 * @interface StringCondition
 */
export interface StringCondition extends Condition {
    /**
     * 
     * @type {string}
     * @memberof StringCondition
     */
    property: string;
    /**
     * 
     * @type {string}
     * @memberof StringCondition
     */
    operator: StringConditionOperatorEnum;
    /**
     * 
     * @type {string}
     * @memberof StringCondition
     */
    pattern: string;
    /**
     * 
     * @type {boolean}
     * @memberof StringCondition
     */
    ignoreCase?: boolean;
}


/**
 * @export
 */
export const StringConditionOperatorEnum = {
    EQUALS: 'EQUALS',
    NOT_EQUALS: 'NOT_EQUALS',
    STARTS_WITH: 'STARTS_WITH',
    ENDS_WITH: 'ENDS_WITH',
    CONTAINS: 'CONTAINS',
    MATCHES: 'MATCHES'
} as const;
export type StringConditionOperatorEnum = typeof StringConditionOperatorEnum[keyof typeof StringConditionOperatorEnum];


/**
 * Check if a given object implements the StringCondition interface.
 */
export function instanceOfStringCondition(value: object): boolean {
    let isInstance = true;
    isInstance = isInstance && "property" in value;
    isInstance = isInstance && "operator" in value;
    isInstance = isInstance && "pattern" in value;

    return isInstance;
}

export function StringConditionFromJSON(json: any): StringCondition {
    return StringConditionFromJSONTyped(json, false);
}

export function StringConditionFromJSONTyped(json: any, ignoreDiscriminator: boolean): StringCondition {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        ...ConditionFromJSONTyped(json, ignoreDiscriminator),
        'property': json['property'],
        'operator': json['operator'],
        'pattern': json['pattern'],
        'ignoreCase': !exists(json, 'ignoreCase') ? undefined : json['ignoreCase'],
    };
}

export function StringConditionToJSON(value?: StringCondition | null): any {
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
        'pattern': value.pattern,
        'ignoreCase': value.ignoreCase,
    };
}

