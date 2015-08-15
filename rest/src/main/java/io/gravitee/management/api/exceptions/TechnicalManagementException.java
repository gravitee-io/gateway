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
package io.gravitee.management.api.exceptions;

import javax.ws.rs.core.Response;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
public class TechnicalManagementException extends AbstractManagementException {

    public TechnicalManagementException() {
    }

    public TechnicalManagementException(Throwable cause) {
        super(cause);
    }

    public TechnicalManagementException(String message) {
        super(message);
    }

    public TechnicalManagementException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public Response.Status getHttpStatusCode() {
        return Response.Status.INTERNAL_SERVER_ERROR;
    }
}
