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
import type { UserRoleEntity } from './UserRoleEntity';
import {
    UserRoleEntityFromJSON,
    UserRoleEntityFromJSONTyped,
    UserRoleEntityToJSON,
} from './UserRoleEntity';

/**
 * 
 * @export
 * @interface UserEntity
 */
export interface UserEntity {
    /**
     * 
     * @type {Date}
     * @memberof UserEntity
     */
    created_at?: Date;
    /**
     * 
     * @type {{ [key: string]: any; }}
     * @memberof UserEntity
     */
    customFields?: { [key: string]: any; };
    /**
     * 
     * @type {string}
     * @memberof UserEntity
     */
    readonly displayName?: string;
    /**
     * 
     * @type {string}
     * @memberof UserEntity
     */
    email?: string;
    /**
     * 
     * @type {{ [key: string]: Array<UserRoleEntity>; }}
     * @memberof UserEntity
     */
    envRoles?: { [key: string]: Array<UserRoleEntity>; };
    /**
     * 
     * @type {Date}
     * @memberof UserEntity
     */
    firstConnectionAt?: Date;
    /**
     * 
     * @type {string}
     * @memberof UserEntity
     */
    firstname?: string;
    /**
     * 
     * @type {string}
     * @memberof UserEntity
     */
    id?: string;
    /**
     * 
     * @type {Date}
     * @memberof UserEntity
     */
    lastConnectionAt?: Date;
    /**
     * 
     * @type {string}
     * @memberof UserEntity
     */
    lastname?: string;
    /**
     * 
     * @type {number}
     * @memberof UserEntity
     */
    loginCount?: number;
    /**
     * 
     * @type {boolean}
     * @memberof UserEntity
     */
    newsletterSubscribed?: boolean;
    /**
     * 
     * @type {number}
     * @memberof UserEntity
     */
    number_of_active_tokens?: number;
    /**
     * 
     * @type {string}
     * @memberof UserEntity
     */
    password?: string;
    /**
     * 
     * @type {string}
     * @memberof UserEntity
     */
    picture?: string;
    /**
     * 
     * @type {boolean}
     * @memberof UserEntity
     */
    primary_owner?: boolean;
    /**
     * 
     * @type {Array<UserRoleEntity>}
     * @memberof UserEntity
     */
    roles?: Array<UserRoleEntity>;
    /**
     * 
     * @type {string}
     * @memberof UserEntity
     */
    source?: string;
    /**
     * 
     * @type {string}
     * @memberof UserEntity
     */
    sourceId?: string;
    /**
     * 
     * @type {string}
     * @memberof UserEntity
     */
    status?: string;
    /**
     * 
     * @type {Date}
     * @memberof UserEntity
     */
    updated_at?: Date;
}

/**
 * Check if a given object implements the UserEntity interface.
 */
export function instanceOfUserEntity(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function UserEntityFromJSON(json: any): UserEntity {
    return UserEntityFromJSONTyped(json, false);
}

export function UserEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): UserEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'created_at': !exists(json, 'created_at') ? undefined : (new Date(json['created_at'])),
        'customFields': !exists(json, 'customFields') ? undefined : json['customFields'],
        'displayName': !exists(json, 'displayName') ? undefined : json['displayName'],
        'email': !exists(json, 'email') ? undefined : json['email'],
        'envRoles': !exists(json, 'envRoles') ? undefined : json['envRoles'],
        'firstConnectionAt': !exists(json, 'firstConnectionAt') ? undefined : (new Date(json['firstConnectionAt'])),
        'firstname': !exists(json, 'firstname') ? undefined : json['firstname'],
        'id': !exists(json, 'id') ? undefined : json['id'],
        'lastConnectionAt': !exists(json, 'lastConnectionAt') ? undefined : (new Date(json['lastConnectionAt'])),
        'lastname': !exists(json, 'lastname') ? undefined : json['lastname'],
        'loginCount': !exists(json, 'loginCount') ? undefined : json['loginCount'],
        'newsletterSubscribed': !exists(json, 'newsletterSubscribed') ? undefined : json['newsletterSubscribed'],
        'number_of_active_tokens': !exists(json, 'number_of_active_tokens') ? undefined : json['number_of_active_tokens'],
        'password': !exists(json, 'password') ? undefined : json['password'],
        'picture': !exists(json, 'picture') ? undefined : json['picture'],
        'primary_owner': !exists(json, 'primary_owner') ? undefined : json['primary_owner'],
        'roles': !exists(json, 'roles') ? undefined : ((json['roles'] as Array<any>).map(UserRoleEntityFromJSON)),
        'source': !exists(json, 'source') ? undefined : json['source'],
        'sourceId': !exists(json, 'sourceId') ? undefined : json['sourceId'],
        'status': !exists(json, 'status') ? undefined : json['status'],
        'updated_at': !exists(json, 'updated_at') ? undefined : (new Date(json['updated_at'])),
    };
}

export function UserEntityToJSON(value?: UserEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'created_at': value.created_at === undefined ? undefined : (value.created_at.toISOString()),
        'customFields': value.customFields,
        'email': value.email,
        'envRoles': value.envRoles,
        'firstConnectionAt': value.firstConnectionAt === undefined ? undefined : (value.firstConnectionAt.toISOString()),
        'firstname': value.firstname,
        'id': value.id,
        'lastConnectionAt': value.lastConnectionAt === undefined ? undefined : (value.lastConnectionAt.toISOString()),
        'lastname': value.lastname,
        'loginCount': value.loginCount,
        'newsletterSubscribed': value.newsletterSubscribed,
        'number_of_active_tokens': value.number_of_active_tokens,
        'password': value.password,
        'picture': value.picture,
        'primary_owner': value.primary_owner,
        'roles': value.roles === undefined ? undefined : ((value.roles as Array<any>).map(UserRoleEntityToJSON)),
        'source': value.source,
        'sourceId': value.sourceId,
        'status': value.status,
        'updated_at': value.updated_at === undefined ? undefined : (value.updated_at.toISOString()),
    };
}

