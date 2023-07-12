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
package io.gravitee.gateway.env;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public class RequestTimeoutConfiguration {

    public RequestTimeoutConfiguration(long requestTimeout, Long requestTimeoutGraceDelay) {
        this.requestTimeout = requestTimeout;
        this.requestTimeoutGraceDelay = requestTimeoutGraceDelay;
    }

    private long requestTimeout;
    private long requestTimeoutGraceDelay;

    public long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public long getRequestTimeoutGraceDelay() {
        return requestTimeoutGraceDelay;
    }

    public void setRequestTimeoutGraceDelay(long requestTimeoutGraceDelay) {
        this.requestTimeoutGraceDelay = requestTimeoutGraceDelay;
    }
}
