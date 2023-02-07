package com.xiaomi.data.push.service.state;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author goodjava@qq.com
 */
public abstract class BaseState implements ApplicationContextAware {

    protected static final int expireTime = 10;

    @Getter
    private ApplicationContext ac;

    public BaseState() {
    }

    public BaseState getState(String state) {
        return (BaseState) this.ac.getBean(state);
    }

    public abstract void execute();

    public void enter() {
    }

    public void exit() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    public String leaderKey(String appName) {
        return appName + "push_server_leader";
    }

    protected String leaderName(String name) {
        return "leader_" + name;
    }
}
