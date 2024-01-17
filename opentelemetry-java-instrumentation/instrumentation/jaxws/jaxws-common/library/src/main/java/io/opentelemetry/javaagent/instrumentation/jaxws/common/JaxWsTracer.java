/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jaxws.common;

import static io.opentelemetry.api.trace.SpanKind.INTERNAL;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.tracer.BaseTracer;
import io.opentelemetry.instrumentation.api.tracer.ServerSpan;
import io.opentelemetry.instrumentation.api.tracer.SpanNames;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import java.lang.reflect.Method;

public class JaxWsTracer extends BaseTracer {

  private static final JaxWsTracer TRACER = new JaxWsTracer();

  public static JaxWsTracer tracer() {
    return TRACER;
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.javaagent.jaxws-common";
  }

  public Context startSpan(Class<?> target, Method method) {
    String spanName = SpanNames.fromMethod(target, method);

    Context parentContext = Context.current();
    Span serverSpan = ServerSpan.fromContextOrNull(parentContext);
    if (serverSpan != null) {
      serverSpan.updateName(spanName);
    }

    Span span =
        spanBuilder(parentContext, spanName, INTERNAL)
            .setAttribute(SemanticAttributes.CODE_NAMESPACE, method.getDeclaringClass().getName())
            .setAttribute(SemanticAttributes.CODE_FUNCTION, method.getName())
            .startSpan();
    return parentContext.with(span);
  }
}
