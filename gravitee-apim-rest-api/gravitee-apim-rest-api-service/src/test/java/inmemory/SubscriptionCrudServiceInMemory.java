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
package inmemory;

import io.gravitee.apim.core.subscription.crud_service.SubscriptionCrudService;
import io.gravitee.apim.core.subscription.model.SubscriptionEntity;
import io.gravitee.rest.api.service.exceptions.SubscriptionNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

public class SubscriptionCrudServiceInMemory implements SubscriptionCrudService, InMemoryAlternative<SubscriptionEntity> {

    Storage<SubscriptionEntity> storage = new Storage<>();

    @Override
    public SubscriptionEntity get(String subscriptionId) {
        return storage
            .data()
            .stream()
            .filter(subscription -> subscriptionId.equals(subscription.getId()))
            .findFirst()
            .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId));
    }

    @Override
    public SubscriptionEntity update(SubscriptionEntity subscriptionEntity) {
        OptionalInt index = this.findIndex(this.storage.data(), subscription -> subscription.getId().equals(subscriptionEntity.getId()));
        if (index.isPresent()) {
            storage.data().set(index.getAsInt(), subscriptionEntity);
            return subscriptionEntity;
        }

        throw new IllegalStateException("Subscription not found");
    }

    @Override
    public void reset() {
        storage.clear();
    }

    @Override
    public Storage<SubscriptionEntity> storage() {
        return storage;
    }

    @Override
    public void syncStorageWith(InMemoryAlternative<SubscriptionEntity> other) {
        storage = other.storage();
    }
}
