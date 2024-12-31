/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.servlet.v2_2;

import static io.opentelemetry.instrumentation.api.servlet.ServerSpanNaming.Source.SERVLET;

import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.servlet.ServerSpanNaming;
import io.opentelemetry.instrumentation.servlet.javax.JavaxServletHttpServerTracer;
import javax.servlet.http.HttpServletRequest;

public class Servlet2HttpServerTracer extends JavaxServletHttpServerTracer<ResponseWithStatus> {
  private static final Servlet2HttpServerTracer TRACER = new Servlet2HttpServerTracer();

  public Servlet2HttpServerTracer() {
    super(Servlet2Accessor.INSTANCE);
  }

  @Override
  protected String bussinessStatus(ResponseWithStatus responseWithStatus) {
    return null;
  }

  @Override
  protected String bussinessMessage(ResponseWithStatus responseWithStatus) {
    return null;
  }

  public static Servlet2HttpServerTracer tracer() {
    return TRACER;
  }

  public Context startSpan(HttpServletRequest request) {
    return startSpan(request, getSpanName(request), true);
  }

  @Override
  public Context updateContext(Context context, HttpServletRequest request) {
    ServerSpanNaming.updateServerSpanName(context, SERVLET, () -> getSpanName(request));
    return super.updateContext(context, request);
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.javaagent.servlet-2.2";
  }
}
