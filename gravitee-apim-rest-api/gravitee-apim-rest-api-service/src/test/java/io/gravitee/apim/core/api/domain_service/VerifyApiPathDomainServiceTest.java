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
package io.gravitee.apim.core.api.domain_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

import inmemory.ApiHostValidatorDomainServiceGoogleImpl;
import io.gravitee.apim.core.api.exception.InvalidPathsException;
import io.gravitee.apim.core.api.model.Api;
import io.gravitee.apim.core.api.model.ApiFieldFilter;
import io.gravitee.apim.core.api.model.ApiSearchCriteria;
import io.gravitee.apim.core.api.model.Path;
import io.gravitee.apim.core.api.query_service.ApiQueryService;
import io.gravitee.apim.core.installation.model.RestrictedDomain;
import io.gravitee.apim.core.installation.query_service.InstallationAccessQueryService;
import io.gravitee.definition.model.DefinitionVersion;
import io.gravitee.definition.model.Proxy;
import io.gravitee.definition.model.VirtualHost;
import io.gravitee.definition.model.v4.listener.http.HttpListener;
import io.gravitee.rest.api.service.common.GraviteeContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerifyApiPathDomainServiceTest {

    public static final String ENVIRONMENT_ID = "environment-id";

    @Mock
    ApiQueryService apiSearchService;

    @Mock
    InstallationAccessQueryService installationAccessQueryService;

    VerifyApiPathDomainService service;

    public static Stream<Arguments> sanitizePathParams() {
        return Stream.of(
            Arguments.of(null, "/"),
            Arguments.of("", "/"),
            Arguments.of("path", "/path/"),
            Arguments.of("/path", "/path/"),
            Arguments.of("path/", "/path/"),
            Arguments.of("//path/", "/path/")
        );
    }

    public static Stream<Arguments> sanitizeHostParams() {
        return Stream.of(
            Arguments.of(null, "domain.com"),
            Arguments.of("domain.com:123", "domain.com:123"),
            Arguments.of("domain.net", "domain.net"),
            Arguments.of("sub.domain.com", "sub.domain.com"),
            Arguments.of("lower.sub.domain.net:443", "lower.sub.domain.net:443")
        );
    }

    @BeforeEach
    void setup() {
        service =
            new VerifyApiPathDomainService(apiSearchService, installationAccessQueryService, new ApiHostValidatorDomainServiceGoogleImpl());
    }

    @AfterEach
    void tearDown() {
        GraviteeContext.cleanContext();
    }

    @ParameterizedTest
    @MethodSource("sanitizePathParams")
    public void should_sanitize_path(String path, String expectedPath) {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, null);

        var res = service.checkAndSanitizeApiPaths(
            ENVIRONMENT_ID,
            "api-id",
            List.of(Path.builder().path(path).overrideAccess(true).build())
        );
        assertThat(res).hasSize(1);
        assertThat(res.get(0).getPath()).isEqualTo(expectedPath);
        assertThat(res.get(0).getHost()).isNull();
        assertThat(res.get(0).isOverrideAccess()).isTrue();
    }

    @Test
    public void should_throw_exception_if_path_is_invalid() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, null);

        var throwable = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().path("invalid>path").build()))
        );

        assertThat(throwable).isInstanceOf(InvalidPathsException.class);
    }

    @ParameterizedTest
    @MethodSource("sanitizeHostParams")
    public void should_set_domain_with_domain_restrictions(String host, String expectedHost) {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of("domain.com", "domain.net", "domain.com:123"));

        var res = service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().host(host).path("/path/").build()));

        assertThat(res).hasSize(1);
        assertThat(res.get(0).getPath()).isEqualTo("/path/");
        assertThat(res.get(0).getHost()).isEqualTo(expectedHost);
    }

    @ParameterizedTest
    @ValueSource(strings = { "wrong-domain.com", "not-same-port:8082" })
    public void should_throw_exception_if_domain_is_invalid(String host) {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of("domain.com", "domain.net", "not-same-port:1234"));

        var throwable = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().host(host).path("/path/").build()))
        );

        assertThat(throwable).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_throw_exception_if_duplicate_paths() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, null);

        var throwable = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(
                ENVIRONMENT_ID,
                null,
                List.of(Path.builder().path("/abc/").build(), Path.builder().path("/path/").build(), Path.builder().path("/path/").build())
            )
        );
        assertThat(throwable).isInstanceOf(InvalidPathsException.class);
        assertThat(throwable.getMessage()).contains("/path");
    }

    @Test
    public void should_throw_exception_if_path_already_covered_by_other_api_for_api_creation() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, null);

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV2WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of("", "/path/")))));

        var throwable = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, null, List.of(Path.builder().path("/path/").build()))
        );
        assertThat(throwable).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_throw_exception_if_path_already_used_by_api_v2() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, null);

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV2WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of("", "/path/")))));

        var throwable = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().path("/path/").build()))
        );
        assertThat(throwable).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_throw_exception_if_path_already_used_by_api_v2_with_default_host() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of("domain.com", "domain.net"));

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV2WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of("domain.com", "/path/")))));

        var pathAlreadyExistExceptionIfDefaultDomain = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().path("/path/").build()))
        );
        assertThat(pathAlreadyExistExceptionIfDefaultDomain).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_throw_exception_if_path_already_used_by_api_v2_with_host() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of("domain.com", "domain.net"));

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV2WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of("domain.com", "/path/")))));

        // If domain is not set, first domain of domain restriction is used
        var pathAlreadyExistExceptionIfDefaultDomain = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().host("domain.com").path("/path/").build()))
        );
        assertThat(pathAlreadyExistExceptionIfDefaultDomain).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_ignore_path_used_by_same_api_v2_with_host() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of("domain.com", "domain.net"));

        givenExistingApis(
            ENVIRONMENT_ID,
            Stream.of(buildApiV2WithPaths(ENVIRONMENT_ID, "api-id", List.of(Pair.of("domain.com", "/path/"))))
        );

        // Check should be ok if same path but different domain
        var res = service.checkAndSanitizeApiPaths(
            ENVIRONMENT_ID,
            "api-id",
            List.of(Path.builder().host("domain.com").path("/path/").build())
        );
        assertThat(res).hasSize(1);
        assertThat(res.get(0).getPath()).isEqualTo("/path/");
        assertThat(res.get(0).getHost()).isEqualTo("domain.com");
    }

    @Test
    public void should_throw_exception_if_path_is_subpath_of_another_api_v2() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of());

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV2WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of(null, "/path/")))));

        // If domain is not set, first domain of domain restriction is used
        var pathAlreadyExistExceptionIfDefaultDomain = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().path("/path/subpath").build()))
        );
        assertThat(pathAlreadyExistExceptionIfDefaultDomain).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_throw_exception_if_path_is_subpath_of_another_api_v2_with_host() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of("domain.com", "domain.net"));

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV2WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of("domain.com", "/path/")))));

        // If domain is not set, first domain of domain restriction is used
        var pathAlreadyExistExceptionIfDefaultDomain = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(
                ENVIRONMENT_ID,
                "api-id",
                List.of(Path.builder().host("domain.com").path("/path/subpath").build())
            )
        );
        assertThat(pathAlreadyExistExceptionIfDefaultDomain).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_throw_exception_if_path_already_used_by_api_v4() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, null);

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV4WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of("", "/path/")))));

        var throwable = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().host("").path("/path/").build()))
        );
        assertThat(throwable).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_ignore_path_already_used_by_same_api_v4() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, null);

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV4WithPaths(ENVIRONMENT_ID, "api-id", List.of(Pair.of("", "/path/")))));

        var res = service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().path("/path/").build()));
        assertThat(res).hasSize(1);
        assertThat(res.get(0).getPath()).isEqualTo("/path/");
        assertThat(res.get(0).getHost()).isNullOrEmpty();
    }

    @Test
    public void should_throw_exception_if_path_already_used_by_api_v4_with_default_host() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of("domain.com", "domain.net"));

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV4WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of("domain.com", "/path/")))));

        var pathAlreadyExistExceptionIfDefaultDomain = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().path("/path/").build()))
        );
        assertThat(pathAlreadyExistExceptionIfDefaultDomain).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_throw_exception_if_path_already_used_by_api_v4_with_host() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of("domain.com", "domain.net"));

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV4WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of("domain.com", "/path/")))));

        // If domain is not set, first domain of domain restriction is used
        var pathAlreadyExistExceptionIfDefaultDomain = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().host("domain.com").path("/path/").build()))
        );
        assertThat(pathAlreadyExistExceptionIfDefaultDomain).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_ignore_path_used_by_same_api_v4_with_host() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of("domain.com", "domain.net"));

        givenExistingApis(
            ENVIRONMENT_ID,
            Stream.of(buildApiV4WithPaths(ENVIRONMENT_ID, "api-id", List.of(Pair.of("domain.com", "/path/"))))
        );

        // Check should be ok if same path but different domain
        var res = service.checkAndSanitizeApiPaths(
            ENVIRONMENT_ID,
            "api-id",
            List.of(Path.builder().host("domain.com").path("/path/").build())
        );
        assertThat(res).hasSize(1);
        assertThat(res.get(0).getPath()).isEqualTo("/path/");
        assertThat(res.get(0).getHost()).isEqualTo("domain.com");
    }

    @Test
    public void should_throw_exception_if_path_is_subpath_of_another_api_v4() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of());

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV4WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of(null, "/path/")))));

        // If domain is not set, first domain of domain restriction is used
        var pathAlreadyExistExceptionIfDefaultDomain = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(ENVIRONMENT_ID, "api-id", List.of(Path.builder().path("/path/subpath").build()))
        );
        assertThat(pathAlreadyExistExceptionIfDefaultDomain).isInstanceOf(InvalidPathsException.class);
    }

    @Test
    public void should_throw_exception_if_path_is_subpath_of_another_api_v4_with_host() {
        givenExistingRestrictedDomains(ENVIRONMENT_ID, List.of("domain.com", "domain.net"));

        givenExistingApis(ENVIRONMENT_ID, Stream.of(buildApiV4WithPaths(ENVIRONMENT_ID, "api1", List.of(Pair.of("domain.com", "/path/")))));

        // If domain is not set, first domain of domain restriction is used
        var pathAlreadyExistExceptionIfDefaultDomain = catchThrowable(() ->
            service.checkAndSanitizeApiPaths(
                ENVIRONMENT_ID,
                "api-id",
                List.of(Path.builder().host("domain.com").path("/path/subpath").build())
            )
        );
        assertThat(pathAlreadyExistExceptionIfDefaultDomain).isInstanceOf(InvalidPathsException.class);
    }

    private void givenExistingRestrictedDomains(String environmentId, List<String> domainRestrictions) {
        lenient().when(installationAccessQueryService.getGatewayRestrictedDomains(any())).thenReturn(List.of());
        lenient()
            .when(installationAccessQueryService.getGatewayRestrictedDomains(eq(environmentId)))
            .thenReturn(
                domainRestrictions != null
                    ? domainRestrictions.stream().map(domain -> RestrictedDomain.builder().domain(domain).build()).toList()
                    : List.of()
            );
    }

    private void givenExistingApis(String environmentId, Stream<Api> apis) {
        lenient()
            .when(
                apiSearchService.search(
                    eq(ApiSearchCriteria.builder().environmentId(environmentId).build()),
                    eq(null),
                    eq(ApiFieldFilter.builder().pictureExcluded(true).build())
                )
            )
            .thenReturn(apis);
    }

    @SneakyThrows
    private Api buildApiV2WithPaths(String environmentId, String apiId, List<Pair<String, String>> paths) {
        io.gravitee.definition.model.Api apiDefV2 = new io.gravitee.definition.model.Api();
        Proxy proxy = new Proxy();
        proxy.setVirtualHosts(
            paths
                .stream()
                .map(p -> {
                    VirtualHost virtualHost = new VirtualHost();
                    virtualHost.setHost(p.getLeft());
                    virtualHost.setPath(p.getRight());
                    return virtualHost;
                })
                .collect(Collectors.toList())
        );
        apiDefV2.setProxy(proxy);

        return Api.builder().id(apiId).environmentId(environmentId).definitionVersion(DefinitionVersion.V2).apiDefinition(apiDefV2).build();
    }

    @SneakyThrows
    private Api buildApiV4WithPaths(String environmentId, String apiId, List<Pair<String, String>> paths) {
        io.gravitee.definition.model.v4.Api apiDefV4 = new io.gravitee.definition.model.v4.Api();

        HttpListener listener = HttpListener
            .builder()
            .paths(
                paths
                    .stream()
                    .map(p -> io.gravitee.definition.model.v4.listener.http.Path.builder().host(p.getLeft()).path(p.getRight()).build())
                    .collect(Collectors.toList())
            )
            .build();
        apiDefV4.setListeners(List.of(listener));
        apiDefV4.setId(apiId);

        return Api
            .builder()
            .id(apiId)
            .environmentId(environmentId)
            .definitionVersion(DefinitionVersion.V4)
            .apiDefinitionV4(apiDefV4)
            .build();
    }
}
