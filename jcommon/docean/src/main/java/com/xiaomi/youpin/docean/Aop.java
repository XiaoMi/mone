package com.xiaomi.youpin.docean;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.anno.Lookup;
import com.xiaomi.youpin.docean.aop.*;
import com.xiaomi.youpin.docean.aop.anno.After;
import com.xiaomi.youpin.docean.aop.anno.Aspect;
import com.xiaomi.youpin.docean.aop.anno.Before;
import com.xiaomi.youpin.docean.common.DoceanConfig;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.interceptor.LookupInterceptor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
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

    public Aop init(LinkedHashMap<Class, EnhanceInterceptor> map) {
        this.interceptorMap.put(Lookup.class, new LookupInterceptor());
        this.interceptorMap.putAll(map);
        this.cglib = DoceanConfig.ins().get("cglib", "true").equals("true");
        log.info("aop use cglib:{}", this.cglib);
        return this;
    }

    /**
     * 支持根据注解Aspect来实现aop
     *
     * @param packages
     * @return
     */
    public Aop useAspect(Ioc ioc, String... packages) {
        Ioc.create(Thread.currentThread().getContextClassLoader()).cleanAnnos().setAnnos(Aspect.class).init(packages).getBeansWithAnnotation(Aspect.class).values().forEach(it -> {
            Arrays.stream(it.getClass().getDeclaredMethods()).forEach(m -> {

                Optional.ofNullable(m.getAnnotation(Before.class)).ifPresent(a -> {
                    EnhanceInterceptor interceptor = new EnhanceInterceptor() {
                        @SneakyThrows
                        @Override
                        public void before(AopContext aopContext, Method method, Object[] args) {
                            ProceedingJoinPoint point = new ProceedingJoinPoint();
                            point.setArgs(args);
                            m.invoke(it, point);
                        }
                    };
                    interceptorMap.put(a.anno(), interceptor);
                });

                Optional.ofNullable(m.getAnnotation(After.class)).ifPresent(a -> {
                    EnhanceInterceptor interceptor = new EnhanceInterceptor() {
                        @SneakyThrows
                        @Override
                        public Object after(AopContext context, Method method, Object res) {
                            ProceedingJoinPoint point = new ProceedingJoinPoint();
                            point.setRes(res);
                            return m.invoke(it, point);
                        }
                    };
                    interceptorMap.put(a.anno(), interceptor);
                });
            });
            ioc.putBean(it);
        });
        return this;
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
