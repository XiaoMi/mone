/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.ERROR
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.EXCEPTION
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.NOT_FOUND
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.QUERY_PARAM
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.REDIRECT
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.SUCCESS

import io.opentelemetry.instrumentation.test.AgentTestTrait
import io.opentelemetry.instrumentation.test.asserts.TraceAssert
import io.opentelemetry.instrumentation.test.base.HttpServerTest
import javax.servlet.DispatcherType
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.handler.ErrorHandler
import spock.lang.Shared

class JettyHandlerTest extends HttpServerTest<Server> implements AgentTestTrait {

  @Shared
  ErrorHandler errorHandler = new ErrorHandler() {
    @Override
    protected void handleErrorPage(HttpServletRequest request, Writer writer, int code, String message) throws IOException {
      Throwable th = (Throwable) request.getAttribute("javax.servlet.error.exception")
      message = th ? th.message : message
      if (message) {
        writer.write(message)
      }
    }
  }

  @Shared
  TestHandler testHandler = new TestHandler()

  @Override
  Server startServer(int port) {
    def server = new Server(port)
    server.setHandler(handler())
    server.addBean(errorHandler)
    server.start()
    return server
  }

  AbstractHandler handler() {
    testHandler
  }

  @Override
  void stopServer(Server server) {
    server.stop()
  }

  @Override
  boolean hasResponseSpan(ServerEndpoint endpoint) {
    endpoint == REDIRECT || endpoint == ERROR
  }

  @Override
  void responseSpan(TraceAssert trace, int index, Object parent, String method, ServerEndpoint endpoint) {
    switch (endpoint) {
      case REDIRECT:
        redirectSpan(trace, index, parent)
        break
      case ERROR:
        sendErrorSpan(trace, index, parent)
        break
    }
  }

  static void handleRequest(Request request, HttpServletResponse response) {
    ServerEndpoint endpoint = ServerEndpoint.forPath(request.requestURI)
    controller(endpoint) {
      response.contentType = "text/plain"
      switch (endpoint) {
        case SUCCESS:
          response.status = endpoint.status
          response.writer.print(endpoint.body)
          break
        case QUERY_PARAM:
          response.status = endpoint.status
          response.writer.print(request.queryString)
          break
        case REDIRECT:
          response.sendRedirect(endpoint.body)
          break
        case ERROR:
          response.sendError(endpoint.status, endpoint.body)
          break
        case EXCEPTION:
          throw new Exception(endpoint.body)
        default:
          response.status = NOT_FOUND.status
          response.writer.print(NOT_FOUND.body)
          break
      }
    }
  }

  static class TestHandler extends AbstractHandler {
    @Override
    void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      //This line here is to verify that we don't break Jetty if it wants to cast to implementation class
      //See https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/1096
      Response jettyResponse = response as Response
      if (baseRequest.dispatcherType != DispatcherType.ERROR) {
        handleRequest(baseRequest, jettyResponse)
        baseRequest.handled = true
      } else {
        errorHandler.handle(target, baseRequest, baseRequest, response)
      }
    }
  }

  @Override
  String expectedServerSpanName(ServerEndpoint endpoint) {
    "HTTP GET"
  }
}
