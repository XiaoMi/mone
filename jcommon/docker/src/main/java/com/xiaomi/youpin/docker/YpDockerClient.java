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

package com.xiaomi.youpin.docker;

import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.InvocationBuilder;
import com.github.dockerjava.core.command.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class YpDockerClient {

    private com.github.dockerjava.api.DockerClient dockerClient;
    private AuthConfig authConfig;

    private YpDockerClient() {
        log.info("docker client version: {}", new Version());
        DefaultDockerClientConfig.Builder config
                = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerTlsVerify(false);
        dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();
    }

    private static final class LazyHolder {
        private static final YpDockerClient ins = new YpDockerClient();
    }

    public void setAuthConfig(String registryAddress, String username, String password) {
        authConfig = new AuthConfig().withRegistryAddress(registryAddress).withUsername(username).withPassword(password);
    }

    public PushImageResultCallback pushImage(String name, PushImageResultCallback callback) {
        return dockerClient.pushImageCmd(name).withAuthConfig(authConfig).exec(callback);
    }

    public PullImageResultCallback pullImage(String name, PullImageResultCallback callback) {
        return dockerClient.pullImageCmd(name).exec(callback);
    }

    public BuildImageResultCallback buildImage(File baseDir, Set<String> tags, BuildImageResultCallback callback) {
        return dockerClient
                .buildImageCmd()
                .withDockerfile(new File(baseDir.getAbsolutePath() + File.separator + "Dockerfile"))
                .withBaseDirectory(baseDir)
                .withTags(tags)
                .exec(callback);
    }

    public static final YpDockerClient ins() {
        return LazyHolder.ins;
    }

    public Info info() {
        Info info = dockerClient.infoCmd().exec();
        return info;
    }

    public List<Container> listContainers(List<String> ids, boolean showAll, String... names) {
        ListContainersCmd cmd = dockerClient.listContainersCmd();
        if (ids.size() > 0) {
            cmd = cmd.withIdFilter(ids);
        }
        List<Container> list = cmd
                .withNameFilter(Lists.newArrayList(names))
                .withShowAll(showAll)
                .exec();
        return list;
    }

    public List<Container> listAllContainer(boolean showAll) {
        return dockerClient.listContainersCmd().withShowAll(showAll).exec();
    }

    public String logContainerCmd(String containerId, Integer tailNum) throws InterruptedException {
        StringBuffer sb = new StringBuffer();
        dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withTail(tailNum)
                .exec(new LogContainerResultCallback() {
                    @Override
                    public void onNext(Frame item) {
                        sb.append(item.toString() + "\n");
                    }
                }).awaitCompletion();
        return sb.toString();
    }

    public CreateNetworkResponse createNetwork(String name) {
        CreateNetworkResponse createNetworkResponse = dockerClient
                .createNetworkCmd()
                .withName(name)
                .withCheckDuplicate(true)
                .exec();
        return createNetworkResponse;
    }

    public List<Network> listNetwork(String name) {
        return dockerClient.listNetworksCmd().withNameFilter(name).exec();
    }

    public void connectToNetwork(String containerId, String networkId) {
        dockerClient.connectToNetworkCmd().withContainerId(containerId).withNetworkId(networkId).exec();
    }

    public void disconnectFromNetworkCmd(String containerId, String networkId) {
        dockerClient.disconnectFromNetworkCmd().withContainerId(containerId).withNetworkId(networkId).exec();
    }

    public List<Container> listContainers(List<String> ids, boolean showAll, List<String> status, List<String> name) {
        ListContainersCmd cmd = dockerClient.listContainersCmd();
        if (ids.size() > 0) {
            cmd = cmd.withIdFilter(ids);
        }
        List<Container> list = cmd
                .withNameFilter(name)
                .withStatusFilter(status)
                .withShowAll(showAll)
                .exec();
        return list;
    }


    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }


    public List<Image> listImages(String name) {
        return dockerClient.listImagesCmd().withImageNameFilter(name).exec();
    }

    public List<Image> listImages(boolean showAll) {
        return dockerClient.listImagesCmd().withShowAll(showAll).exec();
    }


    public void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
    }


    /**
     * 创建容器
     * <p>
     * 支持挂载磁盘/
     * 支持端口暴露
     * 支持参数设定
     *
     * @param image
     * @param name
     */
    public String createContainer(String image, String name, DockerLimit limit, List<ExposedPort> exposedPorts, List<PortBinding> portBindings, List<Bind> binds, String... env) {
        return dockerClient.createContainerCmd(image)
                //使用几核
                .withCpusetCpus(limit.getCpu())
                //使用多少内存
                .withMemory(limit.getMem())
                //io权重
                .withBlkioWeight(limit.getBlkioWeight())
                .withName(name)
                .withRestartPolicy(RestartPolicy.onFailureRestart(3))
                .withExposedPorts(exposedPorts)
                .withPortBindings(portBindings)
                .withBinds(binds)
                .withEnv(env).exec().getId();
    }

    public String createContainer(String image, String hostName, String name, DockerLimit limit, List<ExposedPort> exposedPorts, List<PortBinding> portBindings, List<Bind> binds, String... env) {
        CreateContainerCmd cmd = dockerClient.createContainerCmd(image)
                //使用几核
                .withCpusetCpus(limit.getCpu());
        if (limit.getMem() > 0) {
            //使用多少内存
            cmd.withMemory(limit.getMem());
        }

        //io权重
        return cmd.withBlkioWeight(limit.getBlkioWeight())
                .withRestartPolicy(RestartPolicy.onFailureRestart(3))
                .withName(name)
                .withExposedPorts(exposedPorts)
                .withPortBindings(portBindings)
                .withBinds(binds)
                .withHostName(hostName)
                .withEnv(env).exec().getId();
    }

    /**
     * 创建容器
     * @param image
     * @param hostName  bridge host
     * @param netWorkMode
     * @param name
     * @param limit
     * @param exposedPorts
     * @param portBindings
     * @param binds
     * @param env
     * @return
     */
    public String createContainer(String image, String hostName,String netWorkMode, String name, DockerLimit limit, List<ExposedPort> exposedPorts, List<PortBinding> portBindings, List<Bind> binds, String... env) {
        CreateContainerCmd cmd = dockerClient.createContainerCmd(image)
                //使用几核
                .withCpusetCpus(limit.getCpu());
        if (limit.getMem() > 0) {
            //使用多少内存
            cmd.withMemory(limit.getMem());
        }

        //io权重
        return cmd.withBlkioWeight(limit.getBlkioWeight())
                .withRestartPolicy(RestartPolicy.onFailureRestart(3))
                .withName(name)
                .withExposedPorts(exposedPorts)
                .withPortBindings(portBindings)
                .withBinds(binds)
                .withHostName(hostName)
                //bridge host
                .withNetworkMode(netWorkMode)
                .withEnv(env).exec().getId();
    }

    /**
     * 创建容器
     * @param image
     * @param hostName  bridge host
     * @param netWorkMode
     * @param name
     * @param limit
     * @param exposedPorts
     * @param portBindings
     * @param binds
     * @param env
     * @return
     */
    public String createContainer(String image, String hostName,String netWorkMode, String name, DockerLimit limit, List<ExposedPort> exposedPorts, List<PortBinding> portBindings, List<Bind> binds, String... env) {
        CreateContainerCmd cmd = dockerClient.createContainerCmd(image)
                //使用几核
                .withCpusetCpus(limit.getCpu());
        if (limit.getMem() > 0) {
            //使用多少内存
            cmd.withMemory(limit.getMem());
        }

        //io权重
        return cmd.withBlkioWeight(limit.getBlkioWeight())
                .withRestartPolicy(RestartPolicy.onFailureRestart(3))
                .withName(name)
                .withExposedPorts(exposedPorts)
                .withPortBindings(portBindings)
                .withBinds(binds)
                .withHostName(hostName)
                //bridge host
                .withNetworkMode(netWorkMode)
                .withEnv(env).exec().getId();
    }

    /**
     * build 镜像
     *
     * @param file
     * @param tag
     */
    public void build(String file, String tag) {
        dockerClient.buildImageCmd(new File(file))
                .withTag(tag)
                .exec(new BuildImageResultCallback() {
                    @Override
                    public void onNext(BuildResponseItem item) {
                        log.info("item:{}", item.getImageId());
                    }
                }).awaitImageId();
    }

    public String exec(String containerId, String cmd, long timeout) throws InterruptedException {
        ExecCreateCmdResponse res = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(cmd).exec();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            dockerClient.execStartCmd(res.getId()).exec(
                    new ExecStartResultCallback(os, os)).awaitCompletion(timeout, TimeUnit.MILLISECONDS);

            return new String(os.toByteArray());

        } finally {
            try {
                os.close();
            } catch (IOException e) {
            }
        }
    }


    public InspectContainerResponse inspectContainer(String containerId) {
        InspectContainerResponse res = dockerClient.inspectContainerCmd(containerId).exec();
        return res;
    }


    private String getAppName(String imageName) {
        return imageName.split("-20")[0];
    }

    /**
     * 获取容器 cpu 和 内存的使用情况
     * cpu  是个数
     * 内存  是 使用大小
     *
     * @return
     */
    public UseInfo containerUseInfo(String ip) {
        final List<Container> list = this.listContainers(Lists.newArrayList(), false);
        //app详情
        List<AppInfo> appList = Lists.newLinkedList();
        Safe.run(() -> appList.addAll(list.stream().filter(it -> {
            Map<String, String> labels = it.getLabels();
            log.debug("containerUseInfo labels:{}", labels);
            if (null != labels && labels.size() > 0 && labels.containsKey("ENV_ID")) {
                return true;
            }
            return false;
        }).map(it -> {
            Map<String, String> labels = it.getLabels();
            AppInfo info = new AppInfo();
            info.setEnvId(labels.get("ENV_ID"));
            info.setAppName(getAppName(it.getImage()));
            info.setIp(ip);
            return info;
        }).collect(Collectors.toList())));

        log.info("docker app list size:{}", appList.size());

        List<String> ids = list.stream().map(it -> it.getId()).collect(Collectors.toList());
        Optional<UseInfo> res = ids.stream().map(it -> {
                    try {
                        InspectContainerResponse info = this.inspectContainer(it);
                        UseInfo ui = UseInfo.builder().useCpuNum(getCpuNum(info.getHostConfig().getCpusetCpus()))
                                .useMemNum(info.getHostConfig().getMemory())
                                .build();
                        return ui;
                    } catch (Throwable ex) {
                        log.warn("error:{}", ex.getMessage());
                        return new UseInfo(0, 0, new HashSet<>(), Lists.newArrayList());
                    }
                }
        ).reduce((a, b) -> UseInfo.builder().useCpuNum(a.getUseCpuNum() + b.getUseCpuNum()).build());

        Set<String> appNames = list.stream().map(it -> getAppName(it.getImage())).collect(Collectors.toSet());
        UseInfo info = null;
        if (res.isPresent()) {
            info = res.get();
            if (null != info) {
                info.setApps(appNames);
            }
        } else {
            info = UseInfo.builder().apps(new HashSet<>()).build();
        }

        info.setAppInfos(appList);
        return info;
    }

    private int getCpuNum(String cpusetCpus) {
        return cpusetCpus.split(",").length;
    }


    public String version() {
        try {
            com.github.dockerjava.api.model.Version version = dockerClient.versionCmd().exec();
            return version.getVersion();
        } catch (Throwable ex) {
            log.warn("error:{}", ex.getMessage());
        }
        return "";
    }


    public void rm(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }

    public void rmContainers(List<String> containerIds) {
        if (containerIds == null || containerIds.size() == 0) {
            return;
        }
        containerIds.parallelStream().forEach(it -> {
            try {
                rm(it);
            } catch (Exception e) {
                log.error("YpDockerClient.rmContainers, error: {}", e.getMessage());
            }
        });
    }

    public void rmi(String imageId) {
        dockerClient.removeImageCmd(imageId).exec();
    }

    public void rmImages(List<String> imageIds) {
        if (imageIds == null || imageIds.size() == 0) {
            return;
        }
        imageIds.parallelStream().forEach(it -> {
            try {
                rmi(it);
            } catch (Exception e) {
                log.error("YpDockerClient.rmImages, error: {}", e.getMessage());
            }
        });
    }

    //https://cloud.tencent.com/developer/article/1096453       i
    //https://docs.docker.com/engine/reference/commandline/stats/
    public Statistics status(String containerId) {
        InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
        this.dockerClient.statsCmd(containerId).exec(callback);
        Statistics res = callback.awaitResult();
        return res;
    }

    /**
     * 停机(关闭所有容器)
     *
     * @return
     */
    public List<String> powerOff() {
        return this.listContainers(Lists.newArrayList(), false).stream().map(it -> {
            Safe.run(() -> this.stopContainer(it.getId()));
            return it.getId();
        }).collect(Collectors.toList());
    }

    /**
     * 开机(启动上次被关闭的所有容器)
     *
     * @param containerIdList
     */
    public void powerOn(List<String> containerIdList) {
        containerIdList.stream().forEach(it -> Safe.run(() -> {
            this.startContainer(it);
        }));
    }

}
