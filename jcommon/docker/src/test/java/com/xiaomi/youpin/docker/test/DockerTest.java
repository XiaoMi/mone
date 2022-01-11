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

package com.xiaomi.youpin.docker.test;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.docker.DockerLimit;
import com.xiaomi.youpin.docker.UseInfo;
import com.xiaomi.youpin.docker.YpDockerClient;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DockerTest {


    @Test
    public void testImages() {
        DefaultDockerClientConfig.Builder config
                = DefaultDockerClientConfig.createDefaultConfigBuilder();
        DockerClient dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();

        List<Image> images = dockerClient.listImagesCmd().exec();

        images.forEach(it -> {
            System.out.println(it.getId() + ":" + it.getRepoTags()[0]);
        });

    }


    @Test
    public void testInfo() {
        Info info = YpDockerClient.ins().info();
        System.out.println(info);
        System.out.println(info.getNCPU());
        System.out.println(info.getMemTotal());
    }

    @Test
    public void testListContainers() {
        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList("2c6556f6dd77"), true, "mischedule");
        list.forEach(it -> {
            System.out.println(Arrays.toString(it.getNames()) + "  " + it.getId());
        });
    }

    @Test
    public void testListContainers3() {
        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), true);
        Set<String> set = list.stream().map(it -> it.getImage().split("-")[0]).collect(Collectors.toSet());
        System.out.println(set);
    }

    @Test
    public void testListContainers5() {
        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), false, "jaeger");
        Set<String> set = list.stream().map(it ->it.getNames()[0]).collect(Collectors.toSet());
        System.out.println(set);
    }

    @Test
    public void testListContainers4() {
        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), true);
        Set<Container> set = list.stream().filter(it -> it.getImage().startsWith("mischedule")).collect(Collectors.toSet());
        set.forEach(it -> {
            System.out.println(it.getImage());
        });
    }


    @Test
    public void testListContainers2() {
        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), false);
        list.forEach(it -> {
            System.out.println(Arrays.toString(it.getNames()) + "  " + it.getId());
        });
    }


    @Test
    public void testStart() {
        YpDockerClient.ins().startContainer("mischedule-20191101183752883");
    }


    @Test
    public void testOptional() {
        String str = "ttl";
        System.out.println(Optional.ofNullable(str).orElse("str"));
    }

    @Test
    public void testPool() {

        ExecutorService pool = Executors.newFixedThreadPool(10);

        List<Callable<String>> tasks = IntStream.range(0, 3).mapToObj(index -> {
            return new Callable<String>() {

                @Override
                public String call() throws Exception {
                    if (index == 2) {
//                        throw new RuntimeException("error");
                    }
                    return index + "!";
                }
            };

        }).collect(Collectors.toList());

        try {
            List<Future<String>> futureList = pool.invokeAll(tasks);

            futureList.stream().map(it -> {
                try {
                    return it.get();
                } catch (Throwable e) {
                    return e;
                }
            }).filter(it -> it instanceof Throwable).findFirst().ifPresent(it -> {
                Throwable e = (Throwable) it;
                throw new RuntimeException(e.getMessage(), e);
            });

            String res = futureList.get(futureList.size() - 1).get();
            System.out.println(res);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testRemove() {
        DefaultDockerClientConfig.Builder config
                = DefaultDockerClientConfig.createDefaultConfigBuilder();
        DockerClient dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();

        dockerClient.removeContainerCmd("frosty_lumiere").exec();
    }


    @Test
    public void testCreate() throws InterruptedException, IOException {
        DefaultDockerClientConfig.Builder config
                = DefaultDockerClientConfig.createDefaultConfigBuilder();
        DockerClient dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();
        CreateContainerResponse container = dockerClient.createContainerCmd("ubuntu")
                .withCmd("/bin/bash")
                .withName("java_test")
                .exec();

        System.out.println(container.getId());
    }


    @Test
    public void testStop() {
        YpDockerClient.ins().stopContainer("nifty_brown");
    }


    @Test
    public void testContainers() {
        DefaultDockerClientConfig.Builder config
                = DefaultDockerClientConfig.createDefaultConfigBuilder();
        DockerClient dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();

        List<Container> list = dockerClient.listContainersCmd().exec();
        list.forEach(it -> {
            System.out.println(it.getId() + ":" + it.getImage());
        });
    }


    @Test
    public void testBuild() {
        YpDockerClient.ins().build("/Users/zhangzhiyong/docker/Dockerfile", "mischedule:555");
    }


    @Test
    public void createContainer() {
        PortBinding pb = PortBinding.parse("20880:20880");
        ExposedPort ep = ExposedPort.parse("20880");
        Bind bind = new Bind("/tmp/log", new Volume("/tmp/ssddd/logs"));
        String id = YpDockerClient.ins().createContainer("mischedule-20191101183752883", "zzy3",
                DockerLimit.builder().cpu("0,1").blkioWeight(500).
                        //1g
                                mem(1024 * 1024 * 1024L).
                        build(),
                Lists.newArrayList(ep),
                Lists.newArrayList(pb),
                Lists.newArrayList(bind),
                "DOCKER_DUBBO_IP_TO_BIND=");
        System.out.println(id);
    }

    @Test
    public void testListImages() {
        List<Image> list = YpDockerClient.ins().listImages("");
        list.stream().filter(it -> it.getRepoTags()[0].startsWith("mischedule-")).forEach(it -> {
            System.out.println(it.getId() + ":" + it.getRepoTags()[0]);
        });
    }


    @Test
    public void testInspectContainer() {
        InspectContainerResponse res = YpDockerClient.ins().inspectContainer("1edf125bf2c3");
        System.out.println(res.getHostConfig());
    }


    @Test
    public void testUseInfo() {
        UseInfo res = YpDockerClient.ins().containerUseInfo("");
        System.out.println(res);
    }


    @Test
    public void testVersion() {
        System.out.println("version:" + YpDockerClient.ins().version());
    }


    @Test
    public void testExec() throws InterruptedException {

        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), false);
        Optional<Container> o = list.stream().filter(it -> {
            return Arrays.stream(it.getNames()).anyMatch(it2 -> it2.contains("mytocat_mytocat"));
        }).findAny();

        String id = o.map(it -> it.getId()).orElse("");
        System.out.println("----->" + id);

        if (StringUtils.isNotEmpty(id)) {
            String res = YpDockerClient.ins().exec(id, "uptime", 1000);
            System.out.println("---->" + res);
        }

    }


    @Test
    public void testExec2() {
        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), false);
        String str = list.stream().map(it -> {
            String id = it.getId();
            String res = null;
            try {
                res = YpDockerClient.ins().exec(id, "uptime", 1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("---->" + res);
            return res;
        }).collect(Collectors.joining(","));

        System.out.println(str);
    }


    @Test
    public void testPull() throws InterruptedException {
        YpDockerClient.ins().pullImage("127.0.0.1:5000/miserver", new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                super.onNext(item);
                System.out.println(item);
            }
        }).awaitCompletion();
    }

    @Test
    public void testPush() throws InterruptedException {
        YpDockerClient.ins().setAuthConfig("http://xxxx/v2/", "mione", "");
        YpDockerClient.ins()
                .pushImage("xxxx/mione/renqingfu.node-webca52ff57-3717-47d8-9c51-3b4f93417ed9",
                        new PushImageResultCallback())
                .awaitCompletion();
    }


    @Test
    public void testRm2() {
        String name = "zzytest";


        List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), true).stream().filter(it -> it.getImage().startsWith(name)).collect(Collectors.toList());

        System.out.println(list);


        List<String> cids = YpDockerClient.ins().listContainers(Lists.newArrayList(), true).stream().filter(it -> it.getImage().startsWith(name)).map(it -> it.getId()).collect(Collectors.toList());
        cids.stream().forEach(it -> YpDockerClient.ins().rm(it));
        List<String> iids = YpDockerClient.ins().listImages("").stream()
                .filter(it -> it.getRepoTags()[0].startsWith(name)).map(it -> it.getId()).collect(Collectors.toList());
        iids.stream().forEach(it -> YpDockerClient.ins().rmi(it));
    }


    @Test
    public void testStats() {
        Statistics res = YpDockerClient.ins().status("d614539857d5");
        System.out.println(res);
    }


    @Test
    public void testPowerOff() {
        List<String> list = YpDockerClient.ins().powerOff();
        System.out.println(list);
        Assert.assertTrue(list.size() > 0);
        YpDockerClient.ins().powerOn(list);
        Assert.assertTrue(YpDockerClient.ins().listContainers(Lists.newArrayList(), false).size() > 0);
    }
}
