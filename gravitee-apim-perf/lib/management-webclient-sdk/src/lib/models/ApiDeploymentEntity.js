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
export function ApiDeploymentEntityFromJSON(json) {
    return ApiDeploymentEntityFromJSONTyped(json, false);
}
export function ApiDeploymentEntityFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        'deploymentLabel': !exists(json, 'deploymentLabel') ? undefined : json['deploymentLabel'],
    };
}
export function ApiDeploymentEntityToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        'deploymentLabel': value.deploymentLabel,
    };
}
