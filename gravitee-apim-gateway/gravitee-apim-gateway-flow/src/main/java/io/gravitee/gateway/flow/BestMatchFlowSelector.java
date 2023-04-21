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
package io.gravitee.gateway.flow;

import io.gravitee.definition.model.flow.Flow;
import io.gravitee.definition.model.v4.ApiType;
import java.util.Optional;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@SuppressWarnings("java:S6548")
public class BestMatchFlowSelector extends AbstractBestMatchFlowSelector<Flow> {

    public static BestMatchFlowSelector instance() {
        return Holder.INSTANCE;
    }

    @Override
    protected Optional<String> providePath(ApiType apiType, Flow flow) {
        return flow != null ? Optional.ofNullable(flow.getPath()) : Optional.empty();
    }

    private static class Holder {

        private static final BestMatchFlowSelector INSTANCE = new BestMatchFlowSelector();
    }
}
