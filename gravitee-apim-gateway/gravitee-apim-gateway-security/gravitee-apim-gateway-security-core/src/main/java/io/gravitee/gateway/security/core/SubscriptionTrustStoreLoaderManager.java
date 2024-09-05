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
package io.gravitee.gateway.security.core;

import io.gravitee.gateway.api.service.Subscription;
import io.gravitee.gateway.security.core.exception.MalformedCertificateException;
import io.gravitee.node.api.certificate.KeyStoreEvent;
import io.gravitee.node.api.server.ServerManager;
import io.gravitee.node.vertx.server.VertxServer;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
public class SubscriptionTrustStoreLoaderManager {

    private final Map<String, SubscriptionTrustStoreLoader> subscriptionTrustStoreLoaders = new ConcurrentHashMap<>();
    private final Map<String, Subscription> subscriptionsByApi = new ConcurrentHashMap<>();
    private final ServerManager serverManager;

    public SubscriptionTrustStoreLoaderManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public void registerSubscription(Subscription subscription, Set<String> deployOnServers) {
        final SubscriptionTrustStoreLoader loader;
        try {
            loader = new SubscriptionTrustStoreLoader(subscription);
        } catch (MalformedCertificateException e) {
            log.error(e.getMessage(), e.getCause());
            return;
        }
        if (subscriptionTrustStoreLoaders.containsKey(subscription.getId())) {
            log.debug("A TrustStoreLoader for subscription {} is already registered", subscription.getId());
            return;
        }
        log.debug("Registering TrustStoreLoader for subscription {}", subscription.getId());
        serverManager
            .servers()
            .stream()
            .filter(server -> deployOnServers.isEmpty() || deployOnServers.contains(server.id()))
            .map(s -> (VertxServer<?, ?>) s)
            .forEach(server -> server.trustStoreLoaderManager().registerLoader(loader));
        subscriptionTrustStoreLoaders.put(subscription.getId(), loader);
        subscriptionsByApi.put(
            buildCacheKeyFromCertificateDigest(subscription.getApi(), loader.certificateDigest(), subscription.getPlan()),
            subscription
        );
    }

    public void unregisterSubscription(Subscription subscription) {
        final SubscriptionTrustStoreLoader loader = subscriptionTrustStoreLoaders.remove(subscription.getId());
        if (loader != null) {
            log.debug("Stopping TrustStoreLoader for subscription {}", subscription.getId());
            loader.stop();
            loader.onEvent(new KeyStoreEvent.UnloadEvent(loader.id()));
        }
    }

    public Optional<Subscription> getByCertificate(String api, String certificateDigest, String plan) {
        return Optional.ofNullable(subscriptionsByApi.get(buildCacheKeyFromCertificateDigest(api, certificateDigest, plan)));
    }

    String buildCacheKeyFromCertificateDigest(String api, String certificateDigest, String plan) {
        return String.format("%s.%s.%s", api, certificateDigest, plan);
    }
}
