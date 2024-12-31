/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jms;

import static io.opentelemetry.javaagent.instrumentation.jms.MessageWithDestination.TIBCO_TMP_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.given;

import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessageOperation;
import java.time.Instant;
import java.util.stream.Stream;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageWithDestinationTest {
  private static final Instant START_TIME = Instant.ofEpochSecond(42);

  @Mock Message message;
  @Mock Topic topic;
  @Mock TemporaryTopic temporaryTopic;
  @Mock Queue queue;
  @Mock TemporaryQueue temporaryQueue;
  @Mock Destination destination;

  @Test
  void shouldCreateMessageWithUnknownDestination() throws JMSException {
    // given
    given(message.getJMSDestination()).willReturn(destination);

    // when
    MessageWithDestination result =
        MessageWithDestination.create(message, MessageOperation.SEND, null, START_TIME);

    // then
    assertMessage(
        MessageOperation.SEND,
        "unknown",
        "unknown",
        /* expectedTemporary= */ false,
        START_TIME,
        result);
  }

  @Test
  void shouldUseFallbackDestinationToCreateMessage() throws JMSException {
    // given
    given(message.getJMSDestination()).willThrow(JMSException.class);

    // when
    MessageWithDestination result =
        MessageWithDestination.create(message, MessageOperation.SEND, destination, START_TIME);

    // then
    assertMessage(
        MessageOperation.SEND,
        "unknown",
        "unknown",
        /* expectedTemporary= */ false,
        START_TIME,
        result);
  }

  @ParameterizedTest
  @MethodSource("destinations")
  void shouldCreateMessageWithQueue(
      String queueName,
      boolean useTemporaryDestination,
      String expectedDestinationName,
      boolean expectedTemporary)
      throws JMSException {
    // given
    Queue queue = useTemporaryDestination ? this.temporaryQueue : this.queue;

    given(message.getJMSDestination()).willReturn(queue);
    if (queueName == null) {
      given(queue.getQueueName()).willThrow(JMSException.class);
    } else {
      given(queue.getQueueName()).willReturn(queueName);
    }

    // when
    MessageWithDestination result =
        MessageWithDestination.create(message, MessageOperation.RECEIVE, null);

    // then
    assertMessage(
        MessageOperation.RECEIVE,
        "queue",
        expectedDestinationName,
        expectedTemporary,
        null,
        result);
  }

  @ParameterizedTest
  @MethodSource("destinations")
  void shouldCreateMessageWithTopic(
      String topicName,
      boolean useTemporaryDestination,
      String expectedDestinationName,
      boolean expectedTemporary)
      throws JMSException {
    // given
    Topic topic = useTemporaryDestination ? this.temporaryTopic : this.topic;

    given(message.getJMSDestination()).willReturn(topic);
    if (topicName == null) {
      given(topic.getTopicName()).willThrow(JMSException.class);
    } else {
      given(topic.getTopicName()).willReturn(topicName);
    }

    // when
    MessageWithDestination result =
        MessageWithDestination.create(message, MessageOperation.RECEIVE, null);

    // then
    assertMessage(
        MessageOperation.RECEIVE,
        "topic",
        expectedDestinationName,
        expectedTemporary,
        null,
        result);
  }

  static Stream<Arguments> destinations() {
    return Stream.of(
        Arguments.of("destination", false, "destination", false),
        Arguments.of(null, false, "unknown", false),
        Arguments.of(TIBCO_TMP_PREFIX + "dest", false, TIBCO_TMP_PREFIX + "dest", true),
        Arguments.of("destination", true, "destination", true));
  }

  private void assertMessage(
      MessageOperation expectedMessageOperation,
      String expectedDestinationKind,
      String expectedDestinationName,
      boolean expectedTemporary,
      Instant expectedStartTime,
      MessageWithDestination actual) {

    assertSame(message, actual.getMessage());
    assertSame(expectedMessageOperation, actual.getMessageOperation());
    assertEquals(expectedDestinationKind, actual.getDestinationKind());
    assertEquals(expectedDestinationName, actual.getDestinationName());
    assertEquals(expectedTemporary, actual.isTemporaryDestination());
    assertEquals(expectedStartTime, actual.getStartTime());
  }
}
