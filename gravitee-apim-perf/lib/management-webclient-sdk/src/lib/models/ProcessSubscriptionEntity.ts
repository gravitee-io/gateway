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
 * @interface ProcessSubscriptionEntity
 */
export interface ProcessSubscriptionEntity {
    /**
     * 
     * @type {boolean}
     * @memberof ProcessSubscriptionEntity
     */
    accepted: boolean;
    /**
     * 
     * @type {string}
     * @memberof ProcessSubscriptionEntity
     */
    customApiKey?: string;
    /**
     * 
     * @type {Date}
     * @memberof ProcessSubscriptionEntity
     */
    ending_at?: Date;
    /**
     * 
     * @type {string}
     * @memberof ProcessSubscriptionEntity
     */
    id?: string;
    /**
     * 
     * @type {string}
     * @memberof ProcessSubscriptionEntity
     */
    reason?: string;
    /**
     * 
     * @type {Date}
     * @memberof ProcessSubscriptionEntity
     */
    starting_at?: Date;
}

export function ProcessSubscriptionEntityFromJSON(json: any): ProcessSubscriptionEntity {
    return ProcessSubscriptionEntityFromJSONTyped(json, false);
}

export function ProcessSubscriptionEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): ProcessSubscriptionEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'accepted': json['accepted'],
        'customApiKey': !exists(json, 'customApiKey') ? undefined : json['customApiKey'],
        'ending_at': !exists(json, 'ending_at') ? undefined : (new Date(json['ending_at'])),
        'id': !exists(json, 'id') ? undefined : json['id'],
        'reason': !exists(json, 'reason') ? undefined : json['reason'],
        'starting_at': !exists(json, 'starting_at') ? undefined : (new Date(json['starting_at'])),
    };
}

export function ProcessSubscriptionEntityToJSON(value?: ProcessSubscriptionEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'accepted': value.accepted,
        'customApiKey': value.customApiKey,
        'ending_at': value.ending_at === undefined ? undefined : (value.ending_at.toISOString()),
        'id': value.id,
        'reason': value.reason,
        'starting_at': value.starting_at === undefined ? undefined : (value.starting_at.toISOString()),
    };
}


