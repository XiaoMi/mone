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

package run.mone.raft.rpc;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.common.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.raft.common.AddressUtils;
import run.mone.raft.controller.RaftController;
import run.mone.raft.pojo.RpcCmd;
import run.mone.raft.processor.RaftProcessor;
import run.mone.raft.rpc.client.DoceanRpcClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2022/4/17
 * 处理长连接的服务
 */
@Service
@Slf4j
public class DoceanRpcServer {

    @Resource
    private RaftController raftController;

    @Resource
    private DoceanRpcClient rpcClient;


    @Value("${server.port}")
    private int port;

    private int rpcPort;


    @PostConstruct
    public void init() {
        log.info("init docean rpc server");
        this.rpcPort = AddressUtils.getRpcPort(port);
        startRpcServer();
    }


    public void startRpcServer() {
        log.info("agent manager start port:{}", rpcPort);
        RpcServer rpcServer = new RpcServer("", "nacos_server", false);
        rpcServer.setListenPort(rpcPort);
        //注册处理器
        rpcServer.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.raftReq, new RaftProcessor(raftController, rpcClient)))
        );
        rpcServer.init();
        rpcServer.start(config -> config.setIdle(false));
        log.info("nacos rpc server start finish");
    }


}
