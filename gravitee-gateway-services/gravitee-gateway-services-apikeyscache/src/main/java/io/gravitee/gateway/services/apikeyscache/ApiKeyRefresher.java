/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.services.apikeyscache;

import io.gravitee.definition.model.Api;
import io.gravitee.gateway.handlers.api.definition.Plan;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.ApiKeyRepository;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ApiKeyRefresher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyRefresher.class);

    private ApiKeyRepository apiKeyRepository;

    private Cache cache;

    private final Api api;

    private Collection<Plan> plans;

    public ApiKeyRefresher(final Api api) {
        this.api = api;
    }

    @Override
    public void run() {
        logger.warn("Refresh api-keys for API [name: {}] [id: {}]", api.getName(), api.getId());

        plans.stream()
                .flatMap(plan -> {
                    try {
                        logger.warn("Loading for api-keys for plan [name: {}] [id: {}]", plan.getName(), plan.getId());
                        return apiKeyRepository.findByPlan(plan.getId()).stream();
                    } catch (TechnicalException te) {
                        logger.error("Not able to refresh api-keys from repository: {}", te);
                        return Stream.empty();
                    }
                })
                .forEach(apiKey -> {
                    logger.warn("Caching an api-key [key: {}] [plan: {}] [app: {}]", apiKey.getKey(), apiKey.getPlan(), apiKey.getApplication());
                    cache.put(new Element(apiKey.getKey(), apiKey));
                });
    }

    public void setApiKeyRepository(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void setPlans(Collection<Plan> plans) {
        this.plans = plans;
    }
}
