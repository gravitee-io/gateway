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
export var WorkflowState;
(function (WorkflowState) {
    WorkflowState["DRAFT"] = "DRAFT";
    WorkflowState["INREVIEW"] = "IN_REVIEW";
    WorkflowState["REQUESTFORCHANGES"] = "REQUEST_FOR_CHANGES";
    WorkflowState["REVIEWOK"] = "REVIEW_OK";
})(WorkflowState || (WorkflowState = {}));
export function WorkflowStateFromJSON(json) {
    return WorkflowStateFromJSONTyped(json, false);
}
export function WorkflowStateFromJSONTyped(json, ignoreDiscriminator) {
    return json;
}
export function WorkflowStateToJSON(value) {
    return value;
}
