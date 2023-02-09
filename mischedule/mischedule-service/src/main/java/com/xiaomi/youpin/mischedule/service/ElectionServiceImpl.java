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

package com.xiaomi.youpin.mischedule.service;

import com.alibaba.nacos.client.naming.utils.NetUtils;
import com.xiaomi.data.push.context.ServerContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 底层raft协议 或者redis选举
 *
 * @author goodjava@qq.com
 */
@Service
@Slf4j
public class ElectionServiceImpl {

    @Getter
    private volatile ElectionNode node;

    @Value("${raft.data.path}")
    private String dataPath;

    @Value("${raft.group.id}")
    private String groupId;

    @Value("${raft.serveridstr}")
    private String serverIdStr;

    @Value("${raft.initialconfstr}")
    private String initialConfStr;

    @Value("${raft.server.ip}")
    private String serverIp;

    @Value("${election.type}")
    private String electionType;


    @Autowired
    private ServerContext serverContext;


    private String getHost() {
        if (StringUtils.isNotEmpty(serverIp)) {
            return serverIp;
        }
        String ip = NetUtils.localIP();
        log.info("local ip={} {}", ip, serverIdStr);
        return ip;
    }


    @PostConstruct
    public void init() {
        Executors.newSingleThreadExecutor().submit(() -> {
            if (!electionType.equals("redis")) {
                while (true) {
                    boolean exit = false;
                    try {
                        final ElectionNodeOptions electionOpts = new ElectionNodeOptions();
                        electionOpts.setDataPath(dataPath);
                        electionOpts.setGroupId(groupId);
                        electionOpts.setServerAddress(getHost() + ":" + serverIdStr);
                        electionOpts.setInitialServerAddressList(initialConfStr);

                        log.info("init {} {} {} {}", dataPath, groupId, electionOpts.getServerAddress(), initialConfStr);

                        node = new ElectionNode();
                        LeaderStateListener listener = new LeaderStateListener() {
                            @Override
                            public void onLeaderStart(long leaderTerm) {
                                log.info("Leader start on term:{}", leaderTerm);
                                serverContext.changeToLeader();
                            }

                            @Override
                            public void onLeaderStop(long leaderTerm) {
                                log.info("Leader stop on term:{}", leaderTerm);
                                serverContext.changeToFollower();
                            }
                        };
                        node.setLeaderStateListener(listener);
                        node.init(electionOpts);
                        exit = true;
                    } catch (Throwable ex) {
                        node = null;
                        log.error("ElectionServiceImpl init error:" + ex.getMessage(), ex);
                        try {
                            TimeUnit.SECONDS.sleep(2 + new Random().nextInt(3));
                        } catch (InterruptedException e) {
                            log.error(e.getMessage());
                        }
                    }
                    if (exit) {
                        log.info("ElectionServiceImpl init finish");
                        break;
                    }
                }
            }

        });
    }
}
