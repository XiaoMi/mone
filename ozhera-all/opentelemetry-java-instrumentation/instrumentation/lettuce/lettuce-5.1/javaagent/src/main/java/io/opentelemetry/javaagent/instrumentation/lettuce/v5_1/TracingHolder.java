/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.lettuce.v5_1;

import io.lettuce.core.tracing.Tracing;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.lettuce.v5_1.LettuceTracing;

public final class TracingHolder {

  public static final Tracing TRACING =
      LettuceTracing.create(GlobalOpenTelemetry.get()).newTracing();

  private TracingHolder() {}
}
