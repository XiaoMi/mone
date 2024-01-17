/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.servlet.v5_0.async;

import static io.opentelemetry.instrumentation.api.tracer.HttpServerTracer.CONTEXT_ATTRIBUTE;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.javaagent.instrumentation.api.CallDepthThreadLocalMap;
import io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletRequest;
import net.bytebuddy.asm.Advice;

@SuppressWarnings("unused")
public class AsyncDispatchAdvice {

  @Advice.OnMethodEnter(suppress = Throwable.class)
  public static boolean enter(
      @Advice.This AsyncContext context, @Advice.AllArguments Object[] args) {
    int depth = CallDepthThreadLocalMap.incrementCallDepth(AsyncContext.class);
    if (depth > 0) {
      return false;
    }

    ServletRequest request = context.getRequest();

    Context currentContext = Java8BytecodeBridge.currentContext();
    Span currentSpan = Java8BytecodeBridge.spanFromContext(currentContext);
    if (currentSpan.getSpanContext().isValid()) {
      // this tells the dispatched servlet to use the current span as the parent for its work
      // (if the currentSpan is not valid for some reason, the original servlet span should still
      // be present in the same request attribute, and so that will be used)
      //
      // the original servlet span stored in the same request attribute does not need to be saved
      // and restored on method exit, because dispatch() hands off control of the request
      // processing, and nothing can be done with the request anymore after this
      request.setAttribute(CONTEXT_ATTRIBUTE, currentContext);
    }

    return true;
  }

  @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
  public static void exit(@Advice.Enter boolean topLevel) {
    if (topLevel) {
      CallDepthThreadLocalMap.reset(AsyncContext.class);
    }
  }
}
