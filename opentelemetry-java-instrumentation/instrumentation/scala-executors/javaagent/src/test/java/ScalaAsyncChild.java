/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import scala.concurrent.forkjoin.ForkJoinTask;

public class ScalaAsyncChild extends ForkJoinTask implements Runnable, Callable {
  private static final Tracer tracer = GlobalOpenTelemetry.getTracer("test");

  private final AtomicBoolean blockThread;
  private final boolean doTraceableWork;
  private final CountDownLatch latch = new CountDownLatch(1);

  public ScalaAsyncChild() {
    this(/* doTraceableWork= */ true, /* blockThread= */ false);
  }

  public ScalaAsyncChild(boolean doTraceableWork, boolean blockThread) {
    this.doTraceableWork = doTraceableWork;
    this.blockThread = new AtomicBoolean(blockThread);
  }

  @Override
  public Object getRawResult() {
    return null;
  }

  @Override
  protected void setRawResult(Object value) {}

  @Override
  protected boolean exec() {
    runImpl();
    return true;
  }

  public void unblock() {
    blockThread.set(false);
  }

  @Override
  public void run() {
    runImpl();
  }

  @Override
  public Object call() {
    runImpl();
    return null;
  }

  public void waitForCompletion() throws InterruptedException {
    latch.await();
  }

  private void runImpl() {
    while (blockThread.get()) {
      // busy-wait to block thread
    }
    if (doTraceableWork) {
      asyncChild();
    }
    latch.countDown();
  }

  private static void asyncChild() {
    tracer.spanBuilder("asyncChild").startSpan().end();
  }
}
