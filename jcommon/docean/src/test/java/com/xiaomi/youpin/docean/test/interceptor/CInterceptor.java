package com.xiaomi.youpin.docean.test.interceptor;

import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;

import java.lang.reflect.Method;

public class CInterceptor extends EnhanceInterceptor {


    @Override
    public void before(AopContext context, Method method, Object[] args) {
        System.out.println("before");
    }

    @Override
    public Object after(AopContext context,Method method, Object res) {
        System.out.println("res:" + res);
        return res;
    }

}
