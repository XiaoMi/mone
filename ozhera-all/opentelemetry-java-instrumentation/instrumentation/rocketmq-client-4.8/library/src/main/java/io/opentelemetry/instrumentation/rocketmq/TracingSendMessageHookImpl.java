/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.rocketmq;

import static io.opentelemetry.instrumentation.rocketmq.TextMapInjectAdapter.SETTER;

import io.opentelemetry.context.Context;
import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;
import org.apache.rocketmq.client.trace.TraceBean;
import org.apache.rocketmq.client.trace.TraceContext;

import java.util.ArrayList;
import java.util.List;

final class TracingSendMessageHookImpl implements SendMessageHook {

    private final RocketMqProducerTracer tracer;
    private final boolean propagationEnabled;

    TracingSendMessageHookImpl(RocketMqProducerTracer tracer, boolean propagationEnabled) {
        this.tracer = tracer;
        this.propagationEnabled = propagationEnabled;
    }

    @Override
    public String hookName() {
        return "OpenTelemetrySendMessageTraceHook";
    }

    @Override
    public void sendMessageBefore(SendMessageContext context) {
        if (context == null) {
            return;
        }
        Context otelContext =
                tracer.startProducerSpan(Context.current(), context.getBrokerAddr(), context.getMessage());
        if (propagationEnabled) {
            tracer.inject(otelContext, context.getMessage().getProperties(), SETTER);
        }
        // 兼容rocketmq自带的Hook
        Object mqTraceContextObj = context.getMqTraceContext();
        if (mqTraceContextObj == null) {
            mqTraceContextObj = new TraceContext();
            context.setMqTraceContext(mqTraceContextObj);
        }
        TraceContext mqTraceContext = (TraceContext) mqTraceContextObj;
        List<TraceBean> traceBeans = mqTraceContext.getTraceBeans();
        if (traceBeans == null) {
            traceBeans = new ArrayList<>(1);
            mqTraceContext.setTraceBeans(traceBeans);
        }
        traceBeans.add(ContextAndScope.create(otelContext, otelContext.makeCurrent()));
    }

    @Override
    public void sendMessageAfter(SendMessageContext context) {
        if (context == null || context.getMqTraceContext() == null || context.getSendResult() == null || context.getMqTraceContext() == null) {
            return;
        }
        TraceContext mqTraceContext = (TraceContext) context.getMqTraceContext();
        List<TraceBean> traceBeans = mqTraceContext.getTraceBeans();
        if (traceBeans != null) {
            for (TraceBean traceBean : traceBeans) {
                if (traceBean instanceof ContextAndScope) {
                    ContextAndScope otelContext = (ContextAndScope) traceBean;
                    tracer.afterProduce(otelContext.getContext(), context.getSendResult());
                    otelContext.close();
                    tracer.end(otelContext.getContext());
                }
            }
        }
    }
}
