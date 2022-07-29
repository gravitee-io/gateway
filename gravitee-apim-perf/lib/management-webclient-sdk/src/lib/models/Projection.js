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
import { PropertyProjectionFromJSONTyped } from './';
export function ProjectionFromJSON(json) {
    return ProjectionFromJSONTyped(json, false);
}
export function ProjectionFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    if (!ignoreDiscriminator) {
        if (json['type'] === 'PropertyProjection') {
            return PropertyProjectionFromJSONTyped(json, true);
        }
    }
    return {
        'type': !exists(json, 'type') ? undefined : json['type'],
    };
}
export function ProjectionToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        'type': value.type,
    };
}
/**
* @export
* @enum {string}
*/
export var ProjectionTypeEnum;
(function (ProjectionTypeEnum) {
    ProjectionTypeEnum["PROPERTY"] = "PROPERTY";
})(ProjectionTypeEnum || (ProjectionTypeEnum = {}));
