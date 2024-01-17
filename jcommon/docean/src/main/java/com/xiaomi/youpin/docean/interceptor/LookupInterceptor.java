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

package com.xiaomi.youpin.docean.interceptor;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Lookup;
import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.common.StringUtils;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 */
public class LookupInterceptor extends EnhanceInterceptor {


    @Override
    public Object after(AopContext context, Method method, Object res) {
        Class<?> returnType = method.getReturnType();
        Lookup lookUp = method.getAnnotation(Lookup.class);
        String lookUpvalue = lookUp.value();
        if (StringUtils.isNotEmpty(lookUpvalue)) {
            if (lookUpvalue.startsWith("$")) {
                String value = Ioc.ins().getBean(lookUpvalue);
                lookUpvalue = value;
            }
            returnType = ReflectUtils.classForName(lookUpvalue);
        }
        return Ioc.ins().createBean(returnType);
    }
}
