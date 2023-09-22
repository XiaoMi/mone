/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package server.base

import static io.opentelemetry.api.trace.SpanKind.INTERNAL
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.EXCEPTION
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.NOT_FOUND

import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.instrumentation.test.asserts.TraceAssert
import io.opentelemetry.instrumentation.test.base.HttpServerTest
import io.opentelemetry.sdk.trace.data.SpanData
import org.springframework.web.server.ResponseStatusException

abstract class ControllerSpringWebFluxServerTest extends SpringWebFluxServerTest {
  @Override
  void handlerSpan(TraceAssert trace, int index, Object parent, String method, HttpServerTest.ServerEndpoint endpoint) {
    def handlerSpanName = "${ServerTestController.simpleName}.${endpoint.name().toLowerCase()}"
    if (endpoint == NOT_FOUND) {
      handlerSpanName = "ResourceWebHandler.handle"
    }
    trace.span(index) {
      name handlerSpanName
      kind INTERNAL
      if (endpoint == EXCEPTION) {
        status StatusCode.ERROR
        errorEvent(IllegalStateException, EXCEPTION.body)
      } else if (endpoint == NOT_FOUND) {
        status StatusCode.ERROR
        if (Boolean.getBoolean("testLatestDeps")) {
          errorEvent(ResponseStatusException, "404 NOT_FOUND")
        } else {
          errorEvent(ResponseStatusException, "Response status 404")
        }
      }
      childOf((SpanData) parent)
    }
  }

  @Override
  boolean hasHandlerAsControllerParentSpan(HttpServerTest.ServerEndpoint endpoint) {
    return false
  }
}
