/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.asynchttpclient.v2_0;

import io.opentelemetry.context.propagation.TextMapSetter;
import org.asynchttpclient.Request;

public class HttpHeaderSetter implements TextMapSetter<Request> {

  public static final HttpHeaderSetter SETTER = new HttpHeaderSetter();

  @Override
  public void set(Request carrier, String key, String value) {
    carrier.getHeaders().set(key, value);
  }
}
