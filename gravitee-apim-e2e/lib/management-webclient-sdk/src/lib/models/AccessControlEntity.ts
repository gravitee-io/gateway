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
 * @interface AccessControlEntity
 */
export interface AccessControlEntity {
    /**
     * 
     * @type {string}
     * @memberof AccessControlEntity
     */
    referenceId?: string;
    /**
     * 
     * @type {string}
     * @memberof AccessControlEntity
     */
    referenceType?: string;
}

/**
 * Check if a given object implements the AccessControlEntity interface.
 */
export function instanceOfAccessControlEntity(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function AccessControlEntityFromJSON(json: any): AccessControlEntity {
    return AccessControlEntityFromJSONTyped(json, false);
}

export function AccessControlEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): AccessControlEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'referenceId': !exists(json, 'referenceId') ? undefined : json['referenceId'],
        'referenceType': !exists(json, 'referenceType') ? undefined : json['referenceType'],
    };
}

export function AccessControlEntityToJSON(value?: AccessControlEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'referenceId': value.referenceId,
        'referenceType': value.referenceType,
    };
}

