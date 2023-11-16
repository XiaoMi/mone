package io.opentelemetry.javaagent.instrumentation.extannotations;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.api.tracer.SpanNames;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.safeHasSuperType;
import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;
import static net.bytebuddy.matcher.ElementMatchers.declaresMethod;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class TraceEventTimeInstrumentation implements TypeInstrumentation {

    private static final ElementMatcher.Junction<NamedElement> traceAnnotationMatcher = named("com.xiaomi.hera.trace.annotation.TraceTimeEvent");

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {

        return safeHasSuperType(declaresMethod(isAnnotatedWith(traceAnnotationMatcher)));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isAnnotatedWith(traceAnnotationMatcher),
                TraceEventTimeInstrumentation.class.getName() + "$TraceTimeAdvice");
    }

    @SuppressWarnings({"unused"})
    public static class TraceTimeAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void onEnter(
                @Advice.Origin Method method,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope,
                @Advice.Local("eventStartTime") long eventStartTime) {
            context = currentContext();
            if (context == null) {
                return;
            }
            eventStartTime = System.currentTimeMillis();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void stopSpan(
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope,
                @Advice.Thrown Throwable throwable,
                @Advice.Origin Method method,
                @Advice.Local("eventStartTime") long eventStartTime) {
            if (context == null) {
                return;
            }
            long duration = System.currentTimeMillis() - eventStartTime;
            if (duration > Const.EVENT_TIME_THRESHOLD) {
                String name = SpanNames.fromMethod(method);
                AttributesBuilder put = Attributes.builder().put("name", name).put("time", duration+"ms");
                Span.fromContext(context).addEvent("trace_time", put.build());
            }
        }
    }
}
