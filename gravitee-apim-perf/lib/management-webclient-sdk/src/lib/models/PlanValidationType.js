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
export var PlanValidationType;
(function (PlanValidationType) {
    PlanValidationType["AUTO"] = "AUTO";
    PlanValidationType["MANUAL"] = "MANUAL";
})(PlanValidationType || (PlanValidationType = {}));
export function PlanValidationTypeFromJSON(json) {
    return PlanValidationTypeFromJSONTyped(json, false);
}
export function PlanValidationTypeFromJSONTyped(json, ignoreDiscriminator) {
    return json;
}
export function PlanValidationTypeToJSON(value) {
    return value;
}
