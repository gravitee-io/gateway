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
import type { GrantedAuthority } from './GrantedAuthority';
import {
    GrantedAuthorityFromJSON,
    GrantedAuthorityFromJSONTyped,
    GrantedAuthorityToJSON,
} from './GrantedAuthority';
import type { UserDetailRole } from './UserDetailRole';
import {
    UserDetailRoleFromJSON,
    UserDetailRoleFromJSONTyped,
    UserDetailRoleToJSON,
} from './UserDetailRole';

/**
 * 
 * @export
 * @interface UserDetails
 */
export interface UserDetails {
    /**
     * 
     * @type {Array<GrantedAuthority>}
     * @memberof UserDetails
     */
    authorities?: Array<GrantedAuthority>;
    /**
     * 
     * @type {Date}
     * @memberof UserDetails
     */
    created_at?: Date;
    /**
     * 
     * @type {{ [key: string]: any; }}
     * @memberof UserDetails
     */
    customFields?: { [key: string]: any; };
    /**
     * 
     * @type {string}
     * @memberof UserDetails
     */
    displayName?: string;
    /**
     * 
     * @type {boolean}
     * @memberof UserDetails
     */
    displayNewsletterSubscription?: boolean;
    /**
     * 
     * @type {string}
     * @memberof UserDetails
     */
    email?: string;
    /**
     * 
     * @type {boolean}
     * @memberof UserDetails
     */
    enabled?: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof UserDetails
     */
    firstLogin?: boolean;
    /**
     * 
     * @type {string}
     * @memberof UserDetails
     */
    firstname?: string;
    /**
     * 
     * @type {{ [key: string]: Array<string>; }}
     * @memberof UserDetails
     */
    groupsByEnvironment?: { [key: string]: Array<string>; };
    /**
     * 
     * @type {string}
     * @memberof UserDetails
     */
    id?: string;
    /**
     * 
     * @type {Date}
     * @memberof UserDetails
     */
    last_connection_at?: Date;
    /**
     * 
     * @type {string}
     * @memberof UserDetails
     */
    lastname?: string;
    /**
     * 
     * @type {Array<string>}
     * @memberof UserDetails
     */
    picture?: Array<string>;
    /**
     * 
     * @type {boolean}
     * @memberof UserDetails
     */
    primaryOwner?: boolean;
    /**
     * 
     * @type {Array<UserDetailRole>}
     * @memberof UserDetails
     */
    roles?: Array<UserDetailRole>;
    /**
     * 
     * @type {string}
     * @memberof UserDetails
     */
    source?: string;
    /**
     * 
     * @type {string}
     * @memberof UserDetails
     */
    sourceId?: string;
    /**
     * 
     * @type {boolean}
     * @memberof UserDetails
     */
    system?: boolean;
    /**
     * 
     * @type {Date}
     * @memberof UserDetails
     */
    updated_at?: Date;
}

/**
 * Check if a given object implements the UserDetails interface.
 */
export function instanceOfUserDetails(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function UserDetailsFromJSON(json: any): UserDetails {
    return UserDetailsFromJSONTyped(json, false);
}

export function UserDetailsFromJSONTyped(json: any, ignoreDiscriminator: boolean): UserDetails {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'authorities': !exists(json, 'authorities') ? undefined : ((json['authorities'] as Array<any>).map(GrantedAuthorityFromJSON)),
        'created_at': !exists(json, 'created_at') ? undefined : (new Date(json['created_at'])),
        'customFields': !exists(json, 'customFields') ? undefined : json['customFields'],
        'displayName': !exists(json, 'displayName') ? undefined : json['displayName'],
        'displayNewsletterSubscription': !exists(json, 'displayNewsletterSubscription') ? undefined : json['displayNewsletterSubscription'],
        'email': !exists(json, 'email') ? undefined : json['email'],
        'enabled': !exists(json, 'enabled') ? undefined : json['enabled'],
        'firstLogin': !exists(json, 'firstLogin') ? undefined : json['firstLogin'],
        'firstname': !exists(json, 'firstname') ? undefined : json['firstname'],
        'groupsByEnvironment': !exists(json, 'groupsByEnvironment') ? undefined : json['groupsByEnvironment'],
        'id': !exists(json, 'id') ? undefined : json['id'],
        'last_connection_at': !exists(json, 'last_connection_at') ? undefined : (new Date(json['last_connection_at'])),
        'lastname': !exists(json, 'lastname') ? undefined : json['lastname'],
        'picture': !exists(json, 'picture') ? undefined : json['picture'],
        'primaryOwner': !exists(json, 'primaryOwner') ? undefined : json['primaryOwner'],
        'roles': !exists(json, 'roles') ? undefined : ((json['roles'] as Array<any>).map(UserDetailRoleFromJSON)),
        'source': !exists(json, 'source') ? undefined : json['source'],
        'sourceId': !exists(json, 'sourceId') ? undefined : json['sourceId'],
        'system': !exists(json, 'system') ? undefined : json['system'],
        'updated_at': !exists(json, 'updated_at') ? undefined : (new Date(json['updated_at'])),
    };
}

export function UserDetailsToJSON(value?: UserDetails | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'authorities': value.authorities === undefined ? undefined : ((value.authorities as Array<any>).map(GrantedAuthorityToJSON)),
        'created_at': value.created_at === undefined ? undefined : (value.created_at.toISOString()),
        'customFields': value.customFields,
        'displayName': value.displayName,
        'displayNewsletterSubscription': value.displayNewsletterSubscription,
        'email': value.email,
        'enabled': value.enabled,
        'firstLogin': value.firstLogin,
        'firstname': value.firstname,
        'groupsByEnvironment': value.groupsByEnvironment,
        'id': value.id,
        'last_connection_at': value.last_connection_at === undefined ? undefined : (value.last_connection_at.toISOString()),
        'lastname': value.lastname,
        'picture': value.picture,
        'primaryOwner': value.primaryOwner,
        'roles': value.roles === undefined ? undefined : ((value.roles as Array<any>).map(UserDetailRoleToJSON)),
        'source': value.source,
        'sourceId': value.sourceId,
        'system': value.system,
        'updated_at': value.updated_at === undefined ? undefined : (value.updated_at.toISOString()),
    };
}

