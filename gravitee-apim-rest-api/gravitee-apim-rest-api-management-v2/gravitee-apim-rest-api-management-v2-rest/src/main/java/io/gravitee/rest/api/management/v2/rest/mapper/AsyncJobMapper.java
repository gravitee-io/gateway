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
package io.gravitee.rest.api.management.v2.rest.mapper;

import io.gravitee.rest.api.management.v2.rest.model.AsyncJob;
import io.gravitee.rest.api.management.v2.rest.model.AsyncJobStatus;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper(uses = { DateMapper.class })
public interface AsyncJobMapper {
    Logger logger = LoggerFactory.getLogger(AsyncJobMapper.class);
    AsyncJobMapper INSTANCE = Mappers.getMapper(AsyncJobMapper.class);

    AsyncJob map(io.gravitee.apim.core.async_job.model.AsyncJob source);
    List<AsyncJob> map(List<io.gravitee.apim.core.async_job.model.AsyncJob> source);

    io.gravitee.apim.core.async_job.model.AsyncJob.Type map(AsyncJob.TypeEnum source);
    io.gravitee.apim.core.async_job.model.AsyncJob.Status map(AsyncJobStatus source);
}
