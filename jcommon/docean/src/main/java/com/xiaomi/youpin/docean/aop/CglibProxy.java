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

package com.xiaomi.youpin.docean.aop;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.common.MutableObject;
import com.xiaomi.youpin.docean.exception.DoceanException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 6/5/21
 */
public class CglibProxy implements IProxy {

    private Class clazz;

    private LinkedHashMap<Class, EnhanceInterceptor> interceptor;

    public CglibProxy(Class clazz, LinkedHashMap<Class, EnhanceInterceptor> interceptor) {
        this.clazz = clazz;
        this.interceptor = interceptor;
    }

    @Override
    public <T> T getInstance() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        MethodInterceptor callback = (obj, method, args, proxy) -> {
            List<EnhanceInterceptor> interceptors = Lists.newArrayList();
            for (Map.Entry<Class, EnhanceInterceptor> entry : interceptor.entrySet()) {
                if (method.getAnnotation(entry.getKey()) != null) {
                    interceptors.add(entry.getValue());
                }
            }

            if (interceptors.size() == 0) {
                return proxy.invokeSuper(obj, args);
            }
            AopContext context = new AopContext();
            try {
                interceptors.stream().forEach(it -> {
                    if (it.needEnhance(method)) {
                        it.before(context, method, args);
                    }
                });
                Object result = proxy.invokeSuper(obj, args);
                MutableObject o = new MutableObject(result);
                interceptors.stream().forEach(it -> {
                    if (it.needEnhance(method)) {
                        Object r = it.after(context, method, o.getObj());
                        o.setObj(r);
                    }
                });
                return o.getObj();
            } catch (Throwable ex) {
                interceptors.stream().forEach(it -> {
                    if (it.needEnhance(method)) {
                        it.exception(context, method, ex);
                    }
                });
                throw new DoceanException(ex);
            }
        };
        enhancer.setCallback(callback);
        return (T) enhancer.create();
    }

}
