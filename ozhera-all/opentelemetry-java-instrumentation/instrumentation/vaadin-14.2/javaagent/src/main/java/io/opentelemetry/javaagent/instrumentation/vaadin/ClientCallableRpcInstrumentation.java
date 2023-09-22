/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.vaadin;

import static io.opentelemetry.javaagent.instrumentation.vaadin.VaadinTracer.tracer;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

// add spans around calls to methods with @ClientCallable annotation
public class ClientCallableRpcInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("com.vaadin.flow.server.communication.rpc.PublishedServerEventHandlerRpcHandler");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        named("invokeMethod")
            .and(takesArgument(0, named("com.vaadin.flow.component.Component")))
            .and(takesArgument(1, named(Class.class.getName())))
            .and(takesArgument(2, named(String.class.getName())))
            .and(takesArgument(3, named("elemental.json.JsonArray")))
            .and(takesArgument(4, named(int.class.getName()))),
        this.getClass().getName() + "$InvokeMethodAdvice");
  }

  @SuppressWarnings("unused")
  public static class InvokeMethodAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(
        @Advice.Argument(1) Class<?> componentClass,
        @Advice.Argument(2) String methodName,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {

      context = tracer().startClientCallableSpan(componentClass, methodName);
      scope = context.makeCurrent();
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void onExit(
        @Advice.Thrown Throwable throwable,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      scope.close();

      tracer().endSpan(context, throwable);
    }
  }
}
