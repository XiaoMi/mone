/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jdbc.driver;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;

public final class DriverSingletons {

    private static final String INSTRUMENTATION_NAME = "java.sql.Driver";

    private static final Instrumenter<DriverRequest, Void> INSTRUMENTER;

    static {
        SpanNameExtractor<DriverRequest> spanName = request -> "dbDriver";

        AttributesExtractor extractor = new AttributesExtractor<DriverRequest, Void>() {
            @Override
            protected void onStart(AttributesBuilder attributes, DriverRequest o) {
                attributes.put("db.driver.domainPort", o.getDomainPort());
                attributes.put("db.driver.userName", o.getUserName());
                attributes.put("db.driver.password", o.getPassword());
                attributes.put("db.driver.type", o.getType());
                attributes.put("db.driver.dbName", o.getDataBaseName());
            }

            @Override
            protected void onEnd(AttributesBuilder attributes, DriverRequest o, Void o2) {

            }
        };
        INSTRUMENTER =
                Instrumenter.<DriverRequest, Void>newBuilder(
                        GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanName)
                        .addAttributesExtractor(extractor)
                        .newInstrumenter(SpanKindExtractor.alwaysInternal());
    }

    public static Instrumenter<DriverRequest, Void> instrumenter() {
        return INSTRUMENTER;
    }

    private DriverSingletons() {
    }
}
