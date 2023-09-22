/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.tesla;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.instrumentation.tesla.sidecar.ExecuteProcessorInstrumentation;
import io.opentelemetry.javaagent.instrumentation.tesla.sidecar.SidecarServiceInstrumentation;

import java.util.List;

import static java.util.Arrays.asList;

@AutoService(InstrumentationModule.class)
public class TeslaInstrumentationModule extends InstrumentationModule {

  public TeslaInstrumentationModule() {
    super("tesla", "tesla");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return asList(
            new RequestFilterChainInstrumentation(),
            new FilterContextInstrumentation(),
            new SidecarServiceInstrumentation(),
            new ExecuteProcessorInstrumentation());
  }
}
