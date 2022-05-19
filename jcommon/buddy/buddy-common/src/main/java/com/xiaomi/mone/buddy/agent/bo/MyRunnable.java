package com.xiaomi.mone.buddy.agent.bo;


/**
 * @Author goodjava@qq.com
 * @Date 2021/8/1 11:51
 */
public class MyRunnable implements Runnable {

    private Runnable runnable;

    private Span span;

    public MyRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.span = Context.getContext().getSpan();
    }

    @Override
    public void run() {
        System.out.println("begin:" + this.span);
        try {
            Context.getContext().setSpan(this.span);
            runnable.run();
        } finally {
            System.out.println("finish:" + this.span);
        }
    }

}
