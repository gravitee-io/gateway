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
package io.gravitee.definition.model.v4.listener.tcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.definition.model.v4.listener.Listener;
import io.gravitee.definition.model.v4.listener.ListenerType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TcpListenerV4")
@SuperBuilder(toBuilder = true)
public class TcpListener extends Listener {

    @JsonProperty(required = true)
    @Accessors(chain = true)
    private List<String> hosts;

    public TcpListener() {
        super(ListenerType.TCP);
    }

    protected TcpListener(TcpListenerBuilder<?, ?> b) {
        super(ListenerType.TCP, b);
        this.hosts = b.hosts;
    }
}
