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
package io.gravitee.repository.log.v4.model.message;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Builder(toBuilder = true)
public class MessageLog {

    private String apiId;
    private String requestId;
    private String timestamp;
    private String clientIdentifier;
    private String correlationId;
    private String parentCorrelationId;
    private String operation;
    private String connectorType;
    private String connectorId;
    private Message message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class Message {

        private String id;
        private boolean isError;
        private String payload;
        private Map<String, List<String>> headers;
        private Map<String, String> metadata;
    }
}
