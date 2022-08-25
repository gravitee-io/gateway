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
 * @interface ReviewEntity
 */
export interface ReviewEntity {
    /**
     * 
     * @type {string}
     * @memberof ReviewEntity
     */
    message?: string;
}

/**
 * Check if a given object implements the ReviewEntity interface.
 */
export function instanceOfReviewEntity(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function ReviewEntityFromJSON(json: any): ReviewEntity {
    return ReviewEntityFromJSONTyped(json, false);
}

export function ReviewEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): ReviewEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'message': !exists(json, 'message') ? undefined : json['message'],
    };
}

export function ReviewEntityToJSON(value?: ReviewEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'message': value.message,
    };
}

