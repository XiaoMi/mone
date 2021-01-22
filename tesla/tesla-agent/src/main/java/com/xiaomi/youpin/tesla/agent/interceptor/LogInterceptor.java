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

package com.xiaomi.youpin.tesla.agent.interceptor;

import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Slf4j
public class LogInterceptor extends EnhanceInterceptor {

    private long beginTime;

    @Override
    public void before(AopContext context, Method method, Object[] args) {
        this.beginTime = System.currentTimeMillis();
        log.info("{} {} {} begin", method.getDeclaringClass().getName(), method.getName(), Arrays.toString(args));
    }

    @Override
    public Object after(AopContext context, Method method, Object res) {
        log.info("{} {} finish use time:{}", method.getDeclaringClass().getName(), method.getName(), (System.currentTimeMillis() - beginTime));
        return res;
    }
}
