/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jms;

import io.opentelemetry.context.propagation.TextMapSetter;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class MessagePropertySetter implements TextMapSetter<MessageWithDestination> {

  private static final Logger log = LoggerFactory.getLogger(MessagePropertySetter.class);

  static final String DASH = "__dash__";

  @Override
  public void set(MessageWithDestination carrier, String key, String value) {
    String propName = key.replace("-", DASH);
    try {
      carrier.getMessage().setStringProperty(propName, value);
    } catch (JMSException e) {
      if (log.isDebugEnabled()) {
        log.debug("Failure setting jms property: {}", propName, e);
      }
    }
  }
}
