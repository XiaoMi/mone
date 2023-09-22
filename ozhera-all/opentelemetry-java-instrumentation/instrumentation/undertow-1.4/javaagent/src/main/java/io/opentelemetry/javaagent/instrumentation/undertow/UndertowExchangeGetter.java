/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.undertow;

import io.opentelemetry.context.propagation.TextMapGetter;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import java.util.stream.Collectors;

public class UndertowExchangeGetter implements TextMapGetter<HttpServerExchange> {

  public static final UndertowExchangeGetter GETTER = new UndertowExchangeGetter();

  @Override
  public Iterable<String> keys(HttpServerExchange carrier) {
    return carrier.getRequestHeaders().getHeaderNames().stream()
        .map(HttpString::toString)
        .collect(Collectors.toList());
  }

  @Override
  public String get(HttpServerExchange carrier, String key) {
    return carrier.getRequestHeaders().getFirst(key);
  }
}
