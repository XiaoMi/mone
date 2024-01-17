/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.apachedubbo.v2_7;

import com.google.gson.Gson;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.instrumentation.api.tracer.RpcServerTracer;
import io.opentelemetry.instrumentation.api.tracer.net.NetPeerAttributes;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcInvocation;

import static io.opentelemetry.api.trace.SpanKind.CLIENT;
import static io.opentelemetry.api.trace.SpanKind.SERVER;

class DubboTracer extends RpcServerTracer<RpcInvocation> {

  private static final String BUSSINESS_RESPONSE_DATA = "result.json";
  private static final String BUSSINESS_RESULT_CODE = "result.code";

  protected DubboTracer() {}

  public Context startServerSpan(
      String interfaceName, String methodName, RpcInvocation rpcInvocation) {
    Context parentContext = extract(rpcInvocation, getGetter());
    SpanBuilder spanBuilder =
        spanBuilder(parentContext, DubboHelper.getSpanName(interfaceName, methodName), SERVER)
            .setAttribute(SemanticAttributes.RPC_SYSTEM, "dubbo");
    DubboHelper.prepareSpan(spanBuilder, interfaceName, methodName);
    NetPeerAttributes.INSTANCE.setNetPeer(spanBuilder, RpcContext.getContext().getRemoteAddress());
    return withServerSpan(Context.current(), spanBuilder.startSpan());
  }

  public Context startClientSpan(String interfaceName, String methodName, URL url) {
    Context parentContext = Context.current();
    SpanBuilder spanBuilder =
        spanBuilder(parentContext, DubboHelper.getSpanName(interfaceName, methodName), CLIENT)
            .setAttribute(SemanticAttributes.RPC_SYSTEM, "dubbo");
    DubboHelper.prepareSpan(spanBuilder, interfaceName, methodName);
    NetPeerAttributes.INSTANCE.setNetPeer(spanBuilder, RpcContext.getContext().getRemoteAddress());
    return withClientSpan(parentContext, spanBuilder.startSpan());
  }

  public void end(Context context, Result result) {
    StatusCode statusCode = DubboHelper.statusFromResult(result);
    if (statusCode != StatusCode.UNSET) {
      Span.fromContext(context).setStatus(statusCode);
    }
    end(context);
  }

  public void end(Context context, Object bizResult) {
    CheckCodeResult ccr = parseBussinessCode(context, bizResult);
    if (!ccr.isSuccess()) {
      Span.fromContext(context).setStatus(StatusCode.ERROR);
    }
    end(context);
  }

  public void parseBussinessCode(Context context,  Result result){
    this.parseBussinessCode(context, result.getValue());
  }

  public CheckCodeResult parseBussinessCode(Context context, Object bizResult){
    Span span = Span.fromContext(context);
    CheckCodeResult ccr = CodeHelper.ins().checkCode(bizResult);
    if (!ccr.isSuccess()) {
      AttributesBuilder attributes = Attributes.builder();
      attributes.put(BUSSINESS_RESULT_CODE, ccr.getCode());
      attributes.put(BUSSINESS_RESPONSE_DATA, new Gson().toJson(ccr));
      span.addEvent("biz result code exception",attributes.build());
      span.setStatus(StatusCode.ERROR);
    }

    return ccr;
  }

  @Override
  protected String getInstrumentationName() {
    return "io.opentelemetry.javaagent.apache-dubbo-2.7:0.0.1:20210831";
  }

  @Override
  protected TextMapGetter<RpcInvocation> getGetter() {
    return DubboExtractAdapter.GETTER;
  }

}
