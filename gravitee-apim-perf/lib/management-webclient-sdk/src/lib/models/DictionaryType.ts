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

/**
 * 
 * @export
 * @enum {string}
 */
export enum DictionaryType {
    MANUAL = 'MANUAL',
    DYNAMIC = 'DYNAMIC'
}

export function DictionaryTypeFromJSON(json: any): DictionaryType {
    return DictionaryTypeFromJSONTyped(json, false);
}

export function DictionaryTypeFromJSONTyped(json: any, ignoreDiscriminator: boolean): DictionaryType {
    return json as DictionaryType;
}

export function DictionaryTypeToJSON(value?: DictionaryType | null): any {
    return value as any;
}

