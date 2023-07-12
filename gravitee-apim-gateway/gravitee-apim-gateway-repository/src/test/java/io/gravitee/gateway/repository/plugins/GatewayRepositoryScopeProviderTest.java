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
package io.gravitee.gateway.repository.plugins;

import io.gravitee.platform.repository.api.Scope;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author GraviteeSource Team
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
public class GatewayRepositoryScopeProviderTest {

    private final GatewayRepositoryScopeProvider provider = new GatewayRepositoryScopeProvider();

    @Test
    void should_return_handled_scopes() {
        Assertions.assertArrayEquals(new Scope[] { Scope.MANAGEMENT, Scope.RATE_LIMIT }, provider.getHandledScopes());
    }

    @Test
    void should_return_optional_handled_scopes() {
        Assertions.assertArrayEquals(new Scope[] { Scope.DISTRIBUTED_SYNC }, provider.getOptionalHandledScopes());
    }
}
