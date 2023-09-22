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

package io.opentelemetry.javaagent.instrumentation.docean;

import com.xiaomi.youpin.docean.mvc.HttpRequestMethod;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;

import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class DoceanInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("com.xiaomi.youpin.docean.Mvc");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(ElementMatchers.named("callMethod").and(ElementMatchers.isPrivate()), DoceanInstrumentation.class.getName() + "$InvokeAdvice");
    }


    public static class InvokeAdvice {

        @SuppressWarnings({"SystemOut", "CatchAndPrintStackTrace"})
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void enter(@Advice.Origin Method method,
                                 @Advice.Origin Class clazz,
                                 @Advice.Local("otelContext") Context context,
                                 @Advice.Local("otelScope") Scope scope,
                                 @Advice.Local("request") DoceanRequest request,
                                 @Advice.Argument(4) HttpRequestMethod hrmethod
        ) {
            try {
                Context parentContext = currentContext();
                request = new DoceanRequest();
                request.setClazz(hrmethod.getObj().getClass().getSimpleName());
                request.setMethodName(hrmethod.getMethod().getName());
                context = DoceanSingletons.instrumenter().start(parentContext, request);
                scope = context.makeCurrent();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        @SuppressWarnings("SystemOut")
        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("otelScope") Scope scope,
                                @Advice.Local("request") DoceanRequest request) {
            if (scope == null) {
                System.out.println("docean scope null");
                return;
            }
            Span span = Span.fromContext(context);
            Attributes attributes = Attributes.builder().put("method", request.getMethodName()).build();
            span.addEvent("docean_call_event", attributes);
            scope.close();
            DoceanSingletons.instrumenter().end(context, request, null, throwable);
        }
    }
}
