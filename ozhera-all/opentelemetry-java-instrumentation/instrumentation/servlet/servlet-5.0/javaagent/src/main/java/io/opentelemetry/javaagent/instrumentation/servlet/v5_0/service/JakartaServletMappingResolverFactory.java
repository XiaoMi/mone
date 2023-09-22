/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.servlet.v5_0.service;

import io.opentelemetry.instrumentation.api.servlet.MappingResolver;
import io.opentelemetry.instrumentation.servlet.naming.ServletMappingResolverFactory;
import io.opentelemetry.javaagent.instrumentation.api.ContextStore;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import java.util.Collection;

public class JakartaServletMappingResolverFactory extends ServletMappingResolverFactory
    implements ContextStore.Factory<MappingResolver> {
  private final ServletConfig servletConfig;

  public JakartaServletMappingResolverFactory(ServletConfig servletConfig) {
    this.servletConfig = servletConfig;
  }

  @Override
  @SuppressWarnings("ReturnsNullCollection")
  public Collection<String> getMappings() {
    String servletName = servletConfig.getServletName();
    ServletContext servletContext = servletConfig.getServletContext();
    if (servletName == null || servletContext == null) {
      return null;
    }

    ServletRegistration servletRegistration = servletContext.getServletRegistration(servletName);
    if (servletRegistration == null) {
      return null;
    }
    return servletRegistration.getMappings();
  }
}
