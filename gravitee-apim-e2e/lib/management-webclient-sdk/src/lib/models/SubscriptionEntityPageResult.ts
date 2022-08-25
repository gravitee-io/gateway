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
import type { Page } from './Page';
import {
    PageFromJSON,
    PageFromJSONTyped,
    PageToJSON,
} from './Page';
import type { SubscriptionEntity } from './SubscriptionEntity';
import {
    SubscriptionEntityFromJSON,
    SubscriptionEntityFromJSONTyped,
    SubscriptionEntityToJSON,
} from './SubscriptionEntity';

/**
 * 
 * @export
 * @interface SubscriptionEntityPageResult
 */
export interface SubscriptionEntityPageResult {
    /**
     * 
     * @type {Array<SubscriptionEntity>}
     * @memberof SubscriptionEntityPageResult
     */
    data?: Array<SubscriptionEntity>;
    /**
     * 
     * @type {{ [key: string]: { [key: string]: any; }; }}
     * @memberof SubscriptionEntityPageResult
     */
    metadata?: { [key: string]: { [key: string]: any; }; };
    /**
     * 
     * @type {Page}
     * @memberof SubscriptionEntityPageResult
     */
    page?: Page;
}

/**
 * Check if a given object implements the SubscriptionEntityPageResult interface.
 */
export function instanceOfSubscriptionEntityPageResult(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function SubscriptionEntityPageResultFromJSON(json: any): SubscriptionEntityPageResult {
    return SubscriptionEntityPageResultFromJSONTyped(json, false);
}

export function SubscriptionEntityPageResultFromJSONTyped(json: any, ignoreDiscriminator: boolean): SubscriptionEntityPageResult {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'data': !exists(json, 'data') ? undefined : ((json['data'] as Array<any>).map(SubscriptionEntityFromJSON)),
        'metadata': !exists(json, 'metadata') ? undefined : json['metadata'],
        'page': !exists(json, 'page') ? undefined : PageFromJSON(json['page']),
    };
}

export function SubscriptionEntityPageResultToJSON(value?: SubscriptionEntityPageResult | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'data': value.data === undefined ? undefined : ((value.data as Array<any>).map(SubscriptionEntityToJSON)),
        'metadata': value.metadata,
        'page': PageToJSON(value.page),
    };
}

