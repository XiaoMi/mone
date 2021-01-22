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
import com.xiaomi.data.push.service.ElectionType;
import com.xiaomi.data.push.service.state.BaseState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author goodjava@qq.com
 */
@Component
public class FollowerState extends BaseState {

    private static final Logger logger = LoggerFactory.getLogger(FollowerState.class);

    @Autowired
    private ServerContext serverContext;

    public FollowerState() {
    }

    @Override
    public void execute() {
        try {
            String ip = Optional.ofNullable(System.getenv("host.ip")).orElse(Utils.getIp());
            logger.info("--->state FollowerState {}", ip);

            //单机模式
            if (serverContext.getType().equals("standalone")) {
                serverContext.changeToLeader();
                return;
            }
            // redis 选举模式
            if (serverContext.getElectionType().equals(ElectionType.redis.name())) {
                Redis redis = this.getAc().getBean(Redis.class);
                String leaderIp = redis.get(leaderName(serverContext.getServerName()));
                if (StringUtils.isEmpty(leaderIp)) {
                    //存10秒
                    String v = redis.set(leaderName(serverContext.getServerName()), ip, 10000);
                    //OK  第一个写入成功的
                    if (Optional.ofNullable(v).isPresent()) {
                        serverContext.changeToLeader();
                    } else {
                        //没有选举上
                        leaderIp = redis.get(leaderName(serverContext.getServerName()));
                        serverContext.getServerType().set(ServerType.Follower);
                        serverContext.getLeaderIp().set(leaderIp);
                    }
                } else {
                    serverContext.getServerType().set(ServerType.Follower);
                    serverContext.getLeaderIp().set(leaderIp);
                }
            } else {
                serverContext.getServerType().set(ServerType.Follower);
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        }
    }
}
