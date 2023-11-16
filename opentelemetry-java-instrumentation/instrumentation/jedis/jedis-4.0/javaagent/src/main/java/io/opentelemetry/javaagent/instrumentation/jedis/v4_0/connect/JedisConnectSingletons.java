/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jedis.v4_0.connect;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.db.DbAttributesExtractor;
import io.opentelemetry.javaagent.instrumentation.api.instrumenter.PeerServiceAttributesExtractor;

public final class JedisConnectSingletons {
    private static final String INSTRUMENTATION_NAME = "io.opentelemetry.javaagent.jedis-4.0";

    private static final Instrumenter<JedisConnectionRequest, Void> CONNECT_INSTRUMENTER;

    static {
        DbAttributesExtractor<JedisConnectionRequest, Void> connectAttributesExtractor =
                new JedisConnectDbAttributesExtractor();
        SpanNameExtractor<JedisConnectionRequest> connectSpanName = request -> "connect";
        JedisConnectionNetAttributesExtractor connectNetAttributesExtractor = new JedisConnectionNetAttributesExtractor();

        CONNECT_INSTRUMENTER =
                Instrumenter.<JedisConnectionRequest, Void>newBuilder(
                        GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, connectSpanName)
                        .addAttributesExtractor(connectAttributesExtractor)
                        .addAttributesExtractor(connectNetAttributesExtractor)
                        .addAttributesExtractor(PeerServiceAttributesExtractor.create(connectNetAttributesExtractor))
                        .newInstrumenter(SpanKindExtractor.alwaysClient());
    }

    public static Instrumenter<JedisConnectionRequest, Void> connectInstrumenter() {
        return CONNECT_INSTRUMENTER;
    }

    private JedisConnectSingletons() {
    }
}
