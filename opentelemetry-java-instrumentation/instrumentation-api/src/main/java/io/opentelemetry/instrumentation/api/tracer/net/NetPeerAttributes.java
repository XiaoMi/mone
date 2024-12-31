/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.tracer.net;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.instrumentation.api.config.Config;
import io.opentelemetry.instrumentation.api.tracer.AttributeSetter;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class NetPeerAttributes {

  // TODO: this should only be used by the javaagent; move to javaagent-api after removing all
  // library usages
  public static final NetPeerAttributes INSTANCE =
      new NetPeerAttributes(
          Config.get().getMap("otel.instrumentation.common.peer-service-mapping"));

  private final Map<String, String> peerServiceMapping;

  public NetPeerAttributes() {
    this(Collections.emptyMap());
  }

  public NetPeerAttributes(Map<String, String> peerServiceMapping) {
    this.peerServiceMapping = peerServiceMapping;
  }

  public void setNetPeer(Span span, @Nullable InetSocketAddress remoteConnection) {
    setNetPeer(span::setAttribute, remoteConnection);
  }

  public void setNetPeer(SpanBuilder span, @Nullable InetSocketAddress remoteConnection) {
    setNetPeer(span::setAttribute, remoteConnection);
  }

  public void setNetPeer(AttributeSetter span, @Nullable InetSocketAddress remoteConnection) {
    if (remoteConnection != null) {
      InetAddress remoteAddress = remoteConnection.getAddress();
      if (remoteAddress != null) {
        setNetPeer(
            span,
            remoteAddress.getHostName(),
            remoteAddress.getHostAddress(),
            remoteConnection.getPort());
      } else {
        // Failed DNS lookup, the host string is the name.
        setNetPeer(span, remoteConnection.getHostString(), null, remoteConnection.getPort());
      }
    }
  }

  public void setNetPeer(SpanBuilder span, InetAddress remoteAddress, int port) {
    setNetPeer(
        span::setAttribute, remoteAddress.getHostName(), remoteAddress.getHostAddress(), port);
  }

  public void setNetPeer(Span span, String peerName, String peerIp) {
    setNetPeer(span::setAttribute, peerName, peerIp, -1);
  }

  public void setNetPeer(Span span, String peerName, String peerIp, int port) {
    setNetPeer(span::setAttribute, peerName, peerIp, port);
  }

  public void setNetPeer(
      AttributeSetter span, @Nullable String peerName, @Nullable String peerIp, int port) {
    if (peerName != null && !peerName.equals(peerIp)) {
      span.setAttribute(SemanticAttributes.NET_PEER_NAME, peerName);
    }
    if (peerIp != null) {
      span.setAttribute(SemanticAttributes.NET_PEER_IP, peerIp);
    }

    String peerService = mapToPeerService(peerName);
    if (peerService == null) {
      peerService = mapToPeerService(peerIp);
    }
    if (peerService != null) {
      span.setAttribute(SemanticAttributes.PEER_SERVICE, peerService);
    }
    if (port > 0) {
      span.setAttribute(SemanticAttributes.NET_PEER_PORT, (long) port);
    }
  }

  private String mapToPeerService(String endpoint) {
    if (endpoint == null) {
      return null;
    }

    return peerServiceMapping.get(endpoint);
  }
}
