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

package com.xiaomi.mone.log.stream.bootstrap;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/22 13:58
 */

import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.nacos.NacosConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_GROUP_ID;

@Slf4j
public class MiLogStreamBootstrap {

    public static void main(String[] args) throws IOException {
        log.info("test");
        setConfigFromNacos();
        Ioc.ins().init("com.xiaomi.mone.log.stream", "com.xiaomi.youpin.docean");
        long initDelay = 0;
        long intervalTime = 2;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> log.info("I am health,time:{}", LocalDateTime.now()), initDelay, intervalTime, TimeUnit.MINUTES);
        String serverAddr = Config.ins().get("nacosAddress", "");
        String serverName = Config.ins().get("serverName", "");
        RpcServer rpcServer = new RpcServer(serverAddr, serverName);
        rpcServer.setListenPort(9876);
        rpcServer.init();
        rpcServer.start();
        System.in.read();
    }

    private static void setConfigFromNacos() {
        NacosConfig nacosConfig = new NacosConfig();
        nacosConfig.setDataId(Config.ins().get("nacos_config_dataid", ""));
        nacosConfig.setGroup(Config.ins().get("nacos_config_group", DEFAULT_GROUP_ID));
        nacosConfig.setServerAddr(Config.ins().get("nacos_config_server_addr", ""));
        nacosConfig.init();
        nacosConfig.forEach((k, v) -> Config.ins().set(k, v));
    }
}
