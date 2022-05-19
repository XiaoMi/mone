package com.xiaomi.mone.buddy.agent.interceptor;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * @author goodjava@qq.com
 * @date 7/27/21
 */
public class JedisInterceptor {

    @RuntimeType
    public static Object intercept(@This Object o, @Origin Method method,
                                   @SuperCall Callable<?> callable, @AllArguments Object[] arguments) throws Exception {
        System.out.println("jedis---->" + method.getName() + ":" + Arrays.toString(arguments));
        Object obj = callable.call();
        System.out.println("jedis res:" + obj);
        return obj;
    }


}
