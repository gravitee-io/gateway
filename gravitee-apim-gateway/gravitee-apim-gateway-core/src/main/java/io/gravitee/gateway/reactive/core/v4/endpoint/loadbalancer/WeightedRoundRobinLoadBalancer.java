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

import io.gravitee.gateway.reactive.core.v4.endpoint.ManagedEndpoint;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * This loadbalancer will choose one endpoint after another and verify that its remaining weight is higher than 0, otherwise it will pick the next one.
 * After a selection is made, the total remaining weight is decreased.
 *
 * For example, if you have the following two endpoints:
 * <ul>
 * <li>Endpoint 1 with a weight of 9</li>
 * <li>Endpoint 2 with a weight of 1</li>
 * </ul>
 * Endpoint 1 is selected 9 times out of 10, whereas Endpoint 2 is selected only 1 time out of 10.
 *
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class WeightedRoundRobinLoadBalancer extends WeightedLoadBalancer {

    final AtomicInteger counter = new AtomicInteger(0);

    public WeightedRoundRobinLoadBalancer(final List<ManagedEndpoint> endpoints) {
        super(endpoints);
        refresh();
    }

    @Override
    public void refresh() {
        super.refresh();
        counter.set(0);
    }

    @Override
    protected ManagedEndpoint getManagedEndpoint() {
        WeightDistributions currentWeightDistributions = weightDistributions.get();
        if (endpoints.size() != currentWeightDistributions.getDistributions().size()) {
            refresh();
        }

        WeightDistributions.WeightDistribution foundWeightDistribution = null;
        while (foundWeightDistribution == null) {
            if (currentWeightDistributions.tryResetRemaining()) {
                counter.set(0);
            }
            int position = counter.getAndIncrement();
            if (position >= currentWeightDistributions.getDistributions().size()) {
                counter.set(0);
                position = counter.getAndIncrement();
            }
            WeightDistributions.WeightDistribution weightDistribution = currentWeightDistributions.getDistributions().get(position);
            if (weightDistribution.getRemaining() > 0) {
                weightDistribution.setRemaining(weightDistribution.getRemaining() - 1);
                currentWeightDistributions.decreaseRemaining();
                foundWeightDistribution = weightDistribution;
            }
        }
        return endpoints.get(foundWeightDistribution.getPosition());
    }
}
