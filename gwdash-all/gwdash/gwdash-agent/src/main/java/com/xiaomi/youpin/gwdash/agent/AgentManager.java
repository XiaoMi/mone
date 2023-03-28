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

package com.xiaomi.youpin.gwdash.agent;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.common.InvokeCallback;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import com.xiaomi.youpin.gwdash.agent.common.AgentManagerVersion;
import com.xiaomi.youpin.gwdash.agent.processor.*;
import com.xiaomi.youpin.gwdash.service.DataHubService;
import com.xiaomi.youpin.gwdash.service.MachineManagementService;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;


/**
 * @author goodjava@qq.com
 */
@Slf4j
@Service
public class AgentManager {

    private RpcServer rpcServer;

    @Value("${dubbo.registry.address}")
    @Setter
    private String nacosAddrs;

    @Value("${server.type}")
    @Setter
    private String serverType;

    @Autowired
    private DataHubService dataHubService;

    @Autowired
    private MachineManagementService machineManagementService;

    @Autowired
    private IProjectDeploymentService projectDeploymentService;


    @PostConstruct
    public void init() {
        log.info("agent manager start begin:{}", new AgentManagerVersion());

        if (nacosAddrs.contains("//")) {
            nacosAddrs = nacosAddrs.split("//")[1];
        }


        rpcServer = new RpcServer(nacosAddrs, "tesla_server_" + serverType);
        rpcServer.setListenPort(9898);
//           rpcServer = new RpcServer(nacosAddrs, "tesla_server_local_zjy");


//        rpcServer = new RpcServer(nacosAddrs, "zzy");

        //register processor
        rpcServer.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.pingReq, new GwPingProcessor(this.machineManagementService, this.projectDeploymentService)),
                new Pair<>(AgentCmd.shellRes, new ShellProcessor()),
                new Pair<>(AgentCmd.dockerRes, new DockerProcessor()),
                new Pair<>(AgentCmd.fileRes, new FileProcessor()),
                new Pair<>(AgentCmd.notifyMsgReq, new NotifyMsgProcessor(dataHubService))
        ));

        //register task
        Task task = new GetClientInfoTask(rpcServer);

        rpcServer.setTasks(Lists.newArrayList(
                task
        ));
        rpcServer.init();
        rpcServer.start();
        log.info("agent manager start finish");
    }

    /**
     * 发给所有客户端
     *
     * @param code
     * @param body
     */
    public void send(int code, String body) {
        RemotingCommand req = RemotingCommand.createRequestCommand(code);
        req.setBody(body.getBytes());
        rpcServer.sendMessageToAll(req, null);
    }


    /**
     * 发给指定客户端
     *
     * @param address
     * @param code
     * @param body
     * @param timeout
     */
    public void send(String address, int code, String body, long timeout, InvokeCallback callback) {
        if (StringUtils.isEmpty(address)) {
            log.warn("address is empty");
            return;
        }
        RemotingCommand req = RemotingCommand.createRequestCommand(code);
        req.setBody(body.getBytes());
        rpcServer.send(address, req, timeout, callback);
    }

    /**
     * 发给指定客户端
     *
     * @param address
     * @param code
     * @param body
     * @param timeout
     */
    public RemotingCommand send(String address, int code, String body, long timeout) {
        RemotingCommand req = RemotingCommand.createRequestCommand(code);
        req.setBody(body.getBytes());
        return rpcServer.sendMessage(address, req, timeout);
    }


    public List<String> clientList() {
        return rpcServer.clientList();
    }

    /**
     * 获取带端口号的地址
     *
     * @param ip
     * @return
     */
    public Optional<String> getClientAddress(String ip) {
        final String tmpIp = ip.split(":")[0];
        return rpcServer.clientList().stream().map(it -> it.replaceFirst("/", ""))
                .filter(it -> {
                    String[]ss = it.split(":");
                    if (ss.length > 0) {
                        return ss[0].trim().equals(tmpIp);
                    }
                    return false;
                }).findFirst();
    }

    public void closeClient(String address) {
        rpcServer.closeClient(address);
    }


}
