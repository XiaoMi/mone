package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.plugin.aop.anno.AopConfig;

import java.lang.reflect.Method;


@AopConfig(clazz = TCAnno.class)
public class TCInterceptor extends EnhanceInterceptor {

    @Override
    public void before(AopContext context, Method method, Object[] args) {
        System.out.println("---> c before");
    }

    @Override
    public Object after(AopContext context, Method method, Object res) {
        System.out.println("---> c res:" + res);
        return res;
    }


}
