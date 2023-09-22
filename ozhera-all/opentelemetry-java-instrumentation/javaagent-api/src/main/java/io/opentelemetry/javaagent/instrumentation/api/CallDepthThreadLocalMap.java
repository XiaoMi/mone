/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.api;

/**
 * Utility to track nested instrumentation.
 *
 * <p>For example, this can be used to track nested calls to super() in constructors by calling
 * #incrementCallDepth at the beginning of each constructor.
 *
 * <p>This works the following way. When you enter some method that you want to track, you call
 * {@link #incrementCallDepth} method. If returned number is larger than 0, then you have already
 * been in this method and are in recursive call now. When you then leave the method, you call
 * {@link #decrementCallDepth} method. If returned number is larger than 0, then you have already
 * been in this method and are in recursive call now.
 *
 * <p>In short, the semantic of both methods is the same: they will return value 0 if and only if
 * current method invocation is the first one for the current call stack.
 */
public final class CallDepthThreadLocalMap {

  private static final ClassValue<ThreadLocalDepth> TLS =
      new ClassValue<ThreadLocalDepth>() {
        @Override
        protected ThreadLocalDepth computeValue(Class<?> type) {
          return new ThreadLocalDepth();
        }
      };

  public static CallDepth getCallDepth(Class<?> k) {
    return TLS.get(k).get();
  }

  public static int incrementCallDepth(Class<?> k) {
    return TLS.get(k).get().getAndIncrement();
  }

  public static int decrementCallDepth(Class<?> k) {
    return TLS.get(k).get().decrementAndGet();
  }

  public static void reset(Class<?> k) {
    TLS.get(k).get().reset();
  }

  private static final class ThreadLocalDepth extends ThreadLocal<CallDepth> {
    @Override
    protected CallDepth initialValue() {
      return new CallDepth();
    }
  }

  private CallDepthThreadLocalMap() {}
}
