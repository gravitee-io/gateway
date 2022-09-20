/* tslint:disable */
/* eslint-disable */
/**
 * Gravitee.io - Management API
 * Some news resources are in alpha version. This implies that they are likely to be modified or even removed in future versions. They are marked with the 🧪 symbol
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { exists, mapValues } from '../runtime';
import type { ApiLifecycleState } from './ApiLifecycleState';
import {
    ApiLifecycleStateFromJSON,
    ApiLifecycleStateFromJSONTyped,
    ApiLifecycleStateToJSON,
} from './ApiLifecycleState';
import type { DefinitionContext } from './DefinitionContext';
import {
    DefinitionContextFromJSON,
    DefinitionContextFromJSONTyped,
    DefinitionContextToJSON,
} from './DefinitionContext';
import type { PrimaryOwnerEntity } from './PrimaryOwnerEntity';
import {
    PrimaryOwnerEntityFromJSON,
    PrimaryOwnerEntityFromJSONTyped,
    PrimaryOwnerEntityToJSON,
} from './PrimaryOwnerEntity';
import type { VirtualHost } from './VirtualHost';
import {
    VirtualHostFromJSON,
    VirtualHostFromJSONTyped,
    VirtualHostToJSON,
} from './VirtualHost';
import type { Visibility } from './Visibility';
import {
    VisibilityFromJSON,
    VisibilityFromJSONTyped,
    VisibilityToJSON,
} from './Visibility';
import type { WorkflowState } from './WorkflowState';
import {
    WorkflowStateFromJSON,
    WorkflowStateFromJSONTyped,
    WorkflowStateToJSON,
} from './WorkflowState';

/**
 * 
 * @export
 * @interface ApiListItem
 */
export interface ApiListItem {
    /**
     * the list of categories associated with this API
     * @type {Array<string>}
     * @memberof ApiListItem
     */
    categories?: Array<string>;
    /**
     * API's context path.
     * @type {string}
     * @memberof ApiListItem
     */
    context_path?: string;
    /**
     * The date (as a timestamp) when the API was created.
     * @type {Date}
     * @memberof ApiListItem
     */
    created_at?: Date;
    /**
     * 
     * @type {DefinitionContext}
     * @memberof ApiListItem
     */
    definition_context?: DefinitionContext;
    /**
     * Api's version. It's a simple string only used in the portal.
     * @type {string}
     * @memberof ApiListItem
     */
    description?: string;
    /**
     * true if HealthCheck is enabled globally or on one endpoint
     * @type {boolean}
     * @memberof ApiListItem
     */
    healthcheck_enabled?: boolean;
    /**
     * Api's uuid.
     * @type {string}
     * @memberof ApiListItem
     */
    id?: string;
    /**
     * the free list of labels associated with this API
     * @type {Array<string>}
     * @memberof ApiListItem
     */
    labels?: Array<string>;
    /**
     * 
     * @type {ApiLifecycleState}
     * @memberof ApiListItem
     */
    lifecycle_state?: ApiLifecycleState;
    /**
     * Api's name. Duplicate names can exists.
     * @type {string}
     * @memberof ApiListItem
     */
    name?: string;
    /**
     * How many consumers have evaluated the API
     * @type {number}
     * @memberof ApiListItem
     */
    numberOfRatings?: number;
    /**
     * 
     * @type {PrimaryOwnerEntity}
     * @memberof ApiListItem
     */
    owner?: PrimaryOwnerEntity;
    /**
     * the API logo url.
     * @type {string}
     * @memberof ApiListItem
     */
    picture_url?: string;
    /**
     * How consumers have evaluated the API (between 0 to 5)
     * @type {number}
     * @memberof ApiListItem
     */
    rate?: number;
    /**
     * 
     * @type {string}
     * @memberof ApiListItem
     */
    role?: string;
    /**
     * The status of the API regarding the gateway.
     * @type {string}
     * @memberof ApiListItem
     */
    state?: ApiListItemStateEnum;
    /**
     * the list of sharding tags associated with this API.
     * @type {Array<string>}
     * @memberof ApiListItem
     */
    tags?: Array<string>;
    /**
     * The last date (as a timestamp) when the API was updated.
     * @type {Date}
     * @memberof ApiListItem
     */
    updated_at?: Date;
    /**
     * Api's version. It's a simple string only used in the portal.
     * @type {string}
     * @memberof ApiListItem
     */
    version?: string;
    /**
     * 
     * @type {Array<VirtualHost>}
     * @memberof ApiListItem
     */
    virtual_hosts?: Array<VirtualHost>;
    /**
     * 
     * @type {Visibility}
     * @memberof ApiListItem
     */
    visibility?: Visibility;
    /**
     * 
     * @type {WorkflowState}
     * @memberof ApiListItem
     */
    workflow_state?: WorkflowState;
}


/**
 * @export
 */
export const ApiListItemStateEnum = {
    INITIALIZED: 'INITIALIZED',
    STOPPED: 'STOPPED',
    STOPPING: 'STOPPING',
    STARTED: 'STARTED',
    CLOSED: 'CLOSED'
} as const;
export type ApiListItemStateEnum = typeof ApiListItemStateEnum[keyof typeof ApiListItemStateEnum];


/**
 * Check if a given object implements the ApiListItem interface.
 */
export function instanceOfApiListItem(value: object): boolean {
    let isInstance = true;

    return isInstance;
}

export function ApiListItemFromJSON(json: any): ApiListItem {
    return ApiListItemFromJSONTyped(json, false);
}

export function ApiListItemFromJSONTyped(json: any, ignoreDiscriminator: boolean): ApiListItem {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'categories': !exists(json, 'categories') ? undefined : json['categories'],
        'context_path': !exists(json, 'context_path') ? undefined : json['context_path'],
        'created_at': !exists(json, 'created_at') ? undefined : (new Date(json['created_at'])),
        'definition_context': !exists(json, 'definition_context') ? undefined : DefinitionContextFromJSON(json['definition_context']),
        'description': !exists(json, 'description') ? undefined : json['description'],
        'healthcheck_enabled': !exists(json, 'healthcheck_enabled') ? undefined : json['healthcheck_enabled'],
        'id': !exists(json, 'id') ? undefined : json['id'],
        'labels': !exists(json, 'labels') ? undefined : json['labels'],
        'lifecycle_state': !exists(json, 'lifecycle_state') ? undefined : ApiLifecycleStateFromJSON(json['lifecycle_state']),
        'name': !exists(json, 'name') ? undefined : json['name'],
        'numberOfRatings': !exists(json, 'numberOfRatings') ? undefined : json['numberOfRatings'],
        'owner': !exists(json, 'owner') ? undefined : PrimaryOwnerEntityFromJSON(json['owner']),
        'picture_url': !exists(json, 'picture_url') ? undefined : json['picture_url'],
        'rate': !exists(json, 'rate') ? undefined : json['rate'],
        'role': !exists(json, 'role') ? undefined : json['role'],
        'state': !exists(json, 'state') ? undefined : json['state'],
        'tags': !exists(json, 'tags') ? undefined : json['tags'],
        'updated_at': !exists(json, 'updated_at') ? undefined : (new Date(json['updated_at'])),
        'version': !exists(json, 'version') ? undefined : json['version'],
        'virtual_hosts': !exists(json, 'virtual_hosts') ? undefined : ((json['virtual_hosts'] as Array<any>).map(VirtualHostFromJSON)),
        'visibility': !exists(json, 'visibility') ? undefined : VisibilityFromJSON(json['visibility']),
        'workflow_state': !exists(json, 'workflow_state') ? undefined : WorkflowStateFromJSON(json['workflow_state']),
    };
}

export function ApiListItemToJSON(value?: ApiListItem | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'categories': value.categories,
        'context_path': value.context_path,
        'created_at': value.created_at === undefined ? undefined : (value.created_at.toISOString()),
        'definition_context': DefinitionContextToJSON(value.definition_context),
        'description': value.description,
        'healthcheck_enabled': value.healthcheck_enabled,
        'id': value.id,
        'labels': value.labels,
        'lifecycle_state': ApiLifecycleStateToJSON(value.lifecycle_state),
        'name': value.name,
        'numberOfRatings': value.numberOfRatings,
        'owner': PrimaryOwnerEntityToJSON(value.owner),
        'picture_url': value.picture_url,
        'rate': value.rate,
        'role': value.role,
        'state': value.state,
        'tags': value.tags,
        'updated_at': value.updated_at === undefined ? undefined : (value.updated_at.toISOString()),
        'version': value.version,
        'virtual_hosts': value.virtual_hosts === undefined ? undefined : ((value.virtual_hosts as Array<any>).map(VirtualHostToJSON)),
        'visibility': VisibilityToJSON(value.visibility),
        'workflow_state': WorkflowStateToJSON(value.workflow_state),
    };
}

