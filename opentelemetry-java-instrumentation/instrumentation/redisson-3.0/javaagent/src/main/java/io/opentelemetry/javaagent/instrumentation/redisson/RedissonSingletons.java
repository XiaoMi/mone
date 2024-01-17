/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.redisson;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.db.DbAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.db.DbSpanNameExtractor;

public final class RedissonSingletons {
  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.javaagent.redisson-3.0";

  private static final Instrumenter<RedissonRequest, Void> INSTRUMENTER;

  static {
    DbAttributesExtractor<RedissonRequest, Void> dbAttributesExtractor =
        new RedissonDbAttributesExtractor();
    RedissonNetAttributesExtractor netAttributesExtractor = new RedissonNetAttributesExtractor();
    SpanNameExtractor<RedissonRequest> spanName = DbSpanNameExtractor.create(dbAttributesExtractor);

    INSTRUMENTER =
        Instrumenter.<RedissonRequest, Void>newBuilder(
                GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanName)
            .addAttributesExtractor(dbAttributesExtractor)
            .addAttributesExtractor(netAttributesExtractor)
            .newInstrumenter(SpanKindExtractor.alwaysClient());
  }

  public static Instrumenter<RedissonRequest, Void> instrumenter() {
    return INSTRUMENTER;
  }

  private RedissonSingletons() {}
}
