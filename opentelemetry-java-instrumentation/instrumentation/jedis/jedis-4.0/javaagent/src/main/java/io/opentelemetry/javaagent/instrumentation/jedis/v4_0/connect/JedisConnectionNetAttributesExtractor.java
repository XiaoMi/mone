/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jedis.v4_0.connect;

import io.opentelemetry.instrumentation.api.instrumenter.net.InetSocketAddressNetAttributesExtractor;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public final class JedisConnectionNetAttributesExtractor extends InetSocketAddressNetAttributesExtractor<JedisConnectionRequest, Void> {

  @Override
  @javax.annotation.Nullable
  public InetSocketAddress getAddress(JedisConnectionRequest jedisRequest, @Nullable Void unused) {
    SocketAddress socketAddress = jedisRequest.getRemoteSocketAddress();
    if (socketAddress != null && socketAddress instanceof InetSocketAddress) {
      return (InetSocketAddress) socketAddress;
    }
    return null;
  }

  @Override
  public String transport(JedisConnectionRequest jedisRequest) {
    return SemanticAttributes.NetTransportValues.IP_TCP;
  }
}
