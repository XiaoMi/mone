/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jedis.v4_0;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;

import static io.opentelemetry.javaagent.extension.matcher.ClassLoaderMatcher.hasClassesNamed;
import static java.util.Arrays.asList;

@AutoService(InstrumentationModule.class)
public class JedisInstrumentationModule extends InstrumentationModule {

  public JedisInstrumentationModule() {
    super("jedis", "jedis-4.0");
  }

  @Override
  public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
    return hasClassesNamed("redis.clients.jedis.CommandArguments");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return asList(new JedisConnectionInstrumentation(), new JedisInstrumentation());
  }
}
