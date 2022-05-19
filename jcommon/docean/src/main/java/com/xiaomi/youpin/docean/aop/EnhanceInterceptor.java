package com.xiaomi.youpin.docean.aop;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
public class EnhanceInterceptor {

    public void before(AopContext aopContext, Method method, Object[] args) {
    }

    public Object after(AopContext context, Method method, Object res) {
        return res;
    }

    public void exception(AopContext context, Method method, Throwable ex) {
    }

    public boolean needEnhance(Method method) {
        return true;
    }


}
