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
package com.xiaomi.mone.log.agent.bootstrap;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.bo.ClientInfo;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.mone.log.agent.common.Version;
import com.xiaomi.mone.log.agent.rpc.task.PingTask;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.utils.NetUtil;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.xiaomi.mone.log.utils.ConfigUtils.getConfigValue;
import static com.xiaomi.mone.log.utils.ConfigUtils.getDataHashKey;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/22 11:22
 */
@Slf4j
public class MiLogAgentBootstrap {

    public static void main(String[] args) throws IOException {
        String nacosAddr = getConfigValue("nacosAddr");
        String serviceName = getConfigValue("serviceName");
        log.info("nacosAddr:{},serviceName:{}", nacosAddr, serviceName);
        log.info("hera log agent version:{}", new Version());
        String appName = Config.ins().get("app_name", "milog_agent");
        ClientInfo clientInfo = new ClientInfo(
                String.format("%s_%d", appName, getDataHashKey(NetUtil.getLocalIp(), Integer.parseInt(Config.ins().get("app_max_index", "30")))),
                NetUtil.getLocalIp(),
                Integer.parseInt(Config.ins().get("port", "9799")),
                new Version() + ":" + serviceName + ":" + nacosAddr);
        final RpcClient client = new RpcClient(nacosAddr, serviceName);
        client.setReconnection(false);
        client.setClientInfo(clientInfo);
        client.start();
        client.setTasks(Lists.newArrayList(new PingTask(client)));
        client.init();
        client.waitStarted();
        log.info("create rpc client finish");
        Ioc.ins().putBean(client).init("com.xiaomi.mone.log.agent", "com.xiaomi.youpin.docean");
        //因为client生命周期提前,这里需要从新注册processor
        client.registerProcessor();
        System.in.read();
    }

}
