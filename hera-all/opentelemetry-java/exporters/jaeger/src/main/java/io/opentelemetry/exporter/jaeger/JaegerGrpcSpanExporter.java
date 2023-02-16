/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.exporter.jaeger;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.opentelemetry.api.internal.StringUtils;
import io.opentelemetry.exporter.jaeger.proto.api_v2.Collector;
import io.opentelemetry.exporter.jaeger.proto.api_v2.CollectorServiceGrpc;
import io.opentelemetry.exporter.jaeger.proto.api_v2.Model;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.internal.ThrottlingLogger;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.concurrent.ThreadSafe;

/** Exports spans to Jaeger via gRPC, using Jaeger's protobuf model. */
@ThreadSafe
public final class JaegerGrpcSpanExporter implements SpanExporter {

  private static final String IP_KEY = "ip";
  private static final String ENV_KEY = "service.env";
  private static final String ENV_ID_KEY = "service.env.id";
  private static final String IP_DEFAULT = "0.0.0.0";
  private static final String ENV_DEFAULT = "default_env";
  private final ThrottlingLogger logger =
      new ThrottlingLogger(Logger.getLogger(JaegerGrpcSpanExporter.class.getName()));

  private final CollectorServiceGrpc.CollectorServiceFutureStub stub;
  private final Model.Process.Builder processBuilder;
  private final ManagedChannel managedChannel;
  private final long timeoutNanos;

  /**
   * Creates a new Jaeger gRPC Span Reporter with the given name, using the given channel.
   *
   * @param channel the channel to use when communicating with the Jaeger Collector.
   * @param timeoutNanos max waiting time for the collector to process each span batch. When set to
   *     0 or to a negative value, the exporter will wait indefinitely.
   */
  JaegerGrpcSpanExporter(ManagedChannel channel, long timeoutNanos) {
    String ipv4;

    try {
      String ipv4Env = System.getenv("host.ip");
      if (!StringUtils.isNullOrEmpty(ipv4Env)) {
        ipv4 = ipv4Env;
      } else {
        ipv4 = InetAddress.getLocalHost().getHostAddress();
      }

    } catch (UnknownHostException e) {
      ipv4 = IP_DEFAULT;
    }
    // mifaas env name
    String env = System.getenv("MIONE_PROJECT_ENV_NAME");
    if (StringUtils.isNullOrEmpty(env)) {
      env = ENV_DEFAULT;
    }
    // env id
    String envId = System.getenv("MIONE_PROJECT_ENV_ID");
    if (envId == null) {
      envId = "";
    }
    Model.KeyValue ipv4Tag = Model.KeyValue.newBuilder().setKey(IP_KEY).setVStr(ipv4).build();
    Model.KeyValue envTag = Model.KeyValue.newBuilder().setKey(ENV_KEY).setVStr(env).build();
    Model.KeyValue envIdTag = Model.KeyValue.newBuilder().setKey(ENV_ID_KEY).setVStr(envId).build();
    this.processBuilder =
        Model.Process.newBuilder().addTags(ipv4Tag).addTags(envTag).addTags(envIdTag);
    this.managedChannel = channel;
    this.stub = CollectorServiceGrpc.newFutureStub(channel);
    this.timeoutNanos = timeoutNanos;
  }

  /**
   * Submits all the given spans in a single batch to the Jaeger collector.
   *
   * @param spans the list of sampled Spans to be exported.
   * @return the result of the operation
   */
  @Override
  public CompletableResultCode export(Collection<SpanData> spans) {
    CollectorServiceGrpc.CollectorServiceFutureStub stub = this.stub;
    if (timeoutNanos > 0) {
      stub = stub.withDeadlineAfter(timeoutNanos, TimeUnit.NANOSECONDS);
    }

    List<Collector.PostSpansRequest> requests = new ArrayList<>();
    spans.stream()
        .collect(Collectors.groupingBy(SpanData::getResource))
        .forEach((resource, spanData) -> requests.add(buildRequest(resource, spanData)));

    List<ListenableFuture<Collector.PostSpansResponse>> listenableFutures =
        new ArrayList<>(requests.size());
    for (Collector.PostSpansRequest request : requests) {
      listenableFutures.add(stub.postSpans(request));
    }

    final CompletableResultCode result = new CompletableResultCode();
    AtomicInteger pending = new AtomicInteger(listenableFutures.size());
    AtomicReference<Throwable> error = new AtomicReference<>();
    for (ListenableFuture<Collector.PostSpansResponse> future : listenableFutures) {
      Futures.addCallback(
          future,
          new FutureCallback<Collector.PostSpansResponse>() {
            @Override
            public void onSuccess(Collector.PostSpansResponse result) {
              fulfill();
            }

            @Override
            public void onFailure(Throwable t) {
              error.set(t);
              fulfill();
            }

            private void fulfill() {
              if (pending.decrementAndGet() == 0) {
                Throwable t = error.get();
                if (t != null) {
                  logger.log(Level.WARNING, "Failed to export spans", t);
                  result.fail();
                } else {
                  result.succeed();
                }
              }
            }
          },
          MoreExecutors.directExecutor());
    }
    return result;
  }

  private Collector.PostSpansRequest buildRequest(Resource resource, List<SpanData> spans) {
    Model.Process.Builder builder = this.processBuilder.clone();

    String serviceName = resource.getAttributes().get(ResourceAttributes.SERVICE_NAME);
    if (serviceName == null || serviceName.isEmpty()) {
      serviceName = Resource.getDefault().getAttributes().get(ResourceAttributes.SERVICE_NAME);
    }
    builder.setServiceName(serviceName);

    builder.addAllTags(Adapter.toKeyValues(resource.getAttributes()));

    return Collector.PostSpansRequest.newBuilder()
        .setBatch(
            Model.Batch.newBuilder()
                .addAllSpans(Adapter.toJaeger(spans))
                .setProcess(builder.build())
                .build())
        .build();
  }

  /**
   * The Jaeger exporter does not batch spans, so this method will immediately return with success.
   *
   * @return always Success
   */
  @Override
  public CompletableResultCode flush() {
    return CompletableResultCode.ofSuccess();
  }

  /**
   * Returns a new builder instance for this exporter.
   *
   * @return a new builder instance for this exporter.
   */
  public static JaegerGrpcSpanExporterBuilder builder() {
    return new JaegerGrpcSpanExporterBuilder();
  }

  /**
   * Initiates an orderly shutdown in which preexisting calls continue but new calls are immediately
   * cancelled.
   */
  @Override
  public CompletableResultCode shutdown() {
    final CompletableResultCode result = new CompletableResultCode();
    managedChannel.notifyWhenStateChanged(
        ConnectivityState.SHUTDOWN,
        new Runnable() {
          @Override
          public void run() {
            result.succeed();
          }
        });
    managedChannel.shutdown();
    return result;
  }

  // Visible for testing
  Model.Process.Builder getProcessBuilder() {
    return processBuilder;
  }

  // Visible for testing
  ManagedChannel getManagedChannel() {
    return managedChannel;
  }
}
