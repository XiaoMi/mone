/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.CLIENT
import static io.opentelemetry.api.trace.SpanKind.CONSUMER
import static io.opentelemetry.api.trace.SpanKind.PRODUCER
import static io.opentelemetry.api.trace.SpanKind.SERVER
import static io.opentelemetry.instrumentation.test.server.ServerTraceUtils.runUnderServerTrace

import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes

class SpringIntegrationAndRabbitTest extends AgentInstrumentationSpecification implements WithRabbitProducerConsumerTrait {
  def setupSpec() {
    startRabbit()
  }

  def cleanupSpec() {
    stopRabbit()
  }

  def "should cooperate with existing RabbitMQ instrumentation"() {
    when:
    // simulate the workflow being triggered by HTTP request
    runUnderServerTrace("HTTP GET") {
      producerContext.getBean("producer", Runnable).run()
    }

    then:
    assertTraces(2) {
      trace(0, 7) {
        span(0) {
          name "HTTP GET"
          kind SERVER
          attributes {}
        }
        span(1) {
          name "producer"
          childOf span(0)
          attributes {}
        }
        span(2) {
          // span created by rabbitmq instrumentation
          name "exchange.declare"
          childOf span(1)
          kind CLIENT
          attributes {
            "${SemanticAttributes.NET_PEER_NAME.key}" { it == null || it == "localhost" }
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" { it == null || it instanceof Long }
            "${SemanticAttributes.MESSAGING_SYSTEM.key}" "rabbitmq"
            "${SemanticAttributes.MESSAGING_DESTINATION_KIND.key}" "queue"
          }
        }
        span(3) {
          // span created by rabbitmq instrumentation
          name "testTopic -> testTopic send"
          childOf span(1)
          kind PRODUCER
          attributes {
            "${SemanticAttributes.NET_PEER_NAME.key}" "localhost"
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" Long
            "${SemanticAttributes.MESSAGING_SYSTEM.key}" "rabbitmq"
            "${SemanticAttributes.MESSAGING_DESTINATION.key}" "testTopic"
            "${SemanticAttributes.MESSAGING_DESTINATION_KIND.key}" "queue"
            "${SemanticAttributes.MESSAGING_MESSAGE_PAYLOAD_SIZE_BYTES.key}" Long
          }
        }
        // spring-cloud-stream-binder-rabbit listener puts all messages into a BlockingQueue immediately after receiving
        // that's why the rabbitmq CONSUMER span will never have any child span (and propagate context, actually)
        // and that's why spring-integration creates another CONSUMER span
        span(4) {
          // span created by rabbitmq instrumentation
          name ~/testTopic.anonymous.[-\w]+ process/
          childOf span(3)
          kind CONSUMER
          attributes {
            "${SemanticAttributes.MESSAGING_SYSTEM.key}" "rabbitmq"
            "${SemanticAttributes.MESSAGING_DESTINATION.key}" "testTopic"
            "${SemanticAttributes.MESSAGING_DESTINATION_KIND.key}" "queue"
            "${SemanticAttributes.MESSAGING_OPERATION.key}" "process"
            "${SemanticAttributes.MESSAGING_MESSAGE_PAYLOAD_SIZE_BYTES.key}" Long
          }
        }
        span(5) {
          // span created by spring-integration instrumentation
          name "testConsumer.input process"
          childOf span(3)
          kind CONSUMER
          attributes {}
        }
        span(6) {
          name "consumer"
          childOf span(5)
          attributes {}
        }
      }

      trace(1, 1) {
        span(0) {
          // span created by rabbitmq instrumentation
          name "basic.ack"
          kind CLIENT
          attributes {
            "${SemanticAttributes.NET_PEER_NAME.key}" "localhost"
            "${SemanticAttributes.NET_PEER_IP.key}" "127.0.0.1"
            "${SemanticAttributes.NET_PEER_PORT.key}" Long
            "${SemanticAttributes.MESSAGING_SYSTEM.key}" "rabbitmq"
            "${SemanticAttributes.MESSAGING_DESTINATION_KIND.key}" "queue"
          }
        }
      }
    }
  }
}
