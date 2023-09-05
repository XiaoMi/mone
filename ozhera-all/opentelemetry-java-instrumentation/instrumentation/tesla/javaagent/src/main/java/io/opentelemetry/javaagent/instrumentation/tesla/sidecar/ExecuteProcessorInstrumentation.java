package io.opentelemetry.javaagent.instrumentation.tesla.sidecar;

import com.xiaomi.data.push.uds.po.RpcCommand;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.HashMap;
import java.util.Map;

import static io.opentelemetry.javaagent.instrumentation.tesla.sidecar.SidecarTracer.sidecarTracer;

@SuppressWarnings({"CatchAndPrintStackTrace", "SystemOut"})
public class ExecuteProcessorInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return ElementMatchers.nameEndsWith("ExecuteProcessor");

    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                ElementMatchers.named("processRequest")
                        .and(ElementMatchers.isPublic())
                        .and(ElementMatchers.takesArguments(1))
                        .and(ElementMatchers.takesArgument(0, ElementMatchers.named("com.xiaomi.data.push.uds.po.RpcCommand"))),
                this.getClass().getName() + "$ProcessRequestAdvice");

    }


    public static class ProcessRequestAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void enter(@Advice.Local("otelContext") Context context,
                                 @Advice.Local("otelScope") Scope scope,
                                 @Advice.Argument(0) RpcCommand rpcCommand) {
            if (rpcCommand != null) {
                try {
                    Map<String, String> attachments = rpcCommand.getAttachments();
                    if (attachments == null) {
                        attachments = new HashMap<>();
                    }
                    context = sidecarTracer().extract(rpcCommand, SidecarExtractAdapter.GETTER);
                    scope = context.makeCurrent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("otelScope") Scope scope) {
            if(scope == null){
                return;
            }
            scope.close();
            return;
        }
    }
}
