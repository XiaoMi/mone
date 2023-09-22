/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jedisfactory.v3_5;


import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;

import java.util.ArrayList;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class JedisFactory25InstrumentationModule extends InstrumentationModule {

  public JedisFactory25InstrumentationModule() {
    super("jedis-factory", "jedis-factory-2.5");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    List<TypeInstrumentation> list = new ArrayList<>();
    list.add(new JedisFactory25Instrumentation());
    return list;
  }
}
