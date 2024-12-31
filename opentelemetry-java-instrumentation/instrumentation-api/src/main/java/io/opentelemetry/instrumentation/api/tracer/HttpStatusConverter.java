/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.tracer;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;

public final class HttpStatusConverter {

  // https://github.com/open-telemetry/opentelemetry-specification/blob/master/specification/trace/semantic_conventions/http.md#status
  public static StatusCode statusFromHttpStatus(int httpStatus, SpanKind spanKind) {
    if(spanKind.equals(SpanKind.CLIENT)) {
      if (httpStatus >= 100 && httpStatus < 400) {
        return StatusCode.UNSET;
      }
      return StatusCode.ERROR;
    }
    if(spanKind.equals(SpanKind.SERVER)){
      if (httpStatus >= 100 && httpStatus < 500) {
        return StatusCode.UNSET;
      }
      return StatusCode.ERROR;
    }
    return StatusCode.ERROR;
  }

  private HttpStatusConverter() {}
}
