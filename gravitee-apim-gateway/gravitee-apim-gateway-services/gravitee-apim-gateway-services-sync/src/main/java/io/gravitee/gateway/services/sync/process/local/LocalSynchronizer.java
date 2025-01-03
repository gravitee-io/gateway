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
package io.gravitee.gateway.services.sync.process.local;

import io.reactivex.rxjava3.core.Completable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchService;

/**
 * @author GraviteeSource Team
 */
public interface LocalSynchronizer {
    Completable synchronize(final File localRegistryDir);
    Completable watch(final Path localRegistryPath, final WatchService watcher);
}
