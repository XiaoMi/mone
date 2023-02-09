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

package com.xiaomi.youpin.tesla.agent.bootstrap;

import com.xiaomi.data.push.bo.ClientInfo;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.tesla.agent.common.AgentVersion;
import com.xiaomi.youpin.tesla.agent.common.Config;
import com.xiaomi.youpin.tesla.agent.common.NetUtils;
import com.xiaomi.youpin.tesla.agent.interceptor.Log;
import com.xiaomi.youpin.tesla.agent.interceptor.LogInterceptor;
import com.xiaomi.youpin.tesla.agent.service.DeployService;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class AgentBootstrap {

    public static void main(String... args) {
        String nacosAddr = Config.ins().get("nacosAddr", "");
        String serviceName = Config.ins().get("serviceName", "");
        log.info("tesla agent start begin serverName:{} nacos:{} serviceName:{} agent version:{}", serviceName, nacosAddr, serviceName, new AgentVersion());
        ClientInfo clientInfo = new ClientInfo("tesla_agent", NetUtils.getLocalHost(), Integer.parseInt(Config.ins().get("udp_port", "9799")), new AgentVersion().toString() + ":" + serviceName);
        final RpcClient client = new RpcClient(nacosAddr, serviceName);
        //不再使用断线重连,而是根据ping来从新连接
        client.setReconnection(false);
        client.setClientInfo(clientInfo);
        DeployService.ins().load();
        //aop初始化
        LinkedHashMap<Class, EnhanceInterceptor> m = new LinkedHashMap<>();
        m.put(Log.class, new LogInterceptor());
        //ioc容器初始化
        Aop.ins().init(m);
        Ioc.ins().putBean(client).init("com.xiaomi");
        client.start();
        client.init();
        log.info("tesla agent start finish");
    }


}
