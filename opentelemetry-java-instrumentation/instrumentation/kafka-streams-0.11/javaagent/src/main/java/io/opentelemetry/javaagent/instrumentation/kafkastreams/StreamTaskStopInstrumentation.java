/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.kafkastreams;

import static io.opentelemetry.javaagent.instrumentation.kafkastreams.ContextScopeHolder.HOLDER;
import static io.opentelemetry.javaagent.instrumentation.kafkastreams.KafkaStreamsTracer.tracer;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.context.Context;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class StreamTaskStopInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("org.apache.kafka.streams.processor.internals.StreamTask");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod().and(isPublic()).and(named("process")).and(takesArguments(0)),
        StreamTaskStopInstrumentation.class.getName() + "$StopSpanAdvice");
  }

  @SuppressWarnings("unused")
  public static class StopSpanAdvice {

    @Advice.OnMethodEnter
    public static ContextScopeHolder onEnter() {
      ContextScopeHolder holder = new ContextScopeHolder();
      HOLDER.set(holder);
      return holder;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void stopSpan(
        @Advice.Enter ContextScopeHolder holder, @Advice.Thrown Throwable throwable) {
      HOLDER.remove();
      Context context = holder.getContext();
      if (context != null) {
        holder.closeScope();

        if (throwable != null) {
          tracer().endExceptionally(context, throwable);
        } else {
          tracer().end(context);
        }
      }
    }
  }
}
