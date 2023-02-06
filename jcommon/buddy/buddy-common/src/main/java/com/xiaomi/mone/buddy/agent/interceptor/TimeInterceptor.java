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
