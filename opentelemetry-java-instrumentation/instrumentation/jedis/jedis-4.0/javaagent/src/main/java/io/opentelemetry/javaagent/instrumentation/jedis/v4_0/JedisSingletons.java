/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jedis.v4_0;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.db.DbAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.db.DbSpanNameExtractor;
import io.opentelemetry.javaagent.instrumentation.api.instrumenter.PeerServiceAttributesExtractor;

public final class JedisSingletons {
  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.javaagent.jedis-4.0";

  private static final Instrumenter<JedisRequest, Void> INSTRUMENTER;

  static {
    DbAttributesExtractor<JedisRequest, Void> dbAttributesGetter =
            new JedisDbAttributesGetter();
    JedisNetAttributesGetter netAttributesGetter = new JedisNetAttributesGetter();

    INSTRUMENTER =
        Instrumenter.<JedisRequest, Void>newBuilder(
                GlobalOpenTelemetry.get(),
                INSTRUMENTATION_NAME,
                DbSpanNameExtractor.create(dbAttributesGetter))
            .addAttributesExtractor(dbAttributesGetter)
            .addAttributesExtractor(netAttributesGetter)
            .addAttributesExtractor(PeerServiceAttributesExtractor.create(netAttributesGetter))
            .newInstrumenter(SpanKindExtractor.alwaysClient());

  }

  public static Instrumenter<JedisRequest, Void> instrumenter() {
    return INSTRUMENTER;
  }

  private JedisSingletons() {}
}
