/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jaxrs.v2_0;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import java.lang.reflect.Method;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.UriInfo;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Local;

/**
 * Jersey specific context instrumentation.
 *
 * <p>JAX-RS does not define a way to get the matched resource method from the <code>
 * ContainerRequestContext</code>
 *
 * <p>In the Jersey implementation, <code>UriInfo</code> implements <code>ResourceInfo</code>. The
 * matched resource method can be retrieved from that object
 */
public class JerseyRequestContextInstrumentation extends AbstractRequestContextInstrumentation {
  @Override
  protected String abortAdviceName() {
    return getClass().getName() + "$ContainerRequestContextAdvice";
  }

  @SuppressWarnings("unused")
  public static class ContainerRequestContextAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void decorateAbortSpan(
        @Advice.This ContainerRequestContext requestContext,
        @Local("otelContext") Context context,
        @Local("otelScope") Scope scope) {
      UriInfo uriInfo = requestContext.getUriInfo();

      if (requestContext.getProperty(JaxRsAnnotationsTracer.ABORT_HANDLED) == null
          && uriInfo instanceof ResourceInfo) {

        ResourceInfo resourceInfo = (ResourceInfo) uriInfo;
        Method method = resourceInfo.getResourceMethod();
        Class<?> resourceClass = resourceInfo.getResourceClass();

        context =
            RequestContextHelper.createOrUpdateAbortSpan(requestContext, resourceClass, method);
        if (context != null) {
          scope = context.makeCurrent();
        }
      }
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void stopSpan(
        @Local("otelContext") Context context,
        @Local("otelScope") Scope scope,
        @Advice.Thrown Throwable throwable) {
      RequestContextHelper.closeSpanAndScope(context, scope, throwable);
    }
  }
}
