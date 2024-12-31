/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.spring.autoconfigure.webmvc;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.spring.webmvc.WebMvcTracingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

/** Configures {@link WebMvcTracingFilter} for tracing. */
@Configuration
@EnableConfigurationProperties(WebMvcProperties.class)
@ConditionalOnProperty(prefix = "otel.springboot.web", name = "enabled", matchIfMissing = true)
@ConditionalOnClass(OncePerRequestFilter.class)
public class WebMvcFilterAutoConfiguration {

  @Bean
  public WebMvcTracingFilter otelWebMvcTracingFilter(OpenTelemetry openTelemetry) {
    return new WebMvcTracingFilter(openTelemetry);
  }
}
