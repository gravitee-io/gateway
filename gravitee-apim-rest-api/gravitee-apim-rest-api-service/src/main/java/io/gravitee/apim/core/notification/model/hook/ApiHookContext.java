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
package io.gravitee.apim.core.notification.model.hook;

import io.gravitee.rest.api.service.notification.ApiHook;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class ApiHookContext extends AbstractHookContext {

    private final ApiHook hook;

    private final String apiId;

    public ApiHookContext(ApiHook hook, String apiId) {
        this.hook = hook;
        this.apiId = apiId;
    }

    public Map<HookContextEntry, String> getProperties() {
        var props = new HashMap<>(getChildProperties());
        props.put(HookContextEntry.API_ID, apiId);
        return props;
    }
}
