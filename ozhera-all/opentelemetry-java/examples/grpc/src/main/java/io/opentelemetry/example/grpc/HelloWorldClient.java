/*
 * Copyright 2015 The gRPC Authors
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.example.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HelloWorldClient {
  private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());
  private final ManagedChannel channel;
  private final String serverHostname;
  private final Integer serverPort;
  private final GreeterGrpc.GreeterBlockingStub blockingStub;

  // it is important to initialize the OpenTelemetry SDK as early as possible in your application's
  // lifecycle.
  private static final OpenTelemetry openTelemetry = ExampleConfiguration.initOpenTelemetry();

  // OTel Tracing API
  private final Tracer tracer =
      openTelemetry.getTracer("io.opentelemetry.example.HelloWorldClient");
  // Share context via text headers
  private final TextMapPropagator textFormat =
      openTelemetry.getPropagators().getTextMapPropagator();
  // Inject context into the gRPC request metadata
  private final TextMapSetter<Metadata> setter =
      (carrier, key, value) ->
          carrier.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value);

  /** Construct client connecting to HelloWorld server at {@code host:port}. */
  public HelloWorldClient(String host, int port) {
    this.serverHostname = host;
    this.serverPort = port;
    this.channel =
        ManagedChannelBuilder.forAddress(host, port)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            // Intercept the request to tag the span context
            .intercept(new OpenTelemetryClientInterceptor())
            .build();
    blockingStub = GreeterGrpc.newBlockingStub(channel);
    // Initialize the OTel tracer
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /** Say hello to server. */
  public void greet(String name) {
    logger.info("Will try to greet " + name + " ...");

    // Start a span
    Span span =
        tracer.spanBuilder("helloworld.Greeter/SayHello").setSpanKind(SpanKind.CLIENT).startSpan();
    span.setAttribute("component", "grpc");
    span.setAttribute("rpc.service", "Greeter");
    span.setAttribute("net.peer.ip", this.serverHostname);
    span.setAttribute("net.peer.port", this.serverPort);

    // Set the context with the current span
    try (Scope scope = span.makeCurrent()) {
      HelloRequest request = HelloRequest.newBuilder().setName(name).build();
      try {
        HelloReply response = blockingStub.sayHello(request);
        logger.info("Greeting: " + response.getMessage());
      } catch (StatusRuntimeException e) {
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
        span.setStatus(StatusCode.ERROR, "gRPC status: " + e.getStatus());
      }
    } finally {
      span.end();
    }
  }

  public final class OpenTelemetryClientInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
      return new ForwardingClientCall.SimpleForwardingClientCall<>(
          channel.newCall(methodDescriptor, callOptions)) {
        @Override
        public void start(Listener<RespT> responseListener, Metadata headers) {
          // Inject the request with the current context
          textFormat.inject(Context.current(), headers, setter);
          // Perform the gRPC request
          super.start(responseListener, headers);
        }
      };
    }
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {
    // Access a service running on the local machine on port 50051
    HelloWorldClient client = new HelloWorldClient("localhost", 50051);
    try {
      String user = "World";
      // Use the arg as the name to greet if provided
      if (args.length > 0) {
        user = args[0];
      }
      for (int i = 0; i < 10; i++) {
        client.greet(user + " " + i);
      }
    } finally {
      client.shutdown();
    }
  }
}
