/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.instrumenter.http;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusExtractor;
import io.opentelemetry.instrumentation.api.tracer.HttpStatusConverter;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Extractor of the <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/trace/semantic_conventions/http.md#status">HTTP
 * span status</a>. Instrumentation of HTTP server or client frameworks should use this class to
 * comply with OpenTelemetry HTTP semantic conventions.
 */
public final class HttpSpanStatusExtractor<REQUEST, RESPONSE>
    implements SpanStatusExtractor<REQUEST, RESPONSE> {

  /**
   * Returns the {@link SpanStatusExtractor} for HTTP requests, which will use the HTTP status code
   * to determine the {@link StatusCode} if available or fallback to {@linkplain #getDefault() the
   * default status} otherwise.
   */
  public static <REQUEST, RESPONSE> SpanStatusExtractor<REQUEST, RESPONSE> create(
      HttpAttributesExtractor<REQUEST, RESPONSE> attributesExtractor) {
    return new HttpSpanStatusExtractor<>(attributesExtractor);
  }

  private final HttpAttributesExtractor<REQUEST, RESPONSE> attributesExtractor;

  private HttpSpanStatusExtractor(HttpAttributesExtractor<REQUEST, RESPONSE> attributesExtractor) {
    this.attributesExtractor = attributesExtractor;
  }

  @Override
  public StatusCode extract(REQUEST request, @Nullable RESPONSE response, Throwable error, SpanKind spanKind) {
    if (response != null) {
      Integer statusCode = attributesExtractor.statusCode(request, response);
      if (statusCode != null) {
        return HttpStatusConverter.statusFromHttpStatus(statusCode, spanKind);
      }
    }
    return SpanStatusExtractor.getDefault().extract(request, response, error, spanKind);
  }
}
