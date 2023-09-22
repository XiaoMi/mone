package io.opentelemetry.sdk.common;

public final class SystemCommon {
  public static String getEnvOrProperties(String key) {
    String result = System.getenv(key);
    if (result == null) {
      result = System.getProperty(key);
    }
    return result;
  }

  private SystemCommon(){}
}
