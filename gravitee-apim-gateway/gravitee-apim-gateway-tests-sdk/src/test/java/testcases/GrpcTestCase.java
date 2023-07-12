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
package testcases;/**
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.gravitee.apim.gateway.tests.sdk.AbstractGrpcGatewayTest;
import io.gravitee.apim.gateway.tests.sdk.annotations.DeployApi;
import io.gravitee.apim.gateway.tests.sdk.annotations.GatewayTest;
import io.gravitee.gateway.grpc.helloworld.GreeterGrpc;
import io.gravitee.gateway.grpc.helloworld.HelloReply;
import io.gravitee.gateway.grpc.helloworld.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import io.vertx.grpc.VertxServer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@GatewayTest
@DeployApi({ "/apis/grpc/hello-world.json" })
@EnableForGatewayTestingExtensionTesting
public class GrpcTestCase extends AbstractGrpcGatewayTest {

    @Test
    void should_request_and_get_response() throws InterruptedException {
        GreeterGrpc.GreeterImplBase service = buildRPCService();

        VertxServer rpcServer = createRpcServer(service);

        // Wait for result
        CountDownLatch latch = new CountDownLatch(1);

        // Prepare gRPC Client
        ManagedChannel channel = createManagedChannel();

        // Start is asynchronous
        rpcServer.start(event -> {
            // Get a stub to use for interacting with the remote service
            GreeterGrpc.GreeterStub stub = GreeterGrpc.newStub(channel);

            HelloRequest request = HelloRequest.newBuilder().setName("You").build();

            // Call the remote service
            stub.sayHello(
                request,
                new StreamObserver<>() {
                    private HelloReply helloReply;

                    @Override
                    public void onNext(HelloReply helloReply) {
                        this.helloReply = helloReply;
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        fail(throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        assertThat(helloReply).isNotNull();
                        assertThat(helloReply.getMessage()).isEqualTo("Hello You");

                        latch.countDown();
                    }
                }
            );
        });

        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
    }

    private static GreeterGrpc.GreeterImplBase buildRPCService() {
        return new GreeterGrpc.GreeterImplBase() {
            @Override
            public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
                responseObserver.onNext(HelloReply.newBuilder().setMessage("Hello " + request.getName()).build());
                responseObserver.onCompleted();
            }
        };
    }
}
