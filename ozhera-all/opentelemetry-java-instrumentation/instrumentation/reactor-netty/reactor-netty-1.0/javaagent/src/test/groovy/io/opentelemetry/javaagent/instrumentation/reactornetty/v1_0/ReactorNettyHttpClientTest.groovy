/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.reactornetty.v1_0

import io.netty.channel.ChannelOption
import io.opentelemetry.instrumentation.test.base.SingleConnection
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException
import reactor.netty.http.client.HttpClient

class ReactorNettyHttpClientTest extends AbstractReactorNettyHttpClientTest {

  HttpClient createHttpClient() {
    return HttpClient.create().tcpConfiguration({ tcpClient ->
      tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
    }).resolver(getAddressResolverGroup())
  }

  @Override
  SingleConnection createSingleConnection(String host, int port) {
    def httpClient = HttpClient
      .newConnection()
      .host(host)
      .port(port)

    return new SingleConnection() {

      @Override
      int doRequest(String path, Map<String, String> headers) throws ExecutionException, InterruptedException, TimeoutException {
        return httpClient
          .headers({ h -> headers.each { k, v -> h.add(k, v) } })
          .get()
          .uri(path)
          .responseSingle {resp, content ->
            // Make sure to consume content since that's when we close the span.
            content.map { resp }
          }
          .block()
          .status().code()
      }
    }
  }
}
