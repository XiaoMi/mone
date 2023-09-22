/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.instrumenter;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import org.checkerframework.checker.nullness.qual.Nullable;

final class DefaultSpanStatusExtractor<REQUEST, RESPONSE>
    implements SpanStatusExtractor<REQUEST, RESPONSE> {

  static final SpanStatusExtractor<Object, Object> INSTANCE = new DefaultSpanStatusExtractor<>();

  @Override
  public StatusCode extract(
          REQUEST request, @Nullable RESPONSE response, @Nullable Throwable error, SpanKind spanKind) {
    if (error != null) {
      return StatusCode.ERROR;
    }
    return StatusCode.UNSET;
  }
}
