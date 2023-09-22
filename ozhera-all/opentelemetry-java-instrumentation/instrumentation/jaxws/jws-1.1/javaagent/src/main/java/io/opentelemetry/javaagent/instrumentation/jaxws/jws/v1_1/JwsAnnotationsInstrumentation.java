/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jaxws.jws.v1_1;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasSuperMethod;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.methodIsDeclaredByType;
import static io.opentelemetry.javaagent.extension.matcher.ClassLoaderMatcher.hasClassesNamed;
import static io.opentelemetry.javaagent.instrumentation.jaxws.common.JaxWsTracer.tracer;
import static net.bytebuddy.matcher.ElementMatchers.inheritsAnnotation;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.instrumentation.api.CallDepthThreadLocalMap;
import java.lang.reflect.Method;
import javax.jws.WebService;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class JwsAnnotationsInstrumentation implements TypeInstrumentation {

  public static final String JWS_WEB_SERVICE_ANNOTATION = "javax.jws.WebService";

  @Override
  public ElementMatcher<ClassLoader> classLoaderOptimization() {
    return hasClassesNamed(JWS_WEB_SERVICE_ANNOTATION);
  }

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return implementsInterface(isAnnotatedWith(named(JWS_WEB_SERVICE_ANNOTATION)))
        .or(isAnnotatedWith(named(JWS_WEB_SERVICE_ANNOTATION)));
  }

  @Override
  public void transform(TypeTransformer transformer) {
    // JaxWS WebService methods are defined either by implementing an interface annotated
    // with @WebService or by any public method from a class annotated with @WebService.
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(
                hasSuperMethod(
                    methodIsDeclaredByType(inheritsAnnotation(named(JWS_WEB_SERVICE_ANNOTATION))))),
        JwsAnnotationsInstrumentation.class.getName() + "$JwsAnnotationsAdvice");
  }

  @SuppressWarnings("unused")
  public static class JwsAnnotationsAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void startSpan(
        @Advice.This Object target,
        @Advice.Origin Method method,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      if (CallDepthThreadLocalMap.incrementCallDepth(WebService.class) > 0) {
        return;
      }
      context = tracer().startSpan(target.getClass(), method);
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
      CallDepthThreadLocalMap.reset(WebService.class);

      scope.close();
      if (throwable == null) {
        tracer().end(context);
      } else {
        tracer().endExceptionally(context, throwable);
      }
    }
  }
}
