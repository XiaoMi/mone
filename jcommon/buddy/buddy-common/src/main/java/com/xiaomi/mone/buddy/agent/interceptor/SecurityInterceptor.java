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

package com.xiaomi.mone.buddy.agent.interceptor;

import com.xiaomi.mone.demo.anno.Secured;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/26 09:49
 */
public class SecurityInterceptor {

    static String user = "root";

    @RuntimeType
    public static Object intercept(@This Object o, @Origin Method method,
                                   @SuperCall Callable<?> callable, @AllArguments Object[] arguments) throws Exception {
        System.out.println("security interceptor");
        System.out.println("Object:" + o.toString());
        System.out.println("security inperceptor args:" + Arrays.toString(arguments));
        if (null == method.getAnnotation(Secured.class)) {
            return callable.call();
        }
        if (!method.getAnnotation(Secured.class).user().equals(user)) {
            throw new IllegalStateException("Wrong user");
        }
        Object obj = callable.call();
        System.out.println("res:" + obj);
        return obj;
    }

}
