/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.apachedubbo.v2_7;

import io.opentelemetry.api.trace.HeraContext;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcInvocation;

@Activate(group = {"consumer", "provider"},order = Integer.MAX_VALUE)
public class OpenTelemetryLeastFilter implements Filter {

    public OpenTelemetryLeastFilter() {
    }

  @Override
  public Result invoke(Invoker<?> invoker, Invocation invocation) {
    if (!(invocation instanceof RpcInvocation)) {
      return invoker.invoke(invocation);
    }
    Context context = Context.current();
      if(context != null) {
          String heraContext = Span.fromContext(context).getSpanContext().getHeraContext().get(HeraContext.HERA_CONTEXT_PROPAGATOR_KEY);
          if(heraContext != null) {
              RpcContext.getContext().getAttachments().put("heracontext", heraContext);
          }
    }
    return invoker.invoke(invocation);
    }
}
