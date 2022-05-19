package com.xiaomi.youpin.docean.plugin.mybatisplus;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
public class TransactionalContext {

    private static ThreadLocal<TransactionalContext> context = new ThreadLocal<TransactionalContext>() {
        @Override
        protected TransactionalContext initialValue() {
            return new TransactionalContext();
        }
    };

    private MybatisTransaction jdbcTransaction;


    public void set(MybatisTransaction transaction) {
        this.jdbcTransaction = transaction;
    }

    public MybatisTransaction get() {
        return this.jdbcTransaction;
    }


    public static TransactionalContext getContext() {
        return context.get();
    }

    public void close() {
        context.remove();
    }

}
