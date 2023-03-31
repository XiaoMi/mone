package io.opentelemetry.sdk.common;

public class EnvOrJvmProperties {
  public static final String JVM_OTEL_RESOURCE_ATTRIBUTES = "otel.resource.attributes";
  public static final String JVM_OTEL_TRACES_EXPORTER = "otel.traces.exporter";
  public static final String JVM_OTEL_METRICS_EXPORTER = "otel.metrics.exporter";
  public static final String JVM_OTEL_NACOS_ADDRESS = "otel.exporter.prometheus.nacos.addr";
  public static final String JVM_OTEL_EXCLUDE_CLASSES = "otel.javaagent.exclude-classes";
  public static final String JVM_OTEL_EXPORTER_LOG_ISASYNC = "otel.exporter.log.isasync";
  public static final String JVM_OTEL_EXPORTER_LOG_PATH_PREFIX = "otel.exporter.log.pathprefix";
  public static final String JVM_OTEL_PROPAGATORS = "otel.propagators";
  public static final String JVM_OTEL_SERVICE_IP = "otel.service.ip";
  public static final String JVM_OTEL_METRICS_PROMETHEUS_PORT = "otel.metrics.prometheus.port";
  public static final String JVM_OTEL_EXPORTER_LOG_INTERVAL = "otel.exporter.log.interval";
  public static final String JVM_OTEL_EXPORTER_LOG_DELETE_AGE = "otel.exporter.log.delete.age";
  public static final String ENV_HOST_IP = "host.ip";
  public static final String ENV_NODE_IP = "node.ip";
  public static final String ENV_MIONE_LOG_PATH = "MIONE_LOG_PATH";
  public static final String ENV_JAVAAGENT_PROMETHEUS_PORT = "JAVAAGENT_PROMETHEUS_PORT";
  public static final String ENV_HERA_BUILD_K8S = "hera.buildin.k8s";
  public static final String ENV_MIONE_PROJECT_ENV_NAME = "MIONE_PROJECT_ENV_NAME";
  public static final String ENV_MIONE_PROJECT_ENV_ID = "MIONE_PROJECT_ENV_ID";
  public static final String ENV_APPLICATION = "APPLICATION";
  public static final String ENV_SERVER_ENV = "serverEnv";
}
