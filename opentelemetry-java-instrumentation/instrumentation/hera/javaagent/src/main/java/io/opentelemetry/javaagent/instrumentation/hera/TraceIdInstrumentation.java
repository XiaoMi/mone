/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.opentelemetry.javaagent.instrumentation.hera;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class TraceIdInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("com.xiaomi.hera.trace.context.TraceIdUtil");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                ElementMatchers.named("traceId")
                        .and(ElementMatchers.isPublic()),
                this.getClass().getName() + "$GetTraceIdAdvice");
        transformer.applyAdviceToMethod(
                ElementMatchers.named("spanId")
                        .and(ElementMatchers.isPublic()),
                this.getClass().getName() + "$GetSpanIdAdvice");
    }


    public static class GetTraceIdAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void enter(@Advice.Local("otelContext") Context context,
                                 @Advice.Local("otelScope") Scope scope
        ) {
        }

        @SuppressWarnings({"SystemOut", "CatchAndPrintStackTrace"})
        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("otelScope") Scope scope,
                                @Advice.Return(readOnly = false) String traceId) {
            try {
                Context parentContext = currentContext();
                traceId = Span.fromContext(parentContext).getSpanContext().getTraceId();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class GetSpanIdAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void enter(@Advice.Local("otelContext") Context context,
                                 @Advice.Local("otelScope") Scope scope
        ) {
        }

        @SuppressWarnings({"SystemOut", "CatchAndPrintStackTrace"})
        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("otelScope") Scope scope,
                                @Advice.Return(readOnly = false) String spanId) {
            try {
                Context parentContext = currentContext();
                spanId = Span.fromContext(parentContext).getSpanContext().getSpanId();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }
}
