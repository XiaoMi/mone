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

package com.xiaomi.youpin.tesla.agent.common;

import com.github.dockerjava.api.model.Container;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.po.DeployInfo;
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import com.xiaomi.youpin.tesla.agent.po.NotifyMsg;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
@Slf4j
public class DockerRestart {

    public RemotingCommand restart(Stopwatch sw, DockerReq req, DeployInfo deployInfo, Consumer<NotifyMsg> consumer) {
        log.info("restart begin id:{}", deployInfo.getId());
        RemotingCommand response = RemotingCommand.createResponseCommand(AgentCmd.dockerRes);
        String containerName = CommonUtils.getName(req.getContainerName());
        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), false, containerName);

        if (list.size() > 0) {
            response.setBody("deploy".getBytes());
            return response;
        }


        //停止旧的container
        list.stream().forEach(it -> YpDockerClient.ins().stopContainer(it.getId()));
        consumer.accept(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 0, "stop", "[INFO] stop container finish", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));


        List<Container> oldList = YpDockerClient.ins().listContainers(Lists.newArrayList(), true, (containerName));
        if (oldList.size() == 0) {
            //need create
            log.warn("old list == 0");
            response.setBody("deploy".getBytes());
            return response;
        }


        //启动container
        YpDockerClient.ins().startContainer(req.getJarName().split("\\.")[0]);
        consumer.accept(new NotifyMsg(NotifyMsg.STATUS_SUCESSS, 4, "start", "[SUCCESS] start container successfully" + "\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));

        response.setBody("success".getBytes());

        log.info("restart finish id:{}", deployInfo.getId());

        return response;
    }


}
