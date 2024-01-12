package io.opentelemetry.javaagent.instrumentation.tesla.sidecar;

import com.xiaomi.data.push.uds.po.RpcCommand;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.instrumentation.api.tracer.RpcServerTracer;

public class SidecarTracer extends RpcServerTracer<RpcCommand> {

    private static final SidecarTracer TRACER;
    static{
        TRACER = new SidecarTracer();
    }

    public static SidecarTracer sidecarTracer(){
        return TRACER;
    }

    @Override
    protected String getInstrumentationName() {
        return "run.mone.tesla.sidecar.filter";
    }

    @Override
    protected TextMapGetter<RpcCommand> getGetter() {
        return SidecarExtractAdapter.GETTER;
    }

}
