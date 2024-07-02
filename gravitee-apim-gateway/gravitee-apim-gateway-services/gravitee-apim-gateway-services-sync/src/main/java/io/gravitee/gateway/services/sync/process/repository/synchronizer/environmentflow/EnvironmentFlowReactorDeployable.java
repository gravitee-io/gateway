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
package io.gravitee.gateway.services.sync.process.repository.synchronizer.environmentflow;

import io.gravitee.gateway.reactive.reactor.environmentflow.ReactableEnvironmentFlow;
import io.gravitee.gateway.services.sync.process.common.model.EnvironmentFlowDeployable;
import io.gravitee.gateway.services.sync.process.common.model.SyncAction;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@Builder
@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class EnvironmentFlowReactorDeployable implements EnvironmentFlowDeployable {

    private String environmentFlowId;

    private ReactableEnvironmentFlow reactableEnvironmentFlow;

    private SyncAction syncAction;

    @Override
    public String id() {
        return environmentFlowId;
    }

    public String environmentFlowId() {
        if (environmentFlowId == null) {
            return reactableEnvironmentFlow.getId();
        }
        return environmentFlowId;
    }
}
