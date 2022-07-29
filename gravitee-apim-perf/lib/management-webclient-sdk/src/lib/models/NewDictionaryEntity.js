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
import { DictionaryProviderEntityFromJSON, DictionaryProviderEntityToJSON, DictionaryTriggerEntityFromJSON, DictionaryTriggerEntityToJSON, DictionaryTypeFromJSON, DictionaryTypeToJSON, } from './';
export function NewDictionaryEntityFromJSON(json) {
    return NewDictionaryEntityFromJSONTyped(json, false);
}
export function NewDictionaryEntityFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        'description': !exists(json, 'description') ? undefined : json['description'],
        'name': json['name'],
        'properties': !exists(json, 'properties') ? undefined : json['properties'],
        'provider': !exists(json, 'provider') ? undefined : DictionaryProviderEntityFromJSON(json['provider']),
        'trigger': !exists(json, 'trigger') ? undefined : DictionaryTriggerEntityFromJSON(json['trigger']),
        'type': DictionaryTypeFromJSON(json['type']),
    };
}
export function NewDictionaryEntityToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        'description': value.description,
        'name': value.name,
        'properties': value.properties,
        'provider': DictionaryProviderEntityToJSON(value.provider),
        'trigger': DictionaryTriggerEntityToJSON(value.trigger),
        'type': DictionaryTypeToJSON(value.type),
    };
}
