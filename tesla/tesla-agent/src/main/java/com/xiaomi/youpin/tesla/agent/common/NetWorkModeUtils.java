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

import com.github.dockerjava.api.model.Network;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import com.xiaomi.youpin.tesla.agent.service.LabelService;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2021/11/30
 */
@Slf4j
public class NetWorkModeUtils {

    private static final String DEFAULT_NET_MODULE = "bridge";

    private static final SubnetCal subnetCal = new SubnetCal();

    public static String netWorkMode(DockerReq req) {
        String netWorkMode = LabelService.ins().getLabelValue(req.getLabels(), LabelService.NET_MODULE, DEFAULT_NET_MODULE);
        // 不是host模式，使用自定义的bridge网桥
        if (DEFAULT_NET_MODULE.equals(netWorkMode)) {
            String name = "bridge_" + req.getEnvId();
            Optional<Network> optional = YpDockerClient.ins().listNetwork(name).stream().findFirst();
            if (optional.isPresent()) {
                netWorkMode = optional.get().getId();
            } else {
                try {
                    String sub = subnetCal.calSubset();
                    netWorkMode = YpDockerClient.ins().createNetwork(name, sub).getId();
                } catch (Throwable ex) {
                    log.error("create network error:{}", ex.getMessage());
                }
            }
        }
        return netWorkMode;
    }

}
