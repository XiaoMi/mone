/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.servlet.v5_0.service;

import io.opentelemetry.instrumentation.api.servlet.MappingResolver;
import io.opentelemetry.javaagent.instrumentation.api.InstrumentationContext;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import net.bytebuddy.asm.Advice;

@SuppressWarnings("unused")
public class JakartaServletInitAdvice {

  @Advice.OnMethodEnter(suppress = Throwable.class)
  public static void servletInit(
      @Advice.This Servlet servlet, @Advice.Argument(0) ServletConfig servletConfig) {
    if (servletConfig == null) {
      return;
    }
    InstrumentationContext.get(Servlet.class, MappingResolver.class)
        .putIfAbsent(servlet, new JakartaServletMappingResolverFactory(servletConfig));
  }
}
