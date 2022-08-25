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
 * @interface ApiEntrypointEntity
 */
export interface ApiEntrypointEntity {
    /**
     * 
     * @type {string}
     * @memberof ApiEntrypointEntity
     */
    host?: string;
    /**
     * 
     * @type {Array<string>}
     * @memberof ApiEntrypointEntity
     */
    tags?: Array<string>;
    /**
     * 
     * @type {string}
     * @memberof ApiEntrypointEntity
     */
    target?: string;
}

/**
 * Check if a given object implements the ApiEntrypointEntity interface.
 */
export function instanceOfApiEntrypointEntity(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function ApiEntrypointEntityFromJSON(json: any): ApiEntrypointEntity {
    return ApiEntrypointEntityFromJSONTyped(json, false);
}

export function ApiEntrypointEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): ApiEntrypointEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'host': !exists(json, 'host') ? undefined : json['host'],
        'tags': !exists(json, 'tags') ? undefined : json['tags'],
        'target': !exists(json, 'target') ? undefined : json['target'],
    };
}

export function ApiEntrypointEntityToJSON(value?: ApiEntrypointEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'host': value.host,
        'tags': value.tags,
        'target': value.target,
    };
}

