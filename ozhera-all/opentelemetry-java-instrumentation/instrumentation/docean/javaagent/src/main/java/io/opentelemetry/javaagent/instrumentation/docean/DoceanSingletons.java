/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.docean;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;

public final class DoceanSingletons {

    private static final String INSTRUMENTATION_NAME = "com.xiaomi.mone.docean";

    private static final Instrumenter<DoceanRequest, Void> INSTRUMENTER;

    static {
        SpanNameExtractor<DoceanRequest> spanName = request -> "Docean" + ":" + request.getMethodName();

        AttributesExtractor extractor = new AttributesExtractor<DoceanRequest, Void>() {
            @Override
            protected void onStart(AttributesBuilder attributes, DoceanRequest o) {
                attributes.put("class.name", o.getClazz());
                attributes.put("method.name", o.getMethodName());
            }

            @Override
            protected void onEnd(AttributesBuilder attributes, DoceanRequest o, Void o2) {

            }
        };
        INSTRUMENTER =
                Instrumenter.<DoceanRequest, Void>newBuilder(
                        GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanName)
                        .addAttributesExtractor(extractor)
                        .newInstrumenter(SpanKindExtractor.alwaysInternal());
    }

    public static Instrumenter<DoceanRequest, Void> instrumenter() {
        return INSTRUMENTER;
    }

    private DoceanSingletons() {
    }
}
