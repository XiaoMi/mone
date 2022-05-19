package com.xiaomi.youpin.docean.plugin.cat;

import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;

import java.lang.reflect.Method;

/**
 * @author zheng.xucn@outlook.com
 */
public class CatInterceptor extends EnhanceInterceptor {

    private static final String CAT_TYPE = "METHODS";

    @Override
    public void before(AopContext aopContext, Method method, Object[] args) {
    }

    @Override
    public Object after(AopContext context, Method method, Object res) {
        return res;
    }

    @Override
    public void exception(AopContext context, Method method, Throwable ex) {
    }

}
