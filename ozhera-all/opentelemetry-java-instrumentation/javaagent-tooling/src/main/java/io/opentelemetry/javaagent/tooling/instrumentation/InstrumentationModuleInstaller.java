/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.tooling.instrumentation;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.failSafe;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.tooling.HelperInjector;
import io.opentelemetry.javaagent.tooling.TransformSafeLogger;
import io.opentelemetry.javaagent.tooling.Utils;
import io.opentelemetry.javaagent.tooling.context.FieldBackedProvider;
import io.opentelemetry.javaagent.tooling.context.InstrumentationContextProvider;
import io.opentelemetry.javaagent.tooling.context.NoopContextProvider;
import io.opentelemetry.javaagent.tooling.muzzle.matcher.Mismatch;
import io.opentelemetry.javaagent.tooling.muzzle.matcher.ReferenceMatcher;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InstrumentationModuleInstaller {
  private static final TransformSafeLogger log =
      TransformSafeLogger.getLogger(InstrumentationModule.class);
  private static final Logger muzzleLog = LoggerFactory.getLogger("muzzleMatcher");

  // Added here instead of AgentInstaller's ignores because it's relatively
  // expensive. https://github.com/DataDog/dd-trace-java/pull/1045
  public static final ElementMatcher.Junction<AnnotationSource> NOT_DECORATOR_MATCHER =
      not(isAnnotatedWith(named("javax.decorator.Decorator")));

  AgentBuilder install(
      InstrumentationModule instrumentationModule, AgentBuilder parentAgentBuilder) {
    // 简化排除module的jvm参数
    boolean exclude = InstrumentationModule.EXCLUDE_MODULE.contains(instrumentationModule.instrumentationName());
    log.warn("Instrumentation:{} {}",instrumentationModule.instrumentationName(),instrumentationModule.isEnabled() && !exclude);
    if (!instrumentationModule.isEnabled() || exclude) {
      log.debug("Instrumentation {} is disabled", instrumentationModule.instrumentationName());
      return parentAgentBuilder;
    }
    List<String> helperClassNames = instrumentationModule.getMuzzleHelperClassNames();
    List<String> helperResourceNames = instrumentationModule.helperResourceNames();
    List<TypeInstrumentation> typeInstrumentations = instrumentationModule.typeInstrumentations();
    if (typeInstrumentations.isEmpty()) {
      if (!helperClassNames.isEmpty() || !helperResourceNames.isEmpty()) {
        log.warn(
            "Helper classes and resources won't be injected if no types are instrumented: {}",
            instrumentationModule.instrumentationName());
      }

      return parentAgentBuilder;
    }

    ElementMatcher.Junction<ClassLoader> moduleClassLoaderMatcher =
        instrumentationModule.classLoaderMatcher();
    MuzzleMatcher muzzleMatcher = new MuzzleMatcher(instrumentationModule, helperClassNames);
    AgentBuilder.Transformer helperInjector =
        new HelperInjector(
            instrumentationModule.instrumentationName(),
            helperClassNames,
            helperResourceNames,
            Utils.getExtensionsClassLoader());
    InstrumentationContextProvider contextProvider =
        createInstrumentationContextProvider(instrumentationModule);

    AgentBuilder agentBuilder = parentAgentBuilder;
    for (TypeInstrumentation typeInstrumentation : typeInstrumentations) {
      AgentBuilder.Identified.Extendable extendableAgentBuilder =
          agentBuilder
              .type(
                  failSafe(
                      typeInstrumentation.typeMatcher(),
                      "Instrumentation type matcher unexpected exception: " + getClass().getName()),
                  failSafe(
                      moduleClassLoaderMatcher.and(typeInstrumentation.classLoaderOptimization()),
                      "Instrumentation class loader matcher unexpected exception: "
                          + getClass().getName()))
              .and(NOT_DECORATOR_MATCHER)
              .and(muzzleMatcher)
              .transform(ConstantAdjuster.instance())
              .transform(helperInjector);
      extendableAgentBuilder = contextProvider.instrumentationTransformer(extendableAgentBuilder);
      TypeTransformerImpl typeTransformer = new TypeTransformerImpl(extendableAgentBuilder);
      typeInstrumentation.transform(typeTransformer);
      extendableAgentBuilder = typeTransformer.getAgentBuilder();
      extendableAgentBuilder = contextProvider.additionalInstrumentation(extendableAgentBuilder);

      agentBuilder = extendableAgentBuilder;
    }

    return agentBuilder;
  }

  private static InstrumentationContextProvider createInstrumentationContextProvider(
      InstrumentationModule instrumentationModule) {
    Map<String, String> contextStore = instrumentationModule.getMuzzleContextStoreClasses();
    if (!contextStore.isEmpty()) {
      return new FieldBackedProvider(instrumentationModule.getClass(), contextStore);
    } else {
      return NoopContextProvider.INSTANCE;
    }
  }

  /**
   * A ByteBuddy matcher that decides whether this instrumentation should be applied. Calls
   * generated {@link ReferenceMatcher}: if any mismatch with the passed {@code classLoader} is
   * found this instrumentation is skipped.
   */
  private static class MuzzleMatcher implements AgentBuilder.RawMatcher {
    private final InstrumentationModule instrumentationModule;
    private final List<String> helperClassNames;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private volatile ReferenceMatcher referenceMatcher;

    private MuzzleMatcher(
        InstrumentationModule instrumentationModule, List<String> helperClassNames) {
      this.instrumentationModule = instrumentationModule;
      this.helperClassNames = helperClassNames;
    }

    @Override
    public boolean matches(
        TypeDescription typeDescription,
        ClassLoader classLoader,
        JavaModule module,
        Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain) {
      ReferenceMatcher muzzle = getReferenceMatcher();
      boolean isMatch = muzzle.matches(classLoader);

      if (!isMatch) {
        if (muzzleLog.isWarnEnabled()) {
          muzzleLog.warn(
              "Instrumentation skipped, mismatched references were found: {} [class {}] on {}",
              instrumentationModule.instrumentationName(),
              instrumentationModule.getClass().getName(),
              classLoader);
          List<Mismatch> mismatches = muzzle.getMismatchedReferenceSources(classLoader);
          for (Mismatch mismatch : mismatches) {
            muzzleLog.warn("-- {}", mismatch);
          }
        }
      } else {
        if (log.isDebugEnabled()) {
          log.debug(
              "Applying instrumentation: {} [class {}] on {}",
              instrumentationModule.instrumentationName(),
              instrumentationModule.getClass().getName(),
              classLoader);
        }
      }

      return isMatch;
    }

    // ReferenceMatcher internally caches the muzzle check results per classloader, that's why we
    // keep its instance in a field
    // it is lazily created to avoid unnecessarily loading the muzzle references from the module
    // during the agent setup
    private ReferenceMatcher getReferenceMatcher() {
      if (initialized.compareAndSet(false, true)) {
        referenceMatcher =
            new ReferenceMatcher(
                helperClassNames,
                instrumentationModule.getMuzzleReferences(),
                instrumentationModule::isHelperClass);
      }
      return referenceMatcher;
    }
  }
}
