/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.CLIENT

import groovy.json.JsonSlurper
import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.Response
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder
import org.testcontainers.elasticsearch.ElasticsearchContainer
import spock.lang.Shared

class Elasticsearch5RestClientTest extends AgentInstrumentationSpecification {
  @Shared
  ElasticsearchContainer elasticsearch

  @Shared
  HttpHost httpHost

  @Shared
  static RestClient client

  def setupSpec() {
    if (!Boolean.getBoolean("testLatestDeps")) {
      elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:5.6.16")
        .withEnv("xpack.ml.enabled", "false")
        .withEnv("xpack.security.enabled", "false")
    } else {
      elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch-oss:6.8.16")
    }
    elasticsearch.start()

    httpHost = HttpHost.create(elasticsearch.getHttpHostAddress())
    client = RestClient.builder(httpHost)
      .setMaxRetryTimeoutMillis(Integer.MAX_VALUE)
      .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
        @Override
        RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
          return builder.setConnectTimeout(Integer.MAX_VALUE).setSocketTimeout(Integer.MAX_VALUE)
        }
      })
      .build()
  }

  def cleanupSpec() {
    elasticsearch.stop()
  }

  def "test elasticsearch status"() {
    setup:
    Response response = client.performRequest("GET", "_cluster/health")

    Map result = new JsonSlurper().parseText(EntityUtils.toString(response.entity))

    expect:
    result.status == "green"

    assertTraces(1) {
      trace(0, 1) {
        span(0) {
          name "GET _cluster/health"
          kind CLIENT
          hasNoParent()
          attributes {
            "${SemanticAttributes.DB_SYSTEM.key}" "elasticsearch"
            "${SemanticAttributes.DB_OPERATION.key}" "GET _cluster/health"
            "${SemanticAttributes.NET_PEER_NAME.key}" httpHost.hostName
            "${SemanticAttributes.NET_PEER_PORT.key}" httpHost.port
          }
        }
      }
    }
  }
}
