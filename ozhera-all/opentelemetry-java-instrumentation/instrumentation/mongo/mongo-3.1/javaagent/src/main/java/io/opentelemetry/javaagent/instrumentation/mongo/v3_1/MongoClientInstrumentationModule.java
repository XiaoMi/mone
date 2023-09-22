/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.mongo.v3_1;

import static java.util.Collections.singletonList;
import static net.bytebuddy.matcher.ElementMatchers.declaresMethod;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.google.auto.service.AutoService;
import com.mongodb.MongoClientOptions;
import com.mongodb.event.CommandListener;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import java.util.List;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

@AutoService(InstrumentationModule.class)
public class MongoClientInstrumentationModule extends InstrumentationModule {

  public MongoClientInstrumentationModule() {
    super("mongo", "mongo-3.1");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return singletonList(new MongoClientOptionsBuilderInstrumentation());
  }

  private static final class MongoClientOptionsBuilderInstrumentation
      implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
      return named("com.mongodb.MongoClientOptions$Builder")
          .and(
              declaresMethod(
                  named("addCommandListener")
                      .and(isPublic())
                      .and(
                          takesArguments(1)
                              .and(takesArgument(0, named("com.mongodb.event.CommandListener"))))));
    }

    @Override
    public void transform(TypeTransformer transformer) {
      transformer.applyAdviceToMethod(
          isMethod().and(isPublic()).and(named("build")).and(takesArguments(0)),
          MongoClientInstrumentationModule.class.getName() + "$MongoClientAdvice");
    }
  }

  @SuppressWarnings("unused")
  public static class MongoClientAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void injectTraceListener(
        @Advice.This MongoClientOptions.Builder builder,
        @Advice.FieldValue("commandListeners") List<CommandListener> commandListeners) {
      for (CommandListener commandListener : commandListeners) {
        if (commandListener == MongoInstrumentationSingletons.LISTENER) {
          return;
        }
      }
      builder.addCommandListener(MongoInstrumentationSingletons.LISTENER);
    }
  }
}
