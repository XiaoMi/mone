/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import java.util.concurrent.Callable;

class LambdaGen {

  static Callable<?> wrapCallable(Callable<?> callable) {
    return () -> callable.call();
  }

  static Runnable wrapRunnable(Runnable runnable) {
    return () -> runnable.run();
  }
}
