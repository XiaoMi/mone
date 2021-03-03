package com.xiaomi.youpin.docean.plugin.dmesh.test;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class PluginTest {

    interface  A {
        public String t();


        String sum(Integer a,Integer b);
    }


    @Test
    public void testEnhancer() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(A.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                String mn = method.getName();
                if ("getClass".equals(mn)) {
                    return methodProxy.invokeSuper(o,objects);
                }
                if ("hashCode".equals(mn)) {
                    return methodProxy.invokeSuper(o,objects);
                }
                if ("toString".equals(mn)) {
                    return methodProxy.invokeSuper(o,objects);
                }
                if ("equals".equals(mn)) {
                    if (objects.length == 1) {
                        return methodProxy.invokeSuper(o,objects);
                    }
                    throw new IllegalArgumentException("Invoke method [" + mn + "] argument number error.");
                }

                Class<?>[] types = method.getParameterTypes();
                log.info("types:{} params:{}", Arrays.toString(types),Arrays.toString(objects));

                return "abc";
            }
        });

        A a = (A) enhancer.create();
        System.out.println(a.t());
        System.out.println(a.toString());
        System.out.println(a.getClass());

        System.out.println(a.sum(11,22));
    }
}
