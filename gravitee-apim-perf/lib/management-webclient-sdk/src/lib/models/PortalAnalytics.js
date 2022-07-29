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
export function PortalAnalyticsFromJSON(json) {
    return PortalAnalyticsFromJSONTyped(json, false);
}
export function PortalAnalyticsFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        'enabled': !exists(json, 'enabled') ? undefined : json['enabled'],
        'trackingId': !exists(json, 'trackingId') ? undefined : json['trackingId'],
    };
}
export function PortalAnalyticsToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        'enabled': value.enabled,
        'trackingId': value.trackingId,
    };
}
