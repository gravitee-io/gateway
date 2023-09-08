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
package io.gravitee.apim.core.user.crud_service;

import io.gravitee.apim.core.user.model.BaseUserEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserCrudService {
    Optional<BaseUserEntity> findBaseUserById(String id);
    Set<BaseUserEntity> findBaseUsersByIds(List<String> userIds);
    BaseUserEntity getBaseUser(String id);
}
