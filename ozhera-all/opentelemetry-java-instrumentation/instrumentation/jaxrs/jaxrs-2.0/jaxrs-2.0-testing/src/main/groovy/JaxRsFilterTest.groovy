/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.INTERNAL
import static io.opentelemetry.api.trace.SpanKind.SERVER
import static io.opentelemetry.api.trace.StatusCode.ERROR
import static io.opentelemetry.instrumentation.test.utils.TraceUtils.runUnderServerTrace

import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.PreMatching
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider
import org.junit.Assume
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
abstract class JaxRsFilterTest extends AgentInstrumentationSpecification {

  @Shared
  SimpleRequestFilter simpleRequestFilter = new SimpleRequestFilter()

  @Shared
  PrematchRequestFilter prematchRequestFilter = new PrematchRequestFilter()

  abstract makeRequest(String url)

  Tuple2<String, String> runRequest(String resource) {
    if (runsOnServer()) {
      return makeRequest(resource)
    }
    // start a trace because the test doesn't go through any servlet or other instrumentation.
    return runUnderServerTrace("test.span") {
      makeRequest(resource)
    }
  }

  boolean testAbortPrematch() {
    true
  }

  boolean runsOnServer() {
    false
  }

  def "test #resource, #abortNormal, #abortPrematch"() {
    Assume.assumeTrue(!abortPrematch || testAbortPrematch())

    given:
    simpleRequestFilter.abort = abortNormal
    prematchRequestFilter.abort = abortPrematch
    def abort = abortNormal || abortPrematch

    when:

    def (responseText, responseStatus) = runRequest(resource)

    then:
    responseText == expectedResponse

    if (abort) {
      responseStatus == Response.Status.UNAUTHORIZED.statusCode
    } else {
      responseStatus == Response.Status.OK.statusCode
    }

    assertTraces(1) {
      trace(0, 2) {
        span(0) {
          name parentSpanName != null ? parentSpanName : "test.span"
          kind SERVER
          if (runsOnServer() && abortNormal) {
            status ERROR
          }
        }
        span(1) {
          childOf span(0)
          name controllerName
          if (abortPrematch) {
            attributes {
              "${SemanticAttributes.CODE_NAMESPACE.key}" "JaxRsFilterTest\$PrematchRequestFilter"
              "${SemanticAttributes.CODE_FUNCTION.key}" "filter"
            }
          } else {
            attributes {
              "${SemanticAttributes.CODE_NAMESPACE.key}" ~/Resource[$]Test*/
              "${SemanticAttributes.CODE_FUNCTION.key}" "hello"
            }
          }
        }
      }
    }

    where:
    resource           | abortNormal | abortPrematch | parentSpanName        | controllerName                 | expectedResponse
    "/test/hello/bob"  | false       | false         | "/test/hello/{name}"  | "Test1.hello"                  | "Test1 bob!"
    "/test2/hello/bob" | false       | false         | "/test2/hello/{name}" | "Test2.hello"                  | "Test2 bob!"
    "/test3/hi/bob"    | false       | false         | "/test3/hi/{name}"    | "Test3.hello"                  | "Test3 bob!"

    // Resteasy and Jersey give different resource class names for just the below case
    // Resteasy returns "SubResource.class"
    // Jersey returns "Test1.class
    // "/test/hello/bob"  | true        | false         | "/test/hello/{name}"  | "Test1.hello"                  | "Aborted"

    "/test2/hello/bob" | true        | false         | "/test2/hello/{name}" | "Test2.hello"                  | "Aborted"
    "/test3/hi/bob"    | true        | false         | "/test3/hi/{name}"    | "Test3.hello"                  | "Aborted"
    "/test/hello/bob"  | false       | true          | null                  | "PrematchRequestFilter.filter" | "Aborted Prematch"
    "/test2/hello/bob" | false       | true          | null                  | "PrematchRequestFilter.filter" | "Aborted Prematch"
    "/test3/hi/bob"    | false       | true          | null                  | "PrematchRequestFilter.filter" | "Aborted Prematch"
  }

  def "test nested call"() {
    given:
    simpleRequestFilter.abort = false
    prematchRequestFilter.abort = false

    when:
    def (responseText, responseStatus) = runRequest(resource)

    then:
    responseStatus == Response.Status.OK.statusCode
    responseText == expectedResponse

    assertTraces(1) {
      trace(0, 2) {
        span(0) {
          name parentResourceName
          kind SERVER
          if (!runsOnServer()) {
            attributes {
            }
          }
        }
        span(1) {
          childOf span(0)
          name controller1Name
          kind INTERNAL
          attributes {
            "${SemanticAttributes.CODE_NAMESPACE.key}" ~/Resource[$]Test*/
            "${SemanticAttributes.CODE_FUNCTION.key}" "nested"
          }
        }
      }
    }

    where:
    resource        | parentResourceName | controller1Name | expectedResponse
    "/test3/nested" | "/test3/nested"    | "Test3.nested"  | "Test3 nested!"
  }

  @Provider
  static class SimpleRequestFilter implements ContainerRequestFilter {
    boolean abort = false

    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
      if (abort) {
        requestContext.abortWith(
          Response.status(Response.Status.UNAUTHORIZED)
            .entity("Aborted")
            .type(MediaType.TEXT_PLAIN_TYPE)
            .build())
      }
    }
  }

  @Provider
  @PreMatching
  static class PrematchRequestFilter implements ContainerRequestFilter {
    boolean abort = false

    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
      if (abort) {
        requestContext.abortWith(
          Response.status(Response.Status.UNAUTHORIZED)
            .entity("Aborted Prematch")
            .type(MediaType.TEXT_PLAIN_TYPE)
            .build())
      }
    }
  }
}
