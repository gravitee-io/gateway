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
export function ThresholdConditionAllOfFromJSON(json) {
    return ThresholdConditionAllOfFromJSONTyped(json, false);
}
export function ThresholdConditionAllOfFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        'property': !exists(json, 'property') ? undefined : json['property'],
        'operator': !exists(json, 'operator') ? undefined : json['operator'],
        'threshold': !exists(json, 'threshold') ? undefined : json['threshold'],
    };
}
export function ThresholdConditionAllOfToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        'property': value.property,
        'operator': value.operator,
        'threshold': value.threshold,
    };
}
/**
* @export
* @enum {string}
*/
export var ThresholdConditionAllOfOperatorEnum;
(function (ThresholdConditionAllOfOperatorEnum) {
    ThresholdConditionAllOfOperatorEnum["LT"] = "LT";
    ThresholdConditionAllOfOperatorEnum["LTE"] = "LTE";
    ThresholdConditionAllOfOperatorEnum["GTE"] = "GTE";
    ThresholdConditionAllOfOperatorEnum["GT"] = "GT";
})(ThresholdConditionAllOfOperatorEnum || (ThresholdConditionAllOfOperatorEnum = {}));
