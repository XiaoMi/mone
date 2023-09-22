/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.hera;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;

import java.util.List;

import static java.util.Arrays.asList;

@AutoService(InstrumentationModule.class)
public class HeraInstrumentationModule extends InstrumentationModule {
    public HeraInstrumentationModule() {
        super("hera");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return asList(new TraceIdInstrumentation(),new HeraContextInstrumentation());
    }
}
