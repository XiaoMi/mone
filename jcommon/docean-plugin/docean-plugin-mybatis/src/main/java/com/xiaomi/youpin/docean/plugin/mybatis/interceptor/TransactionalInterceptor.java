package com.xiaomi.youpin.docean.plugin.mybatis.interceptor;

import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.plugin.cat.CatInterceptor;
import com.xiaomi.youpin.docean.plugin.mybatis.MybatisTransaction;
import com.xiaomi.youpin.docean.plugin.mybatis.Transactional;
import com.xiaomi.youpin.docean.plugin.mybatis.TransactionalContext;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
public class TransactionalInterceptor extends CatInterceptor {

    @Override
    public void before(AopContext context, Method method, Object[] args) {
        super.before(context, method, args);
        TransactionalContext.getContext().set(new MybatisTransaction(null, false));
    }


    @SneakyThrows
    @Override
    public Object after(AopContext context, Method method, Object res) {
        MybatisTransaction transaction = TransactionalContext.getContext().get();
        try {
            Object r = super.after(context, method, res);
            transaction.commit();
            return r;
        } finally {
            transaction.close();
            TransactionalContext.getContext().close();
        }
    }


    @SneakyThrows
    @Override
    public void exception(AopContext context, Method method, Throwable ex) {
        super.exception(context, method, ex);
        MybatisTransaction transaction = TransactionalContext.getContext().get();
        try {
            transaction.rollback();
        } finally {
            transaction.close();
            TransactionalContext.getContext().close();
        }
    }

    @Override
    public boolean needEnhance(Method method) {
        Transactional tr = method.getAnnotation(Transactional.class);
        return tr.type().equals("mybatis");
    }
}
