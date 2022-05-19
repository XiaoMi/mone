package com.xiaomi.youpin.docean.plugin.dmesh.state.client;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 12:00
 */
public abstract class BaseState {

    public void enter() {

    }

    public void exit() {

    }


    public abstract void execute();


    public long delay() {
        return 3000L;
    }


}
