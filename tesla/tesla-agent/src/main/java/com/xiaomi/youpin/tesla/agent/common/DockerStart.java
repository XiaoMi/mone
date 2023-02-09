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

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.po.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
@Slf4j
public class DockerStart {


    public void start(DockerContext context, Stopwatch sw, DockerReq req, DeployInfo deployInfo, RemotingCommand response, ExecutorService dockerProcessorPool, Consumer<NotifyMsg> consumer) {
        log.info("start begin");
        new DockerSideCar().start(context, deployInfo);
        DockerRes dockerRes = new DockerRes();
        dockerRes.setProjectId(req.getProjectId());
        dockerRes.setStatus(0);
        deployInfo.setStep(DeployInfo.DockerStep.start.ordinal());
        YpDockerClient.ins().startContainer(req.getContainerName());
        req.getAttachments().put("containerId", req.getContainerId());
        long time = sw.elapsed(TimeUnit.MILLISECONDS);
        consumer.accept(new NotifyMsg(NotifyMsg.STATUS_SUCESSS, 4, "start", "[SUCCESS] start container successfully(" + time + "ms)\n", time, req.getId(), req.getAttachments()));
        response.setBody(new Gson().toJson(dockerRes).getBytes());
        deployInfo.setState(DeployInfo.DeployState.running.ordinal());

        dockerProcessorPool.submit(() -> cleanExpiredContainer(deployInfo.getName()));
    }

    /**
     * 删除之前的容器，避免重启后拉起不必要的container
     * @param containerName
     */
    public void cleanExpiredContainer(String containerName){
        SafeRun.run(() -> {
            log.info("cleanExpiredContainer container name:{}", containerName);
            List<String> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), true, Lists.newArrayList("exited", "dead"), Lists.newArrayList(containerName))
                    .stream()
                    .map(it -> it.getId())
                    .collect(Collectors.toList());
            log.info("cleanExpiredContainer container list:{} ", list);
            YpDockerClient.ins().rmContainers(list);
        });
    }
}
