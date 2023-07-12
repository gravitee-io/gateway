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
package io.gravitee.gateway.reactive.core.v4.endpoint.loadbalancer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.gravitee.definition.model.v4.endpointgroup.loadbalancer.LoadBalancerType;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
class LoadBalancerStrategyFactoryTest {

    @Test
    void shouldReturnRandomStrategy() {
        LoadBalancerStrategy balancerStrategy = LoadBalancerStrategyFactory.create(LoadBalancerType.RANDOM, List.of());
        assertThat(balancerStrategy).isInstanceOf(RandomLoadBalancer.class);
    }

    @Test
    void shouldReturnRoundRobinStrategy() {
        LoadBalancerStrategy balancerStrategy = LoadBalancerStrategyFactory.create(LoadBalancerType.ROUND_ROBIN, List.of());
        assertThat(balancerStrategy).isInstanceOf(RoundRobinLoadBalancer.class);
    }

    @Test
    void shouldReturnWeightedRandomStrategy() {
        LoadBalancerStrategy balancerStrategy = LoadBalancerStrategyFactory.create(LoadBalancerType.WEIGHTED_RANDOM, List.of());
        assertThat(balancerStrategy).isInstanceOf(WeightedRandomLoadBalancer.class);
    }

    @Test
    void shouldReturnWeightedRoundRobinStrategy() {
        LoadBalancerStrategy balancerStrategy = LoadBalancerStrategyFactory.create(LoadBalancerType.WEIGHTED_ROUND_ROBIN, List.of());
        assertThat(balancerStrategy).isInstanceOf(WeightedRoundRobinLoadBalancer.class);
    }
}
