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

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docker.YpDockerClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @author renqingfu
 * @date 2020/9/1
 */
@Slf4j
public class DockerInspect {

    public static void dockerInspectWithRunning(Stopwatch sw, String containerName, RemotingCommand response) {
        log.info("dockerContainerId container name: {}", containerName);
        Optional<Container> optional = YpDockerClient.ins().listContainers(Lists.newArrayList(), false)
                .stream()
                .filter(it -> CommonUtils.matchImage(containerName, it.getImage()))
                .findFirst();

        String logs = "{}";
        if (optional.isPresent()) {
            try {
                InspectContainerResponse res = YpDockerClient.ins().inspectContainer(optional.get().getId());
                logs = new Gson().toJson(res);
            } catch (Exception e) {
                log.error("docker processor dockerInspect Exception" + e.getMessage(), e);
                logs = "{ \"error\":" + e.getMessage() + "}";
            }
        } else {
            logs = "{ \"error\": \"没有检查到容器\" }";
        }
        log.info("dockerInspect use time:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        response.setBody(logs.getBytes());
    }

    public static void dockerInspect(Stopwatch sw, String containerName, RemotingCommand response) {
        log.info("dockerInspect container name: {}", containerName);
        Optional<Container> optional = YpDockerClient.ins().listContainers(Lists.newArrayList(), true)
                .stream()
                .filter(it -> {
                    String imageName = it.getImage();
                    if (StringUtils.isNotEmpty(imageName)) {
                        if (!(imageName.equals(containerName))) {
                            return imageName.endsWith(getName(containerName));
                        }
                        return true;
                    }
                    return false;
                })
                .findFirst();

        String logs = "{}";
        if (optional.isPresent()) {
            try {
                InspectContainerResponse res = YpDockerClient.ins().inspectContainer(optional.get().getId());
                logs = new Gson().toJson(res);
            } catch (Exception e) {
                log.error("docker processor dockerInspect Exception" + e.getMessage(), e);
                logs = "{ \"error\":" + e.getMessage() + "}";
            }
        } else {
            logs = "{ \"error\": \"没有检查到容器\" }";
        }
        log.info("dockerInspect use time:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        response.setBody(logs.getBytes());
    }

    private static String getName(String containerName) {
        if (StringUtils.isNotEmpty(containerName)) {
            int lastIndex = containerName.lastIndexOf("-");
            if (lastIndex != -1) {
                return containerName.substring(0, lastIndex) + ":" + containerName.substring(lastIndex + 1);
            }
        }
        return containerName;
    }
}
