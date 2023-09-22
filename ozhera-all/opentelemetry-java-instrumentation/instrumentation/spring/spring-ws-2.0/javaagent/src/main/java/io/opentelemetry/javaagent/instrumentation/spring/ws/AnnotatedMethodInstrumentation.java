/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.spring.ws;

import static io.opentelemetry.javaagent.extension.matcher.ClassLoaderMatcher.hasClassesNamed;
import static io.opentelemetry.javaagent.instrumentation.spring.ws.SpringWsTracer.tracer;
import static net.bytebuddy.matcher.ElementMatchers.declaresMethod;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.instrumentation.api.CallDepthThreadLocalMap;
import java.lang.reflect.Method;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

public class AnnotatedMethodInstrumentation implements TypeInstrumentation {
  private static final String[] ANNOTATION_CLASSES =
      new String[] {
        "org.springframework.ws.server.endpoint.annotation.PayloadRoot",
        "org.springframework.ws.soap.server.endpoint.annotation.SoapAction",
        "org.springframework.ws.soap.addressing.server.annotation.Action"
      };

  @Override
  public ElementMatcher<ClassLoader> classLoaderOptimization() {
    return hasClassesNamed("org.springframework.ws.server.endpoint.annotation.PayloadRoot");
  }

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return declaresMethod(isAnnotatedWith(namedOneOf(ANNOTATION_CLASSES)));
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod().and(isAnnotatedWith(namedOneOf(ANNOTATION_CLASSES))),
        AnnotatedMethodInstrumentation.class.getName() + "$AnnotatedMethodAdvice");
  }

  @SuppressWarnings("unused")
  public static class AnnotatedMethodAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void startSpan(
        @Advice.Origin Method method,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      if (CallDepthThreadLocalMap.incrementCallDepth(PayloadRoot.class) > 0) {
        return;
      }
      context = tracer().startSpan(method);
      scope = context.makeCurrent();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void stopSpan(
        @Advice.Thrown Throwable throwable,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      if (scope == null) {
        return;
      }
      CallDepthThreadLocalMap.reset(PayloadRoot.class);

      scope.close();
      if (throwable == null) {
        tracer().end(context);
      } else {
        tracer().endExceptionally(context, throwable);
      }
    }
  }
}
