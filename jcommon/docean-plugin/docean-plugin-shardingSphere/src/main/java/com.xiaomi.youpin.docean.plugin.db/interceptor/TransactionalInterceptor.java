package com.xiaomi.youpin.docean.plugin.db.interceptor;

import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.exception.DoceanException;
import lombok.SneakyThrows;
import org.nutz.trans.NutTransaction;
import org.nutz.trans.Trans;
import org.nutz.trans.Transaction;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
public class TransactionalInterceptor extends EnhanceInterceptor {

    @Override
    public void before(AopContext context, Method method, Object[] args) {
        NutTransaction transaction = new NutTransaction();
        transaction.setLevel(2);
        Trans.set(transaction);
        Trans.setCount(0);
        try {
            Trans.begin();
        } catch (Exception e) {
            throw new DoceanException(e);
        }
    }


    @SneakyThrows
    @Override
    public Object after(AopContext context, Method method, Object res) {
        try {
            Object r = super.after(context, method, res);
            Trans.commit();
            return r;
        } finally {
//            Trans.close();
        }
    }


    @SneakyThrows
    @Override
    public void exception(AopContext context, Method method, Throwable ex) {
        try {
            Trans.rollback();
        } finally {
//            Trans.close();
        }
    }

}
