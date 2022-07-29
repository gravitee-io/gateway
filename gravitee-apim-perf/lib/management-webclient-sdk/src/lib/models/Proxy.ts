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
import {
    Cors,
    CorsFromJSON,
    CorsFromJSONTyped,
    CorsToJSON,
    EndpointGroup,
    EndpointGroupFromJSON,
    EndpointGroupFromJSONTyped,
    EndpointGroupToJSON,
    Failover,
    FailoverFromJSON,
    FailoverFromJSONTyped,
    FailoverToJSON,
    Logging,
    LoggingFromJSON,
    LoggingFromJSONTyped,
    LoggingToJSON,
    VirtualHost,
    VirtualHostFromJSON,
    VirtualHostFromJSONTyped,
    VirtualHostToJSON,
} from './';

/**
 * API's definition.
 * @export
 * @interface Proxy
 */
export interface Proxy {
    /**
     * 
     * @type {Cors}
     * @memberof Proxy
     */
    cors?: Cors;
    /**
     * 
     * @type {Failover}
     * @memberof Proxy
     */
    failover?: Failover;
    /**
     * 
     * @type {Array<EndpointGroup>}
     * @memberof Proxy
     */
    groups?: Array<EndpointGroup>;
    /**
     * 
     * @type {Logging}
     * @memberof Proxy
     */
    logging?: Logging;
    /**
     * 
     * @type {boolean}
     * @memberof Proxy
     */
    preserve_host?: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof Proxy
     */
    strip_context_path?: boolean;
    /**
     * 
     * @type {Array<VirtualHost>}
     * @memberof Proxy
     */
    virtual_hosts?: Array<VirtualHost>;
}

export function ProxyFromJSON(json: any): Proxy {
    return ProxyFromJSONTyped(json, false);
}

export function ProxyFromJSONTyped(json: any, ignoreDiscriminator: boolean): Proxy {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'cors': !exists(json, 'cors') ? undefined : CorsFromJSON(json['cors']),
        'failover': !exists(json, 'failover') ? undefined : FailoverFromJSON(json['failover']),
        'groups': !exists(json, 'groups') ? undefined : ((json['groups'] as Array<any>).map(EndpointGroupFromJSON)),
        'logging': !exists(json, 'logging') ? undefined : LoggingFromJSON(json['logging']),
        'preserve_host': !exists(json, 'preserve_host') ? undefined : json['preserve_host'],
        'strip_context_path': !exists(json, 'strip_context_path') ? undefined : json['strip_context_path'],
        'virtual_hosts': !exists(json, 'virtual_hosts') ? undefined : ((json['virtual_hosts'] as Array<any>).map(VirtualHostFromJSON)),
    };
}

export function ProxyToJSON(value?: Proxy | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'cors': CorsToJSON(value.cors),
        'failover': FailoverToJSON(value.failover),
        'groups': value.groups === undefined ? undefined : ((value.groups as Array<any>).map(EndpointGroupToJSON)),
        'logging': LoggingToJSON(value.logging),
        'preserve_host': value.preserve_host,
        'strip_context_path': value.strip_context_path,
        'virtual_hosts': value.virtual_hosts === undefined ? undefined : ((value.virtual_hosts as Array<any>).map(VirtualHostToJSON)),
    };
}


