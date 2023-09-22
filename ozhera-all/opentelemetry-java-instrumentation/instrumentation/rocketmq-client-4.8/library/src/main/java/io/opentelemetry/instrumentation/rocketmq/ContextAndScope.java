/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rocketmq;

import com.google.auto.value.AutoValue;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.apache.rocketmq.client.trace.TraceBean;

@AutoValue
abstract class ContextAndScope extends TraceBean {

  static ContextAndScope create(Context context, Scope scope) {
    return new AutoValue_ContextAndScope(context, scope);
  }

  abstract Context getContext();

  abstract Scope getScope();

  void close() {
    getScope().close();
  }
}
