/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.spring.integration;

import io.opentelemetry.context.propagation.TextMapGetter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;

// Reading native headers is required by some protocols, e.g. STOMP
// see https://github.com/spring-cloud/spring-cloud-sleuth/issues/716 for more details
// Native headers logic inspired by
// https://github.com/spring-cloud/spring-cloud-sleuth/blob/main/spring-cloud-sleuth-instrumentation/src/main/java/org/springframework/cloud/sleuth/instrument/messaging/MessageHeaderPropagatorGetter.java
enum MessageHeadersGetter implements TextMapGetter<MessageWithChannel> {
  INSTANCE;

  @Override
  public Iterable<String> keys(MessageWithChannel carrier) {
    MessageHeaders headers = carrier.getMessage().getHeaders();
    Map<String, List<String>> nativeHeaders =
        headers.get(NativeMessageHeaderAccessor.NATIVE_HEADERS, Map.class);
    if (nativeHeaders != null) {
      return nativeHeaders.keySet();
    }
    return headers.keySet();
  }

  @Override
  public String get(MessageWithChannel carrier, String key) {
    MessageHeaders headers = carrier.getMessage().getHeaders();
    String nativeHeaderValue = getNativeHeader(headers, key);
    if (nativeHeaderValue != null) {
      return nativeHeaderValue;
    }
    Object headerValue = headers.get(key);
    if (headerValue == null) {
      return null;
    }
    if (headerValue instanceof byte[]) {
      return new String((byte[]) headerValue, StandardCharsets.UTF_8);
    }
    return headerValue.toString();
  }

  @Nullable
  private static String getNativeHeader(MessageHeaders carrier, String key) {
    Map<String, List<String>> nativeMap =
        carrier.get(NativeMessageHeaderAccessor.NATIVE_HEADERS, Map.class);
    if (nativeMap == null) {
      return null;
    }
    List<String> values = nativeMap.get(key);
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }
}
