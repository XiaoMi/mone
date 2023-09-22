/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.SERVER
import static io.opentelemetry.api.trace.StatusCode.ERROR

import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import io.opentelemetry.instrumentation.test.utils.PortUtils
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes
import io.opentelemetry.testing.internal.armeria.client.WebClient
import io.opentelemetry.testing.internal.armeria.common.AggregatedHttpResponse
import io.opentelemetry.testing.internal.armeria.common.HttpMethod
import io.opentelemetry.testing.internal.armeria.common.MediaType
import io.opentelemetry.testing.internal.armeria.common.RequestHeaders
import java.nio.file.Files
import org.apache.catalina.Context
import org.apache.catalina.startup.Tomcat
import org.apache.jasper.JasperException
import spock.lang.Shared
import spock.lang.Unroll

//TODO should this be HttpServerTest?
class JspInstrumentationBasicTests extends AgentInstrumentationSpecification {

  @Shared
  int port
  @Shared
  Tomcat tomcatServer
  @Shared
  Context appContext
  @Shared
  String jspWebappContext = "jsptest-context"

  @Shared
  File baseDir
  @Shared
  String baseUrl

  @Shared
  WebClient client

  def setupSpec() {
    baseDir = Files.createTempDirectory("jsp").toFile()
    baseDir.deleteOnExit()

    port = PortUtils.findOpenPort()

    tomcatServer = new Tomcat()
    tomcatServer.setBaseDir(baseDir.getAbsolutePath())
    tomcatServer.setPort(port)
    tomcatServer.getConnector()
    // comment to debug
    tomcatServer.setSilent(true)
    // this is needed in tomcat 9, this triggers the creation of a connector, will not
    // affect tomcat 7 and 8
    // https://stackoverflow.com/questions/48998387/code-works-with-embedded-apache-tomcat-8-but-not-with-9-whats-changed
    tomcatServer.getConnector()
    baseUrl = "http://localhost:$port/$jspWebappContext"
    client = WebClient.of(baseUrl)

    appContext = tomcatServer.addWebapp("/$jspWebappContext",
      JspInstrumentationBasicTests.getResource("/webapps/jsptest").getPath())

    tomcatServer.start()
    System.out.println(
      "Tomcat server: http://" + tomcatServer.getHost().getName() + ":" + port + "/")
  }

  def cleanupSpec() {
    tomcatServer.stop()
    tomcatServer.destroy()
  }

  @Unroll
  def "non-erroneous GET #test test"() {
    when:
    AggregatedHttpResponse res = client.get("/${jspFileName}").aggregate().join()

    then:
    assertTraces(1) {
      trace(0, 3) {
        span(0) {
          hasNoParent()
          name "/$jspWebappContext/$jspFileName"
          kind SERVER
          attributes {
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" Long
            "${SemanticAttributes.HTTP_URL.key}" "http://localhost:$port/$jspWebappContext/$jspFileName"
            "${SemanticAttributes.HTTP_METHOD.key}" "GET"
            "${SemanticAttributes.HTTP_STATUS_CODE.key}" 200
            "${SemanticAttributes.HTTP_FLAVOR.key}" "1.1"
            "${SemanticAttributes.HTTP_USER_AGENT.key}" String
            "${SemanticAttributes.HTTP_CLIENT_IP.key}" "127.0.0.1"
          }
        }
        span(1) {
          childOf span(0)
          name "Compile /$jspFileName"
          attributes {
            "jsp.classFQCN" "org.apache.jsp.$jspClassNamePrefix$jspClassName"
            "jsp.compiler" "org.apache.jasper.compiler.JDTCompiler"
          }
        }
        span(2) {
          childOf span(0)
          name "Render /$jspFileName"
          attributes {
            "jsp.requestURL" "${baseUrl}/${jspFileName}"
          }
        }
      }
    }
    res.status().code() == 200

    where:
    test                  | jspFileName         | jspClassName        | jspClassNamePrefix
    "no java jsp"         | "nojava.jsp"        | "nojava_jsp"        | ""
    "basic loop jsp"      | "common/loop.jsp"   | "loop_jsp"          | "common."
    "invalid HTML markup" | "invalidMarkup.jsp" | "invalidMarkup_jsp" | ""
  }

  def "non-erroneous GET with query string"() {
    setup:
    String queryString = "HELLO"

    when:
    AggregatedHttpResponse res = client.get("/getQuery.jsp?${queryString}").aggregate().join()

    then:
    assertTraces(1) {
      trace(0, 3) {
        span(0) {
          hasNoParent()
          name "/$jspWebappContext/getQuery.jsp"
          kind SERVER
          attributes {
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" Long
            "${SemanticAttributes.HTTP_URL.key}" "http://localhost:$port/$jspWebappContext/getQuery.jsp?$queryString"
            "${SemanticAttributes.HTTP_METHOD.key}" "GET"
            "${SemanticAttributes.HTTP_STATUS_CODE.key}" 200
            "${SemanticAttributes.HTTP_FLAVOR.key}" "1.1"
            "${SemanticAttributes.HTTP_USER_AGENT.key}" String
            "${SemanticAttributes.HTTP_CLIENT_IP.key}" "127.0.0.1"
          }
        }
        span(1) {
          childOf span(0)
          name "Compile /getQuery.jsp"
          attributes {
            "jsp.classFQCN" "org.apache.jsp.getQuery_jsp"
            "jsp.compiler" "org.apache.jasper.compiler.JDTCompiler"
          }
        }
        span(2) {
          childOf span(0)
          name "Render /getQuery.jsp"
          attributes {
            "jsp.requestURL" "${baseUrl}/getQuery.jsp"
          }
        }
      }
    }
    res.status().code() == 200
  }

  def "non-erroneous POST"() {
    setup:
    RequestHeaders headers = RequestHeaders.builder(HttpMethod.POST, "/post.jsp")
      .contentType(MediaType.FORM_DATA)
      .build()

    when:
    AggregatedHttpResponse res = client.execute(headers, "name=world").aggregate().join()

    then:
    assertTraces(1) {
      trace(0, 3) {
        span(0) {
          hasNoParent()
          name "/$jspWebappContext/post.jsp"
          kind SERVER
          attributes {
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" Long
            "${SemanticAttributes.HTTP_URL.key}" "http://localhost:$port/$jspWebappContext/post.jsp"
            "${SemanticAttributes.HTTP_METHOD.key}" "POST"
            "${SemanticAttributes.HTTP_STATUS_CODE.key}" 200
            "${SemanticAttributes.HTTP_FLAVOR.key}" "1.1"
            "${SemanticAttributes.HTTP_USER_AGENT.key}" String
            "${SemanticAttributes.HTTP_CLIENT_IP.key}" "127.0.0.1"
          }
        }
        span(1) {
          childOf span(0)
          name "Compile /post.jsp"
          attributes {
            "jsp.classFQCN" "org.apache.jsp.post_jsp"
            "jsp.compiler" "org.apache.jasper.compiler.JDTCompiler"
          }
        }
        span(2) {
          childOf span(0)
          name "Render /post.jsp"
          attributes {
            "jsp.requestURL" "${baseUrl}/post.jsp"
          }
        }
      }
    }
    res.status().code() == 200
  }

  @Unroll
  def "erroneous runtime errors GET jsp with #test test"() {
    when:
    AggregatedHttpResponse res = client.get("/${jspFileName}").aggregate().join()

    then:
    assertTraces(1) {
      trace(0, 3) {
        span(0) {
          hasNoParent()
          name "/$jspWebappContext/$jspFileName"
          kind SERVER
          status ERROR
          event(0) {
            eventName(SemanticAttributes.EXCEPTION_EVENT_NAME)
            attributes {
              "${SemanticAttributes.EXCEPTION_TYPE.key}" { String tagExceptionType ->
                return tagExceptionType == exceptionClass.getName() || tagExceptionType.contains(exceptionClass.getSimpleName())
              }
              "${SemanticAttributes.EXCEPTION_MESSAGE.key}" { String tagErrorMsg ->
                return errorMessageOptional || tagErrorMsg instanceof String
              }
              "${SemanticAttributes.EXCEPTION_STACKTRACE.key}" String
            }
          }
          attributes {
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" Long
            "${SemanticAttributes.HTTP_URL.key}" "http://localhost:$port/$jspWebappContext/$jspFileName"
            "${SemanticAttributes.HTTP_METHOD.key}" "GET"
            "${SemanticAttributes.HTTP_STATUS_CODE.key}" 500
            "${SemanticAttributes.HTTP_FLAVOR.key}" "1.1"
            "${SemanticAttributes.HTTP_USER_AGENT.key}" String
            "${SemanticAttributes.HTTP_CLIENT_IP.key}" "127.0.0.1"
          }
        }
        span(1) {
          childOf span(0)
          name "Compile /$jspFileName"
          attributes {
            "jsp.classFQCN" "org.apache.jsp.$jspClassName"
            "jsp.compiler" "org.apache.jasper.compiler.JDTCompiler"
          }
        }
        span(2) {
          childOf span(0)
          name "Render /$jspFileName"
          status ERROR
          event(0) {
            eventName(SemanticAttributes.EXCEPTION_EVENT_NAME)
            attributes {
              "${SemanticAttributes.EXCEPTION_TYPE.key}" { String tagExceptionType ->
                return tagExceptionType == exceptionClass.getName() || tagExceptionType.contains(exceptionClass.getSimpleName())
              }
              "${SemanticAttributes.EXCEPTION_MESSAGE.key}" { String tagErrorMsg ->
                return errorMessageOptional || tagErrorMsg instanceof String
              }
              "${SemanticAttributes.EXCEPTION_STACKTRACE.key}" String
            }
          }
          attributes {
            "jsp.requestURL" "${baseUrl}/${jspFileName}"
          }
        }
      }
    }
    res.status().code() == 500

    where:
    test                       | jspFileName        | jspClassName       | exceptionClass            | errorMessageOptional
    "java runtime error"       | "runtimeError.jsp" | "runtimeError_jsp" | ArithmeticException       | false
    "invalid write"            | "invalidWrite.jsp" | "invalidWrite_jsp" | IndexOutOfBoundsException | true
    "missing query gives null" | "getQuery.jsp"     | "getQuery_jsp"     | NullPointerException      | true
  }

  def "non-erroneous include plain HTML GET"() {
    when:
    AggregatedHttpResponse res = client.get("/includes/includeHtml.jsp").aggregate().join()

    then:
    assertTraces(1) {
      trace(0, 3) {
        span(0) {
          hasNoParent()
          name "/$jspWebappContext/includes/includeHtml.jsp"
          kind SERVER
          attributes {
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" Long
            "${SemanticAttributes.HTTP_URL.key}" "http://localhost:$port/$jspWebappContext/includes/includeHtml.jsp"
            "${SemanticAttributes.HTTP_METHOD.key}" "GET"
            "${SemanticAttributes.HTTP_STATUS_CODE.key}" 200
            "${SemanticAttributes.HTTP_FLAVOR.key}" "1.1"
            "${SemanticAttributes.HTTP_USER_AGENT.key}" String
            "${SemanticAttributes.HTTP_CLIENT_IP.key}" "127.0.0.1"
          }
        }
        span(1) {
          childOf span(0)
          name "Compile /includes/includeHtml.jsp"
          attributes {
            "jsp.classFQCN" "org.apache.jsp.includes.includeHtml_jsp"
            "jsp.compiler" "org.apache.jasper.compiler.JDTCompiler"
          }
        }
        span(2) {
          childOf span(0)
          name "Render /includes/includeHtml.jsp"
          attributes {
            "jsp.requestURL" "${baseUrl}/includes/includeHtml.jsp"
          }
        }
      }
    }
    res.status().code() == 200
  }

  def "non-erroneous multi GET"() {
    when:
    AggregatedHttpResponse res = client.get("/includes/includeMulti.jsp").aggregate().join()

    then:
    assertTraces(1) {
      trace(0, 7) {
        span(0) {
          hasNoParent()
          name "/$jspWebappContext/includes/includeMulti.jsp"
          kind SERVER
          attributes {
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" Long
            "${SemanticAttributes.HTTP_URL.key}" "http://localhost:$port/$jspWebappContext/includes/includeMulti.jsp"
            "${SemanticAttributes.HTTP_METHOD.key}" "GET"
            "${SemanticAttributes.HTTP_STATUS_CODE.key}" 200
            "${SemanticAttributes.HTTP_FLAVOR.key}" "1.1"
            "${SemanticAttributes.HTTP_USER_AGENT.key}" String
            "${SemanticAttributes.HTTP_CLIENT_IP.key}" "127.0.0.1"
          }
        }
        span(1) {
          childOf span(0)
          name "Compile /includes/includeMulti.jsp"
          attributes {
            "jsp.classFQCN" "org.apache.jsp.includes.includeMulti_jsp"
            "jsp.compiler" "org.apache.jasper.compiler.JDTCompiler"
          }
        }
        span(2) {
          childOf span(0)
          name "Render /includes/includeMulti.jsp"
          attributes {
            "jsp.requestURL" "${baseUrl}/includes/includeMulti.jsp"
          }
        }
        span(3) {
          childOf span(2)
          name "Compile /common/javaLoopH2.jsp"
          attributes {
            "jsp.classFQCN" "org.apache.jsp.common.javaLoopH2_jsp"
            "jsp.compiler" "org.apache.jasper.compiler.JDTCompiler"
          }
        }
        span(4) {
          childOf span(2)
          name "Render /common/javaLoopH2.jsp"
          attributes {
            "jsp.requestURL" "${baseUrl}/includes/includeMulti.jsp"
          }
        }
        span(5) {
          childOf span(2)
          name "Compile /common/javaLoopH2.jsp"
          attributes {
            "jsp.classFQCN" "org.apache.jsp.common.javaLoopH2_jsp"
            "jsp.compiler" "org.apache.jasper.compiler.JDTCompiler"
          }
        }
        span(6) {
          childOf span(2)
          name "Render /common/javaLoopH2.jsp"
          attributes {
            "jsp.requestURL" "${baseUrl}/includes/includeMulti.jsp"
          }
        }
      }
    }
    res.status().code() == 200
  }

  def "#test compile error should not produce render traces and spans"() {
    when:
    AggregatedHttpResponse res = client.get("/${jspFileName}").aggregate().join()

    then:
    assertTraces(1) {
      trace(0, 2) {
        span(0) {
          hasNoParent()
          name "/$jspWebappContext/$jspFileName"
          kind SERVER
          status ERROR
          errorEvent(JasperException, String)
          attributes {
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" Long
            "${SemanticAttributes.HTTP_URL.key}" "http://localhost:$port/$jspWebappContext/$jspFileName"
            "${SemanticAttributes.HTTP_METHOD.key}" "GET"
            "${SemanticAttributes.HTTP_STATUS_CODE.key}" 500
            "${SemanticAttributes.HTTP_FLAVOR.key}" "1.1"
            "${SemanticAttributes.HTTP_USER_AGENT.key}" String
            "${SemanticAttributes.HTTP_CLIENT_IP.key}" "127.0.0.1"
          }
        }
        span(1) {
          childOf span(0)
          name "Compile /$jspFileName"
          status ERROR
          errorEvent(JasperException, String)
          attributes {
            "jsp.classFQCN" "org.apache.jsp.$jspClassNamePrefix$jspClassName"
            "jsp.compiler" "org.apache.jasper.compiler.JDTCompiler"
          }
        }
      }
    }
    res.status().code() == 500

    where:
    test      | jspFileName                            | jspClassName                  | jspClassNamePrefix
    "normal"  | "compileError.jsp"                     | "compileError_jsp"            | ""
    "forward" | "forwards/forwardWithCompileError.jsp" | "forwardWithCompileError_jsp" | "forwards."
  }

  def "direct static file reference"() {
    when:
    AggregatedHttpResponse res = client.get("/${staticFile}").aggregate().join()

    then:
    res.status().code() == 200
    assertTraces(1) {
      trace(0, 1) {
        span(0) {
          hasNoParent()
          name "/$jspWebappContext/*"
          kind SERVER
          attributes {
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" Long
            "${SemanticAttributes.HTTP_URL.key}" "http://localhost:$port/$jspWebappContext/$staticFile"
            "${SemanticAttributes.HTTP_METHOD.key}" "GET"
            "${SemanticAttributes.HTTP_STATUS_CODE.key}" 200
            "${SemanticAttributes.HTTP_FLAVOR.key}" "1.1"
            "${SemanticAttributes.HTTP_USER_AGENT.key}" String
            "${SemanticAttributes.HTTP_CLIENT_IP.key}" "127.0.0.1"
          }
        }
      }
    }

    where:
    staticFile = "common/hello.html"
  }
}
