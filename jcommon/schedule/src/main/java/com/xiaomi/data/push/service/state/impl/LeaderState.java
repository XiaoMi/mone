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

package com.xiaomi.data.push.service.state.impl;

import com.xiaomi.data.push.bo.ServerType;
import com.xiaomi.data.push.common.Utils;
import com.xiaomi.data.push.context.ServerContext;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.service.ElectionType;
import com.xiaomi.data.push.service.state.BaseState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author zhangzhiyong
 * @date 12/06/2018
 */
@Component
@Slf4j
public class LeaderState extends BaseState {

    @Autowired
    private RpcServer rpcServer;


    @Autowired
    private ServerContext serverContext;


    @Override
    public void enter() {
        //注册到nacos
        rpcServer.registerInstance();
    }


    @Override
    public void exit() {
        //从nacos上取消注册
        rpcServer.deregisterInstance();
    }

    @Override
    public void execute() {
        String ip = Optional.ofNullable(System.getenv("host.ip")).orElse(Utils.getIp());
        log.info("--->state LeaderState ip:{}", ip);
        try {

            //单机模式
            if (serverContext.getType().equals("standalone")) {
                serverContext.getServerType().set(ServerType.Leader);
                return;
            }

            if (serverContext.getElectionType().equals(ElectionType.redis.name())) {
                Redis redis = this.getAc().getBean(Redis.class);
                String rip = redis.get(leaderName(serverContext.getServerName()));

                //状态已经有问题了,退为选举模式
                if (!Optional.ofNullable(rip).isPresent()) {
                    serverContext.getLeaderIp().set("");
                    serverContext.changeToFollower();
                    return;
                }
                //自己还是leader
                if (rip.equals(ip)) {
                    String v = redis.set(leaderName(serverContext.getServerName()), ip, "XX", 10000);
                    if (!Optional.ofNullable(v).isPresent()) {
                        serverContext.changeToFollower();
                    }
                } else {
                    serverContext.changeToFollower();
                }
                serverContext.getLeaderIp().set(rip);
                serverContext.getServerType().set(ServerType.Leader);
            } else {
                serverContext.getServerType().set(ServerType.Leader);
            }
        } catch (Throwable ex) {
            log.warn(ex.getMessage());
        }
    }
}
