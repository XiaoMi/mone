package com.xiaomi.youpin.docean.interceptor;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 */
public class LookupInterceptor extends EnhanceInterceptor {


    @Override
    public Object after(AopContext context, Method method, Object res) {
        Class<?> returnType = method.getReturnType();
        return Ioc.ins().createBean(returnType);
    }
}
