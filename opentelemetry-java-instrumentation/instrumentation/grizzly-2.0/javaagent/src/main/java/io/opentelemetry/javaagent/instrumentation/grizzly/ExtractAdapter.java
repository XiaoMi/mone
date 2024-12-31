/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.grizzly;

import io.opentelemetry.context.propagation.TextMapGetter;
import org.glassfish.grizzly.http.HttpRequestPacket;

public class ExtractAdapter implements TextMapGetter<HttpRequestPacket> {
  public static final ExtractAdapter GETTER = new ExtractAdapter();

  @Override
  public Iterable<String> keys(HttpRequestPacket request) {
    return request.getHeaders().names();
  }

  @Override
  public String get(HttpRequestPacket request, String key) {
    return request.getHeader(key);
  }
}
