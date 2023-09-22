/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

// Includes work from:
/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opentelemetry.instrumentation.reactor;

import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.tracer.async.AsyncSpanEndStrategies;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;
import reactor.core.Scannable;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

/** Based on Spring Sleuth's Reactor instrumentation. */
public final class TracingOperator {

  public static TracingOperator create() {
    return newBuilder().build();
  }

  public static TracingOperatorBuilder newBuilder() {
    return new TracingOperatorBuilder();
  }

  private final boolean captureExperimentalSpanAttributes;

  TracingOperator(boolean captureExperimentalSpanAttributes) {
    this.captureExperimentalSpanAttributes = captureExperimentalSpanAttributes;
  }

  /**
   * Registers a hook that applies to every operator, propagating {@link Context} to downstream
   * callbacks to ensure spans in the {@link Context} are available throughout the lifetime of a
   * reactive stream. This should generally be called in a static initializer block in your
   * application.
   */
  public void registerOnEachOperator() {
    Hooks.onEachOperator(TracingSubscriber.class.getName(), tracingLift());
    AsyncSpanEndStrategies.getInstance()
        .registerStrategy(
            ReactorAsyncSpanEndStrategy.newBuilder()
                .setCaptureExperimentalSpanAttributes(captureExperimentalSpanAttributes)
                .build());
  }

  /** Unregisters the hook registered by {@link #registerOnEachOperator()}. */
  public void resetOnEachOperator() {
    Hooks.resetOnEachOperator(TracingSubscriber.class.getName());
    AsyncSpanEndStrategies.getInstance().unregisterStrategy(ReactorAsyncSpanEndStrategy.class);
  }

  private static <T> Function<? super Publisher<T>, ? extends Publisher<T>> tracingLift() {
    return Operators.lift(new Lifter<>());
  }

  public static class Lifter<T>
      implements BiFunction<Scannable, CoreSubscriber<? super T>, CoreSubscriber<? super T>> {

    @Override
    public CoreSubscriber<? super T> apply(Scannable publisher, CoreSubscriber<? super T> sub) {
      // if Flux/Mono #just, #empty, #error
      if (publisher instanceof Fuseable.ScalarCallable) {
        return sub;
      }
      return new TracingSubscriber<>(sub, sub.currentContext());
    }
  }
}
