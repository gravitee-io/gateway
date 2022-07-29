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
export function AlertEventEntityFromJSON(json) {
    return AlertEventEntityFromJSONTyped(json, false);
}
export function AlertEventEntityFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        'created_at': !exists(json, 'created_at') ? undefined : (new Date(json['created_at'])),
        'message': !exists(json, 'message') ? undefined : json['message'],
    };
}
export function AlertEventEntityToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        'created_at': value.created_at === undefined ? undefined : (value.created_at.toISOString()),
        'message': value.message,
    };
}
