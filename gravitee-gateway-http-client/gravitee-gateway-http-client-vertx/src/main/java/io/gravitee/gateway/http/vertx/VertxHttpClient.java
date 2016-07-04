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
package io.gravitee.gateway.http.vertx;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.common.http.HttpHeadersValues;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.definition.model.HttpClientSslOptions;
import io.gravitee.definition.model.HttpProxy;
import io.gravitee.definition.model.Proxy;
import io.gravitee.gateway.api.ClientRequest;
import io.gravitee.gateway.api.ClientResponse;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.handler.Handler;
import io.gravitee.gateway.http.core.client.AbstractHttpClient;
import io.netty.channel.ConnectTimeoutException;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
public class VertxHttpClient extends AbstractHttpClient {

    private final Logger LOGGER = LoggerFactory.getLogger(VertxHttpClient.class);

    private HttpClient httpClient;

    @Resource
    private Vertx vertx;

    @Override
    public ClientRequest request(String host, int port, io.gravitee.common.http.HttpMethod method, String requestURI, Request serverRequest, Handler<ClientResponse> responseHandler) {
        HttpClientRequest clientRequest = httpClient.request(
                convert(method),
                port,
                host,
                requestURI,
                clientResponse -> handleClientResponse(clientResponse, responseHandler));

        clientRequest.setTimeout(api.getProxy().getHttpClient().getOptions().getReadTimeout());

        VertxClientRequest invokerRequest = new VertxClientRequest(clientRequest);

        clientRequest.exceptionHandler(event -> {
            LOGGER.error("{} server proxying failed: {}", serverRequest.id(), event.getMessage());

            if (invokerRequest.connectTimeoutHandler() != null && event instanceof ConnectTimeoutException) {
                invokerRequest.connectTimeoutHandler().handle(event);
            } else {
                VertxClientResponse clientResponse = new VertxClientResponse(
                        ((event instanceof ConnectTimeoutException) || (event instanceof TimeoutException)) ?
                        HttpStatusCode.GATEWAY_TIMEOUT_504 : HttpStatusCode.BAD_GATEWAY_502);

                clientResponse.headers().set(HttpHeaders.CONNECTION, HttpHeadersValues.CONNECTION_CLOSE);

                Buffer buffer = null;

                if (event.getMessage() != null) {
                    // Create body content with error message
                    buffer = Buffer.buffer(event.getMessage());
                    clientResponse.headers().set(HttpHeaders.CONTENT_LENGTH, Integer.toString(buffer.length()));
                }

                responseHandler.handle(clientResponse);

                if (buffer != null) {
                    clientResponse.bodyHandler().handle(buffer);
                }

                clientResponse.endHandler().handle(null);
            }
        });

        // Copy headers to final API
        copyRequestHeaders(serverRequest, clientRequest, host);

        // Check chunk flag on the request if there are some content to push and if transfer_encoding is set
        // with chunk value
        if (hasContent(serverRequest)) {
            String transferEncoding = serverRequest.headers().getFirst(HttpHeaders.TRANSFER_ENCODING);
            if (HttpHeadersValues.TRANSFER_ENCODING_CHUNKED.equalsIgnoreCase(transferEncoding)) {
                clientRequest.setChunked(true);
            }
        }

        // Send HTTP head as soon as possible
        clientRequest.sendHead();

        return invokerRequest;
    }

    private void handleClientResponse(HttpClientResponse clientResponse,
                                      Handler<ClientResponse> clientResponseHandler) {
        VertxClientResponse proxyClientResponse = new VertxClientResponse(
                clientResponse.statusCode());

        // Copy HTTP headers
        clientResponse.headers().forEach(header ->
                proxyClientResponse.headers().add(header.getKey(), header.getValue()));

        // Copy body content
        clientResponse.handler(event -> proxyClientResponse.bodyHandler().handle(Buffer.buffer(event.getBytes())));

        // Signal end of the response
        clientResponse.endHandler(v -> proxyClientResponse.endHandler().handle(null));

        clientResponseHandler.handle(proxyClientResponse);
    }

    protected void copyRequestHeaders(Request clientRequest, HttpClientRequest httpClientRequest, String host) {
        for (Map.Entry<String, List<String>> headerValues : clientRequest.headers().entrySet()) {
            String headerName = headerValues.getKey();
            String lowerHeaderName = headerName.toLowerCase(Locale.ENGLISH);

            // Remove hop-by-hop headers.
            if (HOP_HEADERS.contains(lowerHeaderName)) {
                continue;
            }

            for (String headerValue : headerValues.getValue()) {
                httpClientRequest.putHeader(headerName, headerValue);
            }
        }

        httpClientRequest.putHeader(HttpHeaders.HOST, host);
    }

    @Override
    protected void doStart() throws Exception {
        LOGGER.info("Starting HTTP Client for API {}", api);

        initialize(api.getProxy());
    }

    private HttpMethod convert(io.gravitee.common.http.HttpMethod httpMethod) {
        switch (httpMethod) {
            case CONNECT:
                return HttpMethod.CONNECT;
            case DELETE:
                return HttpMethod.DELETE;
            case GET:
                return HttpMethod.GET;
            case HEAD:
                return HttpMethod.HEAD;
            case OPTIONS:
                return HttpMethod.OPTIONS;
            case PATCH:
                return HttpMethod.PATCH;
            case POST:
                return HttpMethod.POST;
            case PUT:
                return HttpMethod.PUT;
            case TRACE:
                return HttpMethod.TRACE;
        }

        return null;
    }

    @Override
    protected void doStop() throws Exception {
        LOGGER.info("Close Vert.x HTTP Client for {}", api);
        httpClient.close();
    }

    private void initialize(Proxy proxyDefinition) {
        Objects.requireNonNull(proxyDefinition, "Proxy must not be null");

        LOGGER.info("Initializing Vert.x HTTP Client with {}", proxyDefinition.getHttpClient());

        HttpClientOptions options = new HttpClientOptions();

        options.setPipelining(proxyDefinition.getHttpClient().getOptions().isPipelining());
        options.setKeepAlive(proxyDefinition.getHttpClient().getOptions().isKeepAlive());
        options.setIdleTimeout((int) (proxyDefinition.getHttpClient().getOptions().getIdleTimeout() / 1000));
        options.setConnectTimeout((int) proxyDefinition.getHttpClient().getOptions().getConnectTimeout());
        options.setUsePooledBuffers(true);
        options.setMaxPoolSize(proxyDefinition.getHttpClient().getOptions().getMaxConcurrentConnections());
        options.setTryUseCompression(proxyDefinition.getHttpClient().getOptions().isUseCompression());

        // Configure proxy
        HttpProxy proxy = proxyDefinition.getHttpClient().getHttpProxy();
        if (proxy != null && proxy.isEnabled()) {
            ProxyOptions proxyOptions = new ProxyOptions();
            proxyOptions.setHost(proxy.getHost());
            proxyOptions.setPort(proxy.getPort());
            proxyOptions.setUsername(proxy.getUsername());
            proxyOptions.setPassword(proxy.getPassword());
            proxyOptions.setType(ProxyType.valueOf(proxy.getType().name()));

            options.setProxyOptions(proxyOptions);
        }

        // Configure SSL
        HttpClientSslOptions sslOptions = proxyDefinition.getHttpClient().getSsl();
        if (sslOptions != null && sslOptions.isEnabled()) {
            options
                    .setSsl(sslOptions.isEnabled())
                    .setVerifyHost(sslOptions.isHostnameVerifier())
                    .setTrustAll(sslOptions.isTrustAll());

            if (sslOptions.getPem() != null && ! sslOptions.getPem().isEmpty()) {
                options.setPemTrustOptions(
                        new PemTrustOptions().addCertValue(
                                io.vertx.core.buffer.Buffer.buffer(sslOptions.getPem())));
            }
        }

        httpClient = vertx.createHttpClient(options);

        LOGGER.info("Vert.x HTTP Client created {}", httpClient);
    }
}
