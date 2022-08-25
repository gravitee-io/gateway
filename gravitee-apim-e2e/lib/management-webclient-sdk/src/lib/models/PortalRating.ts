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
import type { RatingComment } from './RatingComment';
import {
    RatingCommentFromJSON,
    RatingCommentFromJSONTyped,
    RatingCommentToJSON,
} from './RatingComment';

/**
 * 
 * @export
 * @interface PortalRating
 */
export interface PortalRating {
    /**
     * 
     * @type {RatingComment}
     * @memberof PortalRating
     */
    comment?: RatingComment;
    /**
     * 
     * @type {boolean}
     * @memberof PortalRating
     */
    enabled?: boolean;
}

/**
 * Check if a given object implements the PortalRating interface.
 */
export function instanceOfPortalRating(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function PortalRatingFromJSON(json: any): PortalRating {
    return PortalRatingFromJSONTyped(json, false);
}

export function PortalRatingFromJSONTyped(json: any, ignoreDiscriminator: boolean): PortalRating {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'comment': !exists(json, 'comment') ? undefined : RatingCommentFromJSON(json['comment']),
        'enabled': !exists(json, 'enabled') ? undefined : json['enabled'],
    };
}

export function PortalRatingToJSON(value?: PortalRating | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'comment': RatingCommentToJSON(value.comment),
        'enabled': value.enabled,
    };
}

