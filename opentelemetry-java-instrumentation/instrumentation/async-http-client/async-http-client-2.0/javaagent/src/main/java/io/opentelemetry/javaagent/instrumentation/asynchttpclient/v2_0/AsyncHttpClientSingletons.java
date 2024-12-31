/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.asynchttpclient.v2_0;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpSpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpSpanStatusExtractor;
import io.opentelemetry.javaagent.instrumentation.api.instrumenter.PeerServiceAttributesExtractor;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

public final class AsyncHttpClientSingletons {
  private static final String INSTRUMENTATION_NAME =
      "io.opentelemetry.javaagent.async-http-client-2.0";

  private static final Instrumenter<Request, Response> INSTRUMENTER;

  static {
    HttpAttributesExtractor<Request, Response> httpAttributesExtractor =
        new AsyncHttpClientHttpAttributesExtractor();
    SpanNameExtractor<? super Request> spanNameExtractor =
        HttpSpanNameExtractor.create(httpAttributesExtractor);
    SpanStatusExtractor<? super Request, ? super Response> spanStatusExtractor =
        HttpSpanStatusExtractor.create(httpAttributesExtractor);
    AsyncHttpClientNetAttributesExtractor netAttributesExtractor =
        new AsyncHttpClientNetAttributesExtractor();

    INSTRUMENTER =
        Instrumenter.<Request, Response>newBuilder(
                GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanNameExtractor)
            .setSpanStatusExtractor(spanStatusExtractor)
            .addAttributesExtractor(httpAttributesExtractor)
            .addAttributesExtractor(netAttributesExtractor)
            .addAttributesExtractor(PeerServiceAttributesExtractor.create(netAttributesExtractor))
            .newClientInstrumenter(new HttpHeaderSetter());
  }

  public static Instrumenter<Request, Response> instrumenter() {
    return INSTRUMENTER;
  }

  private AsyncHttpClientSingletons() {}
}
