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

package com.xiaomi.youpin.mischedule.config;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.context.ServerContext;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.processor.PingProcessor;
import com.xiaomi.data.push.schedule.TaskManager;
import com.xiaomi.youpin.mischedule.processor.CTaskProcessor;
import com.xiaomi.youpin.mischedule.service.ElectionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author goodjava@qq.com
 * <p>
 * 业务的rpc
 */
@Configuration
public class RpcConfig {

    @Value("${nacos.config.addrs}")
    private String nacosAddress;


    @Value("${rpc.server.port}")
    private int port;

    @Value("${schedule.server.type}")
    private String type;

    @Value("${election.type}")
    private String electionType;

    @Value("${service_name}")
    private String serviceName = "MiScheduleServer";


    /**
     * 处理业务逻辑
     *
     * @return
     */
    @Bean
    public RpcServer rpcServer(@Autowired TaskManager taskManager) {
        RpcServer rpcServer = new RpcServer(nacosAddress, serviceName, false);
        rpcServer.setListenPort(port);
        rpcServer.setProcessorList(Lists.newArrayList(
                new Pair<>(RpcCmd.pingReq, new PingProcessor()),
                new Pair<>(3000, new STaskProcessor(taskManager))
        ));
        rpcServer.init();
        rpcServer.start();
        return rpcServer;
    }


    @Bean
    public RpcClient rpcClient(@Autowired TaskManager taskManager, @Autowired ServerContext serverContext,@Autowired ElectionServiceImpl electionService) {
        RpcClient rpcClient = new RpcClient(nacosAddress, serviceName) {
            @Override
            public String getServerAddrs() {
                if (electionType.equals("redis")) {
                    String leaderIp = serverContext.getLeaderIp().get();
                    if (StringUtils.isEmpty(leaderIp)) {
                        return "";
                    }
                    return leaderIp + ":" + port;
                } else {
                    String ip = electionService.getNode().getNode().getLeaderId().getIp();
                    return ip + ":" + port;
                }
            }
        };


        if (("standalone".equals(type))) {
            return rpcClient;
        }

        //不允许断线重连
        rpcClient.setReconnection(false);

        //任务处理器
        rpcClient.setProcessorList(Lists.newArrayList(
                new Pair<>(4000, new CTaskProcessor(taskManager))
        ));

        //注册调度任务
        rpcClient.setTasks(Lists.newArrayList(
                new PingTask(rpcClient)
        ));

        rpcClient.init();
        rpcClient.start();
        return rpcClient;
    }

}
