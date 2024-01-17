/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.internal.classloader;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.extendsClass;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.javaagent.bootstrap.HelperResources;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * Instruments {@link ClassLoader} to have calls to get resources intercepted and check our map of
 * helper resources that is filled by instrumentation when they need helpers.
 *
 * <p>We currently only intercept {@link ClassLoader#getResources(String)} because this is the case
 * we are currently always interested in, where it's used for service loading.
 */
public class ResourceInjectionInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return extendsClass(named("java.lang.ClassLoader"));
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod().and(named("getResources")).and(takesArguments(String.class)),
        ResourceInjectionInstrumentation.class.getName() + "$GetResourcesAdvice");
  }

  @SuppressWarnings("unused")
  public static class GetResourcesAdvice {

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void onExit(
        @Advice.This ClassLoader classLoader,
        @Advice.Argument(0) String name,
        @Advice.Return(readOnly = false) Enumeration<URL> resources) {
      URL helper = HelperResources.load(classLoader, name);
      if (helper == null) {
        return;
      }

      if (!resources.hasMoreElements()) {
        resources = Collections.enumeration(Collections.singleton(helper));
        return;
      }

      List<URL> result = Collections.list(resources);
      boolean duplicate = false;
      for (URL loadedUrl : result) {
        if (helper.sameFile(loadedUrl)) {
          duplicate = true;
          break;
        }
      }
      if (!duplicate) {
        result.add(helper);
      }

      resources = Collections.enumeration(result);
    }
  }
}
