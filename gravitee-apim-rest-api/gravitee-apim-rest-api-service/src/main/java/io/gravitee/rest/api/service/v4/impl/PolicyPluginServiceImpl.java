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
package io.gravitee.rest.api.service.v4.impl;

import io.gravitee.plugin.core.api.ConfigurablePluginManager;
import io.gravitee.plugin.core.api.Plugin;
import io.gravitee.plugin.policy.PolicyPlugin;
import io.gravitee.rest.api.model.platform.plugin.SchemaDisplayFormat;
import io.gravitee.rest.api.model.v4.policy.ApiProtocolType;
import io.gravitee.rest.api.model.v4.policy.FlowPhase;
import io.gravitee.rest.api.model.v4.policy.PolicyPluginEntity;
import io.gravitee.rest.api.service.JsonSchemaService;
import io.gravitee.rest.api.service.impl.AbstractPluginService;
import io.gravitee.rest.api.service.v4.PolicyPluginService;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PolicyPluginServiceImpl extends AbstractPluginService<PolicyPlugin<?>, PolicyPluginEntity> implements PolicyPluginService {

    private enum POLICY_FLOW_PHASE {
        INTERACT,
        CONNECT,
        PUBLISH,
        SUBSCRIBE,
        REQUEST,
        RESPONSE,
        /**
         * @deprecated since 4.6.0, use {@link #PUBLISH} instead
         * To be kept as long as policies use these phase in plugin.properties
         */
        @Deprecated
        MESSAGE_REQUEST,
        /**
         * @deprecated since 4.6.0, use {@link #PUBLISH} instead
         * To be kept as long as policies use these phase in plugin.properties
         */
        @Deprecated
        MESSAGE_RESPONSE,
    }

    public static final Set<String> INTERNAL_POLICIES_ID = Set.of("shared-policy-group-policy");

    protected PolicyPluginServiceImpl(
        final JsonSchemaService jsonSchemaService,
        final ConfigurablePluginManager<PolicyPlugin<?>> policyManager
    ) {
        super(jsonSchemaService, policyManager);
    }

    @Override
    public Set<PolicyPluginEntity> findAll() {
        // Need to preserve list order in Set
        return super.list().stream().map(this::convert).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public PolicyPluginEntity findById(String policyPluginId) {
        PolicyPlugin resourceDefinition = super.get(policyPluginId);
        return convert(resourceDefinition);
    }

    @Override
    protected PolicyPluginEntity convert(Plugin plugin) {
        PolicyPluginEntity entity = new PolicyPluginEntity();

        entity.setId(plugin.id());
        entity.setDescription(plugin.manifest().description());
        entity.setName(plugin.manifest().name());
        entity.setIcon(getIcon(plugin.id()));
        entity.setVersion(plugin.manifest().version());
        entity.setCategory(plugin.manifest().category());
        entity.setDeployed(plugin.deployed());

        var httpProxyFlowPhase = getFlowPhase(plugin, ApiProtocolType.HTTP_PROXY);
        var httpMessageFlowPhase = getFlowPhase(plugin, ApiProtocolType.HTTP_MESSAGE);
        var nativeKafkaFlowPhase = getFlowPhase(plugin, ApiProtocolType.NATIVE_KAFKA);
        if (httpProxyFlowPhase.isEmpty()) {
            httpProxyFlowPhase = getDeprecatedFlowPhase(plugin, "proxy");
        }
        if (httpMessageFlowPhase.isEmpty()) {
            httpMessageFlowPhase = getDeprecatedFlowPhase(plugin, "message");
        }

        entity.putFlowPhaseCompatibility(ApiProtocolType.HTTP_PROXY, httpProxyFlowPhase);
        entity.putFlowPhaseCompatibility(ApiProtocolType.HTTP_MESSAGE, httpMessageFlowPhase);
        entity.putFlowPhaseCompatibility(ApiProtocolType.NATIVE_KAFKA, nativeKafkaFlowPhase);

        return entity;
    }

    private static Set<FlowPhase> getDeprecatedFlowPhase(Plugin plugin, String property) {
        if (
            plugin.manifest().properties() != null &&
            plugin.manifest().properties().get(property) != null &&
            !plugin.manifest().properties().get(property).isEmpty()
        ) {
            return Arrays
                .stream(plugin.manifest().properties().get(property).split(","))
                .map(String::trim)
                .map(POLICY_FLOW_PHASE::valueOf)
                .map(p ->
                    switch (p) {
                        case INTERACT -> FlowPhase.INTERACT;
                        case CONNECT -> FlowPhase.CONNECT;
                        case PUBLISH, MESSAGE_REQUEST -> FlowPhase.PUBLISH;
                        case SUBSCRIBE, MESSAGE_RESPONSE -> FlowPhase.SUBSCRIBE;
                        case REQUEST -> FlowPhase.REQUEST;
                        case RESPONSE -> FlowPhase.RESPONSE;
                    }
                )
                .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    private static Set<FlowPhase> getFlowPhase(Plugin plugin, ApiProtocolType apiProtocolType) {
        String apiProtocolTypeProperty = apiProtocolType.name().toLowerCase();
        if (
            plugin.manifest().properties() != null &&
            plugin.manifest().properties().get(apiProtocolTypeProperty) != null &&
            !plugin.manifest().properties().get(apiProtocolTypeProperty).isEmpty()
        ) {
            return Arrays
                .stream(plugin.manifest().properties().get(apiProtocolTypeProperty).split(","))
                .map(String::trim)
                .map(FlowPhase::valueOf)
                .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public String validatePolicyConfiguration(final String policyPluginId, final String configuration) {
        // Ignore validation for internal policies
        if (INTERNAL_POLICIES_ID.contains(policyPluginId)) {
            return configuration;
        }

        PolicyPluginEntity policyPluginEntity = this.findById(policyPluginId);
        return validateConfiguration(policyPluginEntity.getId(), configuration);
    }

    @Override
    public String validatePolicyConfiguration(final PolicyPluginEntity policyPluginEntity, final String configuration) {
        return validateConfiguration(policyPluginEntity.getId(), configuration);
    }

    @Override
    public String getSchema(String policyPluginId, SchemaDisplayFormat schemaDisplayFormat) {
        if (schemaDisplayFormat == SchemaDisplayFormat.GV_SCHEMA_FORM) {
            try {
                logger.debug("Find plugin schema for format {} by ID: {}", schemaDisplayFormat, policyPluginId);
                String schema = pluginManager.getSchema(policyPluginId, "display-gv-schema-form", true);
                if (schema != null) {
                    return schema;
                }
                logger.debug("No specific schema-form exists for this display format. Fall back on default schema-form.");
            } catch (IOException ioex) {
                logger.debug(
                    "Error while getting specific specific schema-form for this display format. Fall back on default schema-form."
                );
            }
        }
        return getSchema(policyPluginId);
    }
}
