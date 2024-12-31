/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.INTERNAL
import static io.opentelemetry.api.trace.StatusCode.ERROR
import static io.opentelemetry.instrumentation.test.utils.TraceUtils.basicServerSpan

import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import io.opentelemetry.instrumentation.test.asserts.TraceAssert
import io.opentelemetry.instrumentation.test.base.HttpServerTestTrait
import io.opentelemetry.sdk.trace.data.SpanData
import io.opentelemetry.testing.internal.armeria.common.AggregatedHttpRequest
import io.opentelemetry.testing.internal.armeria.common.AggregatedHttpResponse
import io.opentelemetry.testing.internal.armeria.common.HttpData
import io.opentelemetry.testing.internal.armeria.common.HttpMethod
import io.opentelemetry.testing.internal.armeria.common.MediaType
import io.opentelemetry.testing.internal.armeria.common.QueryParams
import io.opentelemetry.testing.internal.armeria.common.RequestHeaders
import org.eclipse.jetty.annotations.AnnotationConfiguration
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.webapp.WebAppContext
import org.jsoup.Jsoup
import spock.lang.Unroll

abstract class BaseJsfTest extends AgentInstrumentationSpecification implements HttpServerTestTrait<Server> {

  @Override
  Server startServer(int port) {
    String jsfVersion = getJsfVersion()

    List<String> configurationClasses = new ArrayList<>()
    Collections.addAll(configurationClasses, WebAppContext.getDefaultConfigurationClasses())
    configurationClasses.add(AnnotationConfiguration.getName())

    WebAppContext webAppContext = new WebAppContext()
    webAppContext.setContextPath(getContextPath())
    webAppContext.setConfigurationClasses(configurationClasses)
    // set up test application
    webAppContext.setBaseResource(Resource.newSystemResource("test-app-" + jsfVersion))
    // add additional resources for test app
    Resource extraResource = Resource.newSystemResource("test-app-" + jsfVersion + "-extra")
    if (extraResource != null) {
      webAppContext.getMetaData().addWebInfJar(extraResource)
    }
    webAppContext.getMetaData().getWebInfClassesDirs().add(Resource.newClassPathResource("/"))

    def jettyServer = new Server(port)
    jettyServer.connectors.each {
      it.setHost('localhost')
    }

    jettyServer.setHandler(webAppContext)
    jettyServer.start()

    return jettyServer
  }

  abstract String getJsfVersion();

  @Override
  void stopServer(Server server) {
    server.stop()
    server.destroy()
  }

  @Override
  String getContextPath() {
    return "/jetty-context"
  }

  @Unroll
  def "test #path"() {
    setup:
    AggregatedHttpResponse response = client.get(address.resolve("hello.jsf").toString()).aggregate().join()

    expect:
    response.status().code() == 200
    response.contentUtf8().trim() == "Hello"

    and:
    assertTraces(1) {
      trace(0, 1) {
        basicServerSpan(it, 0, getContextPath() + "/hello.xhtml", null)
      }
    }

    where:
    path << ['hello.jsf', 'faces/hello.xhtml']
  }

  def "test greeting"() {
    // we need to display the page first before posting data to it
    setup:
    AggregatedHttpResponse response = client.get(address.resolve("greeting.jsf").toString()).aggregate().join()
    def doc = Jsoup.parse(response.contentUtf8())

    expect:
    response.status().code() == 200
    doc.selectFirst("title").text() == "Hello, World!"

    and:
    assertTraces(1) {
      trace(0, 1) {
        basicServerSpan(it, 0, getContextPath() + "/greeting.xhtml", null)
      }
    }
    clearExportedData()

    when:
    // extract parameters needed to post back form
    def viewState = doc.selectFirst("[name=javax.faces.ViewState]")?.val()
    def formAction = doc.selectFirst("#app-form").attr("action")
    def jsessionid = formAction.substring(formAction.indexOf("jsessionid=") + "jsessionid=".length())

    then:
    viewState != null
    jsessionid != null

    when:
    // set up form parameter for post
    QueryParams formBody = QueryParams.builder()
      .add("app-form", "app-form")
    // value used for name is returned in app-form:output-message element
      .add("app-form:name", "test")
      .add("app-form:submit", "Say hello")
      .add("app-form_SUBMIT", "1") // MyFaces
      .add("javax.faces.ViewState", viewState)
      .build()
    // use the session created for first request
    def request2 = AggregatedHttpRequest.of(
      RequestHeaders.builder(HttpMethod.POST, address.resolve("greeting.jsf;jsessionid=" + jsessionid).toString())
        .contentType(MediaType.FORM_DATA)
        .build(),
      HttpData.ofUtf8(formBody.toQueryString()))
    AggregatedHttpResponse response2 = client.execute(request2).aggregate().join()
    def responseContent = response2.contentUtf8()
    def doc2 = Jsoup.parse(responseContent)

    then:
    response2.status().code() == 200
    doc2.getElementById("app-form:output-message").text() == "Hello test"

    and:
    assertTraces(1) {
      trace(0, 2) {
        basicServerSpan(it, 0, getContextPath() + "/greeting.xhtml", null)
        handlerSpan(it, 1, span(0), "#{greetingForm.submit()}")
      }
    }
  }

  def "test exception"() {
    // we need to display the page first before posting data to it
    setup:
    AggregatedHttpResponse response = client.get(address.resolve("greeting.jsf").toString()).aggregate().join()
    def doc = Jsoup.parse(response.contentUtf8())

    expect:
    response.status().code() == 200
    doc.selectFirst("title").text() == "Hello, World!"

    and:
    assertTraces(1) {
      trace(0, 1) {
        basicServerSpan(it, 0, getContextPath() + "/greeting.xhtml", null)
      }
    }
    clearExportedData()

    when:
    // extract parameters needed to post back form
    def viewState = doc.selectFirst("[name=javax.faces.ViewState]").val()
    def formAction = doc.selectFirst("#app-form").attr("action")
    def jsessionid = formAction.substring(formAction.indexOf("jsessionid=") + "jsessionid=".length())

    then:
    viewState != null
    jsessionid != null

    when:
    // set up form parameter for post
    QueryParams formBody = QueryParams.builder()
      .add("app-form", "app-form")
    // setting name parameter to "exception" triggers throwing exception in GreetingForm
      .add("app-form:name", "exception")
      .add("app-form:submit", "Say hello")
      .add("app-form_SUBMIT", "1") // MyFaces
      .add("javax.faces.ViewState", viewState)
      .build()
    // use the session created for first request
    def request2 = AggregatedHttpRequest.of(
      RequestHeaders.builder(HttpMethod.POST, address.resolve("greeting.jsf;jsessionid=" + jsessionid).toString())
        .contentType(MediaType.FORM_DATA)
        .build(),
      HttpData.ofUtf8(formBody.toQueryString()))
    AggregatedHttpResponse response2 = client.execute(request2).aggregate().join()

    then:
    response2.status().code() == 500

    and:
    assertTraces(1) {
      trace(0, 2) {
        basicServerSpan(it, 0, getContextPath() + "/greeting.xhtml", null, new Exception("submit exception"))
        handlerSpan(it, 1, span(0), "#{greetingForm.submit()}", new Exception("submit exception"))
      }
    }
  }

  void handlerSpan(TraceAssert trace, int index, Object parent, String spanName, Exception expectedException = null) {
    trace.span(index) {
      name spanName
      kind INTERNAL
      if (expectedException != null) {
        status ERROR
        errorEvent(expectedException.getClass(), expectedException.getMessage())
      }
      childOf((SpanData) parent)
    }
  }
}
