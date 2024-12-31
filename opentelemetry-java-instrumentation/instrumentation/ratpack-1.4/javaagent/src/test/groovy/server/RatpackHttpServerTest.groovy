/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package server

import static io.opentelemetry.api.trace.SpanKind.INTERNAL
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.ERROR
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.EXCEPTION
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.INDEXED_CHILD
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.PATH_PARAM
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.QUERY_PARAM
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.REDIRECT
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.SUCCESS

import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.instrumentation.test.AgentTestTrait
import io.opentelemetry.instrumentation.test.asserts.TraceAssert
import io.opentelemetry.instrumentation.test.base.HttpServerTest
import io.opentelemetry.sdk.trace.data.SpanData
import ratpack.error.ServerErrorHandler
import ratpack.handling.Context
import ratpack.server.RatpackServer

class RatpackHttpServerTest extends HttpServerTest<RatpackServer> implements AgentTestTrait {

  @Override
  RatpackServer startServer(int bindPort) {
    def ratpack = RatpackServer.start {
      it.serverConfig {
        it.port(bindPort)
        it.address(InetAddress.getByName("localhost"))
      }
      it.handlers {
        it.register {
          it.add(ServerErrorHandler, new TestErrorHandler())
        }
        it.prefix(SUCCESS.rawPath()) {
          it.all {context ->
            controller(SUCCESS) {
              context.response.status(SUCCESS.status).send(SUCCESS.body)
            }
          }
        }
        it.prefix(INDEXED_CHILD.rawPath()) {
          it.all {context ->
            controller(INDEXED_CHILD) {
              INDEXED_CHILD.collectSpanAttributes { context.request.queryParams.get(it) }
              context.response.status(INDEXED_CHILD.status).send()
            }
          }
        }
        it.prefix(QUERY_PARAM.rawPath()) {
          it.all { context ->
            controller(QUERY_PARAM) {
              context.response.status(QUERY_PARAM.status).send(context.request.query)
            }
          }
        }
        it.prefix(REDIRECT.rawPath()) {
          it.all {context ->
            controller(REDIRECT) {
              context.redirect(REDIRECT.body)
            }
          }
        }
        it.prefix(ERROR.rawPath()) {
          it.all {context ->
            controller(ERROR) {
              context.response.status(ERROR.status).send(ERROR.body)
            }
          }
        }
        it.prefix(EXCEPTION.rawPath()) {
          it.all {
            controller(EXCEPTION) {
              throw new Exception(EXCEPTION.body)
            }
          }
        }
        it.prefix("path/:id/param") {
          it.all {context ->
            controller(PATH_PARAM) {
              context.response.status(PATH_PARAM.status).send(context.pathTokens.id)
            }
          }
        }
      }
    }

    assert ratpack.bindPort == bindPort
    return ratpack
  }

  static class TestErrorHandler implements ServerErrorHandler {
    @Override
    void error(Context context, Throwable throwable) throws Exception {
      context.response.status(500).send(throwable.message)
    }
  }

  @Override
  void stopServer(RatpackServer server) {
    server.stop()
  }

  @Override
  boolean hasHandlerSpan(ServerEndpoint endpoint) {
    true
  }

  @Override
  boolean testPathParam() {
    true
  }

  @Override
  boolean testConcurrency() {
    true
  }

  @Override
  void handlerSpan(TraceAssert trace, int index, Object parent, String method = "GET", ServerEndpoint endpoint = SUCCESS) {
    trace.span(index) {
      name endpoint.status == 404 ? "/" : endpoint == PATH_PARAM ? "/path/:id/param" : endpoint.path
      kind INTERNAL
      childOf((SpanData) parent)
      if (endpoint == EXCEPTION) {
        status StatusCode.ERROR
        errorEvent(Exception, EXCEPTION.body)
      }
    }
  }

  @Override
  String expectedServerSpanName(ServerEndpoint endpoint) {
    return endpoint.status == 404 ? "/" : endpoint == PATH_PARAM ? "/path/:id/param" : endpoint.path
  }
}
