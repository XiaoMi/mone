/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.reactornetty.v0_9

import static io.opentelemetry.instrumentation.test.utils.PortUtils.UNUSABLE_PORT
import static io.opentelemetry.instrumentation.test.utils.TraceUtils.basicSpan
import static io.opentelemetry.instrumentation.test.utils.TraceUtils.runUnderTrace

import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.instrumentation.test.AgentTestTrait
import io.opentelemetry.instrumentation.test.asserts.SpanAssert
import io.opentelemetry.instrumentation.test.base.HttpClientTest
import io.opentelemetry.sdk.trace.data.SpanData
import java.util.concurrent.atomic.AtomicReference
import reactor.netty.http.client.HttpClient

abstract class AbstractReactorNettyHttpClientTest extends HttpClientTest<HttpClient.ResponseReceiver> implements AgentTestTrait {

  @Override
  boolean testRedirects() {
    false
  }

  @Override
  String userAgent() {
    return "ReactorNetty"
  }

  @Override
  HttpClient.ResponseReceiver buildRequest(String method, URI uri, Map<String, String> headers) {
    return createHttpClient()
      .followRedirect(true)
      .headers({ h -> headers.each { k, v -> h.add(k, v) } })
      .baseUrl(resolveAddress("").toString())
      ."${method.toLowerCase()}"()
      .uri(uri.toString())
  }

  @Override
  int sendRequest(HttpClient.ResponseReceiver request, String method, URI uri, Map<String, String> headers) {
    return request.responseSingle {resp, content ->
      // Make sure to consume content since that's when we close the span.
      content.map {
        resp
      }
    }.block().status().code()
  }

  @Override
  void sendRequestWithCallback(HttpClient.ResponseReceiver request, String method, URI uri, Map<String, String> headers, RequestResult requestResult) {
    request.responseSingle {resp, content ->
      // Make sure to consume content since that's when we close the span.
      content.map { resp }
    }.subscribe({
      requestResult.complete(it.status().code())
    }, { throwable ->
      requestResult.complete(throwable)
    })
  }

  @Override
  String expectedClientSpanName(URI uri, String method) {
    switch (uri.toString()) {
      case "http://localhost:61/": // unopened port
      case "https://192.0.2.1/": // non routable address
        return "CONNECT"
      default:
        return super.expectedClientSpanName(uri, method)
    }
  }

  @Override
  void assertClientSpanErrorEvent(SpanAssert spanAssert, URI uri, Throwable exception) {
    if (exception.class.getName().endsWith("ReactiveException")) {
      switch (uri.toString()) {
        case "http://localhost:61/": // unopened port
        case "https://192.0.2.1/": // non routable address
          exception = exception.getCause()
      }
    }
    super.assertClientSpanErrorEvent(spanAssert, uri, exception)
  }

  @Override
  Set<AttributeKey<?>> httpAttributes(URI uri) {
    switch (uri.toString()) {
      case "http://localhost:61/": // unopened port
      case "https://192.0.2.1/": // non routable address
        return []
    }
    return super.httpAttributes(uri)
  }

  abstract HttpClient createHttpClient()

  def "should expose context to http client callbacks"() {
    given:
    def onRequestSpan = new AtomicReference<Span>()
    def afterRequestSpan = new AtomicReference<Span>()
    def onResponseSpan = new AtomicReference<Span>()
    def afterResponseSpan = new AtomicReference<Span>()

    def httpClient = createHttpClient()
      .doOnRequest({ rq, con -> onRequestSpan.set(Span.current()) })
      .doAfterRequest({ rq, con -> afterRequestSpan.set(Span.current()) })
      .doOnResponse({ rs, con -> onResponseSpan.set(Span.current()) })
      .doAfterResponse({ rs, con -> afterResponseSpan.set(Span.current()) })

    when:
    runUnderTrace("parent") {
      httpClient.baseUrl(resolveAddress("").toString())
        .get()
        .uri("/success")
        .responseSingle {resp, content ->
          // Make sure to consume content since that's when we close the span.
          content.map { resp }
        }
        .block()
    }

    then:
    assertTraces(1) {
      trace(0, 3) {
        def parentSpan = span(0)
        def nettyClientSpan = span(1)

        basicSpan(it, 0, "parent")
        clientSpan(it, 1, parentSpan, "GET", resolveAddress("/success"))
        serverSpan(it, 2, nettyClientSpan)

        assertSameSpan(parentSpan, onRequestSpan)
        assertSameSpan(nettyClientSpan, afterRequestSpan)
        assertSameSpan(nettyClientSpan, onResponseSpan)
        assertSameSpan(parentSpan, afterResponseSpan)
      }
    }
  }

  def "should expose context to http request error callback"() {
    given:
    def onRequestErrorSpan = new AtomicReference<Span>()

    def httpClient = createHttpClient()
      .doOnRequestError({ rq, err -> onRequestErrorSpan.set(Span.current()) })

    when:
    runUnderTrace("parent") {
      httpClient.get()
        .uri("http://localhost:$UNUSABLE_PORT/")
        .responseSingle {resp, content ->
          // Make sure to consume content since that's when we close the span.
          content.map { resp }
        }
        .block()
    }

    then:
    def ex = thrown(Exception)

    assertTraces(1) {
      trace(0, 2) {
        def parentSpan = span(0)

        basicSpan(it, 0, "parent", null, ex)
        span(1) {
          def actualException = ex.cause
          kind SpanKind.CLIENT
          childOf parentSpan
          status StatusCode.ERROR
          errorEvent(actualException.class, actualException.message)
        }

        assertSameSpan(parentSpan, onRequestErrorSpan)
      }
    }
  }


  private static void assertSameSpan(SpanData expected, AtomicReference<Span> actual) {
    def expectedSpanContext = expected.spanContext
    def actualSpanContext = actual.get().spanContext
    assert expectedSpanContext.traceId == actualSpanContext.traceId
    assert expectedSpanContext.spanId == actualSpanContext.spanId
  }
}
