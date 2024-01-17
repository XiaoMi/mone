/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.vertx.client;

import static io.opentelemetry.javaagent.instrumentation.vertx.client.VertxClientTracer.tracer;

import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.instrumentation.api.ContextStore;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientRequest;

public class ExceptionHandlerWrapper implements Handler<Throwable> {
  private final HttpClientRequest request;
  private final ContextStore<HttpClientRequest, Contexts> contextStore;
  private final Handler<Throwable> handler;

  public ExceptionHandlerWrapper(
      HttpClientRequest request,
      ContextStore<HttpClientRequest, Contexts> contextStore,
      Handler<Throwable> handler) {
    this.request = request;
    this.contextStore = contextStore;
    this.handler = handler;
  }

  @Override
  public void handle(Throwable throwable) {
    Contexts contexts = contextStore.get(request);
    if (contexts == null) {
      callHandler(throwable);
      return;
    }

    tracer().endExceptionally(contexts.context, throwable);

    try (Scope ignored = contexts.parentContext.makeCurrent()) {
      callHandler(throwable);
    }
  }

  private void callHandler(Throwable throwable) {
    handler.handle(throwable);
  }
}
