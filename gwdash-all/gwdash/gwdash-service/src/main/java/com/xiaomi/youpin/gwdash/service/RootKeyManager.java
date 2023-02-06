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

package com.xiaomi.youpin.gwdash.service;

import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.agent.AgentManager;
import com.xiaomi.youpin.tesla.agent.po.ShellReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangjunyi
 * created on 2020/4/21 4:33 下午
 */
@Service
@Slf4j
public class RootKeyManager {

    private static final String clear = "__clear__";

    @Autowired
    private AgentManager agentManager;

    ConcurrentHashMap<String, Boolean> hashMap = new ConcurrentHashMap();

    public boolean plant(String ip) {
        return hashMap.putIfAbsent(ip,true) == null;
    }

    public void remove(String ip) {
        hashMap.remove(ip);

    }

    @PreDestroy
    public void shutdown() {
        log.info("root key manager shutdown");
        hashMap.entrySet().stream().forEach(it -> {
            ShellReq req = new ShellReq();
            req.setShellCmd(clear);
            req.setPath("/root/");
            agentManager.send(it.getKey(), 5000, new Gson().toJson(req), 5000);
        });
    }
}