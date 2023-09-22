/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.docean;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;

import java.util.List;

import static java.util.Arrays.asList;

@AutoService(InstrumentationModule.class)
public class DoceanInstrumentationModule extends InstrumentationModule {
    public DoceanInstrumentationModule() {
        super("docean");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return asList(new DoceanInstrumentation());
    }
}
