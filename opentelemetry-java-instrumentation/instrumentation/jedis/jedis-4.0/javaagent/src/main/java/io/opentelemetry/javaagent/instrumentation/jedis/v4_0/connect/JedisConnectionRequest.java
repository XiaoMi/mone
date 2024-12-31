/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jedis.v4_0.connect;

import com.google.auto.value.AutoValue;

import java.net.Socket;
import java.net.SocketAddress;

@AutoValue
public abstract class JedisConnectionRequest {

  public static JedisConnectionRequest create() {
    return new AutoValue_JedisConnectionRequest();
  }

  private SocketAddress remoteSocketAddress;

  public void setSocket(Socket socket) {
    if (socket != null) {
      remoteSocketAddress = socket.getRemoteSocketAddress();
    }
  }

  public SocketAddress getRemoteSocketAddress() {
    return remoteSocketAddress;
  }
}
