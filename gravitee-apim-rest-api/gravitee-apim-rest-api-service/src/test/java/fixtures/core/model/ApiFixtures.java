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
package fixtures.core.model;

import io.gravitee.apim.core.api.model.Api;
import io.gravitee.definition.model.ApiBuilder;
import io.gravitee.definition.model.DefinitionVersion;
import io.gravitee.definition.model.Proxy;
import io.gravitee.definition.model.v4.ApiType;
import io.gravitee.definition.model.v4.analytics.Analytics;
import io.gravitee.definition.model.v4.endpointgroup.Endpoint;
import io.gravitee.definition.model.v4.endpointgroup.EndpointGroup;
import io.gravitee.definition.model.v4.listener.entrypoint.Entrypoint;
import io.gravitee.definition.model.v4.listener.http.HttpListener;
import io.gravitee.definition.model.v4.listener.http.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ApiFixtures {

    private ApiFixtures() {}

    private static final Supplier<Api.ApiBuilder> BASE = () ->
        Api
            .builder()
            .id("my-api")
            .name("My Api")
            .environmentId("environment-id")
            .crossId("my-api-crossId")
            .description("api-description")
            .version("1.0.0")
            .createdAt(Instant.parse("2020-02-01T20:22:02.00Z").atZone(ZoneId.systemDefault()))
            .updatedAt(Instant.parse("2020-02-02T20:22:02.00Z").atZone(ZoneId.systemDefault()))
            .deployedAt(Instant.parse("2020-02-03T20:22:02.00Z").atZone(ZoneId.systemDefault()))
            .visibility(Api.Visibility.PUBLIC)
            .lifecycleState(Api.LifecycleState.STARTED)
            .apiLifecycleState(Api.ApiLifecycleState.PUBLISHED)
            .picture("api-picture")
            .groups(Set.of("group-1"))
            .categories(Set.of("category-1"))
            .labels(List.of("label-1"))
            .disableMembershipNotifications(true)
            .background("api-background");

    public static Api aProxyApiV4() {
        return BASE
            .get()
            .type(ApiType.PROXY)
            .definitionVersion(DefinitionVersion.V4)
            .apiDefinitionV4(
                io.gravitee.definition.model.v4.Api
                    .builder()
                    .id("my-api")
                    .name("My Api")
                    .analytics(Analytics.builder().enabled(false).build())
                    .type(ApiType.PROXY)
                    .tags(Set.of("tag1"))
                    .listeners(
                        List.of(
                            HttpListener
                                .builder()
                                .paths(List.of(Path.builder().path("/http_proxy").build()))
                                .entrypoints(List.of(Entrypoint.builder().type("http-proxy").configuration("{}").build()))
                                .build()
                        )
                    )
                    .endpointGroups(
                        List.of(
                            EndpointGroup
                                .builder()
                                .name("default-group")
                                .type("http-proxy")
                                .sharedConfiguration("{}")
                                .endpoints(
                                    List.of(
                                        Endpoint
                                            .builder()
                                            .name("default-endpoint")
                                            .type("http-proxy")
                                            .inheritConfiguration(true)
                                            .configuration("{\"target\":\"https://api.gravitee.io/echo\"}")
                                            .build()
                                    )
                                )
                                .build()
                        )
                    )
                    .flows(List.of())
                    .build()
            )
            .build();
    }

    public static Api aProxyApiV2() {
        return BASE
            .get()
            .type(ApiType.PROXY)
            .definitionVersion(DefinitionVersion.V2)
            .apiDefinition(
                ApiBuilder
                    .anApiV2()
                    .id("my-id")
                    .name("api-name")
                    .apiVersion("1.0.0")
                    .tags(Set.of("tag1"))
                    .proxy(
                        Proxy
                            .builder()
                            .groups(
                                Set.of(
                                    io.gravitee.definition.model.EndpointGroup
                                        .builder()
                                        .name("default-group")
                                        .endpoints(
                                            Set.of(
                                                io.gravitee.definition.model.Endpoint
                                                    .builder()
                                                    .name("default")
                                                    .type("http1")
                                                    .target("https://api.gravitee.io/echo")
                                                    .build()
                                            )
                                        )
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build()
            )
            .build();
    }
}
