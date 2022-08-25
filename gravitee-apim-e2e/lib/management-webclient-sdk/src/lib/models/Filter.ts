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
import type { Projection } from './Projection';
import {
    ProjectionFromJSON,
    ProjectionFromJSONTyped,
    ProjectionToJSON,
} from './Projection';

/**
 * 
 * @export
 * @interface Filter
 */
export interface Filter {
    /**
     * 
     * @type {Array<Projection>}
     * @memberof Filter
     */
    projections?: Array<Projection>;
    /**
     * 
     * @type {string}
     * @memberof Filter
     */
    type?: FilterTypeEnum;
}


/**
 * @export
 */
export const FilterTypeEnum = {
    STRING: 'STRING',
    THRESHOLD: 'THRESHOLD',
    THRESHOLD_RANGE: 'THRESHOLD_RANGE',
    RATE: 'RATE',
    FREQUENCY: 'FREQUENCY',
    AGGREGATION: 'AGGREGATION',
    COMPARE: 'COMPARE',
    STRING_COMPARE: 'STRING_COMPARE',
    MISSING_DATA: 'MISSING_DATA'
} as const;
export type FilterTypeEnum = typeof FilterTypeEnum[keyof typeof FilterTypeEnum];


/**
 * Check if a given object implements the Filter interface.
 */
export function instanceOfFilter(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function FilterFromJSON(json: any): Filter {
    return FilterFromJSONTyped(json, false);
}

export function FilterFromJSONTyped(json: any, ignoreDiscriminator: boolean): Filter {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'projections': !exists(json, 'projections') ? undefined : ((json['projections'] as Array<any>).map(ProjectionFromJSON)),
        'type': !exists(json, 'type') ? undefined : json['type'],
    };
}

export function FilterToJSON(value?: Filter | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'projections': value.projections === undefined ? undefined : ((value.projections as Array<any>).map(ProjectionToJSON)),
        'type': value.type,
    };
}

