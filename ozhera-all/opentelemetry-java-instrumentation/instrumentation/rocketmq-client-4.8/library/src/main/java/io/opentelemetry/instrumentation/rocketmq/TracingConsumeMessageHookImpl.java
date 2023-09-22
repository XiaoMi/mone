/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rocketmq;

import io.opentelemetry.context.Context;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.client.trace.TraceBean;
import org.apache.rocketmq.client.trace.TraceContext;

import java.util.ArrayList;
import java.util.List;

final class TracingConsumeMessageHookImpl implements ConsumeMessageHook {

  private final RocketMqConsumerTracer tracer;

  TracingConsumeMessageHookImpl(RocketMqConsumerTracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public String hookName() {
    return "OpenTelemetryConsumeMessageTraceHook";
  }

  @Override
  public void consumeMessageBefore(ConsumeMessageContext context) {
    if (context == null || context.getMsgList() == null || context.getMsgList().isEmpty()) {
      return;
    }
    Context otelContext = tracer.startSpan(Context.current(), context.getMsgList());

    // it's safe to store the scope in the rocketMq trace context, both before() and after() methods
    // are always called from the same thread; see:
    // - ConsumeMessageConcurrentlyService$ConsumeRequest#run()
    // - ConsumeMessageOrderlyService$ConsumeRequest#run()
    // 兼容自带的rocketmq
    Object mqTraceContextObj = context.getMqTraceContext();
    if(mqTraceContextObj == null){
      mqTraceContextObj = new TraceContext();
      context.setMqTraceContext(mqTraceContextObj);
    }
    TraceContext mqTraceContext = (TraceContext)mqTraceContextObj;
    List<TraceBean> traceBeans = mqTraceContext.getTraceBeans();
    if(traceBeans == null){
      traceBeans = new ArrayList<>(1);
      mqTraceContext.setTraceBeans(traceBeans);
    }
    traceBeans.add(ContextAndScope.create(otelContext, otelContext.makeCurrent()));
  }

  @Override
  public void consumeMessageAfter(ConsumeMessageContext context) {
    if (context == null || context.getMsgList() == null || context.getMsgList().isEmpty() || context.getMqTraceContext() == null) {
      return;
    }
    TraceContext mqTraceContext = (TraceContext) context.getMqTraceContext();
    List<TraceBean> traceBeans = mqTraceContext.getTraceBeans();
    if(traceBeans != null){
      for(TraceBean traceBean : traceBeans){
        if(traceBean instanceof ContextAndScope){
          ContextAndScope contextAndScope = (ContextAndScope) traceBean;
          contextAndScope.close();
          tracer.end(contextAndScope.getContext());
        }
      }
    }
  }
}
