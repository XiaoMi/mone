/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.spring.autoconfigure.exporters;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration;
import io.opentelemetry.instrumentation.spring.autoconfigure.exporters.jaeger.JaegerSpanExporterAutoConfiguration;
import io.opentelemetry.instrumentation.spring.autoconfigure.exporters.jaeger.JaegerSpanExporterProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/** Spring Boot auto configuration test for {@link JaegerGrpcSpanExporter}. */
class JaegerSpanExporterAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  OpenTelemetryAutoConfiguration.class, JaegerSpanExporterAutoConfiguration.class));

  @AfterEach
  void tearDown() {
    GlobalOpenTelemetry.resetForTest();
  }

  @Test
  @DisplayName("when exporters are ENABLED should initialize JaegerGrpcSpanExporter bean")
  void exportersEnabled() {
    this.contextRunner
        .withPropertyValues("otel.exporter.jaeger.enabled=true")
        .run(
            context ->
                assertThat(context.getBean("otelJaegerSpanExporter", JaegerGrpcSpanExporter.class))
                    .isNotNull());
  }

  @Test
  @DisplayName(
      "when otel.exporter.jaeger properties are set should initialize JaegerSpanExporterProperties")
  void handlesProperties() {
    this.contextRunner
        .withPropertyValues(
            "otel.exporter.jaeger.enabled=true",
            "otel.exporter.jaeger.endpoint=http://localhost:8080/test",
            "otel.exporter.jaeger.timeout=420ms")
        .run(
            context -> {
              JaegerSpanExporterProperties jaegerSpanExporterProperties =
                  context.getBean(JaegerSpanExporterProperties.class);
              assertThat(jaegerSpanExporterProperties.getEndpoint())
                  .isEqualTo("http://localhost:8080/test");
              assertThat(jaegerSpanExporterProperties.getTimeout()).hasMillis(420);
            });
  }

  @Test
  @DisplayName("when exporters are DISABLED should NOT initialize JaegerGrpcSpanExporter bean")
  void disabledProperty() {
    this.contextRunner
        .withPropertyValues("otel.exporter.jaeger.enabled=false")
        .run(context -> assertThat(context.containsBean("otelJaegerSpanExporter")).isFalse());
  }

  @Test
  @DisplayName(
      "when jaeger enabled property is MISSING should initialize JaegerGrpcSpanExporter bean")
  void noProperty() {
    this.contextRunner.run(
        context ->
            assertThat(context.getBean("otelJaegerSpanExporter", JaegerGrpcSpanExporter.class))
                .isNotNull());
  }
}
