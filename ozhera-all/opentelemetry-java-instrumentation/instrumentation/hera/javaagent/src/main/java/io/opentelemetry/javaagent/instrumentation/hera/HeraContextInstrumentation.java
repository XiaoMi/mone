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

import io.opentelemetry.api.trace.HeraContext;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;
import static net.bytebuddy.matcher.ElementMatchers.named;

@SuppressWarnings("CatchAndPrintStackTrace")
public class
HeraContextInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("com.xiaomi.hera.trace.context.HeraContextUtil");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(ElementMatchers.named("getHeraContext")
                        .and(ElementMatchers.isPublic())
                , HeraContextInstrumentation.class.getName() + "$GetContextInvokeAdvice");

        transformer.applyAdviceToMethod(ElementMatchers.named("get")
                        .and(ElementMatchers.isPublic())
                , HeraContextInstrumentation.class.getName() + "$GetInvokeAdvice");

        transformer.applyAdviceToMethod(ElementMatchers.named("set")
                        .and(ElementMatchers.isPublic())
                , HeraContextInstrumentation.class.getName() + "$SetInvokeAdvice");
    }


    public static class GetContextInvokeAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void methodEnter(
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
        }

        @SuppressWarnings("SystemOut")
        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("otelScope") Scope scope,
                                @Advice.Return(readOnly = false) Map<String, String> result) {
            if (context == null) {
                context = currentContext();
                if (context == null) {
                    System.out.println("java8 context is null");
                    return;
                }
            }
            SpanContext spanContext = Span.fromContext(context).getSpanContext();
            result = spanContext.getHeraContext();
            return;
        }

    }

    public static class GetInvokeAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void methodEnter(
                @Advice.Argument(0) String key,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
        }

        @SuppressWarnings("SystemOut")
        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Argument(0) String key,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("otelScope") Scope scope,
                                @Advice.Return(readOnly = false) String result) {
            if (key == null || key.isEmpty()) {
                return;
            }
            if (context == null) {
                context = currentContext();
                if (context == null) {
                    System.out.println("java8 context is null");
                    return;
                }
            }
            SpanContext spanContext = Span.fromContext(context).getSpanContext();
            Map<String, String> heraContextMap = spanContext.getHeraContext();
            if (heraContextMap != null && heraContextMap.size() > 0) {
                String heraContext = heraContextMap.get(HeraContext.HERA_CONTEXT_PROPAGATOR_KEY);
                if (heraContext != null && !heraContext.isEmpty()) {
                    String[] split = heraContext.split(HeraContext.ENTRY_SPLIT);
                    for (String entry : split) {
                        String[] kv = entry.split(HeraContext.KEY_VALUE_SPLIT);
                        if (key.equals(kv[0])) {
                            result = kv[1];
                        }
                    }
                }
            }
            if (result != null) {
                try {
                    result = URLDecoder.decode(result, "UTF-8");
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return;
        }
    }

    public static class SetInvokeAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void methodEnter(
                @Advice.Argument(0) String key,
                @Advice.Argument(1) String value,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
        }

        @SuppressWarnings("SystemOut")
        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void exit(@Advice.Thrown Throwable throwable,
                                @Advice.Argument(0) String key,
                                @Advice.Argument(1) String value,
                                @Advice.Local("otelContext") Context context,
                                @Advice.Local("otelScope") Scope scope,
                                @Advice.Return(readOnly = false) boolean result) {
            if (key == null || key.isEmpty()) {
                return;
            }
            if (context == null) {
                context = currentContext();
                if (context == null) {
                    System.out.println("java8 context is null");
                    return;
                }
            }
            String encodeStr = value;
            if (encodeStr != null) {
                try {
                    encodeStr = URLEncoder.encode(encodeStr, "UTF-8");
                } catch (Throwable t) {
                    t.printStackTrace();
                    return;
                }
            }
            StringBuilder sb = new StringBuilder(key).append(HeraContext.KEY_VALUE_SPLIT).append(encodeStr);
            try {
                SpanContext spanContext = Span.fromContext(context).getSpanContext();
                Map<String, String> heraContextMap = spanContext.getHeraContext();
                String heraContext = heraContextMap.get(HeraContext.HERA_CONTEXT_PROPAGATOR_KEY);
                if (heraContext == null || heraContext.isEmpty()) {
                    heraContextMap.put(HeraContext.HERA_CONTEXT_PROPAGATOR_KEY, sb.toString());
                    result = true;
                } else {
                    String[] entrys = heraContext.split(HeraContext.ENTRY_SPLIT);
                    if (entrys.length >= HeraContext.LIMIT_LENGTH) {
                        System.out.println("set HeraContext size break bounds");
                        result = false;
                    } else {
                        // the key duplicate validation
                        if (heraContext.contains(key)) {
                            StringBuilder newSb = new StringBuilder();
                            for (String entry : entrys) {
                                String[] keyValue = entry.split(HeraContext.KEY_VALUE_SPLIT);
                                if (keyValue[1].equals(key)) {
                                    newSb.append(HeraContext.ENTRY_SPLIT).append(key).append(HeraContext.KEY_VALUE_SPLIT).append(encodeStr);
                                } else {
                                    if (newSb.length() == 0) {
                                        newSb.append(entry);
                                    } else {
                                        newSb.append(HeraContext.ENTRY_SPLIT).append(entry);
                                    }
                                }
                            }
                            heraContextMap.put(HeraContext.HERA_CONTEXT_PROPAGATOR_KEY, newSb.toString());
                        } else {
                            heraContextMap.put(HeraContext.HERA_CONTEXT_PROPAGATOR_KEY, new StringBuilder(heraContext).append(HeraContext.ENTRY_SPLIT).append(sb).toString());
                        }
                        result = true;
                    }
                }
            } catch (Throwable t) {
                System.out.println("set HeraContext error : " + t.getMessage());
            }
            return;
        }

    }
}
