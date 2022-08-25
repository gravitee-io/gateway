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
import type { GroupMemberEntity } from './GroupMemberEntity';
import {
    GroupMemberEntityFromJSON,
    GroupMemberEntityFromJSONTyped,
    GroupMemberEntityToJSON,
} from './GroupMemberEntity';

/**
 * 
 * @export
 * @interface ApiGroupsWithMembersMap
 */
export interface ApiGroupsWithMembersMap {
    [key: string]: Array<GroupMemberEntity> | any;
    /**
     * 
     * @type {boolean}
     * @memberof ApiGroupsWithMembersMap
     */
    empty?: boolean;
}

/**
 * Check if a given object implements the ApiGroupsWithMembersMap interface.
 */
export function instanceOfApiGroupsWithMembersMap(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function ApiGroupsWithMembersMapFromJSON(json: any): ApiGroupsWithMembersMap {
    return ApiGroupsWithMembersMapFromJSONTyped(json, false);
}

export function ApiGroupsWithMembersMapFromJSONTyped(json: any, ignoreDiscriminator: boolean): ApiGroupsWithMembersMap {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
            ...json,
        'empty': !exists(json, 'empty') ? undefined : json['empty'],
    };
}

export function ApiGroupsWithMembersMapToJSON(value?: ApiGroupsWithMembersMap | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
            ...value,
        'empty': value.empty,
    };
}

