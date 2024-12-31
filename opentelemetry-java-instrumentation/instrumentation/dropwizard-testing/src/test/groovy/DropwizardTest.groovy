/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.INTERNAL
import static io.opentelemetry.api.trace.SpanKind.SERVER
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.ERROR
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.EXCEPTION
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.NOT_FOUND
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.PATH_PARAM
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.QUERY_PARAM
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.REDIRECT
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.SUCCESS

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.testing.ConfigOverride
import io.dropwizard.testing.DropwizardTestSupport
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.instrumentation.test.AgentTestTrait
import io.opentelemetry.instrumentation.test.asserts.TraceAssert
import io.opentelemetry.instrumentation.test.base.HttpServerTest
import io.opentelemetry.instrumentation.test.utils.PortUtils
import io.opentelemetry.sdk.trace.data.SpanData
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

class DropwizardTest extends HttpServerTest<DropwizardTestSupport> implements AgentTestTrait {

  @Override
  DropwizardTestSupport startServer(int port) {
    println "Port: $port"
    def testSupport = new DropwizardTestSupport(testApp(),
      null,
      ConfigOverride.config("server.applicationConnectors[0].port", "$port"),
      ConfigOverride.config("server.adminConnectors[0].port", PortUtils.findOpenPort().toString()))
    testSupport.before()
    return testSupport
  }

  Class testApp() {
    TestApp
  }

  Class testResource() {
    ServiceResource
  }

  @Override
  void stopServer(DropwizardTestSupport testSupport) {
    testSupport.after()
  }

  @Override
  boolean hasHandlerSpan(ServerEndpoint endpoint) {
    endpoint != NOT_FOUND
  }

  @Override
  boolean testPathParam() {
    true
  }

  @Override
  String expectedServerSpanName(ServerEndpoint endpoint) {
    switch (endpoint) {
      case PATH_PARAM:
        return "/path/{id}/param"
      case NOT_FOUND:
        return "/*"
      default:
        return endpoint.resolvePath(address).path
    }
  }

  @Override
  void handlerSpan(TraceAssert trace, int index, Object parent, String method = "GET", ServerEndpoint endpoint = SUCCESS) {
    trace.span(index) {
      name "${this.testResource().simpleName}.${endpoint.name().toLowerCase()}"
      kind INTERNAL
      if (endpoint == EXCEPTION) {
        status StatusCode.ERROR
        errorEvent(Exception, EXCEPTION.body)
      }
      childOf((SpanData) parent)
    }
  }

  // this override is needed because dropwizard reports peer ip as the client ip
  @Override
  void serverSpan(TraceAssert trace, int index, String traceID = null, String parentID = null, String method = "GET", Long responseContentLength = null, ServerEndpoint endpoint = SUCCESS) {
    trace.span(index) {
      name expectedServerSpanName(endpoint)
      kind SERVER
      if (endpoint.errored) {
        status StatusCode.ERROR
      }
      if (parentID != null) {
        traceId traceID
        parentSpanId parentID
      } else {
        hasNoParent()
      }
      attributes {
        // dropwizard reports peer ip as the client ip
        "${SemanticAttributes.NET_PEER_IP.key}" TEST_CLIENT_IP
        "${SemanticAttributes.NET_PEER_PORT.key}" Long
        "${SemanticAttributes.HTTP_URL.key}" { it == "${endpoint.resolve(address)}" || it == "${endpoint.resolveWithoutFragment(address)}" }
        "${SemanticAttributes.HTTP_METHOD.key}" method
        "${SemanticAttributes.HTTP_STATUS_CODE.key}" endpoint.status
        "${SemanticAttributes.HTTP_FLAVOR.key}" "1.1"
        "${SemanticAttributes.HTTP_USER_AGENT.key}" TEST_USER_AGENT
        "${SemanticAttributes.HTTP_CLIENT_IP.key}" TEST_CLIENT_IP
      }
    }
  }

  static class TestApp extends Application<Configuration> {
    @Override
    void initialize(Bootstrap<Configuration> bootstrap) {
    }

    @Override
    void run(Configuration configuration, Environment environment) {
      environment.jersey().register(ServiceResource)
    }
  }

  @Path("/ignored1")
  static interface TestInterface {}

  @Path("/ignored2")
  static abstract class AbstractClass implements TestInterface {

    @GET
    @Path("success")
    Response success() {
      controller(SUCCESS) {
        Response.status(SUCCESS.status).entity(SUCCESS.body).build()
      }
    }

    @GET
    @Path("query")
    Response query_param(@QueryParam("some") String param) {
      controller(QUERY_PARAM) {
        Response.status(QUERY_PARAM.status).entity("some=$param".toString()).build()
      }
    }

    @GET
    @Path("redirect")
    Response redirect() {
      controller(REDIRECT) {
        Response.status(REDIRECT.status).location(new URI(REDIRECT.body)).build()
      }
    }
  }

  @Path("/ignored3")
  static class ParentClass extends AbstractClass {

    @GET
    @Path("error-status")
    Response error() {
      controller(ERROR) {
        Response.status(ERROR.status).entity(ERROR.body).build()
      }
    }

    @GET
    @Path("exception")
    Response exception() {
      controller(EXCEPTION) {
        throw new Exception(EXCEPTION.body)
      }
      return null
    }

    @GET
    @Path("path/{id}/param")
    Response path_param(@PathParam("id") int param) {
      controller(PATH_PARAM) {
        Response.status(PATH_PARAM.status).entity(param.toString()).build()
      }
    }
  }

  @Path("/")
  static class ServiceResource extends ParentClass {}
}
