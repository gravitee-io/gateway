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
export var InitialAccessTokenType;
(function (InitialAccessTokenType) {
    InitialAccessTokenType["INITIALACCESSTOKEN"] = "INITIAL_ACCESS_TOKEN";
    InitialAccessTokenType["CLIENTCREDENTIALS"] = "CLIENT_CREDENTIALS";
})(InitialAccessTokenType || (InitialAccessTokenType = {}));
export function InitialAccessTokenTypeFromJSON(json) {
    return InitialAccessTokenTypeFromJSONTyped(json, false);
}
export function InitialAccessTokenTypeFromJSONTyped(json, ignoreDiscriminator) {
    return json;
}
export function InitialAccessTokenTypeToJSON(value) {
    return value;
}
