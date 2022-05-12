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
package io.gravitee.gateway.handlers.api;

import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.Invoker;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.gateway.api.processor.ProcessorFailure;
import io.gravitee.gateway.api.proxy.ProxyResponse;
import io.gravitee.gateway.core.endpoint.lifecycle.GroupLifecycleManager;
import io.gravitee.gateway.core.invoker.EndpointInvoker;
import io.gravitee.gateway.core.processor.StreamableProcessor;
import io.gravitee.gateway.handlers.api.definition.Api;
import io.gravitee.gateway.handlers.api.processor.OnErrorProcessorChainFactory;
import io.gravitee.gateway.handlers.api.processor.RequestProcessorChainFactory;
import io.gravitee.gateway.handlers.api.processor.ResponseProcessorChainFactory;
import io.gravitee.gateway.policy.PolicyManager;
import io.gravitee.gateway.reactor.handler.AbstractReactorHandler;
import io.gravitee.gateway.resource.ResourceLifecycleManager;
import java.util.Map;
import java.util.Objects;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ApiReactorHandler extends AbstractReactorHandler<Api> {

    /**
     * Invoker is the connector to access the remote backend / endpoint.
     * If not override by a policy, default invoker is {@link EndpointInvoker}.
     */
    private Invoker invoker;

    private RequestProcessorChainFactory requestProcessorChain;

    private ResponseProcessorChainFactory responseProcessorChain;

    private OnErrorProcessorChainFactory errorProcessorChain;

    private ResourceLifecycleManager resourceLifecycleManager;

    private PolicyManager policyManager;

    private GroupLifecycleManager groupLifecycleManager;

    public ApiReactorHandler(final Api api) {
        super(api);
    }

    @Override
    protected void doHandle(final ExecutionContext context, Handler<ExecutionContext> endHandler) {
        final Request request = context.request();

        // Set the timeout handler on the request
        request.timeoutHandler(result -> handleError(context, endHandler, TIMEOUT_PROCESSOR_FAILURE));

        // Pause the request and resume it as soon as all the stream are plugged and we have processed the HEAD part
        // of the request. (see handleProxyInvocation method).
        request.pause();

        context.setAttribute(ExecutionContext.ATTR_CONTEXT_PATH, request.contextPath());
        context.setAttribute(ExecutionContext.ATTR_API, reactable.getId());
        context.setAttribute(ExecutionContext.ATTR_API_DEPLOYED_AT, reactable.getDeployedAt().getTime());
        context.setAttribute(ExecutionContext.ATTR_INVOKER, invoker);
        context.setAttribute(ExecutionContext.ATTR_ORGANIZATION, reactable.getOrganizationId());
        context.setAttribute(ExecutionContext.ATTR_ENVIRONMENT, reactable.getEnvironmentId());

        // Prepare request metrics
        request.metrics().setApi(reactable.getId());
        request.metrics().setPath(request.pathInfo());

        // It's time to process incoming client request
        handleClientRequest(context, endHandler);
    }

    private void handleClientRequest(final ExecutionContext context, Handler<ExecutionContext> endHandler) {
        final StreamableProcessor<ExecutionContext, Buffer> chain = requestProcessorChain.create();

        chain
            .handler(__ -> handleProxyInvocation(context, endHandler, chain))
            .streamErrorHandler(failure -> handleError(context, endHandler, failure))
            .errorHandler(failure -> handleError(context, endHandler, failure))
            .exitHandler(
                __ -> {
                    context.request().resume();
                    endHandler.handle(context);
                }
            )
            .handle(context);
    }

    private void handleProxyInvocation(
        final ExecutionContext context,
        Handler<ExecutionContext> endHandler,
        final StreamableProcessor<ExecutionContext, Buffer> chain
    ) {
        // Call an invoker to get a proxy connection (connection to an underlying backend, default to HTTP)
        Invoker upstreamInvoker = getInvoker(context);

        context.request().metrics().setApiResponseTimeMs(System.currentTimeMillis());

        upstreamInvoker.invoke(
            context,
            chain,
            connection -> {
                context.request().customFrameHandler(connection::writeCustomFrame);

                connection.responseHandler(proxyResponse -> handleProxyResponse(context, endHandler, proxyResponse));

                // Override the stream error handler to be able to cancel connection to backend
                chain.streamErrorHandler(
                    failure -> {
                        context
                            .request()
                            .metrics()
                            .setApiResponseTimeMs(System.currentTimeMillis() - context.request().metrics().getApiResponseTimeMs());
                        connection.cancel();
                        handleError(context, endHandler, failure);
                    }
                );
            }
        );

        // Plug server request stream to request processor stream
        context.request().bodyHandler(chain::write);

        if (context.request().ended()) {
            // since Vert.x 3.6.0 it can happen that requests without body (e.g. a GET) are ended even while in paused-State
            // Setting the endHandler would then lead to an Exception
            // see also https://github.com/eclipse-vertx/vert.x/issues/2763
            // so we now check if the request already is ended before installing an endHandler
            chain.end();
        } else {
            context.request().endHandler(result -> chain.end());
        }
    }

    protected Invoker getInvoker(ExecutionContext context) {
        return (Invoker) context.getAttribute(ExecutionContext.ATTR_INVOKER);
    }

    private void handleProxyResponse(
        final ExecutionContext context,
        Handler<ExecutionContext> endHandler,
        final ProxyResponse proxyResponse
    ) {
        // If the response is not yet ended (by a request timeout for example)
        if (!context.response().ended()) {
            if (proxyResponse == null || !proxyResponse.connected()) {
                context.response().status((proxyResponse == null) ? HttpStatusCode.SERVICE_UNAVAILABLE_503 : proxyResponse.status());
                context
                    .request()
                    .metrics()
                    .setApiResponseTimeMs(System.currentTimeMillis() - context.request().metrics().getApiResponseTimeMs());
                if (proxyResponse instanceof ProcessorFailure) {
                    handleError(context, endHandler, (ProcessorFailure) proxyResponse);
                } else {
                    endHandler.handle(context);
                }
            } else {
                handleClientResponse(context, endHandler, proxyResponse);
            }
        }
    }

    private void handleClientResponse(
        final ExecutionContext context,
        Handler<ExecutionContext> endHandler,
        final ProxyResponse proxyResponse
    ) {
        // Set the status
        context.response().status(proxyResponse.status());
        context.response().reason(proxyResponse.reason());

        // Copy HTTP headers
        proxyResponse.headers().forEach(entry -> context.response().headers().add(entry.getKey(), entry.getValue()));

        final StreamableProcessor<ExecutionContext, Buffer> chain = responseProcessorChain.create();

        // For HTTP/2, plug custom frame handler from upstream response to server response
        proxyResponse.customFrameHandler(frame -> context.response().writeCustomFrame(frame));

        chain
            .errorHandler(
                failure -> {
                    proxyResponse.cancel();
                    handleError(context, endHandler, failure);
                }
            )
            .streamErrorHandler(
                failure -> {
                    proxyResponse.cancel();
                    handleError(context, endHandler, failure);
                }
            )
            .exitHandler(__ -> endHandler.handle(context))
            .handler(
                stream -> {
                    chain.bodyHandler(chunk -> context.response().write(chunk)).endHandler(__ -> endHandler.handle(context));

                    proxyResponse
                        .bodyHandler(
                            buffer -> {
                                chain.write(buffer);

                                if (context.response().writeQueueFull()) {
                                    proxyResponse.pause();
                                    context.response().drainHandler(aVoid -> proxyResponse.resume());
                                }
                            }
                        )
                        .endHandler(
                            __ -> {
                                // Write trailers
                                final HttpHeaders trailers = proxyResponse.trailers();
                                if (trailers != null && !trailers.isEmpty()) {
                                    trailers.forEach((entry -> context.response().trailers().add(entry.getKey(), entry.getValue())));
                                }

                                context
                                    .request()
                                    .metrics()
                                    .setApiResponseTimeMs(System.currentTimeMillis() - context.request().metrics().getApiResponseTimeMs());

                                chain.end();
                            }
                        );

                    // Resume response read
                    proxyResponse.resume();
                }
            )
            .handle(context);
    }

    private void handleError(ExecutionContext context, Handler<ExecutionContext> endHandler, ProcessorFailure failure) {
        if (context.request().metrics().getApiResponseTimeMs() > Integer.MAX_VALUE) {
            context
                .request()
                .metrics()
                .setApiResponseTimeMs(System.currentTimeMillis() - context.request().metrics().getApiResponseTimeMs());
        }
        context.setAttribute(ExecutionContext.ATTR_PREFIX + "failure", failure);

        // Ensure we are consuming everything from the inbound queue
        if (!context.request().ended()) {
            context.request().bodyHandler(__ -> {});
            context.request().endHandler(__ -> {});
            context.request().resume();
        }

        errorProcessorChain
            .create()
            .handler(__ -> endHandler.handle(context))
            .errorHandler(__ -> endHandler.handle(context))
            .handle(context);
    }

    @Override
    protected void doStart() throws Exception {
        logger.debug("API handler is now starting, preparing API context...");
        long startTime = System.currentTimeMillis(); // Get the start Time
        super.doStart();

        // Start resources before
        resourceLifecycleManager.start();
        policyManager.start();
        groupLifecycleManager.start();

        dumpVirtualHosts();

        long endTime = System.currentTimeMillis(); // Get the end Time
        logger.debug("API handler started in {} ms", (endTime - startTime));
    }

    @Override
    protected void doStop() throws Exception {
        logger.debug("API handler is now stopping, closing context for {} ...", this);

        policyManager.stop();
        resourceLifecycleManager.stop();
        groupLifecycleManager.stop();

        super.doStop();
        logger.debug("API handler is now stopped: {}", this);
    }

    @Override
    public String toString() {
        return "Handler API id[" + reactable.getId() + "] name[" + reactable.getName() + "] version[" + reactable.getVersion() + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiReactorHandler that = (ApiReactorHandler) o;
        return reactable.equals(that.reactable);
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public void setRequestProcessorChain(RequestProcessorChainFactory requestProcessorChain) {
        this.requestProcessorChain = requestProcessorChain;
    }

    public void setResponseProcessorChain(ResponseProcessorChainFactory responseProcessorChain) {
        this.responseProcessorChain = responseProcessorChain;
    }

    public void setErrorProcessorChain(OnErrorProcessorChainFactory errorProcessorChain) {
        this.errorProcessorChain = errorProcessorChain;
    }

    public void setResourceLifecycleManager(ResourceLifecycleManager resourceLifecycleManager) {
        this.resourceLifecycleManager = resourceLifecycleManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public void setGroupLifecycleManager(GroupLifecycleManager groupLifecycleManager) {
        this.groupLifecycleManager = groupLifecycleManager;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reactable);
    }

    private static final ProcessorFailure TIMEOUT_PROCESSOR_FAILURE = new ProcessorFailure() {
        private static final String REQUEST_TIMEOUT = "REQUEST_TIMEOUT";

        @Override
        public int statusCode() {
            return HttpStatusCode.GATEWAY_TIMEOUT_504;
        }

        @Override
        public String message() {
            return "Request timeout";
        }

        @Override
        public String key() {
            return REQUEST_TIMEOUT;
        }

        @Override
        public Map<String, Object> parameters() {
            return null;
        }

        @Override
        public String contentType() {
            return null;
        }
    };
}
