package io.opentelemetry.javaagent.instrumentation.tesla.sidecar;

import com.xiaomi.data.push.uds.po.RpcCommand;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.HashMap;
import java.util.Map;

import static io.opentelemetry.javaagent.instrumentation.tesla.sidecar.SidecarTracer.sidecarTracer;

@SuppressWarnings({"CatchAndPrintStackTrace","SystemOut"})
public class SidecarServiceInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return ElementMatchers.named("com.xiaomi.youpin.gateway.sidecar.SidecarService");

    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                ElementMatchers.named("call")
                        .and(ElementMatchers.isPublic())
                        .and(ElementMatchers.takesArguments(1))
                        .and(ElementMatchers.takesArgument(0, ElementMatchers.named("com.xiaomi.data.push.uds.po.RpcCommand"))),
                this.getClass().getName() + "$CallAdvice");

    }


    public static class CallAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void enter(@Advice.Local("otelContext") Context context,
                                 @Advice.Local("sidecarStartTime") long startTime,
                                 @Advice.Argument(0) RpcCommand rpcCommand) {
            startTime = System.currentTimeMillis();
            if(rpcCommand != null){
                try {
                    Map<String, String> attachments = rpcCommand.getAttachments();
                    if (attachments == null) {
                        attachments = new HashMap<>();
                    }
                    context = Context.current();
                    sidecarTracer().inject(context, rpcCommand, SidecarInjectAdapter.SETTER);
                }catch (Throwable t){
                    t.printStackTrace();
                }
            }
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("sidecarStartTime") long startTime,
                                @Advice.Argument(0) RpcCommand rpcCommand) {
            context = Context.current();
            long time = System.currentTimeMillis() - startTime;
            Span.fromContext(context).addEvent(rpcCommand.getApp()+".client", Attributes.builder().put("time", time + "ms").build());
            return;
        }
    }
}
