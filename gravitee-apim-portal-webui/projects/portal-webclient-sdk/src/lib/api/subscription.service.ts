/**
 * Gravitee.io Portal Rest API
 * API dedicated to the devportal part of Gravitee
 *
 * Contact: contact@graviteesource.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent, HttpParameterCodec }       from '@angular/common/http';
import { CustomHttpParameterCodec }                          from '../encoder';
import { Observable }                                        from 'rxjs';

import { ErrorResponse } from '../model/models';
import { Key } from '../model/models';
import { Subscription } from '../model/models';
import { SubscriptionConfigurationInput } from '../model/models';
import { SubscriptionInput } from '../model/models';
import { SubscriptionsResponse } from '../model/models';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


export interface CloseSubscriptionRequestParams {
    /** Id of a subscription. */
    subscriptionId: string;
}

export interface CreateSubscriptionRequestParams {
    /** Use to create a subscription. */
    subscriptionInput?: SubscriptionInput;
}

export interface GetSubscriptionByIdRequestParams {
    /** Id of a subscription. */
    subscriptionId: string;
    /** Comma-separated list of related objects to include in the response. */
    include?: Array<'keys'>;
}

export interface GetSubscriptionsRequestParams {
    /** Id of an api. */
    apiId?: string;
    /** Id of an application. */
    applicationId?: string;
    /** status of subscription. */
    statuses?: Array<'ACCEPTED' | 'CLOSED' | 'PAUSED' | 'PENDING' | 'REJECTED'>;
    /** The page number for pagination. */
    page?: number;
    /** The number of items per page for pagination. If the size is 0, the response contains only metadata and returns the values as for a non-paged resource. If the size is -1, the response contains all datas.  */
    size?: number;
}

export interface RenewKeySubscriptionRequestParams {
    /** Id of a subscription. */
    subscriptionId: string;
    /** Use to renew keys of a subscription. */
    requestBody?: Array<string>;
}

export interface RevokeKeySubscriptionRequestParams {
    /** Id of a subscription. */
    subscriptionId: string;
    apiKey: string;
}

export interface UpdateSubscriptionRequestParams {
    /** Id of a subscription. */
    subscriptionId: string;
    /** Configuration of the subscription to update */
    subscriptionConfigurationInput?: SubscriptionConfigurationInput;
}

export interface UpdateSubscriptionConsumerStatusRequestParams {
    /** Id of a subscription. */
    subscriptionId: string;
    /** consumer status of subscription. */
    status: 'STARTED' | 'STOPPED';
}


@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {

    protected basePath = 'http://localhost:8083/portal/environments/DEFAULT';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();
    public encoder: HttpParameterCodec;

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
        if (configuration) {
            this.configuration = configuration;
        }
        if (typeof this.configuration.basePath !== 'string') {
            if (typeof basePath !== 'string') {
                basePath = this.basePath;
            }
            this.configuration.basePath = basePath;
        }
        this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
    }



    private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
        if (typeof value === "object" && value instanceof Date === false) {
            httpParams = this.addToHttpParamsRecursive(httpParams, value);
        } else {
            httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
        }
        return httpParams;
    }

    private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
        if (value == null) {
            return httpParams;
        }

        if (typeof value === "object") {
            if (Array.isArray(value)) {
                (value as any[]).forEach( elem => httpParams = this.addToHttpParamsRecursive(httpParams, elem, key));
            } else if (value instanceof Date) {
                if (key != null) {
                    httpParams = httpParams.append(key,
                        (value as Date).toISOString().substr(0, 10));
                } else {
                   throw Error("key may not be null if value is Date");
                }
            } else {
                Object.keys(value).forEach( k => httpParams = this.addToHttpParamsRecursive(
                    httpParams, value[k], key != null ? `${key}.${k}` : k));
            }
        } else if (key != null) {
            httpParams = httpParams.append(key, value);
        } else {
            throw Error("key may not be null if value is not object or array");
        }
        return httpParams;
    }

    /**
     * Close a subscription
     * Close a subscription.  User must have APPLICATION_SUBSCRIPTION[DELETE] permission. 
     * @param requestParameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public closeSubscription(requestParameters: CloseSubscriptionRequestParams, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<any>;
    public closeSubscription(requestParameters: CloseSubscriptionRequestParams, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<any>>;
    public closeSubscription(requestParameters: CloseSubscriptionRequestParams, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<any>>;
    public closeSubscription(requestParameters: CloseSubscriptionRequestParams, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        const subscriptionId = requestParameters.subscriptionId;
        if (subscriptionId === null || subscriptionId === undefined) {
            throw new Error('Required parameter subscriptionId was null or undefined when calling closeSubscription.');
        }

        let headers = this.defaultHeaders;

        // authentication (BasicAuth) required
        if (this.configuration.username || this.configuration.password) {
            headers = headers.set('Authorization', 'Basic ' + btoa(this.configuration.username + ':' + this.configuration.password));
        }
        // authentication (CookieAuth) required
        if (this.configuration.apiKeys) {
            const key: string | undefined = this.configuration.apiKeys["CookieAuth"] || this.configuration.apiKeys["Auth-Graviteeio-APIM"];
            if (key) {
            }
        }

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.post<any>(`${this.configuration.basePath}/subscriptions/${encodeURIComponent(String(subscriptionId))}/_close`,
            null,
            {
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Create a subscription.
     * Create a new subscription.  User must have APPLICATION_SUBSCRIPTION[CREATE] permission. 
     * @param requestParameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public createSubscription(requestParameters: CreateSubscriptionRequestParams, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<Subscription>;
    public createSubscription(requestParameters: CreateSubscriptionRequestParams, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<Subscription>>;
    public createSubscription(requestParameters: CreateSubscriptionRequestParams, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<Subscription>>;
    public createSubscription(requestParameters: CreateSubscriptionRequestParams, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        const subscriptionInput = requestParameters.subscriptionInput;

        let headers = this.defaultHeaders;

        // authentication (BasicAuth) required
        if (this.configuration.username || this.configuration.password) {
            headers = headers.set('Authorization', 'Basic ' + btoa(this.configuration.username + ':' + this.configuration.password));
        }
        // authentication (CookieAuth) required
        if (this.configuration.apiKeys) {
            const key: string | undefined = this.configuration.apiKeys["CookieAuth"] || this.configuration.apiKeys["Auth-Graviteeio-APIM"];
            if (key) {
            }
        }

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected !== undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.post<Subscription>(`${this.configuration.basePath}/subscriptions`,
            subscriptionInput,
            {
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Get a subscription.
     * Get a subscription.  User must have API_SUBSCRIPTION[CREATE] or APPLICATION_SUBSCRIPTION[CREATE] permission. 
     * @param requestParameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getSubscriptionById(requestParameters: GetSubscriptionByIdRequestParams, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<Subscription>;
    public getSubscriptionById(requestParameters: GetSubscriptionByIdRequestParams, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<Subscription>>;
    public getSubscriptionById(requestParameters: GetSubscriptionByIdRequestParams, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<Subscription>>;
    public getSubscriptionById(requestParameters: GetSubscriptionByIdRequestParams, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        const subscriptionId = requestParameters.subscriptionId;
        if (subscriptionId === null || subscriptionId === undefined) {
            throw new Error('Required parameter subscriptionId was null or undefined when calling getSubscriptionById.');
        }
        const include = requestParameters.include;

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (include) {
            include.forEach((element) => {
                queryParameters = this.addToHttpParams(queryParameters,
                  <any>element, 'include');
            })
        }

        let headers = this.defaultHeaders;

        // authentication (BasicAuth) required
        if (this.configuration.username || this.configuration.password) {
            headers = headers.set('Authorization', 'Basic ' + btoa(this.configuration.username + ':' + this.configuration.password));
        }
        // authentication (CookieAuth) required
        if (this.configuration.apiKeys) {
            const key: string | undefined = this.configuration.apiKeys["CookieAuth"] || this.configuration.apiKeys["Auth-Graviteeio-APIM"];
            if (key) {
            }
        }

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.get<Subscription>(`${this.configuration.basePath}/subscriptions/${encodeURIComponent(String(subscriptionId))}`,
            {
                params: queryParameters,
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * List all ACCEPTED, PAUSED &amp; PENDING subscriptions, filtered by api and/or by application. At least an api or an application must be provided.  User must have the APPLICATION_SUBSCRIPTION[READ] permission to list subscription with application query param.\\ User must have the API_SUBSCRIPTION[READ] permission to list subscription with api query param. 
     * @param requestParameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getSubscriptions(requestParameters: GetSubscriptionsRequestParams, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<SubscriptionsResponse>;
    public getSubscriptions(requestParameters: GetSubscriptionsRequestParams, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<SubscriptionsResponse>>;
    public getSubscriptions(requestParameters: GetSubscriptionsRequestParams, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<SubscriptionsResponse>>;
    public getSubscriptions(requestParameters: GetSubscriptionsRequestParams, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        const apiId = requestParameters.apiId;
        const applicationId = requestParameters.applicationId;
        const statuses = requestParameters.statuses;
        const page = requestParameters.page;
        const size = requestParameters.size;

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (apiId !== undefined && apiId !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>apiId, 'apiId');
        }
        if (applicationId !== undefined && applicationId !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>applicationId, 'applicationId');
        }
        if (statuses) {
            statuses.forEach((element) => {
                queryParameters = this.addToHttpParams(queryParameters,
                  <any>element, 'statuses');
            })
        }
        if (page !== undefined && page !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>page, 'page');
        }
        if (size !== undefined && size !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>size, 'size');
        }

        let headers = this.defaultHeaders;

        // authentication (BasicAuth) required
        if (this.configuration.username || this.configuration.password) {
            headers = headers.set('Authorization', 'Basic ' + btoa(this.configuration.username + ':' + this.configuration.password));
        }
        // authentication (CookieAuth) required
        if (this.configuration.apiKeys) {
            const key: string | undefined = this.configuration.apiKeys["CookieAuth"] || this.configuration.apiKeys["Auth-Graviteeio-APIM"];
            if (key) {
            }
        }

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.get<SubscriptionsResponse>(`${this.configuration.basePath}/subscriptions`,
            {
                params: queryParameters,
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Renew a key subscription.
     * Renew a key subscription.  User must have API_SUBSCRIPTION[UPDATE] or APPLICATION_SUBSCRIPTION[UPDATE] permission. 
     * @param requestParameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public renewKeySubscription(requestParameters: RenewKeySubscriptionRequestParams, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<Key>;
    public renewKeySubscription(requestParameters: RenewKeySubscriptionRequestParams, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<Key>>;
    public renewKeySubscription(requestParameters: RenewKeySubscriptionRequestParams, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<Key>>;
    public renewKeySubscription(requestParameters: RenewKeySubscriptionRequestParams, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        const subscriptionId = requestParameters.subscriptionId;
        if (subscriptionId === null || subscriptionId === undefined) {
            throw new Error('Required parameter subscriptionId was null or undefined when calling renewKeySubscription.');
        }
        const requestBody = requestParameters.requestBody;

        let headers = this.defaultHeaders;

        // authentication (BasicAuth) required
        if (this.configuration.username || this.configuration.password) {
            headers = headers.set('Authorization', 'Basic ' + btoa(this.configuration.username + ':' + this.configuration.password));
        }
        // authentication (CookieAuth) required
        if (this.configuration.apiKeys) {
            const key: string | undefined = this.configuration.apiKeys["CookieAuth"] || this.configuration.apiKeys["Auth-Graviteeio-APIM"];
            if (key) {
            }
        }

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected !== undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.post<Key>(`${this.configuration.basePath}/subscriptions/${encodeURIComponent(String(subscriptionId))}/keys/_renew`,
            requestBody,
            {
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Revoke a key subscription.
     * Revoke a key subscription.  User must have API_SUBSCRIPTION[UPDATE] or APPLICATION_SUBSCRIPTION[UPDATE] permission. 
     * @param requestParameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public revokeKeySubscription(requestParameters: RevokeKeySubscriptionRequestParams, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<any>;
    public revokeKeySubscription(requestParameters: RevokeKeySubscriptionRequestParams, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<any>>;
    public revokeKeySubscription(requestParameters: RevokeKeySubscriptionRequestParams, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<any>>;
    public revokeKeySubscription(requestParameters: RevokeKeySubscriptionRequestParams, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        const subscriptionId = requestParameters.subscriptionId;
        if (subscriptionId === null || subscriptionId === undefined) {
            throw new Error('Required parameter subscriptionId was null or undefined when calling revokeKeySubscription.');
        }
        const apiKey = requestParameters.apiKey;
        if (apiKey === null || apiKey === undefined) {
            throw new Error('Required parameter apiKey was null or undefined when calling revokeKeySubscription.');
        }

        let headers = this.defaultHeaders;

        // authentication (BasicAuth) required
        if (this.configuration.username || this.configuration.password) {
            headers = headers.set('Authorization', 'Basic ' + btoa(this.configuration.username + ':' + this.configuration.password));
        }
        // authentication (CookieAuth) required
        if (this.configuration.apiKeys) {
            const key: string | undefined = this.configuration.apiKeys["CookieAuth"] || this.configuration.apiKeys["Auth-Graviteeio-APIM"];
            if (key) {
            }
        }

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.post<any>(`${this.configuration.basePath}/subscriptions/${encodeURIComponent(String(subscriptionId))}/keys/${encodeURIComponent(String(apiKey))}/_revoke`,
            null,
            {
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Update a subscription
     * Update a subscription.  User must have APPLICATION_SUBSCRIPTION[UPDATE] permission. 
     * @param requestParameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public updateSubscription(requestParameters: UpdateSubscriptionRequestParams, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<any>;
    public updateSubscription(requestParameters: UpdateSubscriptionRequestParams, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<any>>;
    public updateSubscription(requestParameters: UpdateSubscriptionRequestParams, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<any>>;
    public updateSubscription(requestParameters: UpdateSubscriptionRequestParams, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        const subscriptionId = requestParameters.subscriptionId;
        if (subscriptionId === null || subscriptionId === undefined) {
            throw new Error('Required parameter subscriptionId was null or undefined when calling updateSubscription.');
        }
        const subscriptionConfigurationInput = requestParameters.subscriptionConfigurationInput;

        let headers = this.defaultHeaders;

        // authentication (BasicAuth) required
        if (this.configuration.username || this.configuration.password) {
            headers = headers.set('Authorization', 'Basic ' + btoa(this.configuration.username + ':' + this.configuration.password));
        }
        // authentication (CookieAuth) required
        if (this.configuration.apiKeys) {
            const key: string | undefined = this.configuration.apiKeys["CookieAuth"] || this.configuration.apiKeys["Auth-Graviteeio-APIM"];
            if (key) {
            }
        }

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected !== undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.put<any>(`${this.configuration.basePath}/subscriptions/${encodeURIComponent(String(subscriptionId))}`,
            subscriptionConfigurationInput,
            {
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Update the consumer status of a subscription
     * Update the consumer status of a subscription  User must have APPLICATION_SUBSCRIPTION[UPDATE] permission. 
     * @param requestParameters
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public updateSubscriptionConsumerStatus(requestParameters: UpdateSubscriptionConsumerStatusRequestParams, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<any>;
    public updateSubscriptionConsumerStatus(requestParameters: UpdateSubscriptionConsumerStatusRequestParams, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpResponse<any>>;
    public updateSubscriptionConsumerStatus(requestParameters: UpdateSubscriptionConsumerStatusRequestParams, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json'}): Observable<HttpEvent<any>>;
    public updateSubscriptionConsumerStatus(requestParameters: UpdateSubscriptionConsumerStatusRequestParams, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json'}): Observable<any> {
        const subscriptionId = requestParameters.subscriptionId;
        if (subscriptionId === null || subscriptionId === undefined) {
            throw new Error('Required parameter subscriptionId was null or undefined when calling updateSubscriptionConsumerStatus.');
        }
        const status = requestParameters.status;
        if (status === null || status === undefined) {
            throw new Error('Required parameter status was null or undefined when calling updateSubscriptionConsumerStatus.');
        }

        let queryParameters = new HttpParams({encoder: this.encoder});
        if (status !== undefined && status !== null) {
          queryParameters = this.addToHttpParams(queryParameters,
            <any>status, 'status');
        }

        let headers = this.defaultHeaders;

        // authentication (BasicAuth) required
        if (this.configuration.username || this.configuration.password) {
            headers = headers.set('Authorization', 'Basic ' + btoa(this.configuration.username + ':' + this.configuration.password));
        }
        // authentication (CookieAuth) required
        if (this.configuration.apiKeys) {
            const key: string | undefined = this.configuration.apiKeys["CookieAuth"] || this.configuration.apiKeys["Auth-Graviteeio-APIM"];
            if (key) {
            }
        }

        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        let responseType: 'text' | 'json' = 'json';
        if(httpHeaderAcceptSelected && httpHeaderAcceptSelected.startsWith('text')) {
            responseType = 'text';
        }

        return this.httpClient.post<any>(`${this.configuration.basePath}/subscriptions/${encodeURIComponent(String(subscriptionId))}/_changeConsumerStatus`,
            null,
            {
                params: queryParameters,
                responseType: <any>responseType,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
