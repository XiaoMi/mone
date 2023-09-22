/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jdbc;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.db.DbAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.db.DbSpanNameExtractor;
import io.opentelemetry.javaagent.instrumentation.api.instrumenter.PeerServiceAttributesExtractor;

public final class JdbcSingletons {
  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.javaagent.jdbc";

  private static final Instrumenter<DbRequest, Void> INSTRUMENTER;

  static {
    DbAttributesExtractor<DbRequest, Void> dbAttributesExtractor = new JdbcAttributesExtractor();
    SpanNameExtractor<DbRequest> spanName = DbSpanNameExtractor.create(dbAttributesExtractor);
    JdbcNetAttributesExtractor netAttributesExtractor = new JdbcNetAttributesExtractor();

    INSTRUMENTER =
        Instrumenter.<DbRequest, Void>newBuilder(
                GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanName)
            .addAttributesExtractor(dbAttributesExtractor)
            .addAttributesExtractor(netAttributesExtractor)
            .addAttributesExtractor(PeerServiceAttributesExtractor.create(netAttributesExtractor))
            .newInstrumenter(SpanKindExtractor.alwaysClient());
  }

  public static Instrumenter<DbRequest, Void> instrumenter() {
    return INSTRUMENTER;
  }

  private JdbcSingletons() {}
}
