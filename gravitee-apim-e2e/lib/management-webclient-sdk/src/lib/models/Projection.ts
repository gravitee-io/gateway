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
     PropertyProjectionFromJSONTyped
} from './';

/**
 * 
 * @export
 * @interface Projection
 */
export interface Projection {
    /**
     * 
     * @type {string}
     * @memberof Projection
     */
    type?: ProjectionTypeEnum;
}


/**
 * @export
 */
export const ProjectionTypeEnum = {
    PROPERTY: 'PROPERTY'
} as const;
export type ProjectionTypeEnum = typeof ProjectionTypeEnum[keyof typeof ProjectionTypeEnum];


/**
 * Check if a given object implements the Projection interface.
 */
export function instanceOfProjection(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function ProjectionFromJSON(json: any): Projection {
    return ProjectionFromJSONTyped(json, false);
}

export function ProjectionFromJSONTyped(json: any, ignoreDiscriminator: boolean): Projection {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    if (!ignoreDiscriminator) {
        if (json['type'] === 'PropertyProjection') {
            return PropertyProjectionFromJSONTyped(json, true);
        }
    }
    return {
        
        'type': !exists(json, 'type') ? undefined : json['type'],
    };
}

export function ProjectionToJSON(value?: Projection | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'type': value.type,
    };
}

