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
 */
export const InitialAccessTokenType = {
    INITIAL_ACCESS_TOKEN: 'INITIAL_ACCESS_TOKEN',
    CLIENT_CREDENTIALS: 'CLIENT_CREDENTIALS'
} as const;
export type InitialAccessTokenType = typeof InitialAccessTokenType[keyof typeof InitialAccessTokenType];


export function InitialAccessTokenTypeFromJSON(json: any): InitialAccessTokenType {
    return InitialAccessTokenTypeFromJSONTyped(json, false);
}

export function InitialAccessTokenTypeFromJSONTyped(json: any, ignoreDiscriminator: boolean): InitialAccessTokenType {
    return json as InitialAccessTokenType;
}

export function InitialAccessTokenTypeToJSON(value?: InitialAccessTokenType | null): any {
    return value as any;
}

