/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.tesla;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;

public final class TeslaSingletons {

    private static final String INSTRUMENTATION_NAME = "run.mone.tesla";

    private static final Instrumenter<TeslaRequest, Void> INSTRUMENTER;

    static {
        SpanNameExtractor<TeslaRequest> spanName = request -> request.getUri();

        AttributesExtractor extractor = new AttributesExtractor<TeslaRequest, Void>() {
            @Override
            protected void onStart(AttributesBuilder attributes, TeslaRequest request) {
                if(request.isApiInfoIsNull()) {
                    String uri = request.getUri();
                    if (uri == null || uri.isEmpty()) {
                        attributes.put("http.unknow.uri", uri);
                    } else {
                        attributes.put("http.apiinfo.isnull", true);
                    }
                }
            }

            @Override
            protected void onEnd(AttributesBuilder attributes, TeslaRequest request, Void o2) {

            }
        };
        INSTRUMENTER =
                Instrumenter.<TeslaRequest, Void>newBuilder(
                        GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanName)
                        .addAttributesExtractor(extractor)
                        .newInstrumenter(SpanKindExtractor.alwaysInternal());
    }

    public static Instrumenter<TeslaRequest, Void> instrumenter() {
        return INSTRUMENTER;
    }

    private TeslaSingletons() {
    }
}
