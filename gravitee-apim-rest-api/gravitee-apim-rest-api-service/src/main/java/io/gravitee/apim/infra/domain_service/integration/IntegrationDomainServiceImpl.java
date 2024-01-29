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

package io.gravitee.apim.infra.domain_service.integration;

import static io.gravitee.apim.core.license.domain_service.GraviteeLicenseDomainService.APIM_INTEGRATION;

import io.gravitee.apim.core.api.domain_service.CreateFederatedApiDomainService;
import io.gravitee.apim.core.api.model.Api;
import io.gravitee.apim.core.audit.model.AuditInfo;
import io.gravitee.apim.core.integration.crud_service.IntegrationCrudService;
import io.gravitee.apim.core.integration.domain_service.IntegrationDomainService;
import io.gravitee.apim.core.integration.model.IntegrationEntity;
import io.gravitee.apim.core.license.domain_service.GraviteeLicenseDomainService;
import io.gravitee.apim.infra.adapter.IntegrationAdapter;
import io.gravitee.common.service.AbstractService;
import io.gravitee.definition.model.DefinitionVersion;
import io.gravitee.definition.model.federation.FederatedApiBuilder;
import io.gravitee.exchange.api.command.Command;
import io.gravitee.exchange.api.command.CommandHandler;
import io.gravitee.exchange.api.command.CommandStatus;
import io.gravitee.exchange.api.command.Reply;
import io.gravitee.exchange.api.command.ReplyHandler;
import io.gravitee.exchange.api.connector.ExchangeConnectorManager;
import io.gravitee.exchange.api.controller.ExchangeController;
import io.gravitee.exchange.connector.embedded.EmbeddedExchangeConnector;
import io.gravitee.exchange.controller.embedded.channel.EmbeddedChannel;
import io.gravitee.integration.api.Entity;
import io.gravitee.integration.api.IntegrationProvider;
import io.gravitee.integration.api.IntegrationProviderFactory;
import io.gravitee.integration.api.command.fetch.FetchCommand;
import io.gravitee.integration.api.command.fetch.FetchCommandPayload;
import io.gravitee.integration.api.command.fetch.FetchReply;
import io.gravitee.integration.api.command.list.ListCommand;
import io.gravitee.integration.api.command.list.ListReply;
import io.gravitee.integration.connector.command.IntegrationConnectorCommandContext;
import io.gravitee.integration.connector.command.IntegrationConnectorCommandHandlersFactory;
import io.gravitee.plugin.integrationprovider.IntegrationProviderPluginManager;
import io.gravitee.rest.api.model.NewPageEntity;
import io.gravitee.rest.api.model.PageType;
import io.gravitee.rest.api.service.PageService;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Remi Baptiste (remi.baptiste at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
@Service
public class IntegrationDomainServiceImpl extends AbstractService<IntegrationDomainService> implements IntegrationDomainService {

    private final GraviteeLicenseDomainService graviteeLicenseDomainService;
    private final ExchangeConnectorManager exchangeConnectorManager;

    private final ExchangeController exchangeController;
    private final IntegrationConnectorCommandHandlersFactory connectorCommandHandlersFactory;
    private final IntegrationProviderPluginManager integrationProviderPluginManager;
    private final IntegrationCrudService integrationCrudService;

    private final PageService pageService;

    private final CreateFederatedApiDomainService createFederatedApiDomainService;

    public IntegrationDomainServiceImpl(
        final GraviteeLicenseDomainService graviteeLicenseDomainService,
        final ExchangeConnectorManager exchangeConnectorManager,
        @Qualifier("integrationExchangeController") final ExchangeController exchangeController,
        final IntegrationConnectorCommandHandlersFactory connectorCommandHandlersFactory,
        final IntegrationProviderPluginManager integrationProviderPluginManager,
        final IntegrationCrudService integrationCrudService,
        final PageService pageService,
        final CreateFederatedApiDomainService createFederatedApiDomainService
    ) {
        this.graviteeLicenseDomainService = graviteeLicenseDomainService;
        this.exchangeConnectorManager = exchangeConnectorManager;
        this.exchangeController = exchangeController;
        this.connectorCommandHandlersFactory = connectorCommandHandlersFactory;
        this.integrationProviderPluginManager = integrationProviderPluginManager;
        this.integrationCrudService = integrationCrudService;
        this.pageService = pageService;
        this.createFederatedApiDomainService = createFederatedApiDomainService;
    }

    // TODO To be removed when the license is up to date
    private final boolean FORCE_INTEGRATION = true;

    @Override
    public void doStart() throws Exception {
        super.doStart();
        if (graviteeLicenseDomainService.isFeatureEnabled(APIM_INTEGRATION) || FORCE_INTEGRATION) {
            exchangeController.start();

            integrationCrudService.findAll().forEach(this::startIntegration);
            log.info("Integrations started.");
        } else {
            log.warn("License doesn't contain Integrations feature.");
        }
    }

    @Override
    public void startIntegration(IntegrationEntity integration) {
        if (integration.getDeploymentType() == IntegrationEntity.DeploymentType.EMBEDDED) {
            try {
                IntegrationProviderFactory<?> integrationProviderFactory = integrationProviderPluginManager.getIntegrationProviderFactory(
                    integration.getProvider().toLowerCase()
                );

                if (integrationProviderFactory == null) {
                    log.warn("Integration provider {} cannot be instantiated (no factory found). Skipped.", integration.getProvider());
                    return;
                }

                IntegrationProvider integrationProvider = integrationProviderFactory.createIntegrationProvider(
                    integration.getId(),
                    integration.getConfiguration()
                );

                if (integrationProvider == null) {
                    log.warn("Integration provider {} cannot be started. Skipped.", integration.getProvider());
                    return;
                }

                integrationProvider.start();

                IntegrationConnectorCommandContext integrationConnectorCommandContext = new IntegrationConnectorCommandContext(
                    integration.getProvider(),
                    integration.getId(),
                    integration.getEnvironmentId(),
                    integrationProvider
                );
                Map<String, CommandHandler<? extends Command<?>, ? extends Reply<?>>> connectorCommandHandlers =
                    connectorCommandHandlersFactory.buildCommandHandlers(integrationConnectorCommandContext);
                Map<String, ReplyHandler<? extends Command<?>, ? extends Command<?>, ? extends Reply<?>>> connectorReplyHandlers =
                    connectorCommandHandlersFactory.buildReplyHandlers(integrationConnectorCommandContext);
                EmbeddedChannel embeddedChannel = EmbeddedChannel
                    .builder()
                    .targetId(integration.getId())
                    .commandHandlers(connectorCommandHandlers)
                    .replyHandlers(connectorReplyHandlers)
                    .build();
                exchangeController
                    .register(embeddedChannel)
                    .andThen(
                        exchangeConnectorManager.register(EmbeddedExchangeConnector.builder().connectorChannel(embeddedChannel).build())
                    )
                    .blockingAwait();
            } catch (Exception e) {
                log.warn("Unable to properly start the integration provider {}: {}. Skipped.", integration.getProvider(), e.getMessage());
            }
        }
    }

    @Override
    public Flowable<IntegrationEntity> getIntegrationEntities(IntegrationEntity integration) {
        ListCommand listCommand = new ListCommand();
        String targetId = integration.getDeploymentType() == IntegrationEntity.DeploymentType.EMBEDDED
            ? integration.getId()
            : integration.getRemoteId();
        return sendListCommand(listCommand, targetId)
            .flatMapPublisher(listReply -> {
                if (listReply.getCommandStatus() == CommandStatus.SUCCEEDED) {
                    List<IntegrationEntity> integrationEntities = listReply
                        .getPayload()
                        .entities()
                        .stream()
                        .map(IntegrationAdapter.INSTANCE::toEntity)
                        .toList();
                    return Flowable.fromIterable(integrationEntities);
                }
                return Flowable.empty();
            });
    }

    @Override
    public Flowable<IntegrationEntity> fetchEntities(IntegrationEntity integration, List<IntegrationEntity> integrationEntities) {
        List<Entity> entities = integrationEntities.stream().map(IntegrationAdapter.INSTANCE::toEntityApi).toList();

        FetchCommandPayload fetchCommandPayload = new FetchCommandPayload(entities);
        FetchCommand fetchCommand = new FetchCommand(fetchCommandPayload);
        String targetId = integration.getDeploymentType() == IntegrationEntity.DeploymentType.EMBEDDED
            ? integration.getId()
            : integration.getRemoteId();
        return sendFetchCommand(fetchCommand, targetId)
            .toFlowable()
            .flatMap(fetchReply -> {
                if (fetchReply.getCommandStatus() == CommandStatus.SUCCEEDED) {
                    List<IntegrationEntity> fetchEntities = fetchReply
                        .getPayload()
                        .entities()
                        .stream()
                        .map(IntegrationAdapter.INSTANCE::toEntity)
                        .toList();
                    return Flowable.fromIterable(fetchEntities);
                }
                return Flowable.empty();
            });
    }

    @Override
    public Api importApi(IntegrationEntity entity, AuditInfo auditInfo, IntegrationEntity integration) {
        // Create API
        var api = Api
            .builder()
            .version(entity.getVersion())
            .definitionVersion(DefinitionVersion.FEDERATED)
            .name(entity.getName())
            .description(entity.getDescription())
            .apiDefinitionFederated(
                FederatedApiBuilder
                    .aFederatedApi()
                    .apiVersion(entity.getVersion())
                    .name(entity.getName())
                    .accessPoint(entity.getHost() + entity.getPath())
                    .build()
            )
            .build();

        //TODO manage context to save access point, runtime ...

        var createdApiEntity = createFederatedApiDomainService.create(api, auditInfo);

        // Create page
        entity
            .getPages()
            .forEach(page -> {
                PageType pageType = PageType.valueOf(page.getPageType().name());
                createPage(createdApiEntity.getId(), entity.getName(), page.getContent(), pageType, auditInfo.actor().userId());
            });

        log.info("API Imported {}", createdApiEntity.getId());
        return createdApiEntity;
    }

    private void createPage(String apiId, String apiName, String content, PageType pageType, String userId) {
        NewPageEntity newPageEntity = new NewPageEntity();
        newPageEntity.setType(pageType);
        newPageEntity.setName(apiName);
        newPageEntity.setContent(content);

        int order = pageService.findMaxApiPageOrderByApi(apiId) + 1;
        newPageEntity.setOrder(order);
        newPageEntity.setLastContributor(userId);

        pageService.createPage(GraviteeContext.getExecutionContext(), apiId, newPageEntity);
    }

    private Single<ListReply> sendListCommand(ListCommand listCommand, String integrationId) {
        return exchangeController
            .sendCommand(listCommand, integrationId)
            .cast(ListReply.class)
            .defaultIfEmpty(new ListReply(listCommand.getId(), "Command received no reply"))
            .onErrorReturn(throwable -> new ListReply(listCommand.getId(), throwable.getMessage()));
    }

    private Single<FetchReply> sendFetchCommand(FetchCommand fetchCommand, String integrationId) {
        return exchangeController
            .sendCommand(fetchCommand, integrationId)
            .cast(FetchReply.class)
            .defaultIfEmpty(new FetchReply(fetchCommand.getId(), "Command received no reply"))
            .onErrorReturn(throwable -> new FetchReply(fetchCommand.getId(), throwable.getMessage()));
    }
}
