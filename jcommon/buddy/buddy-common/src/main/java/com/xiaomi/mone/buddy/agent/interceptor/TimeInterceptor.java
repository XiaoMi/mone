package com.xiaomi.mone.buddy.agent.interceptor;

import com.xiaomi.mone.buddy.agent.bo.Context;
import com.xiaomi.mone.buddy.agent.bo.Scope;
import com.xiaomi.mone.buddy.agent.bo.Span;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/26 10:05
 */
@Slf4j
public class TimeInterceptor {

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        Scope.start();
        Span span = Context.getContext().getSpan();
        long start = System.currentTimeMillis();
        Object res = null;
        try {
            res = callable.call();
            return res;
        } finally {
            log.info(method + ": took " + (System.currentTimeMillis() - start) + "ms" + " res:" + res + ":" + span.getTraceId());
            Scope.close();
        }
    }


}
