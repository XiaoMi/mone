/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.spring.batch.job;

import static io.opentelemetry.javaagent.instrumentation.spring.batch.job.JobExecutionTracer.tracer;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.instrumentation.api.ContextStore;
import io.opentelemetry.javaagent.instrumentation.spring.batch.ContextAndScope;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.core.Ordered;

public final class TracingJobExecutionListener implements JobExecutionListener, Ordered {
  private final ContextStore<JobExecution, ContextAndScope> executionContextStore;

  public TracingJobExecutionListener(
      ContextStore<JobExecution, ContextAndScope> executionContextStore) {
    this.executionContextStore = executionContextStore;
  }

  @Override
  public void beforeJob(JobExecution jobExecution) {
    Context context = tracer().startSpan(jobExecution);
    // beforeJob & afterJob always execute on the same thread
    Scope scope = context.makeCurrent();
    executionContextStore.put(jobExecution, new ContextAndScope(context, scope));
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    ContextAndScope contextAndScope = executionContextStore.get(jobExecution);
    if (contextAndScope != null) {
      executionContextStore.put(jobExecution, null);
      contextAndScope.closeScope();
      tracer().end(contextAndScope.getContext());
    }
  }

  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE;
  }

  // equals() and hashCode() methods guarantee that only one instance of
  // TracingJobExecutionListener will be present in an ordered set of listeners

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o instanceof TracingJobExecutionListener;
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
