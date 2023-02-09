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

import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.docker.DockerLimit;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.po.*;
import com.xiaomi.youpin.tesla.agent.service.DeployService;
import com.xiaomi.youpin.tesla.agent.service.LabelService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2021/11/30
 * 创建sidecar容器 停止之前的老容器
 */
@Slf4j
public class DockerSideCar {

    private static DockerLimit getDockerLimit() {
        return DockerLimit.builder()
                .blkioWeight(500)
                .cpu("0")
                .mem(1024 * 1024 * 1024L)
                .cpuNum(1).useCpus(true).build();
    }

    /**
     * 停止老的sidecar 容器
     *
     * @param context
     * @param req
     * @param deployInfo
     */
    public void stop(DockerContext context, DockerReq req, DeployInfo deployInfo) {
        //stop old container
        Safe.execute(() -> {
            //这里更相信老的(因为dashboard的可能发生变更了)
            log.info("stop side car list:{}", deployInfo.getSidecarContainerIdList());
            deployInfo.getSidecarContainerIdList().stream().forEach(id -> {
                Safe.execute(() -> {
                    log.info("stop old sidecar container:{}", id);
                    YpDockerClient.ins().stopContainer(id, req.getStopTimeout());
                });
            });
        });
        //save new sidecar list
        List<String> sidecarList = context.getSidecarContainerIdList();
        if (null != sidecarList && sidecarList.size() > 0) {
            deployInfo.setSidecarContainerIdList(sidecarList);
        }
    }

    public void start(DockerContext context, DeployInfo deployInfo) {
        //如果有sidecar 则先拉起来sidecar 容器
        List<String> list = context.getSidecarContainerIdList();

        //直接启动(需要取出曾经记录的)
        if (null == list) {
            String name = deployInfo.getName();
            list = DeployService.ins().get(name).getSidecarContainerIdList();
        }
        log.info("start side car list:{}", list);
        if (list != null && list.size() > 0) {
            deployInfo.setSidecarContainerIdList(list);
            list.stream().forEach(it -> {
                log.info("start side car:{}", it);
                YpDockerClient.ins().startContainer(it);
            });
        }
    }

    /**
     * nuke 只需要停掉就可以了,需要删除容器,不需要删除镜像
     *
     * @param req
     * @param deployInfo
     */
    public void nuke(DockerReq req, DeployInfo deployInfo, ExecutorService dockerProcessorPool) {
        String name = deployInfo.getName();
        List<String> list = DeployService.ins().get(name).getSidecarContainerIdList();
        log.info("nuke name:{} list:{}", name, list);
        if (null != list) {
            list.stream().forEach(it -> {
                Safe.execute(() -> {
                    log.info("nuke side car :{}", it);
                    YpDockerClient.ins().stopContainer(it, req.getStopTimeout());
                    //删除容器相对较慢,放到线程池中执行
                    dockerProcessorPool.submit(()->{
                        YpDockerClient.ins().rm(it);
                    });
                });
            });
        }
    }

    /**
     * remove 需要删除容器,不需要删除镜像
     */
    public void remove(DeployInfo deployInfo, ExecutorService dockerProcessorPool) {
        String name = deployInfo.getName();
        List<String> list = DeployService.ins().get(name).getSidecarContainerIdList();
        log.info("remove name:{} list:{}", name, list);
        if (null != list) {
            list.stream().forEach(it -> {
                Safe.execute(() -> {
                    log.info("remove side car :{}", it);
                    //删除容器相对较慢,放到线程池中执行
                    dockerProcessorPool.submit(()->{
                        YpDockerClient.ins().rm(it);
                    });
                });
            });
        }
    }

    /**
     * 拉取sidecar镜像
     *
     * @param req
     */
    public void pull(DockerReq req) {
        SideCarListData list = req.getSideCarListData();
        if (null != list && list.getSideCarDataList() != null) {
            list.getSideCarDataList().stream().forEach(it -> {
                Optional<Image> opt = YpDockerClient.ins().listImages(true).stream().filter(v -> {
                    return v.getRepoTags()[0].equals(it.getImageName());
                }).findAny();
                if (!opt.isPresent()) {
                    Safe.execute(() -> {
                        log.info("pull side car image:{}", it.getImageName());
                        YpDockerClient.ins().pullImage(it.getImageName(), new PullImageResultCallback()).awaitCompletion();
                    });
                }
            });
        }
    }

    /**
     * 创建sidecar 容器
     *
     * @param context
     * @param sw
     * @param req
     * @param deployInfo
     * @param consumer
     */
    public void create(DockerContext context, Stopwatch sw, DockerReq req, DeployInfo deployInfo, Consumer<NotifyMsg> consumer) {
        log.info("start sidecar:{}", req);
        SideCarListData list = req.getSideCarListData();
        DockerLimit dl = getDockerLimit();
        String pidMode = LabelService.ins().getLabelValue(req.getLabels(), LabelService.PID_MODE, "");
        List<String> sidecarList = new ArrayList<>();
        if (null != list) {
            String hostName = NetUtils.getHostName();
            //一个sidecar中的公用一个网桥(是根据envid计算出来的)
            String netWorkMode = NetWorkModeUtils.netWorkMode(req);

            list.getSideCarDataList().stream().forEach(it -> {
                List<ExposedPort> exposedPorts = Lists.newArrayList();
                List<PortBinding> portBindings = Lists.newArrayList();

                it.getPortInfoList().stream().forEach(info -> {
                    if (info.getType().equals("tcp")) {
                        int port = new PortCal().getPort("tcp");
                        //放入计算好的端口
                        info.setPort(port);
                        ExposedPort ep = ExposedPort.parse(String.valueOf(port));
                        exposedPorts.add(ep);
                        String to = String.valueOf(info.getMapPort() != 0 ? info.getMapPort() : port);
                        PortBinding pb = PortBinding.parse(port + ":" + to);
                        portBindings.add(pb);

                        EnvData ed = new EnvData();
                        ed.setName(info.getEnvName());
                        ed.setValue(String.valueOf(port));
                        it.getEnvList().add(ed);

                    }
                });
                List<Bind> binds = Lists.newArrayList();
                String[] envs = it.getEnvList().stream().map(v -> v.getName() + "=" + v.getValue()).toArray(String[]::new);

                //镜像名称
                String name = deployInfo.getName() + "_" + it.getContainerName();

                Optional<Container> opt = YpDockerClient.ins().listAllContainer(true).stream().filter(v -> v.getNames()[0].substring(1, v.getNames()[0].length()).equals(name)).findAny();
                String id = "";
                if (!opt.isPresent()) {
                    id = YpDockerClient.ins().createContainer(
                            it.getImageName(),
                            hostName,
                            netWorkMode,
                            name,
                            dl,
                            exposedPorts,
                            portBindings,
                            pidMode,
                            binds,
                            envs
                    );
                } else {
                    id = opt.get().getId();
                }
                //记录容器id
                it.setContainerId(id);
                sidecarList.add(id);
                log.info("create side car container:{}", id);
            });
        }
        log.info("sidecarList:{}", sidecarList);
        context.setSidecarContainerIdList(sidecarList);
    }

}
