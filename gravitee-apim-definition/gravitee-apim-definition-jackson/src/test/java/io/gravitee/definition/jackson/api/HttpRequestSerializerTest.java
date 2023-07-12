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
package io.gravitee.definition.jackson.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.gravitee.definition.jackson.AbstractTest;
import io.gravitee.definition.model.HttpRequest;
import io.gravitee.definition.model.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public class HttpRequestSerializerTest extends AbstractTest {

    @Test
    public void definition_defaultHttpRequest() throws Exception {
        HttpRequest request = load("/io/gravitee/definition/jackson/httprequest-simpleheaders.json", HttpRequest.class);
        String expectedDefinition = "/io/gravitee/definition/jackson/httprequest-simpleheaders-expected.json";

        String generatedJsonDefinition = objectMapper().writeValueAsString(request);
        String expectedGeneratedJsonDefinition = IOUtils.toString(read(expectedDefinition));

        assertNotNull(generatedJsonDefinition);

        Assert.assertEquals(
            objectMapper().readTree(expectedGeneratedJsonDefinition.getBytes()),
            objectMapper().readTree(generatedJsonDefinition.getBytes())
        );
    }

    @Test
    public void definition_defaultHttpRequestMultiValueHeaders() throws Exception {
        HttpRequest request = load("/io/gravitee/definition/jackson/httprequest-multivalueheaders.json", HttpRequest.class);
        String expectedDefinition = "/io/gravitee/definition/jackson/httprequest-multivalueheaders-expected.json";

        String generatedJsonDefinition = objectMapper().writeValueAsString(request);
        String expectedGeneratedJsonDefinition = IOUtils.toString(read(expectedDefinition));

        assertNotNull(generatedJsonDefinition);
        assertTrue(generatedJsonDefinition.contains("[ \"deflate\", \"gzip\", \"compress\" ]"));

        Assert.assertEquals(
            objectMapper().readTree(expectedGeneratedJsonDefinition.getBytes()),
            objectMapper().readTree(generatedJsonDefinition.getBytes())
        );
    }
}
