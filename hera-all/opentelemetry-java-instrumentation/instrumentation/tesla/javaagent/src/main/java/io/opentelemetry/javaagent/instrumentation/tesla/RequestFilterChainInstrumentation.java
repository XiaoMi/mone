package io.opentelemetry.javaagent.instrumentation.tesla;/*
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

import com.xiaomi.youpin.gateway.filter.RequestContext;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;

public class RequestFilterChainInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return ElementMatchers.named("com.xiaomi.youpin.gateway.netty.filter.RequestFilterChain");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                ElementMatchers.named("doFilter")
                        .and(ElementMatchers.isPublic()),
                this.getClass().getName() + "$InvokeAdvice");
    }


    public static class InvokeAdvice {

        @SuppressWarnings({"SystemOut","CatchAndPrintStackTrace"})
        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void enter(@Advice.Local("otelContext") Context context,
                                 @Advice.Local("otelScope") Scope scope,
                                 @Advice.Argument(0) ApiInfo apiInfo,
                                 @Advice.Argument(2) RequestContext reuqestContext
        ) {
            try {
                Context parentContext = currentContext();
                TeslaRequest teslaRequest = new TeslaRequest();
                if (apiInfo == null) {
                    teslaRequest.setApiInfoIsNull(true);
                    String ctxUri = reuqestContext.getUri();
                    if (ctxUri == null || ctxUri.isEmpty() || !ctxUri.startsWith(TeslaTraceHelper.URI_PREFIX)) {
                        teslaRequest.setUri(TeslaTraceHelper.UNKNOW_URI_SPANNAME);
                    } else {
                        teslaRequest.setUri(ctxUri);
                    }
                } else {
                    teslaRequest.setApiInfoIsNull(false);
                    teslaRequest.setUri(apiInfo.getUrl());
                }
                context = TeslaSingletons.instrumenter().start(parentContext, teslaRequest);
                scope = context.makeCurrent();
            }catch(Throwable t){
                t.printStackTrace();
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
            TeslaSingletons.instrumenter().end(context,null,null,throwable);
            return;
        }
    }
}
