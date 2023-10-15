/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.repository.management.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
@Data
@Builder
@AllArgsConstructor
public class Subscription implements Serializable {

    public enum AuditEvent implements Audit.ApiAuditEvent {
        SUBSCRIPTION_CREATED,
        SUBSCRIPTION_UPDATED,
        SUBSCRIPTION_DELETED,
        SUBSCRIPTION_CLOSED,
        SUBSCRIPTION_PAUSED,
        SUBSCRIPTION_RESUMED,
        SUBSCRIPTION_PAUSED_BY_CONSUMER,
        SUBSCRIPTION_RESUMED_BY_CONSUMER,
    }

    /**
     * Subscription ID.
     */
    private String id;
    /**
     * The subscribed {@link Api}.
     */
    private String api;
    /**
     * The subscribed {@link Plan}.
     */
    private String plan;
    /**
     * The application linked to the subscription
     */
    private String application;
    /**
     * The clientId linked to the subscription
     */
    private String clientId;
    /**
     * Vhen the subscription have been processed.
     */
    private Date processedAt;
    /**
     * Give a request message to the api owner why a user want to subscribe
     */
    private String request;
    /**
     * Give a reason to the developer if the subscription is accepted or not.
     */
    private String reason;

    /**
     * The status of the current subscription
     */
    private Status status;

    /**
     * The status chosen by the consumer
     */
    private ConsumerStatus consumerStatus = ConsumerStatus.STARTED;

    /**
     * The username of the user who has processed the subscription
     * <code>null</code> if the subscription is relative to an automatic plan.
     */
    private String processedBy;
    /**
     * The username of the user who has subscribed to the plan.
     */
    private String subscribedBy;
    private Date startingAt;
    private Date endingAt;
    /**
     * Subscription creation date
     */
    private Date createdAt;
    /**
     * Subscription last update date
     */
    private Date updatedAt;
    private Date closedAt;
    private Date pausedAt;

    private Date consumerPausedAt;

    private Integer generalConditionsContentRevision;
    private String generalConditionsContentPageId;
    private Boolean generalConditionsAccepted;
    /**
     * Number of days before the expiration of this subscription when the last pre-expiration notification was sent
     */
    private Integer daysToExpirationOnLastNotification;
    private String configuration;
    private Map<String, String> metadata;
    private Type type = Type.STANDARD;

    private String failureCause;

    public Subscription() {}

    public Subscription(Subscription cloned) {
        this.id = cloned.id;
        this.api = cloned.api;
        this.plan = cloned.plan;
        this.application = cloned.application;
        this.clientId = cloned.clientId;
        this.processedAt = cloned.processedAt;
        this.request = cloned.request;
        this.reason = cloned.reason;
        this.status = cloned.status;
        this.consumerStatus = cloned.consumerStatus;
        this.processedBy = cloned.processedBy;
        this.subscribedBy = cloned.subscribedBy;
        this.startingAt = cloned.startingAt;
        this.endingAt = cloned.endingAt;
        this.createdAt = cloned.createdAt;
        this.updatedAt = cloned.updatedAt;
        this.closedAt = cloned.closedAt;
        this.pausedAt = cloned.pausedAt;
        this.consumerPausedAt = cloned.consumerPausedAt;
        this.generalConditionsAccepted = cloned.generalConditionsAccepted;
        this.generalConditionsContentRevision = cloned.generalConditionsContentRevision;
        this.generalConditionsContentPageId = cloned.generalConditionsContentPageId;
        this.daysToExpirationOnLastNotification = cloned.daysToExpirationOnLastNotification;
        this.configuration = cloned.configuration;
        this.metadata = cloned.metadata;
        this.type = cloned.type;
        this.failureCause = cloned.failureCause;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public void setStartingAt(Date startingAt) {
        this.startingAt = startingAt;
    }

    public void setEndingAt(Date endingAt) {
        this.endingAt = endingAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    public void setSubscribedBy(String subscribedBy) {
        this.subscribedBy = subscribedBy;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setConsumerStatus(ConsumerStatus consumerStatus) {
        this.consumerStatus = consumerStatus;
    }

    public void setClosedAt(Date closedAt) {
        this.closedAt = closedAt;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setPausedAt(Date pausedAt) {
        this.pausedAt = pausedAt;
    }

    public void setConsumerPausedAt(Date consumerPausedAt) {
        this.consumerPausedAt = consumerPausedAt;
    }

    public void setGeneralConditionsContentRevision(Integer generalConditionsContentRevision) {
        this.generalConditionsContentRevision = generalConditionsContentRevision;
    }

    public void setGeneralConditionsContentPageId(String generalConditionsContentPageId) {
        this.generalConditionsContentPageId = generalConditionsContentPageId;
    }

    public void setGeneralConditionsAccepted(Boolean generalConditionsAccepted) {
        this.generalConditionsAccepted = generalConditionsAccepted;
    }

    public void setDaysToExpirationOnLastNotification(Integer daysToExpirationOnLastNotification) {
        this.daysToExpirationOnLastNotification = daysToExpirationOnLastNotification;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setFailureCause(String failureCause) {
        this.failureCause = failureCause;
    }

    /**
     * Consumer can start subscription only if its consumer status is {@link this#consumerStatus} is {@link ConsumerStatus#STOPPED} or {@link ConsumerStatus#STARTED}
     * @return true when consumer can start the subscription
     */
    public boolean canBeStartedByConsumer() {
        return ConsumerStatus.STOPPED.equals(consumerStatus) || ConsumerStatus.FAILURE.equals(consumerStatus);
    }

    /**
     * Consumer can stop subscription only if its consumer status is {@link this#consumerStatus} is {@link ConsumerStatus#STARTED} or {@link ConsumerStatus#STARTED}
     * @return true when consumer can stop the subscription
     */
    public boolean canBeStoppedByConsumer() {
        return ConsumerStatus.STARTED.equals(consumerStatus) || ConsumerStatus.FAILURE.equals(consumerStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public enum Status {
        /**
         * Waiting for validation
         */
        PENDING,

        /**
         * Subscription has been rejected
         */
        REJECTED,

        /**
         * Subscription has been accepted
         */
        ACCEPTED,

        /**
         * Subscription has been closed
         */
        CLOSED,

        /**
         * Subscription has been paused
         */
        PAUSED,
    }

    public enum ConsumerStatus {
        /**
         * Subscription is started
         */
        STARTED,
        /**
         * Subscription has been paused
         */
        STOPPED,
        /**
         * Subscription has encountered a failure
         */
        FAILURE,
    }

    public enum Type {
        /**
         * <code>STANDARD</code> subscriptions are subscription used to manage calls in proxy mode (request/response)
         */
        STANDARD,

        /**
         * <code>PUSH</code> type of subscription are subscription used to manage push calls
         * (the call from the gateway is initiated by the incoming subscription, a typical use-case is webhook).
         */
        PUSH,
    }
}
