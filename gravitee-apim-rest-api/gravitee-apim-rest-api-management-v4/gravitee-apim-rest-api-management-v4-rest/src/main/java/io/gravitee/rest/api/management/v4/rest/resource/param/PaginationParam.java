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
package io.gravitee.rest.api.management.v4.rest.resource.param;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class PaginationParam {

    public static final String PAGE_QUERY_PARAM_NAME = "page";
    public static final String PER_PAGE_QUERY_PARAM_NAME = "perPage";

    private static final String PAGE_QUERY_PARAM_DEFAULT = "1";
    private static final String PER_PAGE_QUERY_PARAM_DEFAULT = "10";

    @DefaultValue(PAGE_QUERY_PARAM_DEFAULT)
    @QueryParam(PAGE_QUERY_PARAM_NAME)
    Integer page;

    @DefaultValue(PER_PAGE_QUERY_PARAM_DEFAULT)
    @QueryParam(PER_PAGE_QUERY_PARAM_NAME)
    Integer perPage;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public void validate() {
        if (this.getPerPage() < 1) {
            throw new BadRequestException("Pagination perPage param must be >= 1");
        }

        if (this.getPage() < 1) {
            throw new BadRequestException("Pagination page param must be >= 1");
        }
    }
}
