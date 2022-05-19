package com.xiaomi.youpin.docean.aop;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.common.MutableObject;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.exception.DoceanException;

import java.lang.reflect.InvocationHandler;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 6/5/21
 */
public class Proxy implements IProxy{

    private Object obj;

    private InvocationHandler handler;

    public Proxy(Class clazz, LinkedHashMap<Class, EnhanceInterceptor> interceptor) {
        this.obj = ReflectUtils.getInstance(clazz);
        this.handler = (proxy, method, args) -> {
            List<EnhanceInterceptor> interceptors = Lists.newArrayList();
            for (Map.Entry<Class, EnhanceInterceptor> entry : interceptor.entrySet()) {
                if (method.getAnnotation(entry.getKey()) != null) {
                    interceptors.add(entry.getValue());
                }
            }
            if (interceptors.size() == 0) {
                return method.invoke(obj, args);
            }
            AopContext context = new AopContext();
            try {
                interceptors.stream().forEach(it -> {
                    if (it.needEnhance(method)) {
                        it.before(context, method, args);
                    }
                });
                Object result = method.invoke(obj,args);
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
    }

    @Override
    public <T> T getInstance() {
        return (T) java.lang.reflect.Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), obj.getClass().getInterfaces(), handler);
    }
}
