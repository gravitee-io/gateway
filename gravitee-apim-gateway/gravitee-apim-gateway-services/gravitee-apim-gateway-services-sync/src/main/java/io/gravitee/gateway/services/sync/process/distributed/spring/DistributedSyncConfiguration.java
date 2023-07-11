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
package io.gravitee.gateway.services.sync.process.distributed.spring;

import static io.gravitee.gateway.services.sync.SyncConfiguration.DEFAULT_BULK_ITEMS;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.gateway.services.sync.process.common.deployer.DeployerFactory;
import io.gravitee.gateway.services.sync.process.distributed.fetcher.DistributedEventFetcher;
import io.gravitee.gateway.services.sync.process.distributed.mapper.ApiKeyMapper;
import io.gravitee.gateway.services.sync.process.distributed.mapper.ApiMapper;
import io.gravitee.gateway.services.sync.process.distributed.mapper.DictionaryMapper;
import io.gravitee.gateway.services.sync.process.distributed.mapper.OrganizationMapper;
import io.gravitee.gateway.services.sync.process.distributed.mapper.SubscriptionMapper;
import io.gravitee.gateway.services.sync.process.distributed.service.DefaultDistributedSyncService;
import io.gravitee.gateway.services.sync.process.distributed.synchronizer.api.DistributedApiSynchronizer;
import io.gravitee.gateway.services.sync.process.distributed.synchronizer.apikey.DistributedApiKeySynchronizer;
import io.gravitee.gateway.services.sync.process.distributed.synchronizer.dictionary.DistributedDictionarySynchronizer;
import io.gravitee.gateway.services.sync.process.distributed.synchronizer.organization.DistributedOrganizationSynchronizer;
import io.gravitee.gateway.services.sync.process.distributed.synchronizer.subscription.DistributedSubscriptionSynchronizer;
import io.gravitee.node.api.Node;
import io.gravitee.node.api.cluster.ClusterManager;
import io.gravitee.repository.distributedsync.api.DistributedEventRepository;
import io.gravitee.repository.distributedsync.api.DistributedSyncStateRepository;
import io.gravitee.repository.distributedsync.model.DistributedSyncAction;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@Configuration
@Conditional(DistributedSyncEnabledCondition.class)
public class DistributedSyncConfiguration {

    @Bean
    public ApiMapper distributedApiMapper(ObjectMapper objectMapper, SubscriptionMapper subscriptionMapper, ApiKeyMapper apiKeyMapper) {
        return new ApiMapper(objectMapper, subscriptionMapper, apiKeyMapper);
    }

    @Bean
    public ApiKeyMapper distributedApiKeyMapper(ObjectMapper objectMapper) {
        return new ApiKeyMapper(objectMapper);
    }

    @Bean
    public SubscriptionMapper distributedSubscriptionMapper(ObjectMapper objectMapper) {
        return new SubscriptionMapper(objectMapper);
    }

    @Bean
    public DictionaryMapper distributedDictionaryMapper(ObjectMapper objectMapper) {
        return new DictionaryMapper(objectMapper);
    }

    @Bean
    public OrganizationMapper distributedOrganizationMapper(ObjectMapper objectMapper) {
        return new OrganizationMapper(objectMapper);
    }

    @Bean
    public DistributedEventFetcher distributedEventFetcher(
        @Lazy DistributedEventRepository distributedEventRepository,
        @Value("${services.sync.bulk_items:" + DEFAULT_BULK_ITEMS + "}") int bulkItems
    ) {
        return new DistributedEventFetcher(distributedEventRepository, bulkItems);
    }

    @Bean
    public DistributedSubscriptionSynchronizer distributedSubscriptionSynchronizer(
        DistributedEventFetcher distributedEventFetcher,
        SubscriptionMapper subscriptionMapper,
        DeployerFactory deployerFactory,
        @Qualifier("syncFetcherExecutor") ThreadPoolExecutor syncFetcherExecutor,
        @Qualifier("syncDeployerExecutor") ThreadPoolExecutor syncDeployerExecutor
    ) {
        return new DistributedSubscriptionSynchronizer(
            distributedEventFetcher,
            syncFetcherExecutor,
            syncDeployerExecutor,
            deployerFactory,
            subscriptionMapper
        );
    }

    @Bean
    public DistributedApiKeySynchronizer distributedApiKeySynchronizer(
        DistributedEventFetcher distributedEventFetcher,
        ApiKeyMapper apiKeyMapper,
        DeployerFactory deployerFactory,
        @Qualifier("syncFetcherExecutor") ThreadPoolExecutor syncFetcherExecutor,
        @Qualifier("syncDeployerExecutor") ThreadPoolExecutor syncDeployerExecutor
    ) {
        return new DistributedApiKeySynchronizer(
            distributedEventFetcher,
            syncFetcherExecutor,
            syncDeployerExecutor,
            deployerFactory,
            apiKeyMapper
        );
    }

    @Bean
    public DistributedApiSynchronizer distributedApiSynchronizer(
        DistributedEventFetcher distributedEventFetcher,
        ApiMapper apiMapper,
        DeployerFactory deployerFactory,
        @Qualifier("syncFetcherExecutor") ThreadPoolExecutor syncFetcherExecutor,
        @Qualifier("syncDeployerExecutor") ThreadPoolExecutor syncDeployerExecutor
    ) {
        return new DistributedApiSynchronizer(
            distributedEventFetcher,
            syncFetcherExecutor,
            syncDeployerExecutor,
            deployerFactory,
            apiMapper
        );
    }

    @Bean
    public DistributedDictionarySynchronizer dictionarySynchronizer(
        DistributedEventFetcher distributedEventFetcher,
        DictionaryMapper dictionaryMapper,
        DeployerFactory deployerFactory,
        @Qualifier("syncFetcherExecutor") ThreadPoolExecutor syncFetcherExecutor,
        @Qualifier("syncDeployerExecutor") ThreadPoolExecutor syncDeployerExecutor
    ) {
        return new DistributedDictionarySynchronizer(
            distributedEventFetcher,
            syncFetcherExecutor,
            syncDeployerExecutor,
            deployerFactory,
            dictionaryMapper
        );
    }

    @Bean
    public DistributedOrganizationSynchronizer organizationSynchronizer(
        DistributedEventFetcher distributedEventFetcher,
        OrganizationMapper organizationMapper,
        DeployerFactory deployerFactory,
        @Qualifier("syncFetcherExecutor") ThreadPoolExecutor syncFetcherExecutor,
        @Qualifier("syncDeployerExecutor") ThreadPoolExecutor syncDeployerExecutor
    ) {
        return new DistributedOrganizationSynchronizer(
            distributedEventFetcher,
            syncFetcherExecutor,
            syncDeployerExecutor,
            deployerFactory,
            organizationMapper
        );
    }

    @Bean
    public DefaultDistributedSyncService distributedSyncService(
        final Node node,
        final ClusterManager clusterManager,
        @Value("${distributed-sync.type}") String distributedSyncRepoType,
        @Lazy final DistributedEventRepository distributedEventRepository,
        @Lazy final DistributedSyncStateRepository distributedSyncStateRepository,
        final ApiMapper apiMapper,
        final SubscriptionMapper subscriptionMapper,
        final ApiKeyMapper apiKeyMapper,
        final OrganizationMapper organizationMapper,
        final DictionaryMapper dictionaryMapper
    ) {
        return new DefaultDistributedSyncService(
            node,
            clusterManager,
            distributedSyncRepoType,
            distributedEventRepository,
            distributedSyncStateRepository,
            apiMapper,
            subscriptionMapper,
            apiKeyMapper,
            organizationMapper,
            dictionaryMapper
        );
    }
}
