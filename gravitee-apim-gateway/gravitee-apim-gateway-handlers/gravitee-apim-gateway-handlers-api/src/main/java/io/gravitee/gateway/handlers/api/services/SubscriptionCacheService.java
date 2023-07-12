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
package io.gravitee.gateway.handlers.api.services;

import static io.gravitee.gateway.reactive.api.policy.SecurityToken.TokenType.API_KEY;
import static io.gravitee.gateway.reactive.api.policy.SecurityToken.TokenType.CLIENT_ID;
import static io.gravitee.repository.management.model.Subscription.Status.ACCEPTED;

import io.gravitee.gateway.api.service.ApiKeyService;
import io.gravitee.gateway.api.service.Subscription;
import io.gravitee.gateway.api.service.SubscriptionService;
import io.gravitee.gateway.reactive.api.policy.SecurityToken;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SubscriptionCacheService implements SubscriptionService {

    private final ApiKeyService apiKeyService;

    // Caches only contains active subscriptions
    private final Map<String, Subscription> cacheByApiClientId = new ConcurrentHashMap<>();
    private final Map<String, Subscription> cacheBySubscriptionId = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> cacheByApiId = new ConcurrentHashMap<>();

    @Override
    public Optional<Subscription> getByApiAndSecurityToken(String api, SecurityToken securityToken, String plan) {
        if (securityToken.getTokenType().equals(API_KEY.name())) {
            return apiKeyService.getByApiAndKey(api, securityToken.getTokenValue()).flatMap(apiKey -> getById(apiKey.getSubscription()));
        } else if (securityToken.getTokenType().equals(CLIENT_ID.name())) {
            return getByApiAndClientIdAndPlan(api, securityToken.getTokenValue(), plan);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Subscription> getByApiAndClientIdAndPlan(String api, String clientId, String plan) {
        return Optional.ofNullable(cacheByApiClientId.get(buildClientIdCacheKey(api, clientId, plan)));
    }

    @Override
    public Optional<Subscription> getById(String subscriptionId) {
        return Optional.ofNullable(cacheBySubscriptionId.get(subscriptionId));
    }

    @Override
    public void register(final Subscription subscription) {
        if (ACCEPTED.name().equals(subscription.getStatus())) {
            if (subscription.getClientId() != null) {
                registerFromClientId(subscription);
            } else {
                registerFromId(subscription);
            }
        } else {
            unregister(subscription);
        }
    }

    private void registerFromClientId(final Subscription subscription) {
        final String idKey = subscription.getId();
        final String clientIdKey = buildClientIdCacheKey(subscription);
        // Index the subscription without plan id to allow search without plan criteria.
        final String clientIdKeyWithoutPlan = buildClientIdCacheKey(subscription.getApi(), subscription.getClientId(), null);

        Subscription cachedSubscription = cacheBySubscriptionId.get(idKey);

        // remove previous subscription client_id from cache if client_id has changed
        if (
            cachedSubscription != null &&
            cachedSubscription.getClientId() != null &&
            !cachedSubscription.getClientId().equals(subscription.getClientId())
        ) {
            unregisterFromClientId(cachedSubscription);
        }

        log.debug(
            "Load accepted subscription with client Id  [id: {}] [api: {}] [plan: {}] [application: {}]",
            subscription.getId(),
            subscription.getApi(),
            subscription.getPlan(),
            subscription.getApi()
        );
        // Update subscription
        cacheBySubscriptionId.put(idKey, subscription);
        addKeyForApi(subscription.getApi(), idKey);
        // Put new client_id
        cacheByApiClientId.put(clientIdKey, subscription);
        addKeyForApi(subscription.getApi(), clientIdKey);
        cacheByApiClientId.put(clientIdKeyWithoutPlan, subscription);
        addKeyForApi(subscription.getApi(), clientIdKeyWithoutPlan);
    }

    private void registerFromId(final Subscription subscription) {
        String cacheKey = subscription.getId();
        log.debug(
            "Load accepted subscription [id: {}] [api: {}] [plan: {}] [application: {}]",
            subscription.getId(),
            subscription.getApi(),
            subscription.getPlan(),
            subscription.getApi()
        );
        cacheBySubscriptionId.put(cacheKey, subscription);
        addKeyForApi(subscription.getApi(), cacheKey);
    }

    private void addKeyForApi(final String apiId, final String cacheKey) {
        Set<String> subscriptionsByApi = cacheByApiId.get(apiId);
        if (subscriptionsByApi == null) {
            subscriptionsByApi = new HashSet<>();
        }
        subscriptionsByApi.add(cacheKey);
        cacheByApiId.put(apiId, subscriptionsByApi);
    }

    private void removeKeyForApi(final String apiId, final String cacheKey) {
        Set<String> keysByApi = cacheByApiId.get(apiId);
        if (keysByApi != null && keysByApi.remove(cacheKey)) {
            if (keysByApi.isEmpty()) {
                cacheByApiId.remove(apiId);
            } else {
                cacheByApiId.put(apiId, keysByApi);
            }
        }
    }

    @Override
    public void unregister(final Subscription subscription) {
        log.debug(
            "Unload subscription [id: {}] [api: {}] [plan: {}] [application: {}]",
            subscription.getId(),
            subscription.getApi(),
            subscription.getPlan(),
            subscription.getApi()
        );
        final String idKey = subscription.getId();
        Subscription removeSubscription = cacheBySubscriptionId.remove(idKey);
        if (removeSubscription != null) {
            removeKeyForApi(subscription.getApi(), idKey);
            unregisterFromClientId(removeSubscription);
        }
        // In case new one has different client id than the one in cache
        unregisterFromClientId(subscription);
    }

    private void unregisterFromClientId(final Subscription subscription) {
        if (subscription.getClientId() != null) {
            final String clientIdKey = buildClientIdCacheKey(subscription);
            if (cacheByApiClientId.remove(clientIdKey) != null) {
                removeKeyForApi(subscription.getApi(), clientIdKey);
            }
            final String clientIdKeyWithoutPlan = buildClientIdCacheKey(subscription.getApi(), subscription.getClientId(), null);
            if (cacheByApiClientId.remove(clientIdKeyWithoutPlan) != null) {
                removeKeyForApi(subscription.getApi(), clientIdKeyWithoutPlan);
            }
        }
    }

    @Override
    public void unregisterByApiId(final String apiId) {
        log.debug("Unload all subscriptions by api [api_id: {}]", apiId);
        Set<String> subscriptionsByApi = cacheByApiId.remove(apiId);
        if (subscriptionsByApi != null) {
            subscriptionsByApi.forEach(cacheKey -> {
                Subscription subscription = cacheBySubscriptionId.remove(cacheKey);
                if (subscription != null) {
                    log.debug(
                        "Unload subscription [id: {}] [api: {}] [plan: {}] [application: {}]",
                        subscription.getId(),
                        subscription.getApi(),
                        subscription.getPlan(),
                        subscription.getApi()
                    );
                }
                cacheByApiClientId.remove(cacheKey);
            });
        }
    }

    String buildClientIdCacheKey(Subscription subscription) {
        return buildClientIdCacheKey(subscription.getApi(), subscription.getClientId(), subscription.getPlan());
    }

    String buildClientIdCacheKey(String api, String clientId, String plan) {
        return String.format("%s.%s.%s", api, clientId, plan);
    }
}
