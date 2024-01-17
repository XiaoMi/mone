package io.opentelemetry.sdk.common;

import java.util.ArrayList;
import java.util.List;

public class EnvOrJvmProperties {

  private EnvOrJvmProperties(){}

  public static final HeraJavaagentConfig JVM_OTEL_RESOURCE_ATTRIBUTES = new HeraJavaagentConfig("otel.resource.attributes", HeraJavaagentConfigType.JVM);
  public static final HeraJavaagentConfig JVM_OTEL_TRACES_EXPORTER = new HeraJavaagentConfig("otel.traces.exporter",HeraJavaagentConfigType.JVM,"log4j2");
  public static final HeraJavaagentConfig JVM_OTEL_METRICS_EXPORTER = new HeraJavaagentConfig("otel.metrics.exporter", HeraJavaagentConfigType.JVM,"prometheus");
  public static final HeraJavaagentConfig JVM_OTEL_NACOS_ADDRESS = new HeraJavaagentConfig("otel.exporter.prometheus.nacos.addr", HeraJavaagentConfigType.JVM, "nacos.hera-namespace:80");
  public static final HeraJavaagentConfig JVM_OTEL_EXCLUDE_CLASSES = new HeraJavaagentConfig("otel.javaagent.exclude-classes", HeraJavaagentConfigType.JVM,"com.dianping.cat.*");
  public static final HeraJavaagentConfig JVM_OTEL_EXPORTER_LOG_ISASYNC = new HeraJavaagentConfig("otel.exporter.log.isasync", HeraJavaagentConfigType.JVM,"true");
  public static final HeraJavaagentConfig JVM_OTEL_EXPORTER_LOG_PATH_PREFIX = new HeraJavaagentConfig("otel.exporter.log.pathprefix", HeraJavaagentConfigType.JVM,"/home/work/log/");
  public static final HeraJavaagentConfig JVM_OTEL_PROPAGATORS = new HeraJavaagentConfig("otel.propagators", HeraJavaagentConfigType.JVM,"tracecontext");
  public static final HeraJavaagentConfig JVM_OTEL_SERVICE_IP = new HeraJavaagentConfig("otel.service.ip", HeraJavaagentConfigType.JVM);
  public static final HeraJavaagentConfig JVM_OTEL_METRICS_PROMETHEUS_PORT = new HeraJavaagentConfig("otel.metrics.prometheus.port", HeraJavaagentConfigType.JVM);
  public static final HeraJavaagentConfig JVM_OTEL_EXPORTER_LOG_INTERVAL = new HeraJavaagentConfig("otel.exporter.log.interval", HeraJavaagentConfigType.JVM);
  public static final HeraJavaagentConfig JVM_OTEL_EXPORTER_LOG_DELETE_AGE = new HeraJavaagentConfig("otel.exporter.log.delete.age", HeraJavaagentConfigType.JVM);
  public static final HeraJavaagentConfig JVM_OTEL_MIONE_PROJECT_ENV_ID = new HeraJavaagentConfig("otel.mione.project.env.id", HeraJavaagentConfigType.JVM);
  public static final HeraJavaagentConfig JVM_OTEL_MIONE_PROJECT_ENV_NAME = new HeraJavaagentConfig("otel.mione.project.env.name", HeraJavaagentConfigType.JVM);
  public static final HeraJavaagentConfig ENV_HOST_IP = new HeraJavaagentConfig("host.ip", HeraJavaagentConfigType.ENV);
  public static final HeraJavaagentConfig ENV_NODE_IP = new HeraJavaagentConfig("node.ip", HeraJavaagentConfigType.ENV);
  public static final HeraJavaagentConfig ENV_MIONE_LOG_PATH = new HeraJavaagentConfig("MIONE_LOG_PATH", HeraJavaagentConfigType.ENV);
  public static final HeraJavaagentConfig ENV_JAVAAGENT_PROMETHEUS_PORT = new HeraJavaagentConfig("JAVAAGENT_PROMETHEUS_PORT", HeraJavaagentConfigType.ENV, "55433");
  public static final HeraJavaagentConfig ENV_HERA_BUILD_K8S = new HeraJavaagentConfig("hera.buildin.k8s", HeraJavaagentConfigType.ENV,"1");
  public static final HeraJavaagentConfig MIONE_PROJECT_NAME = new HeraJavaagentConfig("MIONE_PROJECT_NAME", HeraJavaagentConfigType.ENV, "none");
  public static final HeraJavaagentConfig ENV_MIONE_PROJECT_ENV_NAME = new HeraJavaagentConfig("MIONE_PROJECT_ENV_NAME", HeraJavaagentConfigType.ENV, "default");
  public static final HeraJavaagentConfig ENV_MIONE_PROJECT_ENV_ID = new HeraJavaagentConfig("MIONE_PROJECT_ENV_ID", HeraJavaagentConfigType.ENV);
  public static final List<HeraJavaagentConfig> INIT_ENV_JVM_LIST = new ArrayList<>();

  static {
    INIT_ENV_JVM_LIST.add(JVM_OTEL_TRACES_EXPORTER);
    INIT_ENV_JVM_LIST.add(JVM_OTEL_METRICS_EXPORTER);
    INIT_ENV_JVM_LIST.add(JVM_OTEL_NACOS_ADDRESS);
    INIT_ENV_JVM_LIST.add(JVM_OTEL_EXCLUDE_CLASSES);
    INIT_ENV_JVM_LIST.add(JVM_OTEL_EXPORTER_LOG_ISASYNC);
    INIT_ENV_JVM_LIST.add(JVM_OTEL_EXPORTER_LOG_PATH_PREFIX);
    INIT_ENV_JVM_LIST.add(JVM_OTEL_PROPAGATORS);

    INIT_ENV_JVM_LIST.add(ENV_HOST_IP);
    INIT_ENV_JVM_LIST.add(ENV_NODE_IP);
    INIT_ENV_JVM_LIST.add(ENV_MIONE_LOG_PATH);
    INIT_ENV_JVM_LIST.add(ENV_JAVAAGENT_PROMETHEUS_PORT);
    INIT_ENV_JVM_LIST.add(ENV_HERA_BUILD_K8S);
    INIT_ENV_JVM_LIST.add(ENV_MIONE_PROJECT_ENV_NAME);
    INIT_ENV_JVM_LIST.add(ENV_MIONE_PROJECT_ENV_ID);
    INIT_ENV_JVM_LIST.add(MIONE_PROJECT_NAME);
  }
}
