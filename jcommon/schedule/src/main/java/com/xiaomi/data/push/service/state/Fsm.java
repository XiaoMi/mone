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

import com.xiaomi.data.push.common.PushService;
import com.xiaomi.data.push.service.state.impl.FollowerState;
import com.xiaomi.data.push.service.state.impl.GlobalState;

import javax.annotation.PostConstruct;

import com.xiaomi.data.push.service.state.impl.LeaderState;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class Fsm implements PushService {

    private BaseState preState;

    private BaseState currentState;

    @Autowired
    private GlobalState globalState;

    @Autowired
    private FollowerState followerState;

    @Autowired
    private LeaderState leaderState;

    @Value("${support.rpc}")
    private boolean supportRpc;
    private volatile boolean shutdown = false;

    public Fsm() {
    }

    @PostConstruct
    public void init() {
        this.setState(this.followerState);
    }

    public void schedule() {
        if (this.supportRpc) {
            if (null != this.currentState) {
                try {
                    if (!this.shutdown) {
                        this.execute();
                    }
                } catch (Exception var2) {
                    log.error("fsm error:" + var2.getMessage(), var2);
                }
            }

        }
    }

    public synchronized void setState(BaseState state) {
        this.currentState = state;
    }

    public synchronized void execute() {
        this.globalState.execute();
        this.currentState.execute();
    }

    public synchronized void changeState(BaseState state) {
        if (null != state) {
            log.info("change state:{} {}", this.currentState, state);
            this.currentState.exit();
            this.preState = this.currentState;
            state.enter();
            this.currentState = state;
            this.currentState.execute();
        } else {
            log.warn("state == null");
        }
    }

    public void changeState(String state) {
        log.info("change state:{}", state);
        if (state.equals("leader")) {
            changeState(this.leaderState);
        }

        if (state.equals("follower")) {
            changeState(this.followerState);
        }
    }

    public void shutdown() {
        if (this.supportRpc) {
            log.info("fsm shutdown begin");
            this.shutdown = true;
            log.info("fsm shutdown finish");
        }

    }
}
