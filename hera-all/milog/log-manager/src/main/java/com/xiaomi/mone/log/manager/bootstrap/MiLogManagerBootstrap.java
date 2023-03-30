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

package com.xiaomi.mone.log.manager.bootstrap;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.controller.interceptor.HttpRequestInterceptor;
import com.xiaomi.mone.log.manager.porcessor.AgentCollectProgressProcessor;
import com.xiaomi.mone.log.manager.porcessor.AgentConfigProcessor;
import com.xiaomi.mone.log.manager.porcessor.PingProcessor;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;

import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.getConfigFromNanos;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/24 11:29
 */
@Slf4j
public class MiLogManagerBootstrap {


    public static void main(String[] args) throws InterruptedException {
        String nacosAddr = Config.ins().get("nacosAddr", "");
        String serverName = Config.ins().get("serverName", "");
        log.info("nacos:{} name:{}", nacosAddr, serverName);
        RpcServer rpcServer = new RpcServer(nacosAddr, serverName);
        rpcServer.setListenPort(9899);
        //注册处理器
        rpcServer.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.pingReq, new PingProcessor()),
                new Pair<>(Constant.RPCCMD_AGENT_CODE, new AgentCollectProgressProcessor()),
                new Pair<>(Constant.RPCCMD_AGENT_CONFIG_CODE, new AgentConfigProcessor())
        ));
        rpcServer.init();
        rpcServer.start();
        getConfigFromNanos();

        LinkedHashMap<Class, EnhanceInterceptor> m = new LinkedHashMap<>();
        m.put(RequestMapping.class, new HttpRequestInterceptor());
        Aop.ins().init(m);

        Ioc.ins().putBean(rpcServer);
        Ioc.ins().init("com.xiaomi.mone", "com.xiaomi.youpin");
        Config ins = Config.ins();

        int port = Integer.parseInt(ins.get("serverPort", ""));
        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder().websocket(false).port(port).build());
        server.start();
        log.info("milog manager start finish");
    }

}
