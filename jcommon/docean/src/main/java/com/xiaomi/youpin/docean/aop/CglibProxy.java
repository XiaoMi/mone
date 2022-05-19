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
