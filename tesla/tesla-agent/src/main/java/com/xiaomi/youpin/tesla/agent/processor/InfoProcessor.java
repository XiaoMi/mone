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

package com.xiaomi.youpin.tesla.agent.processor;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.jmonitor.Jmonitor;
import com.xiaomi.youpin.jmonitor.MonitorInfo;
import com.xiaomi.youpin.jmonitor.NetworkInfo;
import com.xiaomi.youpin.tesla.agent.bo.HealthInfo;
import com.xiaomi.youpin.tesla.agent.common.AgentVersion;
import com.xiaomi.youpin.tesla.agent.common.NetUtils;
import com.xiaomi.youpin.tesla.agent.common.ProcessUtils;
import com.xiaomi.youpin.tesla.agent.po.GetInfoReq;
import com.xiaomi.youpin.tesla.agent.service.DeployService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * @author goodjava@qq.com
 * 获取客户端信息
 */
@Slf4j
@Component
public class InfoProcessor implements NettyRequestProcessor {

    Gson gson = new Gson();

    private String hostName;

    public InfoProcessor() {
        hostName = NetUtils.getHostName();
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) {
        log.info("get client info");
        GetInfoReq req = new GetInfoReq();
        RemotingCommand response = RemotingCommand.createResponseCommand(RpcCmd.getInfoRes);
        Jmonitor service = new Jmonitor();
        MonitorInfo monitorInfo = service.getMonitorInfo(req.getPid());
        if (null == monitorInfo) {
            monitorInfo = new MonitorInfo();
        }
        monitorInfo.setTime(System.currentTimeMillis());
        monitorInfo.setVersion(new AgentVersion().toString());
        NetworkInfo networkInfo = new NetworkInfo();
        networkInfo.setHostName(hostName);
        monitorInfo.setNetworkInfo(networkInfo);

        HealthInfo healthInfo = new HealthInfo();
        healthInfo.setCpu(this.cpu());
        healthInfo.setUptime(this.getUptime());
        healthInfo.setMonitorInfo(monitorInfo);

        response.setBody(gson.toJson(healthInfo).getBytes());

        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }


    private Double getUptime() {
        try {
            List<String> list = ProcessUtils.process("/tmp/", "uptime").getValue();
            String info = list.get(0);
            //最近1分钟
            return Double.parseDouble(info.split("averages:")[1].trim().split("\\s")[0].trim());
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            return -1.0;
        }
    }


    private int cpu() {
        return DeployService.ins().getCpuNum();
    }

    @Override
    public int cmdId() {
        return RpcCmd.getInfoReq;
    }
}
