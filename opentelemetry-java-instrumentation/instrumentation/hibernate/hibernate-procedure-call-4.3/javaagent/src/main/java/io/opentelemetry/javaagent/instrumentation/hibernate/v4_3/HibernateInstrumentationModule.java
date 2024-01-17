/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.hibernate.v4_3;

import static java.util.Arrays.asList;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class HibernateInstrumentationModule extends InstrumentationModule {
  public HibernateInstrumentationModule() {
    super("hibernate", "hibernate-4.3");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return asList(new ProcedureCallInstrumentation(), new SessionInstrumentation());
  }
}
