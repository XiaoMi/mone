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
