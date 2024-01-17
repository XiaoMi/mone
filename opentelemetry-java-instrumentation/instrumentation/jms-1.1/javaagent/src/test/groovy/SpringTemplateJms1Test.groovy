/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static Jms1Test.consumerSpan
import static Jms1Test.producerSpan

import com.google.common.base.Stopwatch
import io.opentelemetry.instrumentation.test.AgentInstrumentationSpecification
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.jms.Connection
import javax.jms.Session
import javax.jms.TextMessage
import org.apache.activemq.ActiveMQConnectionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jms.core.JmsTemplate
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import spock.lang.Shared

class SpringTemplateJms1Test extends AgentInstrumentationSpecification {
  private static final Logger logger = LoggerFactory.getLogger(SpringTemplateJms1Test)

  private static final GenericContainer broker = new GenericContainer("rmohr/activemq:latest")
    .withExposedPorts(61616, 8161)
    .withLogConsumer(new Slf4jLogConsumer(logger))

  @Shared
  String messageText = "a message"
  @Shared
  JmsTemplate template
  @Shared
  Session session

  def setupSpec() {
    broker.start()
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:" + broker.getMappedPort(61616))
    Connection connection = connectionFactory.createConnection()
    connection.start()
    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)

    template = new JmsTemplate(connectionFactory)
    // Make this longer than timeout on testWriter.waitForTraces
    // Otherwise caller might give up waiting before callee has a chance to respond.
    template.receiveTimeout = TimeUnit.SECONDS.toMillis(21)
  }

  def cleanupSpec() {
    broker.stop()
  }

  def "sending a message to #destinationName generates spans"() {
    setup:
    template.convertAndSend(destination, messageText)
    TextMessage receivedMessage = template.receive(destination)

    expect:
    receivedMessage.text == messageText
    assertTraces(2) {
      trace(0, 1) {
        producerSpan(it, 0, destinationType, destinationName)
      }
      trace(1, 1) {
        consumerSpan(it, 0, destinationType, destinationName, receivedMessage.getJMSMessageID(), null, "receive")
      }
    }

    where:
    destination                               | destinationType | destinationName
    session.createQueue("SpringTemplateJms1") | "queue"         | "SpringTemplateJms1"
  }

  def "send and receive message generates spans"() {
    setup:
    AtomicReference<String> msgId = new AtomicReference<>()
    Thread.start {
      TextMessage msg = template.receive(destination)
      assert msg.text == messageText
      msgId.set(msg.getJMSMessageID())

      template.send(msg.getJMSReplyTo()) {
        session -> template.getMessageConverter().toMessage("responded!", session)
      }
    }
    def receivedMessage
    def stopwatch = Stopwatch.createStarted()
    while (receivedMessage == null && stopwatch.elapsed(TimeUnit.SECONDS) < 10) {
      // sendAndReceive() returns null if template.receive() has not been called yet
      receivedMessage = template.sendAndReceive(destination) {
        session -> template.getMessageConverter().toMessage(messageText, session)
      }
    }

    expect:
    receivedMessage.text == "responded!"
    assertTraces(4) {
      traces.sort(orderByRootSpanName(
        "$destinationName receive",
        "$destinationName send",
        "(temporary) receive",
        "(temporary) send"))

      trace(0, 1) {
        consumerSpan(it, 0, destinationType, destinationName, msgId.get(), null, "receive")
      }
      trace(1, 1) {
        producerSpan(it, 0, destinationType, destinationName)
      }
      trace(2, 1) {
        consumerSpan(it, 0, "queue", "(temporary)", receivedMessage.getJMSMessageID(), null, "receive")
      }
      trace(3, 1) {
        // receive doesn't propagate the trace, so this is a root
        producerSpan(it, 0, "queue", "(temporary)")
      }
    }

    where:
    destination                               | destinationType | destinationName
    session.createQueue("SpringTemplateJms1") | "queue"         | "SpringTemplateJms1"
  }
}
