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
import {
    MediaType,
    MediaTypeFromJSON,
    MediaTypeFromJSONTyped,
    MediaTypeToJSON,
} from './';

/**
 * 
 * @export
 * @interface HttpHeaders
 */
export interface HttpHeaders {
    [key: string]: Array<string> | any;
    /**
     * 
     * @type {Array<MediaType>}
     * @memberof HttpHeaders
     */
    accept?: Array<MediaType>;
    /**
     * 
     * @type {{ [key: string]: string; }}
     * @memberof HttpHeaders
     */
    all?: { [key: string]: string; };
    /**
     * 
     * @type {boolean}
     * @memberof HttpHeaders
     */
    empty?: boolean;
}

export function HttpHeadersFromJSON(json: any): HttpHeaders {
    return HttpHeadersFromJSONTyped(json, false);
}

export function HttpHeadersFromJSONTyped(json: any, ignoreDiscriminator: boolean): HttpHeaders {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
            ...json,
        'accept': !exists(json, 'accept') ? undefined : ((json['accept'] as Array<any>).map(MediaTypeFromJSON)),
        'all': !exists(json, 'all') ? undefined : json['all'],
        'empty': !exists(json, 'empty') ? undefined : json['empty'],
    };
}

export function HttpHeadersToJSON(value?: HttpHeaders | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
            ...value,
        'accept': value.accept === undefined ? undefined : ((value.accept as Array<any>).map(MediaTypeToJSON)),
        'all': value.all,
        'empty': value.empty,
    };
}


