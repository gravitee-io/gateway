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
package io.gravitee.gateway.services.http;

import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.common.service.AbstractService;
import io.gravitee.gateway.services.http.configuration.HttpServerConfiguration;
import io.gravitee.gateway.services.http.handler.NodeHandler;
import io.gravitee.gateway.services.http.handler.apis.ApiHandler;
import io.gravitee.gateway.services.http.handler.apis.ApisHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.gravitee.gateway.services.http.handler.metrics.micrometer.prometheus.PrometheusMetricsHandler;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.micrometer.backends.BackendRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class HttpServer extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    private final static String PATH = "/_node";

    private final static String AUTHENTICATION_TYPE_NONE = "none";
    private final static String AUTHENTICATION_TYPE_BASIC = "basic";
    private final static String AUTHENTICATION_BASIC_REALM = "gravitee.io";

    @Autowired
    @Qualifier("vertxNodeHttpServer")
    private io.vertx.core.http.HttpServer httpServer;

    @Autowired
    private Router nodeRouter;

    @Autowired
    private Vertx vertx;

    @Autowired
    private Environment environment;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private HttpServerConfiguration httpServerConfiguration;

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        LOGGER.info("Start HTTP server for node management");

        // Start HTTP server
        Router mainRouter = Router.router(vertx).mountSubRouter(PATH, nodeRouter);

        AuthHandler authHandler = null;
        switch ( httpServerConfiguration.getAuthenticationType().toLowerCase() ) {
           case AUTHENTICATION_TYPE_NONE:
              break;
           case AUTHENTICATION_TYPE_BASIC:
              authHandler = BasicAuthHandler.create(authProvider, AUTHENTICATION_BASIC_REALM);
              break;
           default:
              throw new IllegalArgumentException("Unsupported Authentication type " + httpServerConfiguration.getAuthenticationType() + " for HTTP core services");
        }

        // Set security handler is defined
        if ( authHandler != null ) {
           mainRouter.route().handler(authHandler);
           nodeRouter.route().handler(authHandler);
        }

        // Set default handler
        mainRouter.route().handler(ctx -> ctx.fail(HttpStatusCode.NOT_FOUND_404));

        // Add request handler
        httpServer
                .requestHandler(mainRouter::accept)
                .listen(event -> {
                    if (event.failed()) {
                        LOGGER.error("HTTP server for node management can not be started properly", event.cause());
                    } else {
                        LOGGER.info("HTTP server for node management listening on port {}", event.result().actualPort());
                    }
                });


        // Set node handler
        NodeHandler nodeHandler = new NodeHandler();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(nodeHandler);
        nodeRouter.get("/").handler(nodeHandler);

        // Set APIs handler
        ApisHandler apisHandler = new ApisHandler();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(apisHandler);
        nodeRouter.get("/apis").handler(apisHandler);

        // Set API handler
        ApiHandler apiHandler = new ApiHandler();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(apiHandler);
        nodeRouter.get("/apis/:apiId").handler(apiHandler);

        // Metrics
        boolean metricsEnabled = environment.getProperty("services.metrics.enabled", Boolean.class, false);
        if (metricsEnabled) {

            // Set Prometheus handler
            boolean prometheusEnabled = environment.getProperty("services.metrics.prometheus.enabled", Boolean.class, true);
            if (prometheusEnabled) {
                PrometheusMetricsHandler prometheusMetricsHandler = new PrometheusMetricsHandler(
                        (PrometheusMeterRegistry) BackendRegistries.getDefaultNow());
                nodeRouter
                        .get("/metrics/prometheus")
                        .handler(prometheusMetricsHandler);
            }
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();

        httpServer.close(event -> LOGGER.info("HTTP server for node management has been stopped"));
    }

    @Override
    protected String name() {
        return "Gateway Services Manager";
    }
}
