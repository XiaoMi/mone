/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rxjava2;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.test.utils.TraceUtils;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This test creates the specified number of traces with three spans: 1) Outer (root) span 2) Middle
 * span, child of outer, created in success handler of the chain subscribed to in the context of the
 * outer span (with some delay and map thrown in for good measure) 3) Inner span, child of middle,
 * created in the success handler of a new chain started and subscribed to in the the middle span
 *
 * <p>The varying delays between the stages where each span is created should guarantee that
 * scheduler threads handling various stages of the chain will have to alternate between contexts
 * from different traces.
 */
public class RxJava2ConcurrencyTestHelper {
  public static void launchAndWait(Scheduler scheduler, int iterations, long timeoutMillis) {
    CountDownLatch latch = new CountDownLatch(iterations);

    for (int i = 0; i < iterations; i++) {
      launchOuter(new Iteration(scheduler, latch, i));
    }

    try {
      // Continue even on timeout so the test assertions can show what is missing
      //noinspection ResultOfMethodCallIgnored
      latch.await(timeoutMillis, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  private static void launchOuter(Iteration iteration) {
    TraceUtils.runUnderTrace(
        "outer",
        () -> {
          Span.current().setAttribute("iteration", iteration.index);

          Single.fromCallable(() -> iteration)
              .subscribeOn(iteration.scheduler)
              .observeOn(iteration.scheduler)
              // Use varying delay so that different stages of the chain would alternate.
              .delay(iteration.index % 10, TimeUnit.MILLISECONDS, iteration.scheduler)
              .map((it) -> it)
              .delay(iteration.index % 10, TimeUnit.MILLISECONDS, iteration.scheduler)
              .doOnSuccess(RxJava2ConcurrencyTestHelper::launchInner)
              .subscribe();

          return null;
        });
  }

  private static void launchInner(Iteration iteration) {
    TraceUtils.runUnderTrace(
        "middle",
        () -> {
          Span.current().setAttribute("iteration", iteration.index);

          Single.fromCallable(() -> iteration)
              .subscribeOn(iteration.scheduler)
              .observeOn(iteration.scheduler)
              .delay(iteration.index % 10, TimeUnit.MILLISECONDS, iteration.scheduler)
              .doOnSuccess(
                  (it) -> {
                    TraceUtils.runUnderTrace(
                        "inner",
                        () -> {
                          Span.current().setAttribute("iteration", it.index);
                          return null;
                        });
                    it.countDown.countDown();
                  })
              .subscribe();

          return null;
        });
  }

  private static class Iteration {
    public final Scheduler scheduler;
    public final CountDownLatch countDown;
    public final int index;

    private Iteration(Scheduler scheduler, CountDownLatch countDown, int index) {
      this.scheduler = scheduler;
      this.countDown = countDown;
      this.index = index;
    }
  }
}
