/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.netty.v4_0.server;

import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.instrumentation.api.tracer.HttpServerTracer;
import io.opentelemetry.javaagent.instrumentation.netty.common.server.NettyRequestExtractAdapter;
import io.opentelemetry.javaagent.instrumentation.netty.v4_0.AttributeKeys;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class NettyHttpServerTracer
    extends HttpServerTracer<HttpRequest, HttpResponse, Channel, Channel> {
  private static final NettyHttpServerTracer TRACER = new NettyHttpServerTracer();

  public static NettyHttpServerTracer tracer() {
    return TRACER;
  }

  @Override
  protected String method(HttpRequest httpRequest) {
    return httpRequest.getMethod().name();
  }

  @Override
  protected String requestHeader(HttpRequest httpRequest, String name) {
    return httpRequest.headers().get(name);
  }

  @Override
  protected int responseStatus(HttpResponse httpResponse) {
    return httpResponse.getStatus().code();
  }

  @Override
  protected String bussinessStatus(HttpResponse httpResponse) {
    return null;
  }

  @Override
  protected String bussinessMessage(HttpResponse httpResponse) {
    return null;
  }

  @Override
  protected void attachServerContext(Context context, Channel channel) {
    channel.attr(AttributeKeys.SERVER_SPAN).set(context);
  }

  @Override
  public Context getServerContext(Channel channel) {
    return channel.attr(AttributeKeys.SERVER_SPAN).get();
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.javaagent.netty-4.0";
  }

  @Override
  protected TextMapGetter<HttpRequest> getGetter() {
    return NettyRequestExtractAdapter.GETTER;
  }

  @Override
  protected String url(HttpRequest request) {
    String uri = request.getUri();
    if (isRelativeUrl(uri) && request.headers().contains(HOST)) {
      return "http://" + request.headers().get(HOST) + request.getUri();
    } else {
      return uri;
    }
  }

  @Override
  protected String peerHostIP(Channel channel) {
    SocketAddress socketAddress = channel.remoteAddress();
    if (socketAddress instanceof InetSocketAddress) {
      return ((InetSocketAddress) socketAddress).getAddress().getHostAddress();
    }
    return null;
  }

  @Override
  protected String flavor(Channel channel, HttpRequest request) {
    return request.getProtocolVersion().toString();
  }

  @Override
  protected Integer peerPort(Channel channel) {
    SocketAddress socketAddress = channel.remoteAddress();
    if (socketAddress instanceof InetSocketAddress) {
      return ((InetSocketAddress) socketAddress).getPort();
    }
    return null;
  }
}
