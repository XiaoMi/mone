/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.log4j.v2_13_2;

import static io.opentelemetry.javaagent.extension.matcher.ClassLoaderMatcher.hasClassesNamed;
import static java.util.Collections.singletonList;
import static net.bytebuddy.matcher.ElementMatchers.named;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

@AutoService(InstrumentationModule.class)
public class Log4j2InstrumentationModule extends InstrumentationModule {
  public Log4j2InstrumentationModule() {
    super("log4j", "log4j-2.13.2");
  }

  @Override
  public List<String> helperResourceNames() {
    return singletonList(
        "META-INF/services/org.apache.logging.log4j.core.util.ContextDataProvider");
  }

  @Override
  public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
    return hasClassesNamed("org.apache.logging.log4j.core.util.ContextDataProvider");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return Arrays.asList(
        new BugFixingInstrumentation(), new ResourceInjectingTypeInstrumentation());
  }

  // A type instrumentation is needed to trigger resource injection.
  public static class ResourceInjectingTypeInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
      // we cannot use ContextDataProvider here because one of the classes that we inject implements
      // this interface, causing the interface to be loaded while it's being transformed, which
      // leads to duplicate class definition error after the interface is transformed and the
      // triggering class loader tries to load it.
      return named("org.apache.logging.log4j.core.impl.ThreadContextDataInjector");
    }

    @Override
    public void transform(TypeTransformer transformer) {
      // Nothing to transform, this type instrumentation is only used for injecting resources.
    }
  }
}
