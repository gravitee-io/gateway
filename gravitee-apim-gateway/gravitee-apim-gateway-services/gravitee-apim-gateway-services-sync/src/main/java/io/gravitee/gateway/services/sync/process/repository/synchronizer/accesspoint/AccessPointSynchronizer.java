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
package io.gravitee.gateway.services.sync.process.repository.synchronizer.accesspoint;

import io.gravitee.gateway.services.sync.process.common.deployer.AccessPointDeployer;
import io.gravitee.gateway.services.sync.process.common.deployer.DeployerFactory;
import io.gravitee.gateway.services.sync.process.common.model.SyncAction;
import io.gravitee.gateway.services.sync.process.repository.RepositorySynchronizer;
import io.gravitee.gateway.services.sync.process.repository.fetcher.AccessPointFetcher;
import io.gravitee.repository.management.model.AccessPoint;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AccessPointSynchronizer implements RepositorySynchronizer {

    private final AccessPointFetcher accessPointFetcher;
    private final DeployerFactory deployerFactory;
    private final ThreadPoolExecutor syncFetcherExecutor;
    private final ThreadPoolExecutor syncDeployerExecutor;

    @Override
    public Completable synchronize(Long from, Long to, List<String> environments) {
        AtomicLong launchTime = new AtomicLong();
        return accessPointFetcher
            .fetchLatest(from, to, environments)
            .subscribeOn(Schedulers.from(syncFetcherExecutor))
            .rebatchRequests(syncFetcherExecutor.getMaximumPoolSize())
            .flatMap(accessPoints -> Flowable.fromStream(accessPoints.stream().map(this::prepareForDeployment)))
            .compose(upstream -> {
                AccessPointDeployer accessPointDeployer = deployerFactory.createAccessPointDeployer();
                return upstream
                    .parallel(syncDeployerExecutor.getMaximumPoolSize())
                    .runOn(Schedulers.from(syncDeployerExecutor))
                    .flatMap(deployable -> deploy(accessPointDeployer, deployable))
                    .sequential(accessPointFetcher.bulkItems());
            })
            .count()
            .doOnSubscribe(disposable -> launchTime.set(Instant.now().toEpochMilli()))
            .doOnSuccess(count -> {
                String logMsg = String.format(
                    "%s access points synchronized in %sms",
                    count,
                    (System.currentTimeMillis() - launchTime.get())
                );
                if (from == -1) {
                    log.info(logMsg);
                } else {
                    log.debug(logMsg);
                }
            })
            .ignoreElement();
    }

    private AccessPointDeployable prepareForDeployment(final AccessPoint accessPoint) {
        return AccessPointDeployable.builder().accessPoint(accessPoint).syncAction(SyncAction.DEPLOY).build();
    }

    private static Flowable<AccessPointDeployable> deploy(
        final AccessPointDeployer accessPointDeployer,
        final AccessPointDeployable deployable
    ) {
        return accessPointDeployer
            .deploy(deployable)
            .andThen(accessPointDeployer.doAfterDeployment(deployable))
            .andThen(Flowable.just(deployable))
            .onErrorResumeNext(throwable -> {
                log.error(throwable.getMessage(), throwable);
                return Flowable.empty();
            });
    }

    @Override
    public int order() {
        return 1;
    }
}
