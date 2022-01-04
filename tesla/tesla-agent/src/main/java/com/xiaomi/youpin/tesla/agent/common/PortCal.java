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

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import com.xiaomi.youpin.tesla.agent.po.Port;
import com.xiaomi.youpin.tesla.agent.service.DeployService;
import com.xiaomi.youpin.tesla.agent.service.LabelService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
@Slf4j
@Data
public class PortCal {

    private static ReentrantLock portLock = new ReentrantLock();

    private int dubboPort;
    private int httpPort;
    private int nacosPushPort;
    private int thirdPort = -1;
    private int fourPort = -1;
    private int gsonDubboPort = -1;
    private int debugPort = -1;
    private int sentinelPort = -1;
    private int prometheusPort = -1;
    private int agentPrometheusPort = -1;
    private int jacocoPort = -1;

    private List<String> tpList = Lists.newArrayList();
    private List<Integer> cports = Lists.newArrayList();
    private List<Port> portList = Lists.newArrayList();


    private List<ExposedPort> exposedPorts = Lists.newArrayList();
    private List<PortBinding> portBindings = Lists.newArrayList();

    public void cal(DockerReq req) {
        List<Integer> ports = getPorts();
        log.info("get ports:{}", ports);
        dubboPort = ports.get(0);
        httpPort = ports.get(1);
        nacosPushPort = ports.get(2);
        sentinelPort = ports.get(3);
        prometheusPort = ports.get(4);
        agentPrometheusPort = ports.get(5);

        //采用指定的dubbo 端口
        String dp = LabelService.ins().getLabelValue(req.getLabels(), LabelService.DUBBO_PORT);
        if (StringUtils.isNotEmpty(dp)) {
            dubboPort = Integer.valueOf(dp);
            cports.add(dubboPort);
        }

        //采用指定的gson dubbo 端口
        String gdp = LabelService.ins().getLabelValue(req.getLabels(), LabelService.GSON_DUBBO_PORT);
        if (StringUtils.isNotEmpty(gdp)) {
            gsonDubboPort = Integer.valueOf(gdp);
            cports.add(gsonDubboPort);
        }

        //采用指定的http 端口（单个）
        String hp = LabelService.ins().getLabelValue(req.getLabels(), LabelService.HTTP_PORT);
        if (StringUtils.isNotEmpty(hp)) {
            httpPort = Integer.valueOf(hp);
            cports.add(httpPort);
        }

        //三方需要暴露的端口（多个，以&分隔）
        String hps = LabelService.ins().getLabelValue(req.getLabels(), LabelService.PORTS);
        if (StringUtils.isNotEmpty(hps)) {
            tpList = Lists.newArrayList(hps.split("&"));
            tpList.stream().forEach(it -> cports.add(Integer.valueOf(it)));
        }

        //第三方端口号
        String tp = LabelService.ins().getLabelValue(req.getLabels(), LabelService.THIRD_PORT);
        if (StringUtils.isNotEmpty(tp)) {
            thirdPort = Integer.valueOf(tp);
            cports.add(thirdPort);
        }

        //第四方端口号
        String fp = LabelService.ins().getLabelValue(req.getLabels(), LabelService.FOUR_PORT);
        if (StringUtils.isNotEmpty(fp)) {
            fourPort = Integer.valueOf(fp);
            cports.add(fourPort);
        }
        //
        String jacocoPortStr = LabelService.ins().getLabelValue(req.getLabels(), LabelService.JACOCO_PORT);
        if (StringUtils.isNotEmpty(jacocoPortStr) && StringUtils.isNumeric(jacocoPortStr)) {
            jacocoPort = Integer.parseInt(jacocoPortStr);
            cports.add(jacocoPort);
        }

        portList.add(Port.builder().type(1).port(dubboPort).build());
        portList.add(Port.builder().type(0).port(httpPort).build());
        portList.add(Port.builder().type(0).port(nacosPushPort).build());
        portList.add(Port.builder().type(0).port(sentinelPort).build());
        portList.add(Port.builder().type(0).port(prometheusPort).build());
        portList.add(Port.builder().type(0).port(agentPrometheusPort).build());
        portList.add(Port.builder().type(0).port(jacocoPort).build());


        //获取暴露的端口号(肯定会暴露两个端口,一个是http的一个是rpc的)
        //dubbo 端口暴露
        String exposePort = String.valueOf(dubboPort);
        ExposedPort ep = ExposedPort.parse(exposePort);
        PortBinding pb = PortBinding.parse(exposePort + ":" + exposePort);
        exposedPorts.add(ep);
        portBindings.add(pb);

        //nacos udp push port 端口暴露(这里开放的事udp端口)
        String nacosPushPortStr = String.valueOf(nacosPushPort);
        ExposedPort nep = ExposedPort.parse(nacosPushPortStr + "/udp");
        PortBinding npb = PortBinding.parse(nacosPushPort + ":" + nacosPushPort + "/udp");
        exposedPorts.add(nep);
        portBindings.add(npb);


        //http 端口暴露
        String exposeHttpPort = String.valueOf(httpPort);
        ExposedPort epHttp = ExposedPort.parse(exposeHttpPort);
        PortBinding pbHttp = PortBinding.parse(exposeHttpPort + ":" + exposeHttpPort);
        exposedPorts.add(epHttp);
        portBindings.add(pbHttp);

        //是否支持暴露debug 端口
        if (CommonUtils.supportDebug(req)) {
            //debug port
            String exposeDebugPort = LabelService.ins().getLabelValue(req.getLabels(), LabelService.DEBUG);
            ExposedPort epDebug = ExposedPort.parse(exposeDebugPort);
            PortBinding pbDebug = PortBinding.parse(exposeDebugPort + ":" + exposeDebugPort);
            exposedPorts.add(epDebug);
            portBindings.add(pbDebug);
        }

        //第三方端口(多个)暴露
        if (tpList != null && tpList.size() > 0) {
            log.info("bind ports:{}", tpList);
            tpList.stream().forEach(it1 -> {
                ExposedPort exposedThirdPort = ExposedPort.parse(it1);
                PortBinding thirdPortBinding = PortBinding.parse(it1 + ":" + it1);
                exposedPorts.add(exposedThirdPort);
                portBindings.add(thirdPortBinding);
            });
        }

        //第三方端口暴露
        if (thirdPort != -1) {
            log.info("bind third port:{}", thirdPort);
            String thirdPortStr = String.valueOf(thirdPort);
            ExposedPort exposedThirdPort = ExposedPort.parse(thirdPortStr);
            PortBinding thirdPortBinding = PortBinding.parse(thirdPortStr + ":" + thirdPortStr);
            exposedPorts.add(exposedThirdPort);
            portBindings.add(thirdPortBinding);
        }

        //第四方端口暴露
        if (fourPort != -1) {
            log.info("bind four port:{}", fourPort);
            String fourPortStr = String.valueOf(fourPort);
            ExposedPort exposedThirdPort = ExposedPort.parse(fourPortStr);
            PortBinding thirdPortBinding = PortBinding.parse(fourPortStr + ":" + fourPortStr);
            exposedPorts.add(exposedThirdPort);
            portBindings.add(thirdPortBinding);
        }

        //暴露dubbo gson 的端口
        if (gsonDubboPort != -1) {
            log.info("bind gson dubbo port:{}", gsonDubboPort);
            String gsonPortStr = String.valueOf(gsonDubboPort);
            ExposedPort exposedGsonPort = ExposedPort.parse(gsonPortStr);
            PortBinding gsonPortBinding = PortBinding.parse(gsonPortStr + ":" + gsonPortStr);
            exposedPorts.add(exposedGsonPort);
            portBindings.add(gsonPortBinding);
        }

        //暴露sentnel 的port
        if (sentinelPort != -1) {
            log.info("bind sentinel port:{}", sentinelPort);
            String sentinelPortStr = String.valueOf(sentinelPort);
            ExposedPort exposedSentinelPort = ExposedPort.parse(sentinelPortStr);
            PortBinding sentinelPortBinding = PortBinding.parse(sentinelPortStr + ":" + sentinelPortStr);
            exposedPorts.add(exposedSentinelPort);
            portBindings.add(sentinelPortBinding);
        }

        //暴露prometheus的port
        if (prometheusPort != -1) {
            log.info("bind prometheus port:{}", prometheusPort);
            String promtheusPortStr = String.valueOf(prometheusPort);
            ExposedPort exposedPrometheusPort = ExposedPort.parse(promtheusPortStr);
            PortBinding prometheusPortBinding = PortBinding.parse(promtheusPortStr + ":" + exposedPrometheusPort);
            exposedPorts.add(exposedPrometheusPort);
            portBindings.add(prometheusPortBinding);
        }

        //暴露agent prometheus的port
        if (agentPrometheusPort != -1) {
            log.info("bind agent prometheus port:{}", agentPrometheusPort);
            String agentPromtheusPortStr = String.valueOf(agentPrometheusPort);
            ExposedPort exposedAgentPrometheusPort = ExposedPort.parse(agentPromtheusPortStr);
            PortBinding agentPrometheusPortBinding = PortBinding.parse(agentPromtheusPortStr + ":" + exposedAgentPrometheusPort);
            exposedPorts.add(exposedAgentPrometheusPort);
            portBindings.add(agentPrometheusPortBinding);
        }

        //暴露jacoco的port
        if (jacocoPort != -1) {
            log.info("bind jacoco port:{}", jacocoPort);
            ExposedPort exposedJacocoPort = ExposedPort.parse(jacocoPortStr);
            PortBinding jacocoPortBinding = PortBinding.parse(jacocoPortStr + ":" + exposedJacocoPort);
            exposedPorts.add(exposedJacocoPort);
            portBindings.add(jacocoPortBinding);
        }

    }

    private List<Integer> getPorts() {
        portLock.lock();
        try {
            int v = DeployService.ins().portNum.get();
            if (v > 60000) {
                DeployService.ins().portNum.compareAndSet(v, DeployService.DEFAULT_PORT_NUM);
            }
            int dubboPort = NetUtils.getAvailablePort(DeployService.ins().portNum.getAndIncrement());
            DeployService.ins().portNum.set(dubboPort + 1);
            int httpPort = NetUtils.getAvailablePort(DeployService.ins().portNum.getAndIncrement());
            DeployService.ins().portNum.set(httpPort + 1);
            int nacosUdpPushPort = NetUtils.getAvailableUdpPort(DeployService.ins().portNum.getAndIncrement());
            DeployService.ins().portNum.set(nacosUdpPushPort + 1);
            int sentinelPort = NetUtils.getAvailablePort(DeployService.ins().portNum.getAndIncrement());
            DeployService.ins().portNum.set(sentinelPort + 1);
            int prometheusPort = NetUtils.getAvailablePort(DeployService.ins().portNum.getAndIncrement());
            DeployService.ins().portNum.set(prometheusPort + 1);
            int agentPrometheusPort = NetUtils.getAvailablePort(DeployService.ins().portNum.getAndIncrement());
            DeployService.ins().portNum.set(agentPrometheusPort + 1);
            int jacocoPort = NetUtils.getAvailablePort(DeployService.ins().portNum.getAndIncrement());
            DeployService.ins().portNum.set(jacocoPort + 1);
            return Lists.newArrayList(dubboPort, httpPort, nacosUdpPushPort, sentinelPort, prometheusPort, agentPrometheusPort, jacocoPort);
        } finally {
            portLock.unlock();
        }
    }

    public Integer getPort(String type) {
        portLock.lock();
        try {
            int v = DeployService.ins().portNum.get();
            if (v > 60000) {
                DeployService.ins().portNum.compareAndSet(v, DeployService.DEFAULT_PORT_NUM);
            }
            int port = type.equals("tcp") ? NetUtils.getAvailablePort(DeployService.ins().portNum.getAndIncrement()) : NetUtils.getAvailableUdpPort(DeployService.ins().portNum.getAndIncrement());
            DeployService.ins().portNum.set(port + 1);
            return port;
        } finally {
            portLock.unlock();
        }
    }

}
