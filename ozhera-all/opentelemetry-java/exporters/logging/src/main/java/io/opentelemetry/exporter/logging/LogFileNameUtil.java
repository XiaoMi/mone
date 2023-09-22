package io.opentelemetry.exporter.logging;

import io.opentelemetry.api.internal.StringUtils;
import io.opentelemetry.sdk.common.EnvOrJvmProperties;
import io.opentelemetry.sdk.common.SystemCommon;

@SuppressWarnings({"PrivateConstructorForUtilityClass", "CatchingUnchecked"})
public class LogFileNameUtil {

  public static final String LOGPATH_PROPERTY_NAME = EnvOrJvmProperties.JVM_OTEL_EXPORTER_LOG_PATH_PREFIX.getKey();
  private static final String LOG_PATH_SUFFIX = "/trace/";
  private static final String LOG_FILE_NAME = "trace.log";

  public static String getLogPathFile() {
    return getLogPath() + LOG_FILE_NAME;
  }

  public static String getLogPath() {
    String logPathPrefixStr = SystemCommon.getEnvOrProperties(
        EnvOrJvmProperties.ENV_MIONE_LOG_PATH.getKey());
    if (StringUtils.isNullOrEmpty(logPathPrefixStr)) {
      String logPathPrefix = System.getProperty(LOGPATH_PROPERTY_NAME);
      if (StringUtils.isNullOrEmpty(logPathPrefix)) {
        logPathPrefix = "/home/work/log/";
      }
      String applicationName = getServiceName();
      logPathPrefixStr = logPathPrefix + applicationName;
    }
    return logPathPrefixStr + LOG_PATH_SUFFIX;
  }

  /**
   * get service name without project id
   */
  public static String getServiceName() {
    String applicationName =
        SystemCommon.getEnvOrProperties(EnvOrJvmProperties.JVM_OTEL_RESOURCE_ATTRIBUTES.getKey())
            == null ? SystemCommon.getEnvOrProperties(
            EnvOrJvmProperties.MIONE_PROJECT_NAME.getKey()) :
            SystemCommon.getEnvOrProperties(
                EnvOrJvmProperties.JVM_OTEL_RESOURCE_ATTRIBUTES.getKey()).split("=")[1];
    if (applicationName == null) {
      applicationName = EnvOrJvmProperties.MIONE_PROJECT_NAME.getDefaultValue();
    }
    // Delete the project name ID generated in mione.
    int i = applicationName.indexOf("-");
    if (i >= 0) {
      String id = applicationName.substring(0, i);
      if (isNumeric(id)) {
        return applicationName.substring(i + 1);
      }
    }
    int j = applicationName.indexOf("_");
    if (j >= 0) {
      String id = applicationName.substring(0, j);
      if (isNumeric(id)) {
        return applicationName.substring(j + 1);
      }
    }
    return applicationName;
  }

  public static boolean isNumeric(final String cs) {
    if (cs == null || cs.length() == 0) {
      return false;
    }
    final int sz = cs.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isDigit(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
