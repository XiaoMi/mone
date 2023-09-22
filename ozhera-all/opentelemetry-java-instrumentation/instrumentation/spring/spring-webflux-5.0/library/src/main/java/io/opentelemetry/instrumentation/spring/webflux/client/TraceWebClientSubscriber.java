/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.spring.webflux.client;

import io.opentelemetry.context.Scope;
import org.reactivestreams.Subscription;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.CoreSubscriber;

/**
 * Based on Spring Sleuth's Reactor instrumentation.
 * https://github.com/spring-cloud/spring-cloud-sleuth/blob/master/spring-cloud-sleuth-core/src/main/java/org/springframework/cloud/sleuth/instrument/web/client/TraceWebClientBeanPostProcessor.java
 */
final class TraceWebClientSubscriber implements CoreSubscriber<ClientResponse> {

  private final SpringWebfluxHttpClientTracer tracer;

  private final CoreSubscriber<? super ClientResponse> actual;

  private final reactor.util.context.Context context;

  private final io.opentelemetry.context.Context tracingContext;

  TraceWebClientSubscriber(
      SpringWebfluxHttpClientTracer tracer,
      CoreSubscriber<? super ClientResponse> actual,
      io.opentelemetry.context.Context tracingContext) {
    this.tracer = tracer;
    this.actual = actual;
    this.tracingContext = tracingContext;
    this.context = actual.currentContext();
  }

  @Override
  public void onSubscribe(Subscription subscription) {
    this.actual.onSubscribe(subscription);
  }

  @Override
  public void onNext(ClientResponse response) {
    try (Scope ignored = tracingContext.makeCurrent()) {
      this.actual.onNext(response);
    } finally {
      tracer.end(tracingContext, response);
    }
  }

  @Override
  public void onError(Throwable t) {
    try (Scope ignored = tracingContext.makeCurrent()) {
      this.actual.onError(t);
    } finally {
      tracer.endExceptionally(tracingContext, t);
    }
  }

  @Override
  public void onComplete() {
    try (Scope ignored = tracingContext.makeCurrent()) {
      this.actual.onComplete();
    }
  }

  @Override
  public reactor.util.context.Context currentContext() {
    return this.context;
  }
}
