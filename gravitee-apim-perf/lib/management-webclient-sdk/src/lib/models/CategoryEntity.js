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
export function CategoryEntityFromJSON(json) {
    return CategoryEntityFromJSONTyped(json, false);
}
export function CategoryEntityFromJSONTyped(json, ignoreDiscriminator) {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        'background': !exists(json, 'background') ? undefined : json['background'],
        'background_url': !exists(json, 'background_url') ? undefined : json['background_url'],
        'createdAt': !exists(json, 'createdAt') ? undefined : (new Date(json['createdAt'])),
        'description': !exists(json, 'description') ? undefined : json['description'],
        'hidden': !exists(json, 'hidden') ? undefined : json['hidden'],
        'highlightApi': !exists(json, 'highlightApi') ? undefined : json['highlightApi'],
        'id': !exists(json, 'id') ? undefined : json['id'],
        'key': !exists(json, 'key') ? undefined : json['key'],
        'name': json['name'],
        'order': !exists(json, 'order') ? undefined : json['order'],
        'page': !exists(json, 'page') ? undefined : json['page'],
        'picture': !exists(json, 'picture') ? undefined : json['picture'],
        'picture_url': !exists(json, 'picture_url') ? undefined : json['picture_url'],
        'totalApis': !exists(json, 'totalApis') ? undefined : json['totalApis'],
        'updatedAt': !exists(json, 'updatedAt') ? undefined : (new Date(json['updatedAt'])),
    };
}
export function CategoryEntityToJSON(value) {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        'background': value.background,
        'background_url': value.background_url,
        'createdAt': value.createdAt === undefined ? undefined : (value.createdAt.toISOString()),
        'description': value.description,
        'hidden': value.hidden,
        'highlightApi': value.highlightApi,
        'id': value.id,
        'key': value.key,
        'name': value.name,
        'order': value.order,
        'page': value.page,
        'picture': value.picture,
        'picture_url': value.picture_url,
        'totalApis': value.totalApis,
        'updatedAt': value.updatedAt === undefined ? undefined : (value.updatedAt.toISOString()),
    };
}
