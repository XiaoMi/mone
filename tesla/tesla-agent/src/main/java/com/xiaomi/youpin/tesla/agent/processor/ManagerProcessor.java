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

import com.github.dockerjava.core.command.PullImageResultCallback;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.common.CommonUtils;
import com.xiaomi.youpin.tesla.agent.po.ManagerReq;
import com.xiaomi.youpin.tesla.agent.service.DeployService;
import com.xiaomi.youpin.tesla.agent.task.PingTask;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Slf4j
@Component
public class ManagerProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws Exception {
        log.info("ManagerProcessor begin");
        ManagerReq req = remotingCommand.getReq(ManagerReq.class);
        log.info("ManagerProcessor process :{}",new Gson().toJson(req));
        RemotingCommand response = RemotingCommand.createResponseCommand(AgentCmd.managerRes);

        switch (ManagerReq.CmdType.valueOf(req.getCmd())) {

            //更新镜像
            case updateImage: {
                String imageName = req.getAttachments().get("imageName");
                log.info("manager pull begin image name:{}", imageName);
                YpDockerClient.ins().pullImage(imageName, new PullImageResultCallback()).awaitCompletion();
                break;
            }

            //docker宿主机的关机操作(其实就是关闭所有容器,并记录容器id)
            case powerOff: {
                log.info("powerOff");
                List<String> ids = YpDockerClient.ins().powerOff();
                log.info("power off ids:{}", ids);
                DeployService.ins().recordContainerIds(ids);
                response.setBody("power off success".getBytes());
                PingTask.stop.set(true);
                CommonUtils.sleep(3);
                break;
            }

            //docker宿主机开机操作(其实就是拉起最后起来的容器)
            case powerOn: {
                List<String> ids = DeployService.ins().getContainerIds();
                log.info("power on ids:{}", ids);
                YpDockerClient.ins().powerOn(ids);
                response.setBody("power on success".getBytes());
                break;
            }

        }
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int cmdId() {
        return AgentCmd.managerReq;
    }
}
