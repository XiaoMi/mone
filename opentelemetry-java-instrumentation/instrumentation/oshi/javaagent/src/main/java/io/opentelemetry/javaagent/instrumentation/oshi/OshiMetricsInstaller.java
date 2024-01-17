/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.oshi;

import com.google.auto.service.AutoService;
import io.opentelemetry.instrumentation.api.config.Config;
import io.opentelemetry.javaagent.extension.AgentListener;
import java.lang.reflect.Method;
import java.util.Collections;

/**
 * An {@link AgentListener} that enables oshi metrics during agent startup if oshi is present on the
 * system classpath.
 */
@AutoService(AgentListener.class)
public class OshiMetricsInstaller implements AgentListener {
  @Override
  public void afterAgent(Config config) {
    if (config.isInstrumentationEnabled(
        Collections.singleton("oshi"), /* defaultEnabled= */ true)) {
      try {
        // Call oshi.SystemInfo.getCurrentPlatformEnum() to activate SystemMetrics.
        // Oshi instrumentation will intercept this call and enable SystemMetrics.
        Class<?> oshiSystemInfoClass =
            ClassLoader.getSystemClassLoader().loadClass("oshi.SystemInfo");
        Method getCurrentPlatformEnumMethod =
            oshiSystemInfoClass.getMethod("getCurrentPlatformEnum");
        getCurrentPlatformEnumMethod.invoke(null);
      } catch (Throwable ex) {
        // OK
      }
    }
  }
}
