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

import com.github.dockerjava.core.command.PullImageResultCallback;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.po.DeployInfo;
import com.xiaomi.youpin.tesla.agent.po.DockerCmd;
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import com.xiaomi.youpin.tesla.agent.po.NotifyMsg;
import com.xiaomi.youpin.tesla.agent.service.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 * @date 2020/9/1
 */
@Slf4j
public class DockerBuildOrPull {

    public void buildOrPull(Stopwatch sw, DockerReq req, DeployInfo deployInfo, Consumer<NotifyMsg> consumer) throws InterruptedException {
        log.info("build begin");
        if (YpDockerClient.ins().listImages(req.getImageName()).size() > 0) {
            log.info("don't need build image:{}", req.getImageName());
            req.setCmd(DockerCmd.create.name());
            return;
        }
        if (req.isPull()) {
            //pull
            log.info("pull begin name:{}", req.getImageName());
            YpDockerClient.ins().pullImage(req.getImageName(), new PullImageResultCallback()).awaitCompletion();
            consumer.accept(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 1, "pull", "[INFO] pull image finish" + "\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));
            req.setCmd(DockerCmd.create.name());
            log.info("pull success name:{}", req.getImageName());
        } else {
            //build
            log.info("build begin name:{}", req.getJarName());
            String filePath = req.getServicePath() + UUID.randomUUID().toString() + File.separator;
            CommonUtils.mkdir(filePath);
            int fileSize = CommonUtils.downloadJarFile(filePath, req.getDownloadKey(), req.getJarName());
            deployInfo.setStep(DeployInfo.DockerStep.build.ordinal());
            consumer.accept(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 0, "download", "[INFO] download file finish size:" + fileSize + "\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));
            String dockerFile = saveDockerFile(req, filePath + "Dockerfile");
            try {
                YpDockerClient.ins().build(dockerFile, req.getImageName());
            } catch (Throwable ex) {
                log.info("build error:{}", ex.getMessage());
                //ignore
            }
            log.info("build success name:{}", req.getImageName());
            consumer.accept(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 1, "build", "[INFO] build image finish" + "\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));
            req.setCmd(DockerCmd.create.name());
        }
    }


    public String saveDockerFile(DockerReq req, String file) {
        try {
            /**
             * 用户配置了docker file
             */
            if (StringUtils.isNotEmpty(req.getDockerFileContent())) {
                Files.write(Paths.get(file), req.getDockerFileContent().getBytes());
                return file;
            }

            String imageName = LabelService.ins().getLabelValue(req.getLabels(), LabelService.IMAGE_NAME);

            if (StringUtils.isEmpty(imageName)) {
                imageName = "miserver";
            }

            log.info("image_name:{}", imageName);

            String tml = TemplateUtils.getTemplate("docker_file.tml");
            Map<String, Object> m = Maps.newHashMap();

            //用户设定了jvm参数
            if (supportJvmParams(req)) {
                int inedex = tml.indexOf("ENTRYPOINT");
                tml = tml.substring(0, inedex) + "${entrypoint}";
                m.put("entrypoint", getEntryPoint(req));
            } else {
                m.put("java_heap", getHeapSize(req.getHeapSize(), req.getMemLimit(), req.getMaxDirectMemorySize()));
                m.put("mdsize", req.getMaxDirectMemorySize());
            }

            m.put("image_name", imageName);
            m.put("project_path", req.getServicePath());
            m.put("log_path", req.getLogPath());
            m.put("log_time", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            m.put("jar_name", req.getJarName());
            m.put("java_heap", getHeapSize(req.getHeapSize(), req.getMemLimit(), req.getMaxDirectMemorySize()));
            m.put("mdsize", req.getMaxDirectMemorySize());
            m.put("mms", req.getMms());
            m.put("env_id", getEnvId(req.getEnvId()));
            m.put("project_id", getProjectId(req.getProjectId()));
            String dockerFileStr = TemplateUtils.renderTemplate(tml, m);
            log.info("dockerFile:{}", dockerFileStr);
            Files.write(Paths.get(file), dockerFileStr.getBytes());

            String python = LabelService.ins().getLabelValue(req.getLabels(), LabelService.PYTHON);
            if (StringUtils.isNotEmpty(python)) {
                supportPython(file);
            }

            return file;
        } catch (IOException e) {
            log.error("error:{}", e.getMessage());
        }
        return null;
    }


    private String getHeapSize(String heapSize, int memLimit, int mdSize) {
        int hs = Integer.valueOf(heapSize.trim());
        //256 MaxMetaspaceSize  300 线程数量(1个线程默认1m) 100 是堆外内存
        int size = hs - 256 - 300 - mdSize - memLimit;
        log.info("heap size:{}", size);
        return String.valueOf(size);
    }

    private boolean supportJvmParams(DockerReq req) {
        return StringUtils.isNotEmpty(req.getJvmParams());
    }


    /**
     * 可以认为就是修改入口那一行
     *
     * @param req
     * @return
     */
    private String getEntryPoint(DockerReq req) {
        List<String> entryPoint = Lists.newArrayList("java", "-jar", "-Dkeycenter.agent.host=172.17.0.1");
        if (CommonUtils.supportDebug(req)) {
            String debugPort = LabelService.ins().getLabelValue(req.getLabels(), LabelService.DEBUG);
            entryPoint.add(LabelService.DEBUG_ARGUMENTS);
            entryPoint.add(LabelService.DEBUG_ARGUMENTS2 + debugPort);
        }
        entryPoint.addAll((Arrays.stream(req.getJvmParams().split("\\s+")).collect(Collectors.toList())));
        entryPoint.addAll(Stream.of(req.getServicePath() + req.getJarName()).collect(Collectors.toList()));
        String entrypointStr = new StringBuilder().append("ENTRYPOINT").append(" ").append("[")
                .append(entryPoint.stream().map(it -> "\"" + it + "\"").collect(Collectors.joining(",")))
                .append("]").toString();
        log.info("entrypoint:{}", entrypointStr);
        return entrypointStr;
    }


    private String getEnvId(Long envId) {
        return String.valueOf(Optional.ofNullable(envId).orElse(0L));
    }

    private String getProjectId(Long projectId) {
        return String.valueOf(Optional.ofNullable(projectId).orElse(0L));
    }


    private void supportPython(String file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(file));
        lines.add(5, "RUN apt-get install -y python3");
        Files.write(Paths.get(file), lines);
    }


}
