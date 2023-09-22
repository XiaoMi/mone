package io.opentelemetry.javaagent.instrumentation.tesla;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.Map;

public class FilterContextInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return ElementMatchers.named("com.xiaomi.youpin.gateway.filter.FilterContext");

    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                ElementMatchers.named("addTraceEvent")
                        .and(ElementMatchers.isPublic())
                        .and(ElementMatchers.takesArguments(2))
                        .and(ElementMatchers.takesArgument(0, String.class))
                        .and(ElementMatchers.takesArgument(1, Long.class)),
                this.getClass().getName() + "$AddTraceEventAdvice1");
        transformer.applyAdviceToMethod(
                ElementMatchers.named("addTraceEvent")
                        .and(ElementMatchers.isPublic())
                        .and(ElementMatchers.takesArguments(2))
                        .and(ElementMatchers.takesArgument(0, String.class))
                        .and(ElementMatchers.takesArgument(1, Map.class)),
                this.getClass().getName() + "$AddTraceEventAdvice2");
        transformer.applyAdviceToMethod(
                ElementMatchers.named("addBusErrorEvent")
                        .and(ElementMatchers.isPublic()),
                this.getClass().getName() + "$AddBusErrorEventAdvice");
    }


    public static class AddTraceEventAdvice1 {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void enter(@Advice.Local("otelContext") Context context,
                                 @Advice.Local("otelScope") Scope scope,
                                 @Advice.Argument(0) String name,
                                 @Advice.Argument(1) long time) {
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("otelScope") Scope scope,
                                @Advice.Argument(0) String name,
                                @Advice.Argument(1) Long time) {
            context = Context.current();
            Span.fromContext(context).addEvent(name, Attributes.builder().put("time", time + "ms").build());
            return;
        }
    }

    public static class AddTraceEventAdvice2 {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void enter(@Advice.Local("otelContext") Context context,
                                 @Advice.Local("otelScope") Scope scope,
                                 @Advice.Argument(0) String name,
                                 @Advice.Argument(1) Map<String,String> events) {
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("otelScope") Scope scope,
                                @Advice.Argument(0) String name,
                                @Advice.Argument(1) Map<String,String> events) {
            if(events != null) {
                context = Context.current();
                AttributesBuilder builder = Attributes.builder();
                for (String key : events.keySet()) {
                    builder.put(key,events.get(key));
                }
                Span.fromContext(context).addEvent(name, builder.build());
            }
            return;
        }
    }

    public static class AddBusErrorEventAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void enter(@Advice.Local("otelContext") Context context,
                                 @Advice.Local("otelScope") Scope scope,
                                 @Advice.Argument(0) String eventName,
                                 @Advice.Argument(1) int errorCode,
                                 @Advice.Argument(2) String errorMsg) {
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("otelScope") Scope scope,
                                @Advice.Argument(0) String eventName,
                                @Advice.Argument(1) int errorCode,
                                @Advice.Argument(2) String errorMsg) {
            context = Context.current();
            Span span = Span.fromContext(context);
            AttributesBuilder builder = Attributes.builder();
            builder.put("errorCode", errorCode);
            if (null != errorMsg && !"".equals(errorMsg)) {
                if(errorMsg.length() > 4000) {
                    builder.put("result", errorMsg.substring(0, 4000) + "......");
                }else {
                    builder.put("result", errorMsg);
                }
            }
            span.addEvent(eventName, builder.build());
            span.setStatus(StatusCode.ERROR);
            return;
        }
    }
}
