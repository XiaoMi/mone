/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.servlet.v3_0;

import static io.opentelemetry.instrumentation.api.servlet.ServerSpanNaming.Source.FILTER;
import static io.opentelemetry.instrumentation.api.servlet.ServerSpanNaming.Source.SERVLET;

import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.servlet.MappingResolver;
import io.opentelemetry.instrumentation.api.servlet.ServerSpanNaming;
import io.opentelemetry.instrumentation.servlet.javax.JavaxServletHttpServerTracer;
import io.opentelemetry.instrumentation.servlet.naming.ServletSpanNameProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Servlet3HttpServerTracer extends JavaxServletHttpServerTracer<HttpServletResponse> {
  private static final Servlet3HttpServerTracer TRACER = new Servlet3HttpServerTracer();
  private static final ServletSpanNameProvider<HttpServletRequest> SPAN_NAME_PROVIDER =
      new ServletSpanNameProvider<>(Servlet3Accessor.INSTANCE);

  protected Servlet3HttpServerTracer() {
    super(Servlet3Accessor.INSTANCE);
  }

  @Override
  protected String bussinessStatus(HttpServletResponse response) {
    return null;
  }

  @Override
  protected String bussinessMessage(HttpServletResponse response) {
    return null;
  }

  public static Servlet3HttpServerTracer tracer() {
    return TRACER;
  }

  public Context startSpan(
      HttpServletRequest request, MappingResolver mappingResolver, boolean servlet) {
    return startSpan(request, SPAN_NAME_PROVIDER.getSpanName(mappingResolver, request), servlet);
  }

  public Context updateContext(
      Context context,
      HttpServletRequest request,
      MappingResolver mappingResolver,
      boolean servlet) {
    ServerSpanNaming.updateServerSpanName(
        context,
        servlet ? SERVLET : FILTER,
        () -> SPAN_NAME_PROVIDER.getSpanNameOrNull(mappingResolver, request));
    return updateContext(context, request);
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.javaagent.servlet-3.0";
  }
}
