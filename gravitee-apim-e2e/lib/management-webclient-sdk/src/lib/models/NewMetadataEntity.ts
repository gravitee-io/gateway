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
import type { MetadataFormat } from './MetadataFormat';
import {
    MetadataFormatFromJSON,
    MetadataFormatFromJSONTyped,
    MetadataFormatToJSON,
} from './MetadataFormat';

/**
 * 
 * @export
 * @interface NewMetadataEntity
 */
export interface NewMetadataEntity {
    /**
     * 
     * @type {MetadataFormat}
     * @memberof NewMetadataEntity
     */
    format?: MetadataFormat;
    /**
     * 
     * @type {boolean}
     * @memberof NewMetadataEntity
     */
    hidden?: boolean;
    /**
     * 
     * @type {string}
     * @memberof NewMetadataEntity
     */
    name: string;
    /**
     * 
     * @type {string}
     * @memberof NewMetadataEntity
     */
    value?: string;
}

/**
 * Check if a given object implements the NewMetadataEntity interface.
 */
export function instanceOfNewMetadataEntity(value: object): boolean {
    let isInstance = true;
    isInstance = isInstance && "name" in value;

    return isInstance;
}

export function NewMetadataEntityFromJSON(json: any): NewMetadataEntity {
    return NewMetadataEntityFromJSONTyped(json, false);
}

export function NewMetadataEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): NewMetadataEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'format': !exists(json, 'format') ? undefined : MetadataFormatFromJSON(json['format']),
        'hidden': !exists(json, 'hidden') ? undefined : json['hidden'],
        'name': json['name'],
        'value': !exists(json, 'value') ? undefined : json['value'],
    };
}

export function NewMetadataEntityToJSON(value?: NewMetadataEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'format': MetadataFormatToJSON(value.format),
        'hidden': value.hidden,
        'name': value.name,
        'value': value.value,
    };
}

