/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.apachehttpclient.v5_0;

import io.opentelemetry.context.propagation.TextMapSetter;
import org.apache.hc.core5.http.ClassicHttpRequest;

final class HttpHeaderSetter implements TextMapSetter<ClassicHttpRequest> {

  @Override
  public void set(ClassicHttpRequest carrier, String key, String value) {
    carrier.setHeader(key, value);
  }
}
