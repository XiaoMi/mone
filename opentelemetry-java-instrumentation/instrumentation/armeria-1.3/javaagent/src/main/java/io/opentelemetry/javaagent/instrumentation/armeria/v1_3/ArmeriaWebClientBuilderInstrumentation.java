/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.armeria.v1_3;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

import com.linecorp.armeria.client.WebClientBuilder;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import java.util.function.Function;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class ArmeriaWebClientBuilderInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("com.linecorp.armeria.client.WebClientBuilder");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod().and(isPublic()).and(named("decorator").and(takesArgument(0, Function.class))),
        ArmeriaWebClientBuilderInstrumentation.class.getName() + "$SuppressDecoratorAdvice");
    transformer.applyAdviceToMethod(
        isMethod().and(isPublic()).and(named("build")),
        ArmeriaWebClientBuilderInstrumentation.class.getName() + "$BuildAdvice");
  }

  // Intercept calls from app to register decorator and suppress them to avoid registering
  // multiple decorators, one from user app and one from our auto instrumentation. Otherwise, we
  // will end up with double telemetry.
  // https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/903
  @SuppressWarnings("unused")
  public static class SuppressDecoratorAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean suppressDecorator(@Advice.Argument(0) Function<?, ?> decorator) {
      return decorator != ArmeriaSingletons.CLIENT_DECORATOR;
    }

    @Advice.OnMethodExit
    public static void handleSuppression(
        @Advice.This WebClientBuilder builder,
        @Advice.Enter boolean suppressed,
        @Advice.Return(readOnly = false) WebClientBuilder returned) {
      if (suppressed) {
        returned = builder;
      }
    }
  }

  @SuppressWarnings("unused")
  public static class BuildAdvice {

    @Advice.OnMethodEnter
    public static void build(@Advice.This WebClientBuilder builder) {
      builder.decorator(ArmeriaSingletons.CLIENT_DECORATOR);
    }
  }
}
