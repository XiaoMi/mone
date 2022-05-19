package com.xiaomi.mone.buddy.agent.common;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/2 18:57
 */
public class RunnableWrapper implements Runnable{

    private final Runnable runnable;

    public RunnableWrapper(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        System.out.println("begin");
        this.runnable.run();
        System.out.println("end");
    }
}
