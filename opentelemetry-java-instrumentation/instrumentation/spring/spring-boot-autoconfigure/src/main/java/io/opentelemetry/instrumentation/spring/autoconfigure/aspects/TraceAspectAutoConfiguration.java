/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.spring.autoconfigure.aspects;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.extension.annotations.WithSpan;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configures {@link WithSpanAspect} to trace bean methods annotated with {@link WithSpan}. */
@Configuration
@EnableConfigurationProperties(TraceAspectProperties.class)
@ConditionalOnProperty(prefix = "otel.springboot.aspects", name = "enabled", matchIfMissing = true)
@ConditionalOnClass({Aspect.class, WithSpan.class})
public class TraceAspectAutoConfiguration {

  @Bean
  public WithSpanAspect withSpanAspect(OpenTelemetry openTelemetry) {
    return new WithSpanAspect(openTelemetry);
  }
}
