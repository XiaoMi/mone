package com.xiaomi.mone.buddy.agent.common;

import java.util.concurrent.Callable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/3 10:35
 */
public class CallableWrapper<V> implements Callable<V> {

    private final Callable<V> callable;

    public CallableWrapper(Callable<V> callable) {
        this.callable = callable;
    }

    @Override
    public V call() throws Exception {
        System.out.println("callable begin");
        V res = callable.call();
        System.out.println("callable end");
        return res;
    }
}
