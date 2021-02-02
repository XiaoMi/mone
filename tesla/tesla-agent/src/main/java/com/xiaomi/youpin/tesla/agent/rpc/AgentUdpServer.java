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

package com.xiaomi.youpin.tesla.agent.rpc;

import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.udp.UdpServer;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.agent.common.Config;
import com.xiaomi.youpin.tesla.agent.common.NetUtils;
import com.xiaomi.youpin.tesla.agent.interceptor.Log;
import com.xiaomi.youpin.tesla.agent.service.IService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Optional;


/**
 * @author goodjava@qq.com
 */
@Slf4j
@Component(desc = "udp service")
public class AgentUdpServer implements IService {

    @Resource
    private RpcClient client;

    private static final String PWD = Config.ins().get("udp_pwd", "");

    private UdpServer udpServer;

    /**
     * 是否启动udp服务
     */
    private static final boolean UDP_SERVER = Boolean.parseBoolean(Config.ins().get("udp_server", "false"));

    public AgentUdpServer(RpcClient client) {
        this.client = client;
    }

    public AgentUdpServer() {
    }

    @Log
    @Override
    public void init() {
        if (!UDP_SERVER) {
            return;
        }
        log.info("udp server init");

        String host = NetUtils.getLocalHost();
        int port = Integer.valueOf(Config.ins().get("udp_port", "9799"));
        log.info("udp server start {}:{}", host, port);
        udpServer = new UdpServer(host, port, req -> UdpHandler.process(this.client, req));
        try {
            udpServer.start();
            log.info("udp server init finish");
        } catch (InterruptedException e) {
            log.info("upd server start error:{}", e.getMessage());
        }
    }

    public void destory() {
        log.info("udp server destory");
        Optional.ofNullable(this.udpServer).ifPresent(it->it.shutdown());

    }

}
