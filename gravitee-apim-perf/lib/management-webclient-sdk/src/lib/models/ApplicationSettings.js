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
import { OAuthClientSettingsFromJSON, OAuthClientSettingsToJSON, SimpleApplicationSettingsFromJSON, SimpleApplicationSettingsToJSON, } from './';
export function ApplicationSettingsFromJSON(json) {
    return ApplicationSettingsFromJSONTyped(json, false);
}
export function ApplicationSettingsFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        'app': !exists(json, 'app') ? undefined : SimpleApplicationSettingsFromJSON(json['app']),
        'oauth': !exists(json, 'oauth') ? undefined : OAuthClientSettingsFromJSON(json['oauth']),
    };
}
export function ApplicationSettingsToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        'app': SimpleApplicationSettingsToJSON(value.app),
        'oauth': OAuthClientSettingsToJSON(value.oauth),
    };
}
