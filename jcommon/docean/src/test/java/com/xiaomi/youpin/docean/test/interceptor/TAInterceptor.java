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

package com.xiaomi.youpin.docean.test.interceptor;

import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;

import java.lang.reflect.Method;

public class TAInterceptor extends EnhanceInterceptor {


    @Override
    public void before(AopContext context, Method method, Object[] args) {
        context.put("a", "gogogo");
        System.out.println("before");
    }

    @Override
    public Object after(AopContext context, Method method, Object res) {
        System.out.println("res:" + res + ":" + context.get("a"));
        return res;
    }

}
