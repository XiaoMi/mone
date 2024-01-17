/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.jedis.v1_4.piplinecluster;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.instrumentation.jedis.v1_4.JedisRequest;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Protocol;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge.currentContext;
import static io.opentelemetry.javaagent.instrumentation.jedis.v1_4.JedisSingletons.instrumenter;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

public class PipelineClusterInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("com.xiaomi.data.push.redis.Redis");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(named("mget"))
                        .and(takesArguments(1))
                        .and(takesArgument(0, named("java.util.List"))),
                this.getClass().getName() + "$SendMget");
    }

    @SuppressWarnings({"unused","SystemOut"})
    public static class SendMget {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void onEnter(
                @Advice.Local("redisHosts") String redisHosts,
                @Advice.Argument(0) List keys,
                @Advice.Local("otelJedisRequest") JedisRequest request,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            Context parentContext = currentContext();
            System.out.println("获取到的redisHosts："+redisHosts);
            Connection connection = new Connection(redisHosts);
            String[] keyArr = (String[]) keys.toArray(new String[keys.size()]);
            byte[][] bkeys = new byte[keyArr.length][];
            for (int i = 0; i < bkeys.length; ++i) {
                bkeys[i] = keyArr[i].getBytes(StandardCharsets.UTF_8);
            }
            request = JedisRequest.create(connection, Protocol.Command.MGET, keys);
            if (!instrumenter().shouldStart(parentContext, request)) {
                return;
            }

            context = instrumenter().start(parentContext, request);
            scope = context.makeCurrent();
            ContextHolder.set(true);
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
        public static void stopSpan(
                @Advice.Thrown Throwable throwable,
                @Advice.Local("otelJedisRequest") JedisRequest request,
                @Advice.Local("otelContext") Context context,
                @Advice.Local("otelScope") Scope scope) {
            if (scope == null) {
                return;
            }
            ContextHolder.clear();
            scope.close();
            instrumenter().end(context, request, null, throwable);
        }
    }
}
