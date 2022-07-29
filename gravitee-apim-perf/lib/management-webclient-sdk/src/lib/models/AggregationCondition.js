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
import { ConditionFromJSONTyped, ConditionToJSON, } from './';
export function AggregationConditionFromJSON(json) {
    return AggregationConditionFromJSONTyped(json, false);
}
export function AggregationConditionFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        ...ConditionFromJSONTyped(json, ignoreDiscriminator),
        '_function': json['function'],
        'property': !exists(json, 'property') ? undefined : json['property'],
        'operator': json['operator'],
        'threshold': json['threshold'],
        'timeUnit': !exists(json, 'timeUnit') ? undefined : json['timeUnit'],
        'duration': json['duration'],
    };
}
export function AggregationConditionToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        ...ConditionToJSON(value),
        'function': value._function,
        'property': value.property,
        'operator': value.operator,
        'threshold': value.threshold,
        'timeUnit': value.timeUnit,
        'duration': value.duration,
    };
}
/**
* @export
* @enum {string}
*/
export var AggregationConditionFunctionEnum;
(function (AggregationConditionFunctionEnum) {
    AggregationConditionFunctionEnum["COUNT"] = "COUNT";
    AggregationConditionFunctionEnum["AVG"] = "AVG";
    AggregationConditionFunctionEnum["MIN"] = "MIN";
    AggregationConditionFunctionEnum["MAX"] = "MAX";
    AggregationConditionFunctionEnum["P50"] = "P50";
    AggregationConditionFunctionEnum["P90"] = "P90";
    AggregationConditionFunctionEnum["P95"] = "P95";
    AggregationConditionFunctionEnum["P99"] = "P99";
})(AggregationConditionFunctionEnum || (AggregationConditionFunctionEnum = {}));
/**
* @export
* @enum {string}
*/
export var AggregationConditionOperatorEnum;
(function (AggregationConditionOperatorEnum) {
    AggregationConditionOperatorEnum["LT"] = "LT";
    AggregationConditionOperatorEnum["LTE"] = "LTE";
    AggregationConditionOperatorEnum["GTE"] = "GTE";
    AggregationConditionOperatorEnum["GT"] = "GT";
})(AggregationConditionOperatorEnum || (AggregationConditionOperatorEnum = {}));
/**
* @export
* @enum {string}
*/
export var AggregationConditionTimeUnitEnum;
(function (AggregationConditionTimeUnitEnum) {
    AggregationConditionTimeUnitEnum["NANOSECONDS"] = "NANOSECONDS";
    AggregationConditionTimeUnitEnum["MICROSECONDS"] = "MICROSECONDS";
    AggregationConditionTimeUnitEnum["MILLISECONDS"] = "MILLISECONDS";
    AggregationConditionTimeUnitEnum["SECONDS"] = "SECONDS";
    AggregationConditionTimeUnitEnum["MINUTES"] = "MINUTES";
    AggregationConditionTimeUnitEnum["HOURS"] = "HOURS";
    AggregationConditionTimeUnitEnum["DAYS"] = "DAYS";
})(AggregationConditionTimeUnitEnum || (AggregationConditionTimeUnitEnum = {}));
