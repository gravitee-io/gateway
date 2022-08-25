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
import type { JKSKeyStoreAllOf } from './JKSKeyStoreAllOf';
import {
    JKSKeyStoreAllOfFromJSON,
    JKSKeyStoreAllOfFromJSONTyped,
    JKSKeyStoreAllOfToJSON,
} from './JKSKeyStoreAllOf';
import type { KeyStore } from './KeyStore';
import {
    KeyStoreFromJSON,
    KeyStoreFromJSONTyped,
    KeyStoreToJSON,
} from './KeyStore';

/**
 * 
 * @export
 * @interface PKCS12KeyStore
 */
export interface PKCS12KeyStore extends KeyStore {
    /**
     * 
     * @type {string}
     * @memberof PKCS12KeyStore
     */
    path?: string;
    /**
     * 
     * @type {string}
     * @memberof PKCS12KeyStore
     */
    content?: string;
    /**
     * 
     * @type {string}
     * @memberof PKCS12KeyStore
     */
    password?: string;
}



/**
 * Check if a given object implements the PKCS12KeyStore interface.
 */
export function instanceOfPKCS12KeyStore(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function PKCS12KeyStoreFromJSON(json: any): PKCS12KeyStore {
    return PKCS12KeyStoreFromJSONTyped(json, false);
}

export function PKCS12KeyStoreFromJSONTyped(json: any, ignoreDiscriminator: boolean): PKCS12KeyStore {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        ...KeyStoreFromJSONTyped(json, ignoreDiscriminator),
        'path': !exists(json, 'path') ? undefined : json['path'],
        'content': !exists(json, 'content') ? undefined : json['content'],
        'password': !exists(json, 'password') ? undefined : json['password'],
    };
}

export function PKCS12KeyStoreToJSON(value?: PKCS12KeyStore | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        ...KeyStoreToJSON(value),
        'path': value.path,
        'content': value.content,
        'password': value.password,
    };
}

