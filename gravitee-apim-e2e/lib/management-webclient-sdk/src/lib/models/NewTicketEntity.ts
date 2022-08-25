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
 * @interface NewTicketEntity
 */
export interface NewTicketEntity {
    /**
     * 
     * @type {string}
     * @memberof NewTicketEntity
     */
    api?: string;
    /**
     * 
     * @type {string}
     * @memberof NewTicketEntity
     */
    application?: string;
    /**
     * 
     * @type {string}
     * @memberof NewTicketEntity
     */
    content: string;
    /**
     * 
     * @type {boolean}
     * @memberof NewTicketEntity
     */
    copyToSender?: boolean;
    /**
     * 
     * @type {string}
     * @memberof NewTicketEntity
     */
    subject: string;
}

/**
 * Check if a given object implements the NewTicketEntity interface.
 */
export function instanceOfNewTicketEntity(value: object): boolean {
    let isInstance = true;
    isInstance = isInstance && "content" in value;
    isInstance = isInstance && "subject" in value;

    return isInstance;
}

export function NewTicketEntityFromJSON(json: any): NewTicketEntity {
    return NewTicketEntityFromJSONTyped(json, false);
}

export function NewTicketEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): NewTicketEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'api': !exists(json, 'api') ? undefined : json['api'],
        'application': !exists(json, 'application') ? undefined : json['application'],
        'content': json['content'],
        'copyToSender': !exists(json, 'copyToSender') ? undefined : json['copyToSender'],
        'subject': json['subject'],
    };
}

export function NewTicketEntityToJSON(value?: NewTicketEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'api': value.api,
        'application': value.application,
        'content': value.content,
        'copyToSender': value.copyToSender,
        'subject': value.subject,
    };
}

