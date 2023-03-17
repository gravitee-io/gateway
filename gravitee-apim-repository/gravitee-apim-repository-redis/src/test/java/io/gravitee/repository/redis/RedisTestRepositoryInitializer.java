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
package io.gravitee.repository.redis;

import io.gravitee.repository.config.TestRepositoryInitializer;
import io.gravitee.repository.redis.vertx.RedisClient;
import io.vertx.redis.client.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author GraviteeSource Team
 */
public class RedisTestRepositoryInitializer implements TestRepositoryInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisTestRepositoryInitializer.class);
    private final RedisClient redisClient;

    @Autowired
    public RedisTestRepositoryInitializer(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public void setUp() {
        // Wait for RedisApi to be ready
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            LOGGER.error("Error while waiting for RedisApi to be ready", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void tearDown() {
        redisClient
            .getRedisApi()
            .send(Command.KEYS, "*")
            .onSuccess(event ->
                event
                    .iterator()
                    .forEachRemaining(key ->
                        redisClient
                            .getRedisApi()
                            .send(Command.DEL, key.toString())
                            .onFailure(t -> LOGGER.error("unable to delete key {}.", key, t))
                            .result()
                    )
            )
            .result();
    }
}
