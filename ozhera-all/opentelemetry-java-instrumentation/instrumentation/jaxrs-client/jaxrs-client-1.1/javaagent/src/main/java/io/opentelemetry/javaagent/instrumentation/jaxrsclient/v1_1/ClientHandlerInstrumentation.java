/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jaxrsclient.v1_1;

import static io.opentelemetry.instrumentation.api.tracer.HttpServerTracer.CONTEXT_ATTRIBUTE;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.extendsClass;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static io.opentelemetry.javaagent.extension.matcher.ClassLoaderMatcher.hasClassesNamed;
import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;
import static io.opentelemetry.javaagent.instrumentation.jaxrsclient.v1_1.JaxRsClientV1Tracer.tracer;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class ClientHandlerInstrumentation implements TypeInstrumentation {
  @Override
  public ElementMatcher<ClassLoader> classLoaderOptimization() {
    return hasClassesNamed("com.sun.jersey.api.client.ClientHandler");
  }

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return implementsInterface(named("com.sun.jersey.api.client.ClientHandler"));
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        named("handle")
            .and(takesArgument(0, extendsClass(named("com.sun.jersey.api.client.ClientRequest"))))
            .and(returns(extendsClass(named("com.sun.jersey.api.client.ClientResponse")))),
        this.getClass().getName() + "$HandleAdvice");
  }

  @SuppressWarnings("unused")
  public static class HandleAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(
        @Advice.Argument(0) ClientRequest request,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      // WARNING: this might be a chain...so we only have to trace the first in the chain.
      boolean isRootClientHandler = null == request.getProperties().get(CONTEXT_ATTRIBUTE);
      Context parentContext = currentContext();
      if (isRootClientHandler && tracer().shouldStartSpan(parentContext)) {
        context = tracer().startSpan(parentContext, request, request);
        scope = context.makeCurrent();
      }
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void onExit(
        @Advice.Return ClientResponse response,
        @Advice.Thrown Throwable throwable,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      if (scope == null) {
        return;
      }

      scope.close();
      if (throwable != null) {
        tracer().endExceptionally(context, throwable);
      } else {
        tracer().end(context, response);
      }
    }
  }
}
