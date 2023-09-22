/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.tomcat.v10_0;

import static io.opentelemetry.javaagent.instrumentation.tomcat.v10_0.Tomcat10Tracer.tracer;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.servlet.jakarta.v5_0.JakartaServletHttpServerTracer;
import io.opentelemetry.javaagent.instrumentation.tomcat.common.TomcatServerHandlerAdviceHelper;
import net.bytebuddy.asm.Advice;
import org.apache.coyote.Request;
import org.apache.coyote.Response;

@SuppressWarnings("unused")
public class Tomcat10ServerHandlerAdvice {

  @Advice.OnMethodEnter(suppress = Throwable.class)
  public static void onEnter(
      @Advice.Argument(0) Request request,
      @Advice.Argument(1) Response response,
      @Advice.Local("otelContext") Context context,
      @Advice.Local("otelScope") Scope scope) {
    if (!tracer().shouldStartSpan(request)) {
      return;
    }

    context = tracer().startServerSpan(request);

    scope = context.makeCurrent();

    TomcatServerHandlerAdviceHelper.attachResponseToRequest(
        Tomcat10ServletEntityProvider.INSTANCE,
        JakartaServletHttpServerTracer.tracer(),
        request,
        response);
  }

  @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
  public static void stopSpan(
      @Advice.Argument(0) Request request,
      @Advice.Argument(1) Response response,
      @Advice.Thrown Throwable throwable,
      @Advice.Local("otelContext") Context context,
      @Advice.Local("otelScope") Scope scope) {

    TomcatServerHandlerAdviceHelper.stopSpan(
        tracer(),
        Tomcat10ServletEntityProvider.INSTANCE,
        JakartaServletHttpServerTracer.tracer(),
        request,
        response,
        throwable,
        context,
        scope);
  }
}
