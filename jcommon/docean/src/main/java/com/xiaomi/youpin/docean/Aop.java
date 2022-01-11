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

import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.anno.Lookup;
import com.xiaomi.youpin.docean.aop.CglibProxy;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.aop.IProxy;
import com.xiaomi.youpin.docean.aop.Proxy;
import com.xiaomi.youpin.docean.common.DoceanConfig;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.interceptor.LookupInterceptor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Slf4j
public class Aop {

    @Getter
    private LinkedHashMap<Class, EnhanceInterceptor> interceptorMap = new LinkedHashMap<>();

    /**
     * native image 模式下是不能使用cglib的
     */
    private boolean cglib = true;

    private Aop() {
    }

    public void init(LinkedHashMap<Class, EnhanceInterceptor> map) {
        this.interceptorMap.put(Lookup.class, new LookupInterceptor());
        this.interceptorMap.putAll(map);
        this.cglib = DoceanConfig.ins().get("cglib", "true").equals("true");
        log.info("aop use cglib:{}", this.cglib);
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
        IProxy proxy = cglib ? new CglibProxy(clazz, interceptor) : new Proxy(clazz, interceptor);
        return proxy.getInstance();
    }


    public static final class LazyHolder {
        private final static Aop ins = new Aop();
    }

    public static Aop ins() {
        return LazyHolder.ins;
    }
}
