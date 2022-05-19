package com.xiaomi.youpin.docean.plugin.sql.interceptor;

import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.plugin.datasource.anno.Transactional;
import com.xiaomi.youpin.docean.plugin.sql.JdbcTransaction;
import com.xiaomi.youpin.docean.plugin.sql.TransactionalContext;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
public class TransactionalInterceptor extends EnhanceInterceptor {

    @Override
    public void before(AopContext context, Method method, Object[] args) {
        TransactionalContext.getContext().set(new JdbcTransaction(null, false));
    }


    @SneakyThrows
    @Override
    public Object after(AopContext context, Method method, Object res) {
        JdbcTransaction transaction = TransactionalContext.getContext().get();
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
        JdbcTransaction transaction = TransactionalContext.getContext().get();
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
        return tr.type().equals("");
    }
}
