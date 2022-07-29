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
import { ThemeDefinitionFromJSON, ThemeDefinitionToJSON, } from './';
export function UpdateThemeEntityFromJSON(json) {
    return UpdateThemeEntityFromJSONTyped(json, false);
}
export function UpdateThemeEntityFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        'backgroundImage': !exists(json, 'backgroundImage') ? undefined : json['backgroundImage'],
        'definition': ThemeDefinitionFromJSON(json['definition']),
        'enabled': !exists(json, 'enabled') ? undefined : json['enabled'],
        'favicon': !exists(json, 'favicon') ? undefined : json['favicon'],
        'id': !exists(json, 'id') ? undefined : json['id'],
        'logo': !exists(json, 'logo') ? undefined : json['logo'],
        'name': json['name'],
        'optionalLogo': !exists(json, 'optionalLogo') ? undefined : json['optionalLogo'],
    };
}
export function UpdateThemeEntityToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        'backgroundImage': value.backgroundImage,
        'definition': ThemeDefinitionToJSON(value.definition),
        'enabled': value.enabled,
        'favicon': value.favicon,
        'id': value.id,
        'logo': value.logo,
        'name': value.name,
        'optionalLogo': value.optionalLogo,
    };
}
