package com.xiaomi.mone.buddy.agent.interceptor;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import okhttp3.Response;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author goodjava@qq.com
 * @date 7/27/21
 */
public class OkHttpInterceptor {

    @RuntimeType
    public static Object intercept(@This Object object, @Origin Method method, @SuperCall Callable<?> callable, @AllArguments Object[] arguments) throws Exception {
        long begin = System.currentTimeMillis();
        Response res = (Response) callable.call();
        System.out.println("okhttp call use time:" + (System.currentTimeMillis() - begin));
        return res;
    }


}
