/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static Jms2Test.consumerSpan
import static Jms2Test.producerSpan
import static io.opentelemetry.api.trace.SpanKind.CONSUMER
import static io.opentelemetry.api.trace.SpanKind.PRODUCER

import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import javax.jms.ConnectionFactory
import listener.Config
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.jms.core.JmsTemplate

class SpringListenerJms2Test extends AgentInstrumentationSpecification {
  def "receiving message in spring listener generates spans"() {
    setup:
    def context = new AnnotationConfigApplicationContext(Config)
    def factory = context.getBean(ConnectionFactory)
    def template = new JmsTemplate(factory)

    template.convertAndSend("SpringListenerJms2", "a message")

    expect:
    assertTraces(2) {
      traces.sort(orderByRootSpanKind(CONSUMER, PRODUCER))

      trace(0, 1) {
        consumerSpan(it, 0, "queue", "SpringListenerJms2", "", null, "receive")
      }
      trace(1, 2) {
        producerSpan(it, 0, "queue", "SpringListenerJms2")
        consumerSpan(it, 1, "queue", "SpringListenerJms2", "", span(0), "process")
      }
    }

    cleanup:
    context.close()
  }
}
