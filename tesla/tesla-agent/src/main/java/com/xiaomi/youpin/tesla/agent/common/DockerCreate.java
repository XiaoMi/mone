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

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.PortBinding;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.docker.DockerLimit;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.po.*;
import com.xiaomi.youpin.tesla.agent.service.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
@Slf4j
public class DockerCreate {


    public void create(Stopwatch sw, DockerReq req, DeployInfo deployInfo, Consumer<NotifyMsg> consumer) {
        log.info("create begin id:{}", deployInfo.getId());
        if (YpDockerClient.ins().listContainers(Lists.newArrayList(), true, (req.getContainerName())).size() > 0) {
            log.info("don't need create continer :{}", req.getContainerName());
            req.setCmd(DockerCmd.stop.name());
            return;
        }

        deployInfo.setStep(DeployInfo.DockerStep.create.ordinal());

        String localHost = NetUtils.getLocalHost();

        String hostName = NetUtils.getHostName();

        String envHost = localHost;


        //需要绑定的磁盘
        List<Bind> bindList = BindUtils.getBindList(req, deployInfo.getName());

        DockerLimit dl = getDockerLimit(req);

        //需要用宿主机的ip,而不是docker内部的ip
        String dubboIpEnv = "DOCKER_DUBBO_IP_TO_BIND=" + localHost;
        //不能使用配置的了,因为在一个宿主机中有可能冲突的,这时候需要再计算出来一个可用的

        //会计算出3个端口 dubbo http nacos_udp_push_port
        PortCal portCal = new PortCal();
        portCal.cal(req);

        int dubboPort = portCal.getDubboPort();
        int httpPort = portCal.getHttpPort();
        int nacosPushPort = portCal.getNacosPushPort();
        int thirdPort = portCal.getThirdPort();
        int gsonDubboPort = portCal.getGsonDubboPort();
        log.info("dubbo port:{} http port:{} third port:{} log path:{} hostName:{}", portCal.getDubboPort(), portCal.getHttpPort(), thirdPort, req.getLogPath(), hostName);
        deployInfo.setPorts(portCal.getPortList());

        List<Integer> cpus = getCups(dl);
        log.info("cpus:{}", cpus);

        ContainerInfo containerInfo = ContainerInfo.builder()
                .ports(portCal.getCports())
                .mem(dl.getMem())
                .blkioWeight(dl.getBlkioWeight())
                .cpus(cpus)
                .build();
        deployInfo.setContainerInfo(containerInfo);

        String dubboPortEnv = "DUBBO_PORT_TO_BIND=" + dubboPort;
        String gsonDubboPortEnv = "GSON_DUBBO_PORT_TO_BIND=" + gsonDubboPort;
        String nacosPushPortEnv = "DOCKER_NACOS_UDP_PUSH_PORT=" + nacosPushPort;
        String httpPortEnv = "HTTP_PORT=" + httpPort;
        String hostEnv = "TESLA_HOST=" + envHost;
        String langEnv = "LANG=C.UTF-8";
        String tzEnv = "TZ=Asia/Shanghai";
        //cat 需要使用宿主机的ip
        String catEnv = "host.ip=" + localHost;
        //falcon会用到这个值
        String host = "HOST=172.17.0.1";

        String netWorkMode = LabelService.ins().getLabelValue(req.getLabels(), LabelService.NET_MODULE, "bridge");

        log.info("name:{} dubbo ip:{} port:{} httpPort:{} nacosPushPort:{} host:{} netWorkMode:{}", req.getJarName(), dubboIpEnv, dubboPortEnv, httpPortEnv, nacosPushPortEnv, hostEnv, netWorkMode);

        List<ExposedPort> exposedPorts = portCal.getExposedPorts();
        List<PortBinding> portBindings = portCal.getPortBindings();


        // 不是host模式，使用自定义的bridge网桥
        String networkName = req.getNetwork();
        if (!"host".equals(netWorkMode) && StringUtils.isNotEmpty(networkName)) {
            Optional<Network> optional = YpDockerClient.ins().listNetwork(networkName).stream().findFirst();
            if (optional.isPresent()) {
                netWorkMode = optional.get().getId();
            } else {
                netWorkMode = YpDockerClient.ins().createNetwork(networkName).getId();
            }
        }

        String id = YpDockerClient.ins().createContainer(
                req.getImageName(),
                hostName,
                netWorkMode,
                req.getContainerName(),
                dl,
                exposedPorts,
                portBindings,
                bindList,
                dubboIpEnv,
                dubboPortEnv,
                nacosPushPortEnv,
                gsonDubboPortEnv,
                httpPortEnv,
                hostEnv,
                langEnv,
                tzEnv,
                host,
                catEnv
        );
        req.setContainerId(id);

        consumer.accept(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 2, "create", "[INFO] create container finish" + "\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));
        deployInfo.setState(DeployInfo.DeployState.create.ordinal());
        req.setCmd(DockerCmd.stop.name());

        log.info("create finish id:{}", deployInfo.getId());
    }


    /**
     * support mem cpu io 限制
     *
     * @param req
     * @return
     */
    private DockerLimit getDockerLimit(DockerReq req) {
        return DockerLimit.builder()
                .blkioWeight(req.getBlkioWeight() == null ? 500 : req.getBlkioWeight())
                .cpu(req.getCpu() == null ? "0" : req.getCpu())
                .mem(req.getMem() == null ? 1024 * 1024 * 1024L : req.getMem()).build();
    }

    private List<Integer> getCups(DockerLimit dl) {
        try {
            String cpuStr = dl.getCpu();
            if (cpuStr.contains(",")) {
                return Arrays.stream(cpuStr.split(",")).map(it -> Integer.parseInt(it)).collect(Collectors.toList());
            }
            ArrayList<Integer> res = Lists.newArrayList();
            res.add(Integer.parseInt(cpuStr));
            return res;
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            return Lists.newArrayList();
        }
    }
}
