/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.CLIENT

import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes
import org.testcontainers.containers.GenericContainer
import redis.clients.jedis.Jedis
import spock.lang.Shared

class JedisClientTest extends AgentInstrumentationSpecification {

  private static GenericContainer redisServer = new GenericContainer<>("redis:6.2.3-alpine").withExposedPorts(6379)

  @Shared
  int port

  @Shared
  Jedis jedis

  def setupSpec() {
    redisServer.start()
    port = redisServer.getMappedPort(6379)
    jedis = new Jedis("localhost", port)
  }

  def cleanupSpec() {
    redisServer.stop()
//    jedis.close()  // not available in the early version we're using.
  }

  def setup() {
    jedis.flushAll()
    clearExportedData()
  }

  def "set command"() {
    when:
    jedis.set("foo", "bar")

    then:
    assertTraces(1) {
      trace(0, 1) {
        span(0) {
          name "SET"
          kind CLIENT
          attributes {
            "$SemanticAttributes.DB_SYSTEM.key" "redis"
            "$SemanticAttributes.DB_STATEMENT.key" "SET foo ?"
            "$SemanticAttributes.DB_OPERATION.key" "SET"
            "$SemanticAttributes.NET_PEER_NAME.key" "localhost"
            "$SemanticAttributes.NET_PEER_PORT.key" port
          }
        }
      }
    }
  }

  def "get command"() {
    when:
    jedis.set("foo", "bar")
    def value = jedis.get("foo")

    then:
    value == "bar"

    assertTraces(2) {
      trace(0, 1) {
        span(0) {
          name "SET"
          kind CLIENT
          attributes {
            "$SemanticAttributes.DB_SYSTEM.key" "redis"
            "$SemanticAttributes.DB_STATEMENT.key" "SET foo ?"
            "$SemanticAttributes.DB_OPERATION.key" "SET"
            "$SemanticAttributes.NET_PEER_NAME.key" "localhost"
            "$SemanticAttributes.NET_PEER_PORT.key" port
          }
        }
      }
      trace(1, 1) {
        span(0) {
          name "GET"
          kind CLIENT
          attributes {
            "$SemanticAttributes.DB_SYSTEM.key" "redis"
            "$SemanticAttributes.DB_STATEMENT.key" "GET foo"
            "$SemanticAttributes.DB_OPERATION.key" "GET"
            "$SemanticAttributes.NET_PEER_NAME.key" "localhost"
            "$SemanticAttributes.NET_PEER_PORT.key" port
          }
        }
      }
    }
  }

  def "command with no arguments"() {
    when:
    jedis.set("foo", "bar")
    def value = jedis.randomKey()

    then:
    value == "foo"

    assertTraces(2) {
      trace(0, 1) {
        span(0) {
          name "SET"
          kind CLIENT
          attributes {
            "$SemanticAttributes.DB_SYSTEM.key" "redis"
            "$SemanticAttributes.DB_STATEMENT.key" "SET foo ?"
            "$SemanticAttributes.DB_OPERATION.key" "SET"
            "$SemanticAttributes.NET_PEER_NAME.key" "localhost"
            "$SemanticAttributes.NET_PEER_PORT.key" port
          }
        }
      }
      trace(1, 1) {
        span(0) {
          name "RANDOMKEY"
          kind CLIENT
          attributes {
            "$SemanticAttributes.DB_SYSTEM.key" "redis"
            "$SemanticAttributes.DB_STATEMENT.key" "RANDOMKEY"
            "$SemanticAttributes.DB_OPERATION.key" "RANDOMKEY"
            "$SemanticAttributes.NET_PEER_NAME.key" "localhost"
            "$SemanticAttributes.NET_PEER_PORT.key" port
          }
        }
      }
    }
  }
}
