/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.server.service;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.model.vo.LogCmd;
import com.xiaomi.mone.log.api.service.PublishConfigService;
import com.xiaomi.mone.log.utils.NetUtil;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.GSON;
import static com.xiaomi.mone.log.common.Constant.SYMBOL_COLON;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/6 17:48
 */
@Slf4j
@Service(interfaceClass = PublishConfigService.class, group = "$dubbo.group", timeout = 14000)
public class DefaultPublishConfigService implements PublishConfigService {

    private static final AtomicInteger COUNT_INCR = new AtomicInteger(0);
    @Resource
    private RpcServer rpcServer;

    /**
     * dubbo interface, the timeout period cannot be too long
     *
     * @param agentIp
     * @param logCollectMeta
     */
    @Override
    public void sengConfigToAgent(String agentIp, LogCollectMeta logCollectMeta) {
        int count = 1;
        while (count < 4) {
            Map<String, AgentChannel> logAgentMap = getAgentChannelMap();
            String agentCurrentIp = queryCurrentDockerAgentIP(agentIp, logAgentMap);
            if (logAgentMap.containsKey(agentCurrentIp)) {
                String sendStr = GSON.toJson(logCollectMeta);
                if (CollectionUtils.isNotEmpty(logCollectMeta.getAppLogMetaList())) {
                    RemotingCommand req = RemotingCommand.createRequestCommand(LogCmd.logReq);
                    req.setBody(sendStr.getBytes());
                    log.info("Send the configuration,agent ip:{},Configuration information:{}", agentCurrentIp, sendStr);
                    Stopwatch started = Stopwatch.createStarted();
                    RemotingCommand res = rpcServer.sendMessage(logAgentMap.get(agentCurrentIp), req, 10000);
                    started.stop();
                    String response = new String(res.getBody());
                    log.info("The configuration is sent successfully---->{},duration：{}s,agentIp:{}", response, started.elapsed().getSeconds(), agentCurrentIp);
                    if (Objects.equals(response, "ok")) {
                        break;
                    }
                }
            } else {
                log.info("The current agent IP is not connected,ip:{},configuration data:{}", agentIp, GSON.toJson(logCollectMeta));
            }
            //Retry policy - Retry 4 times, sleep 500 ms each time
            try {
                TimeUnit.MILLISECONDS.sleep(500L);
            } catch (final InterruptedException ignored) {
            }
            count++;
        }
    }

    @Override
    public List<String> getAllAgentList() {
        List<String> remoteAddress = Lists.newArrayList();
        List<String> ipAddress = Lists.newArrayList();
        AgentContext.ins().map.entrySet().forEach(agentChannelEntry -> {
                    String key = agentChannelEntry.getKey();
                    remoteAddress.add(key);
                    ipAddress.add(StringUtils.substringBefore(key, SYMBOL_COLON));
                }
        );
        if (COUNT_INCR.getAndIncrement() % 200 == 0) {
            log.info("The set of remote addresses of the connected agent machine is:{}", GSON.toJson(remoteAddress));
        }
        return remoteAddress;
    }

    private Map<String, AgentChannel> getAgentChannelMap() {
        Map<String, AgentChannel> logAgentMap = new HashMap<>();
        AgentContext.ins().map.forEach((k, v) -> logAgentMap.put(StringUtils.substringBefore(k, SYMBOL_COLON), v));
        return logAgentMap;
    }

    private String queryCurrentDockerAgentIP(String agentIp, Map<String, AgentChannel> logAgentMap) {
        if (Objects.equals(agentIp, NetUtil.getLocalIp())) {
            //for Docker handles the agent on the current machine
            final String tempIp = agentIp;
            List<String> ipList = getAgentChannelMap().keySet()
                    .stream().filter(ip -> ip.startsWith("172"))
                    .collect(Collectors.toList());
            Optional<String> optionalS = ipList.stream()
                    .filter(ip -> Objects.equals(logAgentMap.get(ip).getIp(), tempIp))
                    .findFirst();
            if (optionalS.isPresent()) {
                String correctIp = optionalS.get();
                log.info("origin ip:{},set agent ip:{}", agentIp, correctIp);
                agentIp = correctIp;
            }
        }
        return agentIp;
    }
}
