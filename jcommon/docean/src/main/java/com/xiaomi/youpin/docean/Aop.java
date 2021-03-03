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

package com.xiaomi.youpin.docean;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.anno.Lookup;
import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.common.MutableObject;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.exception.DoceanException;
import com.xiaomi.youpin.docean.interceptor.LookupInterceptor;
import lombok.Getter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.*;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
public class Aop {

    @Getter
    private LinkedHashMap<Class, EnhanceInterceptor> interceptorMap = new LinkedHashMap<>();

    private Aop() {
    }

    public void init(LinkedHashMap<Class, EnhanceInterceptor> map) {
        this.interceptorMap.put(Lookup.class,new LookupInterceptor());
        this.interceptorMap.putAll(map);
    }

    private boolean needEnhance(Class clazz, Class annotation) {
        return Arrays.stream(clazz.getMethods()).filter(it -> Optional.ofNullable(it.getAnnotation(annotation)).isPresent()).findAny().isPresent();
    }

    public <T> T enhance(Class clazz) {
        Object obj = null;
        LinkedHashMap<Class, EnhanceInterceptor> interceptors = Maps.newLinkedHashMap();
        for (Map.Entry<Class, EnhanceInterceptor> entry : this.interceptorMap.entrySet()) {
            if (needEnhance(clazz, entry.getKey())) {
                interceptors.put(entry.getKey(), entry.getValue());
            }
        }
        if (interceptors.size() > 0) {
            return enhance(clazz, interceptors);
        }
        return (T) Optional.ofNullable(obj).orElse(ReflectUtils.getInstance(clazz));
    }


    public <T> T enhance(Class clazz, LinkedHashMap<Class, EnhanceInterceptor> interceptor) {
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


    public static final class LazyHolder {
        private final static Aop ins = new Aop();
    }

    public static Aop ins() {
        return LazyHolder.ins;
    }
}
