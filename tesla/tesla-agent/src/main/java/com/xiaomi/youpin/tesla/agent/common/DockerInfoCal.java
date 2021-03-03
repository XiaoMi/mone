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

import com.github.dockerjava.api.model.Info;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.po.DockerInfo;
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
@Slf4j
public class DockerInfoCal {

    public void info(DockerReq req, RemotingCommand response) {
        Info info = YpDockerClient.ins().info();
        DockerInfo di = new DockerInfo();
        di.setServerVersion(info.getServerVersion());
        Optional.ofNullable(req.getContainerName()).ifPresent(it -> {
            String name = CommonUtils.getName(it);
            di.setStatus(new DockerStatus().dockerStats(name));
            log.info("info {} {}", name, new Gson().toJson(di.getStatus()));
        });
        response.setBody(new Gson().toJson(di).getBytes());
    }



}
