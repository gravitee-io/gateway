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
 * @interface DuplicateApiEntity
 */
export interface DuplicateApiEntity {
    /**
     * 
     * @type {string}
     * @memberof DuplicateApiEntity
     */
    context_path: string;
    /**
     * 
     * @type {Array<string>}
     * @memberof DuplicateApiEntity
     */
    filtered_fields?: Array<string>;
    /**
     * 
     * @type {string}
     * @memberof DuplicateApiEntity
     */
    version?: string;
}

/**
 * Check if a given object implements the DuplicateApiEntity interface.
 */
export function instanceOfDuplicateApiEntity(value: object): boolean {
    let isInstance = true;
    isInstance = isInstance && "context_path" in value;

    return isInstance;
}

export function DuplicateApiEntityFromJSON(json: any): DuplicateApiEntity {
    return DuplicateApiEntityFromJSONTyped(json, false);
}

export function DuplicateApiEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): DuplicateApiEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'context_path': json['context_path'],
        'filtered_fields': !exists(json, 'filtered_fields') ? undefined : json['filtered_fields'],
        'version': !exists(json, 'version') ? undefined : json['version'],
    };
}

export function DuplicateApiEntityToJSON(value?: DuplicateApiEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'context_path': value.context_path,
        'filtered_fields': value.filtered_fields,
        'version': value.version,
    };
}

