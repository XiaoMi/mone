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

package com.xiaomi.youpin.tesla.agent.task;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.google.common.collect.Lists;
import com.xiaomi.data.push.task.Task;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.common.ProcessUtils;
import com.xiaomi.youpin.tesla.agent.po.DeployInfo;
import com.xiaomi.youpin.tesla.agent.service.DeployService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 丁海洋 goodjava@qq.com
 */
@Slf4j
public class CleanTask extends Task {

    /**
     * status过滤器通过status来匹配容器。可以使用created, restarting, running, paused, exited 和 dead来过滤。
     */
    private static final List<String> containerCleanStatus = Lists.newArrayList("exited", "dead");

    private static final int cleanNum = 5;

    public CleanTask() {
        super(() -> {
            try {
                //判断是否是docker机器
                boolean isDocker = StringUtils.isNotEmpty(YpDockerClient.ins().version());

                if (isDocker) {
                    log.info("CleanTask.cleanDocker");
                    //docker虚拟机
                    cleanDockerByContainer();
                    cleanDockerByImage();
                    cleanJarsDownloadByDockerMethod();
                } else {
                    log.info("CleanTask.cleanJar");
                    //物理机
                    cleanJar();
                }
            } catch (Exception ex) {
                log.error("clean error:{}", ex.getMessage());
            }
        }, 1800);
    }

    private static void cleanJar() {
        try {
            List<DeployInfo> deployInfos = DeployService.ins().getPhysicalDeployInfos();
            log.info("CleanTask.cleanJar, deployInfos is: {}", deployInfos);
            if (deployInfos == null || deployInfos.size() == 0) {
                return;
            }
            deployInfos.stream().forEach(it -> {
                if (!StringUtils.isEmpty(it.getName()) && !StringUtils.isEmpty(it.getDeployPath())) {
                    String appName = it.getName().split("-20")[0];
                    String deployPath = it.getDeployPath().endsWith("/") ? it.getDeployPath() : it.getDeployPath() + "/";

                    Pair<Integer, List<String>> pair = ProcessUtils.process(deployPath, "ls -1t | grep " + appName);
                    List<String> names = pair.getValue();
                    if (names.size() > cleanNum) {
                        names.subList(cleanNum, names.size()).stream().filter(it1 -> !it.getName().equals(it1)).forEach(it2 -> {
                                    try {
                                        FileUtils.forceDelete(new File(deployPath + it2));
                                    } catch (Exception e) {
                                        log.error("CleanTask:cleanJar error, {}", e.getMessage());
                                    }
                                }
                        );
                    }
                }
            });
        } catch (Exception e) {
            log.error("CleanTask:cleanJar, error: {}", e.getMessage());
        }

    }

    private static void cleanDockerByContainer() {
        try {
            YpDockerClient ypDockerClient = YpDockerClient.ins();
            List<Container> list = ypDockerClient.listContainers(Lists.newArrayList(), true);
            log.info("CleanTask.cleanDockerByContainer, containers is: {}", list);
            Set<String> appNames = list.stream().map(it -> it.getImage().split("-")[0]).collect(Collectors.toSet());
            Map<String, List<Container>> map = new HashMap<>();
            log.info("CleanTask.cleanDockerByContainer, appNames is: {}", appNames);
            appNames.stream().forEach(it -> {
                List<Container> containers = ypDockerClient.listContainers(Lists.newArrayList(), true, containerCleanStatus, Lists.newArrayList(it));
                if (containers.size() > cleanNum) {
                    map.put(it, containers.subList(cleanNum, containers.size()));
                }
            });

            if (map.size() > 0) {
                map.entrySet().stream().forEach(it -> {
                    List<String> containerIds = it.getValue().stream().map(it1 -> it1.getId()).collect(Collectors.toList());
                    List<String> imageIds = it.getValue().stream().map(it1 -> it1.getImageId()).collect(Collectors.toList());
                    ypDockerClient.rmContainers(containerIds);
                    ypDockerClient.rmImages(imageIds);
                });
            }
        } catch (Exception e) {
            log.error("CleanTask:cleanDockerByContainer, error: {}", e.getMessage());
        }

    }

    private static void cleanDockerByImage() {
        try {
            YpDockerClient ypDockerClient = YpDockerClient.ins();
            List<Image> list = ypDockerClient.listImages(true);
            log.info("CleanTask.cleanDockerByImage, images is: {}", list);
            Set<String> appNames = list.stream().map(it -> {
                if (null != it.getRepoTags() && it.getRepoTags().length > 0) {
                    String[] ss;
                    if (it.getRepoTags()[0].contains("-20")) {
                        ss = it.getRepoTags()[0].split(("-20"));
                    } else {
                        ss = it.getRepoTags()[0].split((":"));
                    }
                    if (ss.length > 0) {
                        return ss[0];
                    }
                }
                return "";
            }).filter(it -> !it.equals("")).collect(Collectors.toSet());
            Map<String, List<Image>> map = new HashMap<>();
            log.info("CleanTask.cleanDockerByImage, appNames is: {}", appNames);
            appNames.stream().forEach(it -> {
                List<Image> images = ypDockerClient.listImages(it);
                if (images.size() > cleanNum) {
                    map.put(it, images.subList(cleanNum, images.size()));
                }
            });

            if (map.size() > 0) {
                map.entrySet().stream().forEach(it -> {
                    List<String> imageIds = it.getValue().stream().map(it1 -> it1.getId()).collect(Collectors.toList());
                    ypDockerClient.rmImages(imageIds);
                });
            }
        } catch (Exception e) {
            log.error("CleanTask:cleanDockerByImage, error: {}", e.getMessage());
        }

    }

    /**
     * 清理docker部署方式下载的jar包
     */
    private static void cleanJarsDownloadByDockerMethod() {
        try {
            List<DeployInfo> deployInfos = DeployService.ins().getDockerDeployInfos();
            log.info("CleanTask.cleanDockerJar, deployInfos is: {}", deployInfos);
            if (deployInfos == null || deployInfos.size() == 0) {
                return;
            }
            deployInfos.stream().forEach(deployInfo -> {
                if (!StringUtils.isEmpty(deployInfo.getDockerServicePath()) && !StringUtils.isEmpty(deployInfo.getDockerJarName())) {

                    String dockerServicePath = deployInfo.getDockerServicePath().endsWith("/") ? deployInfo.getDockerServicePath() : deployInfo.getDockerServicePath() + "/";
                    Pair<Integer, List<String>> pair = ProcessUtils.process(dockerServicePath, "ls -1t");

                    List<String> jarDirNames = pair.getValue();
                    if (jarDirNames.size() > cleanNum) {
                        jarDirNames.subList(cleanNum, jarDirNames.size()).stream().forEach(jarDirName -> {
                                    try {
                                        FileUtils.forceDelete(new File(dockerServicePath + jarDirName));
                                    } catch (Exception e) {
                                        log.error("CleanTask:cleanDockerJar error, {}", e.getMessage());
                                    }
                                }
                        );
                    }
                }
            });
        } catch (Exception e) {
            log.error("CleanTask:cleanDockerJar, error: {}", e.getMessage());
        }

    }
}
