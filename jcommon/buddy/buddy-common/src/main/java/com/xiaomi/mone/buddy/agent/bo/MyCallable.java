package com.xiaomi.mone.buddy.agent.bo;

import java.util.concurrent.Callable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/7 23:37
 */
public class MyCallable<V> implements Callable<V> {

    private final Callable<V> c;

    private Span span;

    public MyCallable(Callable<V> c) {
        this.c = c;
        this.span = Context.getContext().getSpan();
    }

    @Override
    public V call() throws Exception {
        Context.getContext().setSpan(this.span);
        return c.call();
    }
}
