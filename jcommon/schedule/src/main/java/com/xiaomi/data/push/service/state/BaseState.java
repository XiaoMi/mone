/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
