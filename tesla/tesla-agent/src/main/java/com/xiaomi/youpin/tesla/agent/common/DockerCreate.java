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
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
@Slf4j
public class DockerCreate {


    public void create(DockerContext context, Stopwatch sw, DockerReq req, DeployInfo deployInfo, Consumer<NotifyMsg> consumer) {
        log.info("create begin id:{}", deployInfo.getId());
        if (YpDockerClient.ins().listContainers(Lists.newArrayList(), true, (req.getContainerName())).size() > 0) {
            log.info("don't need create continer :{}", req.getContainerName());
            req.setCmd(DockerCmd.stop.name());
            return;
        }

        //创建sidecar 的容器
        new DockerSideCar().create(context, sw, req, deployInfo, consumer);

        deployInfo.setStep(DeployInfo.DockerStep.create.ordinal());

        SideCarListData sideCarList = req.getSideCarListData();
        List<EnvData> envList = null;
        if (null != sideCarList) {
            envList = sideCarList.getSideCarDataList().stream().map(it -> {
                return it.getPortInfoList().stream().map(info -> {
                    EnvData ed = new EnvData();
                    ed.setName(info.getName());
                    ed.setValue(String.valueOf(info.getPort()));
                    return ed;
                }).collect(Collectors.toList());
            }).flatMap(li -> li.stream()).collect(Collectors.toList());
        }


        String localHost = NetUtils.getLocalHost();

        String hostName = NetUtils.getHostName();

        String envHost = localHost;


        //需要绑定的磁盘
        List<Bind> bindList = BindUtils.getBindList(req, deployInfo.getName());

        String cpusLabel = LabelService.ins().getLabelValue(req.getLabels(), LabelService.CPUS, "false");

        DockerLimit dl = getDockerLimit(req, cpusLabel);

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
        int sentinelPort = portCal.getSentinelPort();
        int prometheusPort = portCal.getPrometheusPort();
        int agentPrometheusPort = portCal.getAgentPrometheusPort();
        List<String> tpList = portCal.getTpList();
        log.info("dubbo port:{} http port:{} third port:{} log path:{} hostName:{} tcpPorts:{}", portCal.getDubboPort(), portCal.getHttpPort(), thirdPort, req.getLogPath(), hostName, tpList);
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

        //nacos1.2.1 mione版本添加的端口映射(client)
        String nacosPushPortEnv = "DOCKER_NACOS_UDP_PUSH_PORT=" + nacosPushPort;

        //nacos1.4.2需要的环境变量(clinet)
        String nacosEnv = "push.receiver.udp.port=" + nacosPushPort;


        String httpPortEnv = "HTTP_PORT=" + httpPort;
        String hostEnv = "TESLA_HOST=" + envHost;
        String langEnv = "LANG=C.UTF-8";
        String tzEnv = "TZ=Asia/Shanghai";

        //mione.app.name设置应用的appname
        String mioneAppNameEnv = "mione.app.name=none";
        if (null != req.getAttachments()
                && null != req.getAttachments().get("mioneAppName")) {
            mioneAppNameEnv = "mione.app.name=" + req.getAttachments().get("mioneAppName");
        }

        //设置dubbo application name
        String dubboAppName = CommonUtils.getName(req.getContainerName());
        String dubboAppNameEnv = "";
        if (!StringUtils.isEmpty(dubboAppName)) {
            dubboAppNameEnv = "dubbo.application.name=" + dubboAppName;
        }

        //cat 需要使用宿主机的ip
        String catEnv = "host.ip=" + localHost;

        //sentinel使用的env
        String sentinelIpEnv = "csp.sentinel.heartbeat.client.ip=" + localHost;
        String sentinelPortEnv = "csp.sentinel.api.port=" + sentinelPort;

        //falcon会用到这个值
        String host = "HOST=172.17.0.1";

        //官方版本dubbo(ip 和 port)
        String dubboIpEnv2 = "DUBBO_IP_TO_REGISTRY=" + localHost;
        String dubboPortEnv2 = "DUBBO_PORT_TO_REGISTRY=" + dubboPort;

        //prometheus
        String prometheusPortEnv = "PROMETHEUS_PORT=" + prometheusPort;
        String agentPrometheusPortEnv = "JAVAAGENT_PROMETHEUS_PORT=" + agentPrometheusPort;

        // 不是host模式，使用自定义的bridge网桥
        String netWorkMode = NetWorkModeUtils.netWorkMode(req);

        String pidMode = LabelService.ins().getLabelValue(req.getLabels(), LabelService.PID_MODE, "");

        log.info("name:{} dubbo ip:{} port:{} httpPort:{} nacosPushPort:{} prometheusPort:{} agentPrometheusPort:{} host:{} netWorkMode:{}", req.getJarName(), dubboIpEnv, dubboPortEnv, httpPortEnv, nacosPushPortEnv, prometheusPort, agentPrometheusPortEnv, hostEnv, netWorkMode);

        List<ExposedPort> exposedPorts = portCal.getExposedPorts();
        List<PortBinding> portBindings = portCal.getPortBindings();

        List<String> dnsList = getDnsList();

        List<String> envs = Lists.newArrayList(
                dubboIpEnv,
                dubboPortEnv,
                dubboIpEnv2,
                dubboPortEnv2,
                nacosPushPortEnv,
                nacosEnv,
                gsonDubboPortEnv,
                httpPortEnv,
                hostEnv,
                langEnv,
                tzEnv,
                host,
                catEnv,
                sentinelIpEnv,
                sentinelPortEnv,
                prometheusPortEnv,
                agentPrometheusPortEnv,
                mioneAppNameEnv
        );
//        if (!StringUtils.isEmpty(dubboAppNameEnv)) {
//            envs.add(dubboAppNameEnv);
//        }

        if (null != envList) {
            List<String> l = envList.stream().map(it -> it.getName() + "=" + it.getValue()).collect(Collectors.toList());
            envs.addAll(l);
        }

        String id = YpDockerClient.ins().createContainer(
                req.getImageName(),
                hostName,
                netWorkMode,
                req.getContainerName(),
                dl,
                exposedPorts,
                portBindings,
                pidMode,
                bindList,
                dnsList,
                envs.toArray(new String[]{})
        );
        req.setContainerId(id);
        long time = sw.elapsed(TimeUnit.MILLISECONDS);
        consumer.accept(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 2, "create", "[INFO] create container finish(" + time + "ms)" + "\n", time, req.getId(), req.getAttachments()));
        deployInfo.setState(DeployInfo.DeployState.create.ordinal());
        req.setCmd(DockerCmd.stop.name());


        log.info("create finish id:{}", deployInfo.getId());
    }

    private List<String> getDnsList() {
        List<String> dnsList = Lists.newArrayList();
        String dns = Config.ins().get("dns", "");
        if (StringUtils.isNotEmpty(dns)) {
            String[] array = dns.split(",");
            Arrays.stream(array).forEach(it -> {
                dnsList.add(it);
            });
        }
        return dnsList;
    }


    /**
     * support mem cpu io 限制
     *
     * @param req
     * @return
     */
    protected static DockerLimit getDockerLimit(DockerReq req, String cpus) {
        DockerLimit.DockerLimitBuilder builder = DockerLimit.builder()
                .blkioWeight(req.getBlkioWeight() == null ? 500 : req.getBlkioWeight())
                .cpu(req.getCpu() == null ? "0" : req.getCpu())
                .mem(req.getMem() == null ? 1024 * 1024 * 1024L : req.getMem());

        if (cpus.equals("true")) {
            return builder.cpuNum(req.getCpuNum()).useCpus(true).build();
        }
        return builder.build();
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
