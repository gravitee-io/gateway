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
import { exists } from '../runtime';
import { ThemeComponentDefinitionFromJSON, ThemeComponentDefinitionToJSON, } from './';
export function ThemeDefinitionFromJSON(json) {
    return ThemeDefinitionFromJSONTyped(json, false);
}
export function ThemeDefinitionFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        'data': !exists(json, 'data') ? undefined : (json['data'].map(ThemeComponentDefinitionFromJSON)),
    };
}
export function ThemeDefinitionToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        'data': value.data === undefined ? undefined : (value.data.map(ThemeComponentDefinitionToJSON)),
    };
}
