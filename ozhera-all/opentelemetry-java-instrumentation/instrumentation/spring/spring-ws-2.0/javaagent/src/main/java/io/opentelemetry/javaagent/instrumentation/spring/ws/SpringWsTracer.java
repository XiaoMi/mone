/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.spring.ws;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.tracer.BaseTracer;
import io.opentelemetry.instrumentation.api.tracer.SpanNames;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import java.lang.reflect.Method;

public class SpringWsTracer extends BaseTracer {

  private static final SpringWsTracer TRACER = new SpringWsTracer();

  public static SpringWsTracer tracer() {
    return TRACER;
  }

  public Context startSpan(Method method) {
    Context parentContext = Context.current();
    Span span =
        spanBuilder(parentContext, SpanNames.fromMethod(method), SpanKind.INTERNAL)
            .setAttribute(SemanticAttributes.CODE_NAMESPACE, method.getDeclaringClass().getName())
            .setAttribute(SemanticAttributes.CODE_FUNCTION, method.getName())
            .startSpan();
    return parentContext.with(span);
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.javaagent.spring-ws-2.0";
  }
}
