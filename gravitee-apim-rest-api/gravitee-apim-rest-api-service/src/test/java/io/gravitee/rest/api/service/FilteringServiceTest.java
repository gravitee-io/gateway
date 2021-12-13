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
package io.gravitee.rest.api.service;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.gravitee.rest.api.idp.api.authentication.UserDetails;
import io.gravitee.rest.api.model.SubscriptionEntity;
import io.gravitee.rest.api.model.SubscriptionStatus;
import io.gravitee.rest.api.model.TopApiEntity;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.model.api.ApiLifecycleState;
import io.gravitee.rest.api.model.application.ApplicationListItem;
import io.gravitee.rest.api.model.subscription.SubscriptionQuery;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.filtering.FilteringService;
import io.gravitee.rest.api.service.impl.filtering.FilteringServiceImpl;
import java.util.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class FilteringServiceTest {

    @InjectMocks
    private FilteringServiceImpl filteringService = new FilteringServiceImpl();

    @Mock
    SubscriptionService subscriptionService;

    @Mock
    RatingService ratingService;

    @Mock
    TopApiService topApiService;

    @Mock
    ApplicationService applicationService;

    private static Set<String> mockApis;

    @AfterClass
    public static void cleanSecurityContextHolder() {
        // reset authentication to avoid side effect during test executions.
        SecurityContextHolder.setContext(
            new SecurityContext() {
                @Override
                public Authentication getAuthentication() {
                    return null;
                }

                @Override
                public void setAuthentication(Authentication authentication) {}
            }
        );
    }

    @BeforeClass
    public static void initAllTest() {
        ApiEntity publishedApi1 = new ApiEntity();
        publishedApi1.setLifecycleState(ApiLifecycleState.PUBLISHED);
        publishedApi1.setName("1");
        publishedApi1.setId("1");

        ApiEntity unpublishedApi = new ApiEntity();
        unpublishedApi.setLifecycleState(ApiLifecycleState.UNPUBLISHED);
        unpublishedApi.setName("2");
        unpublishedApi.setId("2");

        ApiEntity publishedApi2 = new ApiEntity();
        publishedApi2.setLifecycleState(ApiLifecycleState.PUBLISHED);
        publishedApi2.setName("3");
        publishedApi2.setId("3");

        ApiEntity publishedApi3 = new ApiEntity();
        publishedApi3.setLifecycleState(ApiLifecycleState.PUBLISHED);
        publishedApi3.setName("4");
        publishedApi3.setId("4");

        ApiEntity publishedApi4 = new ApiEntity();
        publishedApi4.setLifecycleState(ApiLifecycleState.PUBLISHED);
        publishedApi4.setName("5");
        publishedApi4.setId("5");

        ApiEntity publishedApi5 = new ApiEntity();
        publishedApi5.setLifecycleState(ApiLifecycleState.PUBLISHED);
        publishedApi5.setName("6");
        publishedApi5.setId("6");

        mockApis =
            new HashSet<>(
                Arrays.asList(
                    publishedApi5.getId(),
                    publishedApi2.getId(),
                    publishedApi1.getId(),
                    publishedApi3.getId(),
                    publishedApi4.getId()
                )
            );
    }

    @Test
    public void shouldNotGetMineApi() {
        Collection<String> apiEntityFilteredEntities = filteringService.filterApis(mockApis, FilteringService.FilterType.MINE, null);

        Collection<String> filteredItems = apiEntityFilteredEntities;
        assertEquals(0, filteredItems.size());
    }

    @Test
    public void shouldGetMineApi() {
        final Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new UserDetails("user", "", emptyList()));
        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ApplicationListItem appA = new ApplicationListItem();
        appA.setId("A");
        ApplicationListItem appB = new ApplicationListItem();
        appB.setId("B");
        ApplicationListItem appC = new ApplicationListItem();
        appC.setId("C");
        doReturn(new HashSet<ApplicationListItem>(Arrays.asList(appC, appB, appA)))
            .when(applicationService)
            .findByUser(eq(GraviteeContext.getCurrentOrganization()), eq(GraviteeContext.getCurrentEnvironment()), any());

        SubscriptionEntity subA1 = new SubscriptionEntity();
        subA1.setApplication("A");
        subA1.setApi("1");
        SubscriptionEntity subA2 = new SubscriptionEntity();
        subA2.setApplication("A");
        subA2.setApi("2");
        SubscriptionEntity subB1 = new SubscriptionEntity();
        subB1.setApplication("B");
        subB1.setApi("1");
        SubscriptionEntity subC4 = new SubscriptionEntity();
        subC4.setApplication("C");
        subC4.setApi("4");
        SubscriptionEntity subC8 = new SubscriptionEntity();
        subC8.setApplication("C");
        subC8.setApi("8");
        doReturn(Arrays.asList(subC8, subA2, subB1, subC4, subA1)).when(subscriptionService).search(any());

        Collection<String> filteredItems = filteringService.filterApis(mockApis, FilteringService.FilterType.MINE, null);

        assertEquals(2, filteredItems.size());
        assertEquals(Arrays.asList("1", "4"), filteredItems);
    }

    @Test
    public void shouldNotGetStarredApi() {
        doReturn(false).when(ratingService).isEnabled();

        Collection<String> filteredItems = filteringService.filterApis(mockApis, FilteringService.FilterType.STARRED, null);

        assertEquals(0, filteredItems.size());
    }

    @Test
    public void shouldGetStarredApi() {
        Set<String> apis = new LinkedHashSet<>(Arrays.asList("5", "4", "2", "1"));
        doReturn(true).when(ratingService).isEnabled();
        doReturn(new LinkedHashSet<>(Arrays.asList("3", "4", "1", "5"))).when(ratingService).findReferenceIdsOrderByRate(apis);

        Collection<String> filteredItems = filteringService.filterApis(apis, FilteringService.FilterType.STARRED, null);

        assertEquals(5, filteredItems.size());
        assertEquals(new LinkedHashSet<>(Arrays.asList("3", "4", "1", "5", "2")), filteredItems);
    }

    @Test
    public void shouldGetStarredApiExcluded() {
        Set<String> apis = new LinkedHashSet<>(Arrays.asList("5", "4", "2", "1"));
        doReturn(true).when(ratingService).isEnabled();
        doReturn(new LinkedHashSet<>(Arrays.asList("3", "4", "1", "5"))).when(ratingService).findReferenceIdsOrderByRate(apis);

        Collection<String> filteredItems = filteringService.filterApis(apis, null, FilteringService.FilterType.STARRED);

        assertEquals(1, filteredItems.size());
        assertEquals(new LinkedHashSet<>(Arrays.asList("2")), filteredItems);
    }

    @Test
    public void shouldGetTrendingsApi() {
        Set<String> apis = new LinkedHashSet<>(Arrays.asList("5", "4", "2", "1"));

        SubscriptionQuery subscriptionQuery = new SubscriptionQuery();
        subscriptionQuery.setStatuses(Arrays.asList(SubscriptionStatus.ACCEPTED, SubscriptionStatus.PAUSED));
        subscriptionQuery.setApis(apis);
        doReturn(new LinkedHashSet<>(Arrays.asList("1", "4")))
            .when(subscriptionService)
            .findReferenceIdsOrderByNumberOfSubscriptions(subscriptionQuery);

        Collection<String> filteredItems = filteringService.filterApis(apis, FilteringService.FilterType.TRENDINGS, null);

        assertEquals(4, filteredItems.size());
        assertEquals(new LinkedHashSet<>(Arrays.asList("1", "4", "5", "2")), filteredItems);
    }

    @Test
    public void shouldGetTrendingsApiExcluded() {
        Set<String> apis = new LinkedHashSet<>(Arrays.asList("5", "4", "2", "1"));

        SubscriptionQuery subscriptionQuery = new SubscriptionQuery();
        subscriptionQuery.setStatuses(Arrays.asList(SubscriptionStatus.ACCEPTED, SubscriptionStatus.PAUSED));
        subscriptionQuery.setApis(apis);
        doReturn(new LinkedHashSet<>(Arrays.asList("1", "4")))
            .when(subscriptionService)
            .findReferenceIdsOrderByNumberOfSubscriptions(subscriptionQuery);

        Collection<String> filteredItems = filteringService.filterApis(apis, null, FilteringService.FilterType.TRENDINGS);

        assertEquals(2, filteredItems.size());
        assertEquals(new LinkedHashSet<>(Arrays.asList("5", "2")), filteredItems);
    }

    @Test
    public void shouldGetFeaturedApis() {
        TopApiEntity topApi5 = new TopApiEntity();
        topApi5.setApi("5");
        topApi5.setOrder(2);
        TopApiEntity topApi6 = new TopApiEntity();
        topApi6.setApi("6");
        topApi6.setOrder(1);
        doReturn(Arrays.asList(topApi5, topApi6)).when(topApiService).findAll();

        Collection<String> filteredItems = filteringService.filterApis(mockApis, FilteringService.FilterType.FEATURED, null);

        assertEquals(2, filteredItems.size());
        assertEquals(Arrays.asList("6", "5"), filteredItems);
    }

    @Test
    public void shouldGetFeaturedApisExcluded() {
        TopApiEntity topApi5 = new TopApiEntity();
        topApi5.setApi("5");
        topApi5.setOrder(2);
        TopApiEntity topApi6 = new TopApiEntity();
        topApi6.setApi("6");
        topApi6.setOrder(1);
        doReturn(Arrays.asList(topApi5, topApi6)).when(topApiService).findAll();

        Collection<String> filteredItems = filteringService.filterApis(mockApis, null, FilteringService.FilterType.FEATURED);

        assertEquals(3, filteredItems.size());
        assertEquals(Arrays.asList("1", "3", "4"), filteredItems);
    }
}
