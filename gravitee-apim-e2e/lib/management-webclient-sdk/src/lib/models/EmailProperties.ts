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
 * @interface EmailProperties
 */
export interface EmailProperties {
    /**
     * 
     * @type {boolean}
     * @memberof EmailProperties
     */
    auth?: boolean;
    /**
     * 
     * @type {string}
     * @memberof EmailProperties
     */
    sslTrust?: string;
    /**
     * 
     * @type {boolean}
     * @memberof EmailProperties
     */
    startTlsEnable?: boolean;
}

/**
 * Check if a given object implements the EmailProperties interface.
 */
export function instanceOfEmailProperties(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function EmailPropertiesFromJSON(json: any): EmailProperties {
    return EmailPropertiesFromJSONTyped(json, false);
}

export function EmailPropertiesFromJSONTyped(json: any, ignoreDiscriminator: boolean): EmailProperties {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'auth': !exists(json, 'auth') ? undefined : json['auth'],
        'sslTrust': !exists(json, 'sslTrust') ? undefined : json['sslTrust'],
        'startTlsEnable': !exists(json, 'startTlsEnable') ? undefined : json['startTlsEnable'],
    };
}

export function EmailPropertiesToJSON(value?: EmailProperties | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'auth': value.auth,
        'sslTrust': value.sslTrust,
        'startTlsEnable': value.startTlsEnable,
    };
}

