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
