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

import { exists, mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface UpdateCategoryEntity
 */
export interface UpdateCategoryEntity {
    /**
     * 
     * @type {string}
     * @memberof UpdateCategoryEntity
     */
    background?: string;
    /**
     * 
     * @type {boolean}
     * @memberof UpdateCategoryEntity
     */
    defaultCategory?: boolean;
    /**
     * 
     * @type {string}
     * @memberof UpdateCategoryEntity
     */
    description?: string;
    /**
     * 
     * @type {boolean}
     * @memberof UpdateCategoryEntity
     */
    hidden?: boolean;
    /**
     * 
     * @type {string}
     * @memberof UpdateCategoryEntity
     */
    highlightApi?: string;
    /**
     * 
     * @type {string}
     * @memberof UpdateCategoryEntity
     */
    id?: string;
    /**
     * 
     * @type {string}
     * @memberof UpdateCategoryEntity
     */
    name: string;
    /**
     * 
     * @type {number}
     * @memberof UpdateCategoryEntity
     */
    order?: number;
    /**
     * 
     * @type {string}
     * @memberof UpdateCategoryEntity
     */
    page?: string;
    /**
     * 
     * @type {string}
     * @memberof UpdateCategoryEntity
     */
    picture?: string;
    /**
     * 
     * @type {string}
     * @memberof UpdateCategoryEntity
     */
    picture_url?: string;
}

/**
 * Check if a given object implements the UpdateCategoryEntity interface.
 */
export function instanceOfUpdateCategoryEntity(value: object): boolean {
    let isInstance = true;
    isInstance = isInstance && "name" in value;

    return isInstance;
}

export function UpdateCategoryEntityFromJSON(json: any): UpdateCategoryEntity {
    return UpdateCategoryEntityFromJSONTyped(json, false);
}

export function UpdateCategoryEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): UpdateCategoryEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'background': !exists(json, 'background') ? undefined : json['background'],
        'defaultCategory': !exists(json, 'defaultCategory') ? undefined : json['defaultCategory'],
        'description': !exists(json, 'description') ? undefined : json['description'],
        'hidden': !exists(json, 'hidden') ? undefined : json['hidden'],
        'highlightApi': !exists(json, 'highlightApi') ? undefined : json['highlightApi'],
        'id': !exists(json, 'id') ? undefined : json['id'],
        'name': json['name'],
        'order': !exists(json, 'order') ? undefined : json['order'],
        'page': !exists(json, 'page') ? undefined : json['page'],
        'picture': !exists(json, 'picture') ? undefined : json['picture'],
        'picture_url': !exists(json, 'picture_url') ? undefined : json['picture_url'],
    };
}

export function UpdateCategoryEntityToJSON(value?: UpdateCategoryEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'background': value.background,
        'defaultCategory': value.defaultCategory,
        'description': value.description,
        'hidden': value.hidden,
        'highlightApi': value.highlightApi,
        'id': value.id,
        'name': value.name,
        'order': value.order,
        'page': value.page,
        'picture': value.picture,
        'picture_url': value.picture_url,
    };
}

