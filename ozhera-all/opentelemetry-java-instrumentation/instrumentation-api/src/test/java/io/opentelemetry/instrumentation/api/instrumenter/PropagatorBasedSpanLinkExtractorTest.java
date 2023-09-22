/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.instrumenter;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanId;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceId;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PropagatorBasedSpanLinkExtractorTest {
  private static final String TRACE_ID = TraceId.fromLongs(0, 123);
  private static final String SPAN_ID = SpanId.fromLong(456);

  @Test
  void shouldExtractSpanLink() {
    // given
    ContextPropagators propagators =
        ContextPropagators.create(W3CTraceContextPropagator.getInstance());

    SpanLinkExtractor<Map<String, String>> underTest =
        SpanLinkExtractor.fromUpstreamRequest(propagators, new MapGetter());

    Map<String, String> request =
        singletonMap("traceparent", String.format("00-%s-%s-01", TRACE_ID, SPAN_ID));

    // when
    SpanContext link = underTest.extract(Context.root(), request);

    // then
    assertEquals(
        SpanContext.createFromRemoteParent(
            TRACE_ID, SPAN_ID, TraceFlags.getSampled(), TraceState.getDefault()),
        link);
  }

  static final class MapGetter implements TextMapGetter<Map<String, String>> {

    @Override
    public Iterable<String> keys(Map<String, String> carrier) {
      return carrier.keySet();
    }

    @Override
    public String get(Map<String, String> carrier, String key) {
      return carrier.get(key);
    }
  }
}
