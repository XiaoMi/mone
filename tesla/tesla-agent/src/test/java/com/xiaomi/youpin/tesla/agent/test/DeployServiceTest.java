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

package com.xiaomi.youpin.tesla.agent.test;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.common.ProcessUtils;
import com.xiaomi.youpin.tesla.agent.service.DeployService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeployServiceTest {


    @Test
    public void testCpuNum() {
        System.out.println(DeployService.ins().getCpuNum());
    }


    @Test
    public void testMem() {
        System.out.println(DeployService.ins().getMem() / 1024 / 1024 / 1024);
    }


    @Test
    public void testAvailableCpuNum() {
        System.out.println(DeployService.ins().getAvailableCpuNum());
    }

    @Test
    public void testJar() {
        Pair<Integer, List<String>> pair = ProcessUtils.process("/Users/dingpei/logs/nacos", "ls -1t | grep naming");

        List<String> names = pair.getValue();
        if (names.size() > 1) {
            names.subList(1, names.size()).stream().filter(it1 -> !"naming.log.4".equals(it1)).forEach(it2 -> {
                try {
                    FileUtils.forceDelete(new File("/Users/dingpei/logs/nacos/" + it2));
                } catch (Exception e) {

                }
            });
        }
    }

    @Test
    public void testDocker() {
        try {
            YpDockerClient ypDockerClient = YpDockerClient.ins();
            Map<String, List<Container>> map = new HashMap<>();
            List<String> appNames = Lists.newArrayList("silly");
            appNames.stream().forEach(it -> {
                List<Container> containers = ypDockerClient.listContainers(Lists.newArrayList(), true, Lists.newArrayList("exited", "dead"), Lists.newArrayList(it));
                if (containers.size() > 2) {
                    map.put(it, containers.subList(2, containers.size()));
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

        }
    }

    @Test
    public void testDockerByImage() {
        try {
            YpDockerClient ypDockerClient = YpDockerClient.ins();
            List<Image> list = ypDockerClient.listImages(true);

            Set<String> appNames = list.stream().map(it -> it.getRepoTags()[0].split("-20")[0]).collect(Collectors.toSet());
            Map<String, List<Image>> map = new HashMap<>();

            appNames.stream().forEach(it -> {
                List<Image> images = ypDockerClient.listImages(it);
                if (images.size() > 2) {
                    map.put(it, images.subList(2, images.size()));
                }
            });

            if (map.size() > 0) {
                map.entrySet().stream().forEach(it -> {
                    List<String> imageIds = it.getValue().stream().map(it1 -> it1.getId()).collect(Collectors.toList());
                    ypDockerClient.rmImages(imageIds);
                });
            }
        } catch (Exception e) {

        }
    }

}
