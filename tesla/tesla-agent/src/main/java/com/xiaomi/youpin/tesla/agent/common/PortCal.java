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


        //采用指定的http 端口
        String hp = LabelService.ins().getLabelValue(req.getLabels(), LabelService.HTTP_PORT);
        if (StringUtils.isNotEmpty(hp)) {
            httpPort = Integer.valueOf(hp);
            cports.add(httpPort);
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

        portList.add(Port.builder().type(1).port(dubboPort).build());
        portList.add(Port.builder().type(0).port(httpPort).build());
        portList.add(Port.builder().type(0).port(nacosPushPort).build());


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
            return Lists.newArrayList(dubboPort, httpPort, nacosUdpPushPort);
        } finally {
            portLock.unlock();
        }
    }

}
