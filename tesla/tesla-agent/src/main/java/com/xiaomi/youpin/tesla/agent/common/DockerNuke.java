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
import com.xiaomi.youpin.tesla.agent.po.DeployInfo;
import com.xiaomi.youpin.tesla.agent.po.DockerContext;
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import com.xiaomi.youpin.tesla.agent.po.NukeRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
@Slf4j
public class DockerNuke {

    public void shutdown(DockerContext context, Stopwatch sw, DockerReq req, DeployInfo deployInfo, Runnable notify) {
        log.info("shutdown begin id:{}", deployInfo.getId());
        deployInfo.setStep(DeployInfo.DockerStep.stop.ordinal());

        new DockerSideCar().stop(context, req, deployInfo);

        log.info("stop continer name:{}", deployInfo.getName());
        List<String> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), false)
                .stream()
                .filter(it -> CommonUtils.matchImage(deployInfo.getName(), it.getImage())).map(it -> it.getId())
                .collect(Collectors.toList());
        log.info("container:{} ", list);
        list.forEach(it -> Safe.execute(() -> YpDockerClient.ins().stopContainer(it, req.getStopTimeout())));
        notify.run();
        deployInfo.setState(DeployInfo.DeployState.stop.ordinal());
        log.info("shutdown finish id:{}", deployInfo.getId());
    }


    public void nuke(DockerContext context, Stopwatch sw, DockerReq req, RemotingCommand response, DeployInfo deployInfo, ExecutorService dockerProcessorPool, Runnable notify) {
        log.info("nuke:{} begin", deployInfo.getId());
        NukeRes nukeRes = new NukeRes();
        nukeRes.setName(req.getJarName());
        log.info("nuke {}", new Gson().toJson(req));
        Safe.execute(() -> shutdown(context, sw, req, deployInfo, notify));
        try {
            String name = deployInfo.getName();
            new DockerSideCar().remove(deployInfo, dockerProcessorPool);
            List<String> cids = YpDockerClient.ins().listContainers(Lists.newArrayList(), true).stream()
                    .filter(it -> CommonUtils.matchImage(name, it.getImage()))
                    .map(it -> it.getId()).collect(Collectors.toList());

            //删除容器
            cids.stream().forEach(it -> {
                dockerProcessorPool.submit(() -> YpDockerClient.ins().rm(it));
            });
            List<String> iids = YpDockerClient.ins().listImages("").stream()
                    .filter(it -> {
                        try {
                            return CommonUtils.matchImage(name, it.getRepoTags()[0].split(":")[0]);
                        } catch (Throwable ex) {
                            log.error(ex.getMessage());
                            return false;
                        }
                    })
                    .map(it -> it.getId()).collect(Collectors.toList());
            //删除网桥
            SafeRun.run(() -> YpDockerClient.ins().removeNetwork("bridge_" + req.getEnvId()));
            //删除镜像
            iids.stream().forEach(it -> dockerProcessorPool.submit(() -> YpDockerClient.ins().rmi(it)));
            response.setBody(new Gson().toJson(nukeRes).getBytes());
            deployInfo.setState(DeployInfo.DeployState.nuke.ordinal());
            log.info("nuke:{} success", deployInfo.getId());
        } catch (Throwable ex) {
            log.info("nuke:{} failure:{}", deployInfo.getId(), ex.getMessage());
            nukeRes.setCode(500);
            nukeRes.setMessage(ex.getMessage());
            response.setBody(new Gson().toJson(nukeRes).getBytes());
        }
        log.info("nuke:{} finish {}", deployInfo.getId(), sw.elapsed(TimeUnit.SECONDS));
    }

}
