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
package io.gravitee.rest.api.management.rest.resource;

import static io.gravitee.common.http.HttpStatusCode.BAD_REQUEST_400;
import static io.gravitee.common.http.HttpStatusCode.NOT_FOUND_404;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.repository.management.model.PageReferenceType;
import io.gravitee.rest.api.model.NewPageEntity;
import io.gravitee.rest.api.model.PageEntity;
import io.gravitee.rest.api.model.PageType;
import io.gravitee.rest.api.model.UpdatePageEntity;
import io.gravitee.rest.api.model.Visibility;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.service.common.GraviteeContext;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.junit.Test;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
public class PortalPagesResourceAdminTest extends AbstractResourceTest {

    private static final String PAGE_NAME = "p";

    @Override
    protected String contextPath() {
        return "portal/pages/";
    }

    @Test
    public void shouldNotGetPortalPageBecauseDoesNotBelongToApi() {
        reset(pageService);

        final PageEntity pageMock = new PageEntity();
        pageMock.setPublished(true);
        pageMock.setVisibility(Visibility.PUBLIC);
        pageMock.setName(PAGE_NAME);
        pageMock.setReferenceType(PageReferenceType.ENVIRONMENT.name());
        pageMock.setReferenceId("Another_environment");
        doReturn(pageMock).when(pageService).findById(PAGE_NAME, null);

        final Response response = envTarget(PAGE_NAME).request().get();

        assertEquals(NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void shouldNotCreateSystemFolder() {
        NewPageEntity newPageEntity = new NewPageEntity();
        newPageEntity.setType(PageType.SYSTEM_FOLDER);
        final Response response = envTarget().request().post(Entity.json(newPageEntity));

        assertEquals(BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void shouldNotDeleteSystemFolder() {
        reset(pageService);

        final PageEntity pageMock = new PageEntity();
        pageMock.setType("SYSTEM_FOLDER");
        pageMock.setReferenceType(PageReferenceType.ENVIRONMENT.name());
        pageMock.setReferenceId(GraviteeContext.getCurrentEnvironment());
        doReturn(pageMock).when(pageService).findById(PAGE_NAME);

        final Response response = envTarget(PAGE_NAME).request().delete();

        assertEquals(BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void shouldNotDeletePageBecauseDoesNotBelongToEnvironment() {
        reset(pageService);

        final PageEntity pageMock = new PageEntity();
        pageMock.setType("SYSTEM_FOLDER");
        pageMock.setReferenceType(PageReferenceType.ENVIRONMENT.name());
        pageMock.setReferenceId("Another_environment");
        doReturn(pageMock).when(pageService).findById(PAGE_NAME);

        final Response response = envTarget(PAGE_NAME).request().delete();

        assertEquals(NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void shouldNotUpdateSystemFolder() {
        reset(pageService);

        final PageEntity pageMock = new PageEntity();
        pageMock.setType("SYSTEM_FOLDER");
        pageMock.setReferenceType(PageReferenceType.ENVIRONMENT.name());
        pageMock.setReferenceId(GraviteeContext.getCurrentEnvironment());
        doReturn(pageMock).when(pageService).findById(PAGE_NAME);

        final Response response = envTarget(PAGE_NAME).request().put(Entity.json(new UpdatePageEntity()));

        assertEquals(BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void shouldNotUpdatePageBecauseDoesNotBelongToEnvironment() {
        reset(pageService);

        final PageEntity pageMock = new PageEntity();
        pageMock.setType("SYSTEM_FOLDER");
        pageMock.setReferenceType(PageReferenceType.ENVIRONMENT.name());
        pageMock.setReferenceId("Another_environment");
        doReturn(pageMock).when(pageService).findById(PAGE_NAME);

        final Response response = envTarget(PAGE_NAME).request().put(Entity.json(new UpdatePageEntity()));

        assertEquals(NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void shouldNotUpdatePatchSystemFolder() {
        reset(pageService);

        final PageEntity pageMock = new PageEntity();
        pageMock.setType("SYSTEM_FOLDER");
        pageMock.setReferenceType(PageReferenceType.ENVIRONMENT.name());
        pageMock.setReferenceId(GraviteeContext.getCurrentEnvironment());
        doReturn(pageMock).when(pageService).findById(PAGE_NAME);

        final Response response = envTarget(PAGE_NAME)
            .request()
            .method(jakarta.ws.rs.HttpMethod.PATCH, Entity.json(new UpdatePageEntity()));

        assertEquals(BAD_REQUEST_400, response.getStatus());
    }

    @Test
    public void shouldNotUpdatePatchPageBecauseDoesNotBelongToEnvironment() {
        reset(pageService);

        final PageEntity pageMock = new PageEntity();
        pageMock.setType("SYSTEM_FOLDER");
        pageMock.setReferenceType(PageReferenceType.ENVIRONMENT.name());
        pageMock.setReferenceId("Another_environment");
        doReturn(pageMock).when(pageService).findById(PAGE_NAME);

        final Response response = envTarget(PAGE_NAME)
            .request()
            .method(jakarta.ws.rs.HttpMethod.PATCH, Entity.json(new UpdatePageEntity()));

        assertEquals(NOT_FOUND_404, response.getStatus());
    }

    @Test
    public void shouldCreatePortalPage() {
        reset(pageService);

        NewPageEntity newPageEntity = new NewPageEntity();
        newPageEntity.setName("my-page-name");
        newPageEntity.setType(PageType.MARKDOWN);
        newPageEntity.setVisibility(Visibility.PUBLIC);

        PageEntity returnedPage = new PageEntity();
        returnedPage.setId("my-beautiful-page");
        doReturn(returnedPage).when(pageService).createPage(eq(GraviteeContext.getExecutionContext()), any());
        doReturn(0).when(pageService).findMaxPortalPageOrder(eq(GraviteeContext.getCurrentEnvironment()));

        final Response response = envTarget().request().post(Entity.json(newPageEntity));

        assertEquals(HttpStatusCode.CREATED_201, response.getStatus());
        assertEquals(envTarget().path("my-beautiful-page").getUri().toString(), response.getHeaders().getFirst(HttpHeaders.LOCATION));
    }
}
