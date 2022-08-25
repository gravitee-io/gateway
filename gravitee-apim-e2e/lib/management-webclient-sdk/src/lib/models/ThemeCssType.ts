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
export const ThemeCssType = {
    COLOR: 'color',
    LENGTH: 'length',
    STRING: 'string',
    IMAGE: 'image'
} as const;
export type ThemeCssType = typeof ThemeCssType[keyof typeof ThemeCssType];


export function ThemeCssTypeFromJSON(json: any): ThemeCssType {
    return ThemeCssTypeFromJSONTyped(json, false);
}

export function ThemeCssTypeFromJSONTyped(json: any, ignoreDiscriminator: boolean): ThemeCssType {
    return json as ThemeCssType;
}

export function ThemeCssTypeToJSON(value?: ThemeCssType | null): any {
    return value as any;
}

