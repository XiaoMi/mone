/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package client

import io.opentelemetry.instrumentation.test.AgentTestTrait
import io.opentelemetry.instrumentation.test.base.HttpClientTest
import io.opentelemetry.instrumentation.test.base.SingleConnection
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.http.HttpMethod
import java.util.concurrent.CompletableFuture
import spock.lang.Shared

class VertxHttpClientTest extends HttpClientTest<HttpClientRequest> implements AgentTestTrait {

  @Shared
  def vertx = Vertx.vertx(new VertxOptions())
  @Shared
  def clientOptions = new HttpClientOptions().setConnectTimeout(CONNECT_TIMEOUT_MS)
  @Shared
  def httpClient = vertx.createHttpClient(clientOptions)

  @Override
  HttpClientRequest buildRequest(String method, URI uri, Map<String, String> headers) {
    def request = httpClient.request(HttpMethod.valueOf(method), getPort(uri), uri.host, "$uri")
    headers.each { request.putHeader(it.key, it.value) }
    return request
  }

  CompletableFuture<Integer> sendRequest(HttpClientRequest request) {
    CompletableFuture<Integer> future = new CompletableFuture<>()

    request.handler { response ->
      future.complete(response.statusCode())
    }.exceptionHandler {throwable ->
      future.completeExceptionally(throwable)
    }
    request.end()

    return future
  }

  @Override
  int sendRequest(HttpClientRequest request, String method, URI uri, Map<String, String> headers) {
    // Vertx doesn't seem to provide any synchronous API so bridge through a callback
    return sendRequest(request).get()
  }

  @Override
  void sendRequestWithCallback(HttpClientRequest request, String method, URI uri, Map<String, String> headers, RequestResult requestResult) {
    sendRequest(request).whenComplete { status, throwable ->
      requestResult.complete({ status }, throwable)
    }
  }

  @Override
  boolean testRedirects() {
    false
  }

  @Override
  boolean testReusedRequest() {
    // vertx requests can't be reused
    false
  }

  @Override
  boolean testHttps() {
    false
  }

  @Override
  SingleConnection createSingleConnection(String host, int port) {
    //This test fails on Vert.x 3.0 and only works starting from 3.1
    //Most probably due to https://github.com/eclipse-vertx/vert.x/pull/1126
    boolean shouldRun = Boolean.getBoolean("testLatestDeps")
    return shouldRun ? new VertxSingleConnection(host, port) : null
  }
}
