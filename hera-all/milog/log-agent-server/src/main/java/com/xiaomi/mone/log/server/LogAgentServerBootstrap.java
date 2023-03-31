package com.xiaomi.mone.log.server;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.server.porcessor.AgentCollectProgressProcessor;
import com.xiaomi.mone.log.server.porcessor.AgentConfigProcessor;
import com.xiaomi.mone.log.server.porcessor.PingProcessor;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/5 11:24
 */
@Slf4j
public class LogAgentServerBootstrap {

    public static void main(String[] args) throws IOException {
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

        Ioc.ins().putBean(rpcServer);
        Ioc.ins().init("com.xiaomi.mone", "com.xiaomi.youpin");
        log.info("log server start finish");
    }
}
