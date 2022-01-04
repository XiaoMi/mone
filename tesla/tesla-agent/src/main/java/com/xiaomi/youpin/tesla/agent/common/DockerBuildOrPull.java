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
import com.xiaomi.youpin.docker.DockerLimit;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.tesla.agent.po.DeployInfo;
import com.xiaomi.youpin.tesla.agent.po.DockerCmd;
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import com.xiaomi.youpin.tesla.agent.po.NotifyMsg;
import com.xiaomi.youpin.tesla.agent.service.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public static final String JDK_11_HOME = "jdk-11.0.1";

    private final String kcRegisterUrl = Config.ins().get("keycenter_register_url", "");

    private final String opentelemetryJavaagent = Config.ins().get("opentelemetry_javaagent", "opentelemetry-javaagent-all-0.0.1.jar");

    enum ImageNameEnum {
        JDK_11_IMAGE("xxxx/mixiao/mionedocker:java11"),
        KC_IMAGE("xxxx/mixiao/miserver-kc:0.0.1"),
        MI_SERVER("miserver"),
        ;
        private String imageName;

        ImageNameEnum(String imageName) {
            this.imageName = imageName;
        }

        public String getImageName() {
            return imageName;
        }
    }


    public void buildOrPull(Stopwatch sw, DockerReq req, DeployInfo deployInfo, Consumer<NotifyMsg> consumer) throws InterruptedException {
        log.info("build begin");
        new DockerSideCar().pull(req);

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
            long time = sw.elapsed(TimeUnit.MILLISECONDS);
            deployInfo.setStep(DeployInfo.DockerStep.build.ordinal());
            deployInfo.setDockerServicePath(req.getServicePath());
            deployInfo.setDockerJarName(req.getJarName());
            consumer.accept(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 0, "download", "[INFO] download file finish size:" + fileSize + "kb(" + time + "ms)\n", time, req.getId(), req.getAttachments()));
            String dockerFile = saveDockerFile(req, filePath + "Dockerfile");
            saveInitSH(req, filePath + "init.sh");
            saveMioneCURL(filePath + "mione-curl");
            try {
                YpDockerClient.ins().build(dockerFile, req.getImageName());
            } catch (Throwable ex) {
                log.info("build error:{}", ex);
            }
            log.info("build success name:{}", req.getImageName());
            time = sw.elapsed(TimeUnit.MILLISECONDS);
            consumer.accept(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 1, "build", "[INFO] build image finish(" + time + "ms)" + "\n", time, req.getId(), req.getAttachments()));
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
            String tml = TemplateUtils.getTemplate("docker_file_init.tml");
            if (JDK_11_HOME.equals(req.getJavaHome()) && StringUtils.isEmpty(imageName)) {
                imageName = ImageNameEnum.JDK_11_IMAGE.getImageName();
                tml = TemplateUtils.getTemplate("docker_file_jdk11.tml");
            }

            //私有keycenter sid
            String kcPrivateSid = LabelService.ins().getLabelValue(req.getLabels(), LabelService.KEYCENTER_PRIVATE_SID, "false");
            if ("true".equals(kcPrivateSid)) {
                imageName = ImageNameEnum.KC_IMAGE.getImageName();
                tml = TemplateUtils.getTemplate("docker_file_kc.tml");
            }
            log.info("image_name:{}", imageName);

            Map<String, Object> m = Maps.newHashMap();

            // 设置基础镜像,后面基础镜像选择逻辑放到gwdash处理
            if (StringUtils.isEmpty(imageName)
                    && null != req.getAttachments()
                    && StringUtils.isNotEmpty(req.getAttachments().get("baseImage"))) {
                imageName = req.getAttachments().get("baseImage");
            }

            // 用户设定了jvm参数
            if (supportJvmParams(req)) {
                int inedex = tml.indexOf("ENTRYPOINT");
                tml = tml.substring(0, inedex) + getEntryPointInit(req);
            } else {
                m.put("java_heap", getHeapSize(req.getHeapSize(), req.getMemLimit(), req.getMaxDirectMemorySize()));
                m.put("mdsize", req.getMaxDirectMemorySize());
            }

            // javaAgent参数
            log.info("saveDockerFile javaAgent: {}, {}", req, tml);
            String javaAgent = "";
            if (null != req.getAttachments()
                    && StringUtils.isNotEmpty((javaAgent = req.getAttachments().get("javaAgent")))) {
                tml = tml.replace(" -jar ", " " + javaAgent + " -jar ");
                log.info("saveDockerFile javaAgent: {}", tml);
            }

            // docker环境变量
            if (null != req.getAttachments()
                    && StringUtils.isNotEmpty(req.getAttachments().get("env_var"))) {
                m.put("env_var", req.getAttachments().get("env_var"));
            } else {
                m.put("env_var", "");
            }
            // kc私有sid
            if (null != req.getAttachments()
                    && StringUtils.isNotEmpty(req.getAttachments().get("kc_deploy_token"))) {
                m.put("kc_register_url", kcRegisterUrl);
                m.put("kc_deploy_token", req.getAttachments().get("kc_deploy_token"));
            }

            String cpusLabel = LabelService.ins().getLabelValue(req.getLabels(), LabelService.CPUS, "false");

            DockerLimit dl = DockerCreate.getDockerLimit(req, cpusLabel);
            if (dl.isUseCpus()) {
                int num = Math.round(dl.getCpuNum());
                m.put("processor_count", num == 0 ? 1 : num);
            } else {
                m.put("processor_count", dl.getCpu().split(",").length);
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

    public void saveInitSH(DockerReq req, String file) {
        log.info("save init sh:{}", req.getLabels());
        try {
            String v = LabelService.ins().getLabelValue(req.getLabels(), "script", "false");
            log.info("v:{}", v);
            if (v.equals("true")) {
                String initTmp = TemplateUtils.getTemplate("download.sh");
                Map<String, Object> m = new HashMap<>();
                m.put("opentelemetry_javaagent", opentelemetryJavaagent);
                String initStr = TemplateUtils.renderTemplate(initTmp, m);
                log.info("--->init.sh:{}", initStr);
                Files.write(Paths.get(file), initStr.getBytes());
                return;
            }
            /**
             * 用户配置了docker file
             */
            if (StringUtils.isNotEmpty(req.getDockerFileContent())) {
                return;
            }
            List<String> dnsList = getDnsList();
            Map<String, Object> m = new HashMap<>(1);
            m.put("nameservers", dnsList);
            String tml = TemplateUtils.getTemplate("init_sh.tml");
            String initStr = TemplateUtils.renderTemplate(tml, m);
            log.info("init.sh:{}", initStr);
            Files.write(Paths.get(file), initStr.getBytes());
        } catch (IOException e) {
            log.error("error:{}", e.getMessage());
        }
    }

    public void saveMioneCURL(String file) {
        log.info("save mione-curl");
        InputStream is = TemplateUtils.class.getClassLoader().getResourceAsStream("bin/mione-curl.bin");
        try {
            OutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(is, outputStream);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getDnsList() {
        List<String> dnsList = Lists.newArrayList();
        String dns = Config.ins().get("dns", "");
        if (StringUtils.isNotEmpty(dns)) {
            String[] array = dns.split(",");
            dnsList.addAll(Arrays.asList(array));
        }
        return dnsList;
    }

    private String getHeapSize(String heapSize, int memLimit, int mdSize) {
        int hs = Integer.valueOf(heapSize.trim());
        //256 MaxMetaspaceSize  300 线程数量(1个线程默认1m) 100 是堆外内存  memLimit 给jvm留下的
        memLimit = Math.max(memLimit, 512);
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

    private String getEntryPointInit(DockerReq req) {
        List<String> entryPoint = Lists.newArrayList("bash /root/init.sh >${log_path}/.mione.shell.log 2>&1 ; exec", "java", "-jar", "-Dkeycenter.agent.host=172.17.0.1", "-XX:ActiveProcessorCount=${processor_count}");
        if (CommonUtils.supportDebug(req)) {
            String debugPort = LabelService.ins().getLabelValue(req.getLabels(), LabelService.DEBUG);
            entryPoint.add(LabelService.DEBUG_ARGUMENTS);
            entryPoint.add(LabelService.DEBUG_ARGUMENTS2 + debugPort);
        }
        entryPoint.addAll((Arrays.stream(req.getJvmParams().split("\\s+")).collect(Collectors.toList())));
        entryPoint.addAll(Stream.of(req.getServicePath() + req.getJarName()).collect(Collectors.toList()));
        String entrypointStr = new StringBuilder().append("ENTRYPOINT").append(" [\"bash\",\"-c\",\"")
                .append(String.join(" ", entryPoint)).append("\"]\n")
                .toString();
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
