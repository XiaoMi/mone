/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.trace;

import com.google.auto.value.AutoValue;
import java.util.Map;

@AutoValue
abstract class ImmutableSpanContext implements SpanContext {

  static final SpanContext INVALID =
      createInternal(
          TraceId.getInvalid(),
          SpanId.getInvalid(),
          TraceFlags.getDefault(),
          TraceState.getDefault(),
          /* remote= */ false,
          /* valid= */ false,HeraContext.getInvalid());

  private static AutoValue_ImmutableSpanContext createInternal(
      String traceId,
      String spanId,
      TraceFlags traceFlags,
      TraceState traceState,
      boolean remote,
      boolean valid, Map<String,String> heraContext) {
    return new AutoValue_ImmutableSpanContext(
        traceId, spanId, traceFlags, traceState, remote, heraContext, valid);
  }

  static SpanContext create(
      String traceIdHex,
      String spanIdHex,
      TraceFlags traceFlags,
      TraceState traceState,
      boolean remote, Map<String,String> heraContext) {
    if (SpanId.isValid(spanIdHex) && TraceId.isValid(traceIdHex)) {
      return createInternal(
          traceIdHex, spanIdHex, traceFlags, traceState, remote, /* valid= */ true, HeraContext.isValid(heraContext)?heraContext:HeraContext.getInvalid());
    }
    return createInternal(
        TraceId.getInvalid(),
        SpanId.getInvalid(),
        traceFlags,
        traceState,
        remote,
        /* valid= */ false, HeraContext.isValid(heraContext)?heraContext:HeraContext.getInvalid());
  }

  static SpanContext createInvalidWithHeraContext(Map<String,String> heraContext) {
    return createInternal(
        TraceId.getInvalid(),
        SpanId.getInvalid(),
        TraceFlags.getDefault(),
        TraceState.getDefault(),
        /* remote= */false,
        /* valid= */ false, HeraContext.isValid(heraContext)?heraContext:HeraContext.getInvalid());
  }

  @Override
  public abstract boolean isValid();
}
