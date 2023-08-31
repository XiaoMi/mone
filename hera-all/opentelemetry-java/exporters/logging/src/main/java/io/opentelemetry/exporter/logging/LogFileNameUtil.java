package io.opentelemetry.exporter.logging;

import io.opentelemetry.api.internal.StringUtils;

@SuppressWarnings({"PrivateConstructorForUtilityClass","CatchingUnchecked"})
public class LogFileNameUtil {

  public static final String LOGPATH_PROPERTY_NAME = "otel.exporter.log.pathprefix";
  private static final String LOG_PATH_SUFFIX = "/trace/";
  private static final String LOG_FILE_NAME = "trace.log";

  public static String getLogPathFile() {
    return getLogPath() + LOG_FILE_NAME;
  }

  public static String getLogPath(){
    String logPathPrefixStr = System.getenv("MIONE_LOG_PATH");
    if(StringUtils.isNullOrEmpty(logPathPrefixStr)) {
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
  public static String getServiceName(){
    String applicationName = System.getProperty("otel.resource.attributes") == null ?
            (System.getenv("mione.app.name") == null ? "none" : System.getenv("mione.app.name")) :
            System.getProperty("otel.resource.attributes").split("=")[1];
    // 删除mione中生成的项目名id
    int i = applicationName.indexOf("-");
    if (i >= 0) {
      String id = applicationName.substring(0, i);
      if(isNumeric(id)) {
        return applicationName.substring(i + 1);
      }
    }
    int j = applicationName.indexOf("_");
    if (j >= 0) {
      String id = applicationName.substring(0, j);
      if(isNumeric(id)) {
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
