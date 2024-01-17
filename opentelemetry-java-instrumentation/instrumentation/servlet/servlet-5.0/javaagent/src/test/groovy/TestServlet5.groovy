/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.ERROR
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.EXCEPTION
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.INDEXED_CHILD
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.QUERY_PARAM
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.REDIRECT
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.SUCCESS

import io.opentelemetry.instrumentation.test.base.HttpServerTest
import jakarta.servlet.RequestDispatcher
import jakarta.servlet.ServletException
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.concurrent.Phaser

class TestServlet5 {

  @WebServlet
  static class Sync extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
      String servletPath = req.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH)
      if (servletPath == null) {
        servletPath = req.servletPath
      }
      HttpServerTest.ServerEndpoint endpoint = HttpServerTest.ServerEndpoint.forPath(servletPath)
      HttpServerTest.controller(endpoint) {
        resp.contentType = "text/plain"
        switch (endpoint) {
          case SUCCESS:
            resp.status = endpoint.status
            resp.writer.print(endpoint.body)
            break
          case INDEXED_CHILD:
            resp.status = endpoint.status
            endpoint.collectSpanAttributes { req.getParameter(it) }
            break
          case QUERY_PARAM:
            resp.status = endpoint.status
            resp.writer.print(req.queryString)
            break
          case REDIRECT:
            resp.sendRedirect(endpoint.body)
            break
          case ERROR:
            resp.sendError(endpoint.status, endpoint.body)
            break
          case EXCEPTION:
            throw new ServletException(endpoint.body)
        }
      }
    }
  }

  @WebServlet(asyncSupported = true)
  static class Async extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
      HttpServerTest.ServerEndpoint endpoint = HttpServerTest.ServerEndpoint.forPath(req.servletPath)
      def phaser = new Phaser(2)
      def context = req.startAsync()
      context.start {
        try {
          phaser.arriveAndAwaitAdvance()
          HttpServerTest.controller(endpoint) {
            resp.contentType = "text/plain"
            switch (endpoint) {
              case SUCCESS:
                resp.status = endpoint.status
                resp.writer.print(endpoint.body)
                context.complete()
                break
              case INDEXED_CHILD:
                endpoint.collectSpanAttributes { req.getParameter(it) }
                resp.status = endpoint.status
                context.complete()
                break
              case QUERY_PARAM:
                resp.status = endpoint.status
                resp.writer.print(req.queryString)
                context.complete()
                break
              case REDIRECT:
                resp.sendRedirect(endpoint.body)
                context.complete()
                break
              case ERROR:
                resp.status = endpoint.status
                resp.writer.print(endpoint.body)
//                resp.sendError(endpoint.status, endpoint.body)
                context.complete()
                break
              case EXCEPTION:
                resp.status = endpoint.status
                resp.writer.print(endpoint.body)
                context.complete()
                throw new Exception(endpoint.body)
            }
          }
        } finally {
          phaser.arriveAndDeregister()
        }
      }
      phaser.arriveAndAwaitAdvance()
      phaser.arriveAndAwaitAdvance()
    }
  }

  @WebServlet(asyncSupported = true)
  static class FakeAsync extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
      def context = req.startAsync()
      try {
        HttpServerTest.ServerEndpoint endpoint = HttpServerTest.ServerEndpoint.forPath(req.servletPath)
        HttpServerTest.controller(endpoint) {
          resp.contentType = "text/plain"
          switch (endpoint) {
            case SUCCESS:
              resp.status = endpoint.status
              resp.writer.print(endpoint.body)
              break
            case INDEXED_CHILD:
              endpoint.collectSpanAttributes { req.getParameter(it) }
              resp.status = endpoint.status
              break
            case QUERY_PARAM:
              resp.status = endpoint.status
              resp.writer.print(req.queryString)
              break
            case REDIRECT:
              resp.sendRedirect(endpoint.body)
              break
            case ERROR:
              resp.sendError(endpoint.status, endpoint.body)
              break
            case EXCEPTION:
              throw new Exception(endpoint.body)
          }
        }
      } finally {
        context.complete()
      }
    }
  }

  @WebServlet(asyncSupported = true)
  static class DispatchImmediate extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
      def target = req.servletPath.replace("/dispatch", "")
      req.startAsync().dispatch(target)
    }
  }

  @WebServlet(asyncSupported = true)
  static class DispatchAsync extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
      def target = req.servletPath.replace("/dispatch", "")
      def context = req.startAsync()
      context.start {
        context.dispatch(target)
      }
    }
  }

  // TODO: Add tests for this!
  @WebServlet(asyncSupported = true)
  static class DispatchRecursive extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
      if (req.servletPath == "/recursive") {
        resp.writer.print("Hello Recursive")
        return
      }
      def depth = Integer.parseInt(req.getParameter("depth"))
      if (depth > 0) {
        req.startAsync().dispatch("/dispatch/recursive?depth=" + (depth - 1))
      } else {
        req.startAsync().dispatch("/recursive")
      }
    }
  }
}
