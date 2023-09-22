/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.log4j.v1_2;

import static io.opentelemetry.instrumentation.api.log.LoggingContextConstants.SPAN_ID;
import static io.opentelemetry.instrumentation.api.log.LoggingContextConstants.TRACE_FLAGS;
import static io.opentelemetry.instrumentation.api.log.LoggingContextConstants.TRACE_ID;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.instrumentation.api.InstrumentationContext;
import java.util.Hashtable;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;

public class LoggingEventInstrumentation implements TypeInstrumentation {
  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("org.apache.log4j.spi.LoggingEvent");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(named("getMDC"))
            .and(takesArguments(1))
            .and(takesArgument(0, String.class)),
        LoggingEventInstrumentation.class.getName() + "$GetMdcAdvice");

    transformer.applyAdviceToMethod(
        isMethod().and(isPublic()).and(named("getMDCCopy")).and(takesArguments(0)),
        LoggingEventInstrumentation.class.getName() + "$GetMdcCopyAdvice");
  }

  @SuppressWarnings("unused")
  public static class GetMdcAdvice {

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void onExit(
        @Advice.This LoggingEvent event,
        @Advice.Argument(0) String key,
        @Advice.Return(readOnly = false) Object value) {
      if (TRACE_ID.equals(key) || SPAN_ID.equals(key) || TRACE_FLAGS.equals(key)) {
        if (value != null) {
          // Assume already instrumented event if traceId/spanId/sampled is present.
          return;
        }

        Span span = InstrumentationContext.get(LoggingEvent.class, Span.class).get(event);
        if (span == null || !span.getSpanContext().isValid()) {
          return;
        }

        SpanContext spanContext = span.getSpanContext();
        switch (key) {
          case TRACE_ID:
            value = spanContext.getTraceId();
            break;
          case SPAN_ID:
            value = spanContext.getSpanId();
            break;
          case TRACE_FLAGS:
            value = spanContext.getTraceFlags().asHex();
            break;
          default:
            // do nothing
        }
      }
    }
  }

  @SuppressWarnings("unused")
  public static class GetMdcCopyAdvice {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(
        @Advice.This LoggingEvent event,
        @Advice.FieldValue(value = "mdcCopyLookupRequired", readOnly = false) boolean copyRequired,
        @Advice.FieldValue(value = "mdcCopy", readOnly = false) Hashtable mdcCopy) {
      // this advice basically replaces the original method

      if (copyRequired) {
        copyRequired = false;

        Hashtable mdc = new Hashtable();

        Hashtable originalMdc = MDC.getContext();
        if (originalMdc != null) {
          mdc.putAll(originalMdc);
        }

        // Assume already instrumented event if traceId is present.
        if (!mdc.containsKey(TRACE_ID)) {
          Span span = InstrumentationContext.get(LoggingEvent.class, Span.class).get(event);
          if (span != null && span.getSpanContext().isValid()) {
            SpanContext spanContext = span.getSpanContext();
            mdc.put(TRACE_ID, spanContext.getTraceId());
            mdc.put(SPAN_ID, spanContext.getSpanId());
            mdc.put(TRACE_FLAGS, spanContext.getTraceFlags().asHex());
          }
        }

        mdcCopy = mdc;
      }
    }
  }
}
