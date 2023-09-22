/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.netty.v3_8.client;

import static io.opentelemetry.javaagent.instrumentation.netty.v3_8.client.NettyHttpClientTracer.tracer;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.instrumentation.api.ContextStore;
import io.opentelemetry.javaagent.instrumentation.netty.v3_8.ChannelTraceContext;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HttpClientRequestTracingHandler extends SimpleChannelDownstreamHandler {

  private final ContextStore<Channel, ChannelTraceContext> contextStore;

  public HttpClientRequestTracingHandler(ContextStore<Channel, ChannelTraceContext> contextStore) {
    this.contextStore = contextStore;
  }

  @Override
  public void writeRequested(ChannelHandlerContext ctx, MessageEvent event) {
    Object message = event.getMessage();
    if (!(message instanceof HttpRequest)) {
      ctx.sendDownstream(event);
      return;
    }

    ChannelTraceContext channelTraceContext =
        contextStore.putIfAbsent(ctx.getChannel(), ChannelTraceContext.Factory.INSTANCE);

    Context parentContext = channelTraceContext.getConnectionContext();
    if (parentContext != null) {
      channelTraceContext.setConnectionContext(null);
    } else {
      parentContext = Context.current();
    }

    if (!tracer().shouldStartSpan(parentContext)) {
      ctx.sendDownstream(event);
      return;
    }

    Context context = tracer().startSpan(parentContext, ctx, (HttpRequest) message);
    channelTraceContext.setContext(context);
    channelTraceContext.setClientParentContext(parentContext);

    try (Scope ignored = context.makeCurrent()) {
      ctx.sendDownstream(event);
    } catch (Throwable throwable) {
      tracer().endExceptionally(context, throwable);
      throw throwable;
    }
  }
}
