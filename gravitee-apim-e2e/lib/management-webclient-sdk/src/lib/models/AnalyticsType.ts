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
export const AnalyticsType = {
    GROUP_BY: 'GROUP_BY',
    DATE_HISTO: 'DATE_HISTO',
    COUNT: 'COUNT',
    STATS: 'STATS'
} as const;
export type AnalyticsType = typeof AnalyticsType[keyof typeof AnalyticsType];


export function AnalyticsTypeFromJSON(json: any): AnalyticsType {
    return AnalyticsTypeFromJSONTyped(json, false);
}

export function AnalyticsTypeFromJSONTyped(json: any, ignoreDiscriminator: boolean): AnalyticsType {
    return json as AnalyticsType;
}

export function AnalyticsTypeToJSON(value?: AnalyticsType | null): any {
    return value as any;
}

