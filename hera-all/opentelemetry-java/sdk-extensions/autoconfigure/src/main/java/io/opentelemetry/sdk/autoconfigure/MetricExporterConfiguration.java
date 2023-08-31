/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.autoconfigure;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.opentelemetry.exporter.logging.LoggingMetricExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporterBuilder;
import io.opentelemetry.sdk.internal.ThrottlingLogger;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.IntervalMetricReader;
import io.opentelemetry.sdk.metrics.export.IntervalMetricReaderBuilder;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.prometheus.client.CollectorRegistry;
import org.apache.commons.lang3.StringUtils;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

final class MetricExporterConfiguration {

  private static final ThrottlingLogger logger =
      new ThrottlingLogger(Logger.getLogger(MetricExporterConfiguration.class.getName()));

  private static String applicationName = System.getenv("mione.app.name");
  private static String serverIp = System.getenv("TESLA_HOST");
  private static String projectEnv = System.getenv("MIONE_PROJECT_ENV_NAME");
  private static final String BUILDIN_K8S = System.getenv("hera.buildin.k8s");
  private static final String NODE_IP = System.getenv("node.ip");
  private static final String ENV_ID = System.getenv("MIONE_PROJECT_ENV_ID");
  private static final String ENV_DEFAULT = "default_env";

  static void configureExporter(
      String name, ConfigProperties config, SdkMeterProvider meterProvider) {
    if (StringUtils.isEmpty(applicationName)) {
      applicationName = config.getString("otel.resource.attributes");
      if (StringUtils.isNotEmpty(applicationName)) {
        applicationName = applicationName.split("=")[1];
      } else {
        applicationName = "none";
      }
    }
    if (StringUtils.isEmpty(projectEnv)) {
      projectEnv = ENV_DEFAULT;
    }
    // 替换项目名称中的-为_
    applicationName = applicationName.replaceAll("-", "_");
    if (StringUtils.isEmpty(serverIp)) {
      serverIp = config.getString("otel.service.ip");
    }
    if (StringUtils.isEmpty(name)) {
      name = "default";
    }
    switch (name) {
      case "otlp":
        configureOtlpMetrics(config, meterProvider);
        return;
      case "prometheus":
        configureJcommonPrometheusMetrics(config);
        return;
      case "logging":
        ClasspathUtil.checkClassExists(
            "io.opentelemetry.exporter.logging.LoggingMetricExporter",
            "Logging Metrics Exporter",
            "opentelemetry-exporter-logging");
        configureLoggingMetrics(config, meterProvider);
        return;
      default:
        return;
    }
  }

  private static void configureLoggingMetrics(
      ConfigProperties config, SdkMeterProvider meterProvider) {
    MetricExporter exporter = new LoggingMetricExporter();
    configureIntervalMetricReader(config, meterProvider, exporter);
  }

  // Visible for testing
  @Nullable
  static OtlpGrpcMetricExporter configureOtlpMetrics(
      ConfigProperties config, SdkMeterProvider meterProvider) {
    try {
      ClasspathUtil.checkClassExists(
          "io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter",
          "OTLP Metrics Exporter",
          "opentelemetry-exporter-otlp-metrics");
    } catch (ConfigurationException e) {
      // Squash this for now, until metrics are stable and included in the `exporter-otlp` artifact
      // by default,
      return null;
    }
    OtlpGrpcMetricExporterBuilder builder = OtlpGrpcMetricExporter.builder();

    String endpoint = config.getString("otel.exporter.otlp.metrics.endpoint");
    if (endpoint == null) {
      endpoint = config.getString("otel.exporter.otlp.endpoint");
    }
    if (endpoint != null) {
      builder.setEndpoint(endpoint);
    }

    config.getCommaSeparatedMap("otel.exporter.otlp.headers").forEach(builder::addHeader);

    Duration timeout = config.getDuration("otel.exporter.otlp.timeout");
    if (timeout != null) {
      builder.setTimeout(timeout);
    }

    OtlpGrpcMetricExporter exporter = builder.build();

    configureIntervalMetricReader(config, meterProvider, exporter);

    return exporter;
  }

  private static void configureIntervalMetricReader(
      ConfigProperties config, SdkMeterProvider meterProvider, MetricExporter exporter) {
    IntervalMetricReaderBuilder readerBuilder =
        IntervalMetricReader.builder()
            .setMetricProducers(Collections.singleton(meterProvider))
            .setMetricExporter(exporter);
    Duration exportInterval = config.getDuration("otel.imr.export.interval");
    if (exportInterval != null) {
      readerBuilder.setExportIntervalMillis(exportInterval.toMillis());
    }
    IntervalMetricReader reader = readerBuilder.buildAndStart();
    Runtime.getRuntime().addShutdownHook(new Thread(reader::shutdown));
  }

//  private static void configurePrometheusMetrics(
//      ConfigProperties config, SdkMeterProvider meterProvider) {
//    ClasspathUtil.checkClassExists(
//        "io.opentelemetry.exporter.prometheus.PrometheusCollector",
//        "Prometheus Metrics Server",
//        "opentelemetry-exporter-prometheus");
//    PrometheusCollector.builder().setMetricProducer(meterProvider).buildAndRegister();
//    Integer port = config.getInt("otel.exporter.prometheus.port");
//    if (port == null) {
//      port = 9464;
//    }
//    String host = config.getString("otel.exporter.prometheus.host");
//    if (host == null) {
//      host = "0.0.0.0";
//    }
//    final HTTPServer server;
//    try {
//      server = new HTTPServer(host, port, true);
//    } catch (IOException e) {
//      throw new IllegalStateException("Failed to create Prometheus server", e);
//    }
//    Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
//  }

  @SuppressWarnings({"BooleanParameter", "UnnecessaryParentheses"})
  private static void configureJcommonPrometheusMetrics(ConfigProperties config) {
    // regist nacos for prometheus port
    String javaagentPrometheusPort = System.getenv("JAVAAGENT_PROMETHEUS_PORT");
    if (StringUtils.isEmpty(javaagentPrometheusPort)) {
      javaagentPrometheusPort = config.getString("otel.metrics.prometheus.port");
      if (StringUtils.isEmpty(javaagentPrometheusPort)) {
        javaagentPrometheusPort = "55433";
      }
    }
    String nacosAddr = config.getString("otel.exporter.prometheus.nacos.addr");
    registNacos(javaagentPrometheusPort, nacosAddr);

    PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    if ("1".equals(BUILDIN_K8S)) {
      registry.config().commonTags(
          new String[] {"application", applicationName, "serverIp", serverIp, "jumpIp", NODE_IP,
              "serverEnv", projectEnv, "serverEnvId", ENV_ID});
    } else {
      registry.config().commonTags(
          new String[] {"application", applicationName, "serverIp", serverIp, "jumpIp", serverIp,
              "serverEnv", projectEnv, "serverEnvId", ENV_ID});
    }
    (new ClassLoaderMetrics()).bindTo(registry);
    (new JvmMemoryMetrics()).bindTo(registry);
    (new JvmGcMetrics()).bindTo(registry);
    (new ProcessorMetrics()).bindTo(registry);
    (new JvmThreadMetrics()).bindTo(registry);
    (new UptimeMetrics()).bindTo(registry);
    (new FileDescriptorMetrics()).bindTo(registry);
    String finalValue = javaagentPrometheusPort;
    new Thread(() -> {
      try {
        InetSocketAddress addr = new InetSocketAddress(Integer.valueOf(finalValue));
        Map<String, CollectorRegistry> map = new HashMap<>(5);
        map.put("default", CollectorRegistry.defaultRegistry);
        map.put("jvm", registry.getPrometheusRegistry());
        new JcommonHTTPServer(HttpServer.create(addr, 3), map, false);
      } catch (Exception e) {
        throw new ConfigurationException("Prometheus export metrics exception: " + e.getMessage());
      }
    }).start();
  }

  private static void registNacos(String prometheusPort, String nacosServerAddr) {
    try {
      String appName = "prometheus_server_" + applicationName;
      NacosNamingService nacosNamingService = new NacosNamingService(nacosServerAddr);
      Instance instance = new Instance();
      instance.setIp(serverIp);
      instance.setPort(55255);
      Map<String, String> map = new HashMap<>();
      map.put("javaagent_prometheus_port", prometheusPort);
      if (StringUtils.isNotEmpty(ENV_ID)) {
        map.put("env_id", ENV_ID);
      }
      if (StringUtils.isNotEmpty(projectEnv)){
        map.put("env_name", projectEnv);
      }
      instance.setMetadata(map);
      nacosNamingService.registerInstance(appName, instance);
      // deregister
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        logger.log(Level.INFO, "nacos shutdown hook deregister instance");
        try {
          nacosNamingService.deregisterInstance(appName, instance);
        } catch (Exception e) {
          logger.log(Level.WARNING, "nacos shutdown hook error : " + e.getMessage());
        }
      }));
    } catch (Exception e) {
      throw new ConfigurationException(
          "Prometheus export regist nacos exception: " + e.getMessage());
    }
  }

  private MetricExporterConfiguration() {}
}
