/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.tooling;

import java.util.Locale;

final class LoggingConfigurer {

  private static final String SIMPLE_LOGGER_SHOW_DATE_TIME_PROPERTY =
      "io.opentelemetry.javaagent.slf4j.simpleLogger.showDateTime";
  private static final String SIMPLE_LOGGER_DATE_TIME_FORMAT_PROPERTY =
      "io.opentelemetry.javaagent.slf4j.simpleLogger.dateTimeFormat";
  private static final String SIMPLE_LOGGER_DATE_TIME_FORMAT_DEFAULT =
      "'[otel.javaagent 'yyyy-MM-dd HH:mm:ss:SSS Z']'";
  private static final String SIMPLE_LOGGER_DEFAULT_LOG_LEVEL_PROPERTY =
      "io.opentelemetry.javaagent.slf4j.simpleLogger.defaultLogLevel";
  private static final String SIMPLE_LOGGER_PREFIX =
      "io.opentelemetry.javaagent.slf4j.simpleLogger.log.";

  static void configureLogger() {
    setSystemPropertyDefault(SIMPLE_LOGGER_SHOW_DATE_TIME_PROPERTY, "true");
    setSystemPropertyDefault(
        SIMPLE_LOGGER_DATE_TIME_FORMAT_PROPERTY, SIMPLE_LOGGER_DATE_TIME_FORMAT_DEFAULT);

    if (isDebugMode()) {
      setSystemPropertyDefault(SIMPLE_LOGGER_DEFAULT_LOG_LEVEL_PROPERTY, "DEBUG");
      // suppress a couple of verbose ClassNotFoundException stack traces logged at debug level
      setSystemPropertyDefault(SIMPLE_LOGGER_PREFIX + "io.perfmark.PerfMark", "INFO");
      setSystemPropertyDefault(SIMPLE_LOGGER_PREFIX + "io.grpc.Context", "INFO");
      setSystemPropertyDefault(SIMPLE_LOGGER_PREFIX + "io.grpc.internal.ServerImplBuilder", "INFO");
      setSystemPropertyDefault(SIMPLE_LOGGER_PREFIX + "io.grpc.ManagedChannelRegistry", "INFO");
      setSystemPropertyDefault(
          SIMPLE_LOGGER_PREFIX + "io.netty.util.internal.NativeLibraryLoader", "INFO");
      setSystemPropertyDefault(
          SIMPLE_LOGGER_PREFIX + "io.grpc.internal.ManagedChannelImplBuilder", "INFO");
    } else {
      // by default muzzle warnings are turned off
      setSystemPropertyDefault(SIMPLE_LOGGER_PREFIX + "muzzleMatcher", "OFF");
    }
  }

  private static void setSystemPropertyDefault(String property, String value) {
    if (System.getProperty(property) == null) {
      System.setProperty(property, value);
    }
  }

  /**
   * Determine if we should log in debug level according to otel.javaagent.debug
   *
   * @return true if we should
   */
  private static boolean isDebugMode() {
    String tracerDebugLevelSysprop = "otel.javaagent.debug";
    String tracerDebugLevelProp = System.getProperty(tracerDebugLevelSysprop);

    if (tracerDebugLevelProp != null) {
      return Boolean.parseBoolean(tracerDebugLevelProp);
    }

    String tracerDebugLevelEnv =
        System.getenv(tracerDebugLevelSysprop.replace('.', '_').toUpperCase(Locale.ROOT));

    if (tracerDebugLevelEnv != null) {
      return Boolean.parseBoolean(tracerDebugLevelEnv);
    }
    return false;
  }

  private LoggingConfigurer() {}
}
