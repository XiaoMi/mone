/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes
import reactor.rabbitmq.ExchangeSpecification
import reactor.rabbitmq.RabbitFlux
import reactor.rabbitmq.SenderOptions

class ReactorRabbitMqTest extends AgentInstrumentationSpecification implements WithRabbitMqTrait {

  def setupSpec() {
    startRabbit()
  }

  def cleanupSpec() {
    stopRabbit()
  }

  def "should not fail declaring exchange"() {
    given:
    def sender = RabbitFlux.createSender(new SenderOptions().connectionFactory(connectionFactory))

    when:
    sender.declareExchange(ExchangeSpecification.exchange("testExchange"))
      .block()

    then:
    noExceptionThrown()

    assertTraces(1) {
      trace(0, 1) {
        span(0) {
          name 'exchange.declare'
          kind SpanKind.CLIENT
          attributes {
            "${SemanticAttributes.NET_PEER_NAME.key}" { it == null || it instanceof String }
            "${SemanticAttributes.NET_PEER_IP.key}" String
            "${SemanticAttributes.NET_PEER_PORT.key}" { it == null || it instanceof Long }
            "${SemanticAttributes.MESSAGING_SYSTEM.key}" "rabbitmq"
            "${SemanticAttributes.MESSAGING_DESTINATION_KIND.key}" "queue"
            "rabbitmq.command" "exchange.declare"
          }
        }
      }
    }

    cleanup:
    sender?.close()
  }
}
