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

package com.xiaomi.youpin.rpc.test.tcp;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.bo.MPPing;
import com.xiaomi.data.push.bo.User;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.nacos.NacosConfig;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.data.push.rpc.processor.GetInfoProcessor;
import com.xiaomi.data.push.rpc.processor.MPingProcessor;
import com.xiaomi.data.push.rpc.processor.PingProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
@Slf4j
public class TcpTest {

    @Test
    public void testServer3() {
        testServer();
    }


    @SneakyThrows
    @Test
    public void testServer() {
        String nacosAddr = System.getenv("nacos_addr");
        boolean regNacos = StringUtils.isNotEmpty(nacosAddr) ? true : false;
        RpcServer rpcServer = new RpcServer(null == nacosAddr ? "" : nacosAddr, "demo_server_zzy", regNacos);
        //注册处理器
        rpcServer.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.mpPingReq, new MPingProcessor()),
                new Pair<>(RpcCmd.pingReq, new PingProcessor())
        ));
        //注册周期任务
        rpcServer.setTasks(Lists.newArrayList(
//                new GetInfoTask(rpcServer),
                new Task(() -> {
                    RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.getInfoReq);
                    rpcServer.tell(u -> null != u && u.getName().equals("zzy"), req);

                    List<AgentChannel> list = AgentContext.ins().list();
                    if (list.size() > 0) {
                        AgentChannel ch = list.get(0);
                        RemotingCommand req2 = RemotingCommand.createRequestCommand(RpcCmd.getInfoReq);
                        RemotingCommand res = rpcServer.sendMessage(ch.getChannel(), req2);
                        log.info("res:{}", new String(res.getBody()));
                    }

                }, 10)
        ));
        rpcServer.init();
        rpcServer.start();

        Thread.currentThread().join();
    }

    /**
     * 注册到多个集群,用$分隔(保证兼容之前代码)
     */
    @SneakyThrows
    @Test
    public void testServer2() {
        NacosConfig config = new NacosConfig();
        config.setDataId("zzy_new");
        config.init();
        String url = config.getConfig("rpcServerRegUrl");
        //example 127.0.0.1:80$127.0.0.2:80
        RpcServer rpcServer = new RpcServer(url, "demo_server1_zzy", true);
        //注册处理器
        rpcServer.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.mpPingReq, new MPingProcessor()),
                new Pair<>(RpcCmd.pingReq, new PingProcessor())
        ));
        //注册周期任务
        rpcServer.setTasks(Lists.newArrayList(
//                new GetInfoTask(rpcServer),
                new Task(() -> {
                    RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.getInfoReq);
                    rpcServer.tell(u -> null != u && u.getName().equals("zzy"), req);

                    List<AgentChannel> list = AgentContext.ins().list();
                    if (list.size() > 0) {
                        AgentChannel ch = list.get(0);
                        RemotingCommand req2 = RemotingCommand.createRequestCommand(RpcCmd.getInfoReq);
                        RemotingCommand res = rpcServer.sendMessage(ch.getChannel(), req2);
                        log.info("res:{}", new String(res.getBody()));
                    }

                }, 2)
        ));
        rpcServer.init();
        rpcServer.start();

        Thread.currentThread().join();
    }


    @SneakyThrows
    @Test
    public void testClient() {
        String nacosAddr = System.getenv("nacos_addr");
        nacosAddr = null == nacosAddr ? "127.0.0.1:53442" : nacosAddr;
        RpcClient client = new RpcClient(nacosAddr, "demo_server_zzy");
        client.setReconnection(false);
        client.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.getInfoReq, new GetInfoProcessor())
        ));
        client.setTasks(Lists.newArrayList(
                new Task(() -> {
                    try {
                        log.info("send ping");
                        MPPing p = new MPPing();
                        User user = new User();
                        user.setName("zzy");
                        p.setData("ping");
                        p.setUser(user);
                        RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.pingReq);
                        req.setTimeout(2000L);
                        req.setBody(new Gson().toJson(p).getBytes());

//                        client.sendMessage(client.getServerAddrs(), req, responseFuture -> {
//                            log.info("--->" + responseFuture.getResponseCommand());
//                        });

//                        client.sendToAllMessage(req);
                        client.sendToAllMessage(RpcCmd.pingReq, "ping".getBytes(), resFuture -> {
                            log.info("----->{}", resFuture.getResponseCommand());
                        });

//                        RemotingCommand res = client.sendMessage(client.getServerAddrs(), RpcCmd.pingReq, "abc", 1000);
//                        log.info("res:{}", new String(res.getBody()));
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                }, 5)
        ));
        client.init();
        client.start();
        client.getClient().createChannel();
        Thread.currentThread().join();
    }

}

