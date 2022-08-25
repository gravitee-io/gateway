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
 * @interface RatingSummaryEntity
 */
export interface RatingSummaryEntity {
    /**
     * 
     * @type {string}
     * @memberof RatingSummaryEntity
     */
    api?: string;
    /**
     * 
     * @type {number}
     * @memberof RatingSummaryEntity
     */
    averageRate?: number;
    /**
     * 
     * @type {number}
     * @memberof RatingSummaryEntity
     */
    numberOfRatings?: number;
    /**
     * 
     * @type {{ [key: string]: number; }}
     * @memberof RatingSummaryEntity
     */
    numberOfRatingsByRate?: { [key: string]: number; };
}

/**
 * Check if a given object implements the RatingSummaryEntity interface.
 */
export function instanceOfRatingSummaryEntity(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function RatingSummaryEntityFromJSON(json: any): RatingSummaryEntity {
    return RatingSummaryEntityFromJSONTyped(json, false);
}

export function RatingSummaryEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): RatingSummaryEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'api': !exists(json, 'api') ? undefined : json['api'],
        'averageRate': !exists(json, 'averageRate') ? undefined : json['averageRate'],
        'numberOfRatings': !exists(json, 'numberOfRatings') ? undefined : json['numberOfRatings'],
        'numberOfRatingsByRate': !exists(json, 'numberOfRatingsByRate') ? undefined : json['numberOfRatingsByRate'],
    };
}

export function RatingSummaryEntityToJSON(value?: RatingSummaryEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'api': value.api,
        'averageRate': value.averageRate,
        'numberOfRatings': value.numberOfRatings,
        'numberOfRatingsByRate': value.numberOfRatingsByRate,
    };
}

