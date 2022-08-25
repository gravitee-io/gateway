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
 * @interface ApiMembership
 */
export interface ApiMembership {
    /**
     * User's technical identifier.
     * @type {string}
     * @memberof ApiMembership
     */
    id?: string;
    /**
     * User's reference for user providing from an identity provider.
     * @type {string}
     * @memberof ApiMembership
     */
    reference?: string;
    /**
     * Role's name
     * @type {string}
     * @memberof ApiMembership
     */
    role: string;
}

/**
 * Check if a given object implements the ApiMembership interface.
 */
export function instanceOfApiMembership(value: object): boolean {
    let isInstance = true;
    isInstance = isInstance && "role" in value;

    return isInstance;
}

export function ApiMembershipFromJSON(json: any): ApiMembership {
    return ApiMembershipFromJSONTyped(json, false);
}

export function ApiMembershipFromJSONTyped(json: any, ignoreDiscriminator: boolean): ApiMembership {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'reference': !exists(json, 'reference') ? undefined : json['reference'],
        'role': json['role'],
    };
}

export function ApiMembershipToJSON(value?: ApiMembership | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'reference': value.reference,
        'role': value.role,
    };
}

