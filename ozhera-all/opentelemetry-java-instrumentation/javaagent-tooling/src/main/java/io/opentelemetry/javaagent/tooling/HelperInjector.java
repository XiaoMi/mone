/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.tooling;

import static io.opentelemetry.javaagent.extension.matcher.ClassLoaderMatcher.BOOTSTRAP_CLASSLOADER;

import io.opentelemetry.instrumentation.api.caching.Cache;
import io.opentelemetry.javaagent.bootstrap.HelperResources;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.file.Files;
import java.security.SecureClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.utility.JavaModule;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Injects instrumentation helper classes into the user's classloader. */
public class HelperInjector implements Transformer {

  private static final Logger log = LoggerFactory.getLogger(HelperInjector.class);

  // Need this because we can't put null into the injectedClassLoaders map.
  private static final ClassLoader BOOTSTRAP_CLASSLOADER_PLACEHOLDER =
      new SecureClassLoader(null) {
        @Override
        public String toString() {
          return "<bootstrap>";
        }
      };

  private static final Cache<Class<?>, Boolean> injectedClasses =
      Cache.newBuilder().setWeakKeys().build();

  private final String requestingName;

  private final Set<String> helperClassNames;
  private final Set<String> helperResourceNames;
  @Nullable private final ClassLoader helpersSource;
  private final Map<String, byte[]> dynamicTypeMap = new LinkedHashMap<>();

  private final Cache<ClassLoader, Boolean> injectedClassLoaders =
      Cache.newBuilder().setWeakKeys().build();

  private final List<WeakReference<Object>> helperModules = new CopyOnWriteArrayList<>();

  /**
   * Construct HelperInjector.
   *
   * @param helperClassNames binary names of the helper classes to inject. These class names must be
   *     resolvable by the classloader returned by
   *     io.opentelemetry.javaagent.tooling.Utils#getAgentClassLoader(). Classes are injected in the
   *     order provided. This is important if there is interdependency between helper classes that
   *     requires them to be injected in a specific order. And be careful, the class's package in
   *     library will be renamed like 'io.opentelemetry.instrumentation' to
   *     'io.opentelemetry.javaagent.shaded.instrumentation'
   */
  public HelperInjector(
      String requestingName,
      List<String> helperClassNames,
      List<String> helperResourceNames,
      ClassLoader helpersSource) {
    this.requestingName = requestingName;

    this.helperClassNames = new LinkedHashSet<>(helperClassNames);
    this.helperResourceNames = new LinkedHashSet<>(helperResourceNames);
    this.helpersSource = helpersSource;
  }

  public HelperInjector(String requestingName, Map<String, byte[]> helperMap) {
    this.requestingName = requestingName;

    this.helperClassNames = helperMap.keySet();
    this.dynamicTypeMap.putAll(helperMap);

    this.helperResourceNames = Collections.emptySet();
    this.helpersSource = null;
  }

  public static HelperInjector forDynamicTypes(
      String requestingName, Collection<DynamicType.Unloaded<?>> helpers) {
    Map<String, byte[]> bytes = new HashMap<>(helpers.size());
    for (DynamicType.Unloaded<?> helper : helpers) {
      bytes.put(helper.getTypeDescription().getName(), helper.getBytes());
    }
    return new HelperInjector(requestingName, bytes);
  }

  private Map<String, byte[]> getHelperMap() throws IOException {
    if (dynamicTypeMap.isEmpty()) {
      Map<String, byte[]> classnameToBytes = new LinkedHashMap<>();

      ClassFileLocator locator = ClassFileLocator.ForClassLoader.of(helpersSource);

      for (String helperClassName : helperClassNames) {
        byte[] classBytes = locator.locate(helperClassName).resolve();
        classnameToBytes.put(helperClassName, classBytes);
      }

      return classnameToBytes;
    } else {
      return dynamicTypeMap;
    }
  }

  @Override
  public DynamicType.Builder<?> transform(
      DynamicType.Builder<?> builder,
      TypeDescription typeDescription,
      ClassLoader classLoader,
      JavaModule module) {
    if (!helperClassNames.isEmpty()) {
      if (classLoader == BOOTSTRAP_CLASSLOADER) {
        classLoader = BOOTSTRAP_CLASSLOADER_PLACEHOLDER;
      }

      injectedClassLoaders.computeIfAbsent(
          classLoader,
          cl -> {
            try {
              log.debug("Injecting classes onto classloader {} -> {}", cl, helperClassNames);

              Map<String, byte[]> classnameToBytes = getHelperMap();
              Map<String, Class<?>> classes;
              if (cl == BOOTSTRAP_CLASSLOADER_PLACEHOLDER) {
                classes = injectBootstrapClassLoader(classnameToBytes);
              } else {
                classes = injectClassLoader(cl, classnameToBytes);
              }

              classes.values().forEach(c -> injectedClasses.put(c, Boolean.TRUE));

              // All agent helper classes are in the unnamed module
              // And there's exactly one unnamed module per classloader
              // Use the module of the first class for convenience
              if (JavaModule.isSupported()) {
                JavaModule javaModule = JavaModule.ofType(classes.values().iterator().next());
                helperModules.add(new WeakReference<>(javaModule.unwrap()));
              }
            } catch (Exception e) {
              if (log.isErrorEnabled()) {
                log.error(
                    "Error preparing helpers while processing {} for {}. Failed to inject helper classes into instance {}",
                    typeDescription,
                    requestingName,
                    cl,
                    e);
              }
              throw new IllegalStateException(e);
            }
            return true;
          });

      ensureModuleCanReadHelperModules(module);
    }

    if (!helperResourceNames.isEmpty()) {
      for (String resourceName : helperResourceNames) {
        URL resource = helpersSource.getResource(resourceName);
        if (resource == null) {
          log.debug("Helper resource {} requested but not found.", resourceName);
          continue;
        }

        log.debug("Injecting resource onto classloader {} -> {}", classLoader, resourceName);
        HelperResources.register(classLoader, resourceName, resource);
      }
    }

    return builder;
  }

  private static Map<String, Class<?>> injectBootstrapClassLoader(
      Map<String, byte[]> classnameToBytes) throws IOException {
    // Mar 2020: Since we're proactively cleaning up tempDirs, we cannot share dirs per thread.
    // If this proves expensive, we could do a per-process tempDir with
    // a reference count -- but for now, starting simple.

    // Failures to create a tempDir are propagated as IOException and handled by transform
    File tempDir = createTempDir();
    try {
      return ClassInjector.UsingInstrumentation.of(
              tempDir,
              ClassInjector.UsingInstrumentation.Target.BOOTSTRAP,
              AgentInstaller.getInstrumentation())
          .injectRaw(classnameToBytes);
    } finally {
      // Delete fails silently
      deleteTempDir(tempDir);
    }
  }

  private static Map<String, Class<?>> injectClassLoader(
      ClassLoader classLoader, Map<String, byte[]> classnameToBytes) {
    return new ClassInjector.UsingReflection(classLoader).injectRaw(classnameToBytes);
  }

  // JavaModule.equals doesn't work for some reason
  @SuppressWarnings("ReferenceEquality")
  private void ensureModuleCanReadHelperModules(JavaModule target) {
    if (JavaModule.isSupported() && target != JavaModule.UNSUPPORTED && target.isNamed()) {
      for (WeakReference<Object> helperModuleReference : helperModules) {
        Object realModule = helperModuleReference.get();
        if (realModule != null) {
          JavaModule helperModule = JavaModule.of(realModule);

          if (!target.canRead(helperModule)) {
            log.debug("Adding module read from {} to {}", target, helperModule);
            ClassInjector.UsingInstrumentation.redefineModule(
                AgentInstaller.getInstrumentation(),
                target,
                Collections.singleton(helperModule),
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptySet(),
                Collections.emptyMap());
          }
        }
      }
    }
  }

  private static File createTempDir() throws IOException {
    return Files.createTempDirectory("opentelemetry-temp-jars").toFile();
  }

  private static void deleteTempDir(File file) {
    // Not using Files.delete for deleting the directory because failures
    // create Exceptions which may prove expensive.  Instead using the
    // older File API which simply returns a boolean.
    boolean deleted = file.delete();
    if (!deleted) {
      file.deleteOnExit();
    }
  }

  public static boolean isInjectedClass(Class<?> c) {
    return Boolean.TRUE.equals(injectedClasses.get(c));
  }
}
