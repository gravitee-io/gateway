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
package io.gravitee.definition.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.definition.model.endpoint.EndpointStatusListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EndpointTest {

    private String expectedJsonString = String.join(
        ",",
        "{\"name\":\"my-name\"",
        "\"target\":\"my-target\"",
        "\"weight\":1",
        "\"backup\":false",
        "\"tenants\":null",
        "\"type\":\"my-type\"",
        "\"inherit\":null",
        "\"healthcheck\":null}"
    );

    @Test
    void shouldSerializeEndpoint() throws JsonProcessingException {
        Endpoint endpoint = new Endpoint("my-type", "my-name", "my-target");

        String serializedEndpoint = new ObjectMapper().writeValueAsString(endpoint);

        Assertions.assertEquals(expectedJsonString, serializedEndpoint);
    }

    @Test
    void shouldSerializeEndpoint_withUnserializableStatusListener() throws JsonProcessingException {
        /* This simulates an EndpointStatusListener that jackson will fail to serialize
         * For serialization, jackson will call the getter that throws an exception
         */
        var unserializableStatusListener = new EndpointStatusListener() {
            public void onStatusChanged(Endpoint.Status s) {}

            public String getTestData() throws Exception {
                throw new Exception("Serialization fails");
            }
        };

        Endpoint endpoint = new Endpoint("my-type", "my-name", "my-target");
        endpoint.addEndpointStatusListener(unserializableStatusListener);

        String serializedEndpoint = new ObjectMapper().writeValueAsString(endpoint);

        Assertions.assertEquals(expectedJsonString, serializedEndpoint);
    }
}
