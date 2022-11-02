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
package io.gravitee.plugin.endpoint.kafka.strategy.impl;

import io.gravitee.gateway.jupiter.api.context.ExecutionContext;
import io.gravitee.plugin.endpoint.kafka.configuration.KafkaEndpointConnectorConfiguration;
import io.gravitee.plugin.endpoint.kafka.factory.KafkaReceiverFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverOptions;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class BalancedStrategy extends AbstractQosStrategy {

    public BalancedStrategy(final KafkaReceiverFactory kafkaReceiverFactory) {
        super(kafkaReceiverFactory);
    }

    @Override
    public Flux<ConsumerRecord<String, byte[]>> receive(
        final ExecutionContext executionContext,
        final KafkaEndpointConnectorConfiguration configuration,
        final ReceiverOptions<String, byte[]> receiverOptions
    ) {
        return createReceiver(receiverOptions).receiveAutoAck().concatMap(c -> c);
    }

    @Override
    public Runnable buildAckRunnable(final ConsumerRecord<String, byte[]> consumerRecord) {
        return doNothing;
    }

    @Override
    public String generateId(final ConsumerRecord<String, byte[]> consumerRecord) {
        return null;
    }
}
