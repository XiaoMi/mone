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

package com.xiaomi.youpin.mischedule.docker;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.mischedule.api.service.bo.DockerBuildTaskStatus;
import com.xiaomi.youpin.mischedule.api.service.bo.DockerParam;
import com.xiaomi.youpin.mischedule.bo.DockerResData;
import com.xiaomi.youpin.mischedule.util.RepoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.Service;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DockerServiceImpl implements DockerService {

    @Value("${git.base.path}")
    private String BASE_GIT_PATH;

    @Value("${docker.registry.url}")
    private String registryUrl;

    @Value("${docker.registry.username}")
    private String registryUsername;

    @NacosValue(value = "${docker_registry_password}", autoRefreshed = true)
    private String registryPassword;

    @Value("${docker.registry.prefix.name}")
    private String prefixName;

    @Override
    public TaskResult build(DockerParam param, TaskContext taskContext) {
        String gitUrl = param.getGitUrl();
        String branch = param.getBranch();
        String gitUser = param.getGitUser();
        String gitToken = param.getGitToken();
        String mqTags = param.getTags();
        String dockerfilePath = param.getDockerfilePath();
        long recordId = param.getId();
        String tag = prefixName + param.getAppName() + ":" + recordId;
        Set<String> tags = new HashSet<>();
        tags.add(tag);

        Stopwatch sw = Stopwatch.createStarted();
        File rootDir = delTemp(BASE_GIT_PATH + param.getAppName() + File.separator + branch);
        Pair<Boolean, String> res = RepoUtil.cloneRepository(gitUrl, branch, rootDir, gitUser, gitToken);
        File buildDir = rootDir;
        if (StringUtils.isNotEmpty(dockerfilePath)) {
            buildDir = new File(rootDir.getAbsoluteFile() + File.separator + dockerfilePath.trim());
        }
        if (MapUtils.isNotEmpty(param.getDockerParams())) {
            renderDockerfile(buildDir,param.getDockerParams());
        }
        DockerResData dockerResData = DockerResData
                .builder()
                .id(recordId)
                .step(1)
                .status(DockerBuildTaskStatus.RUNNING.getId())
                .time(0)
                .imageTags(tag)
                .msg("[info] 开始克隆仓库")
                .build();
        taskContext.notifyMsg(mqTags,  new Gson().toJson(dockerResData));
        if (!res.getKey()) {
            log.warn("DockerServiceImpl#build cloneRepository: {}", res.getValue());
            dockerResData.setMsg("[error] clone error:" + res.getValue());
            dockerResData.setStatus(DockerBuildTaskStatus.FAIL.getId());
            taskContext.notifyMsg(mqTags,  new Gson().toJson(dockerResData));
            return TaskResult.Failure(res.getValue());
        }
        dockerResData.setMsg("[success] 克隆仓库成功");
        dockerResData.setTime(sw.elapsed(TimeUnit.MILLISECONDS));
        taskContext.notifyMsg(mqTags,  new Gson().toJson(dockerResData));
        try {
            dockerResData.setStep(2);
            dockerResData.setMsg("[info] 开始构建镜像");
            dockerResData.setTime(sw.elapsed(TimeUnit.MILLISECONDS));
            taskContext.notifyMsg(mqTags,  new Gson().toJson(dockerResData));

            // 设置帐号信息
            YpDockerClient.ins().setAuthConfig(registryUrl, registryUsername, registryPassword);
            DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerTlsVerify(false);
            DockerClient client = DockerClientBuilder.getInstance(config).build();
            String imageId=client.buildImageCmd(buildDir).withTags(tags).exec(new YPBuildImageResultCallback(taskContext, mqTags, sw, dockerResData)).awaitImageId();
            if (StringUtils.isEmpty(imageId)) {
                dockerResData.setMsg("[error] 镜像构建失败");
                dockerResData.setStatus(DockerBuildTaskStatus.FAIL.getId());
                dockerResData.setTime(sw.elapsed(TimeUnit.MILLISECONDS));
                taskContext.notifyMsg(mqTags,  new Gson().toJson(dockerResData));
                return TaskResult.Failure("镜像id为空");
            } else {
                dockerResData.setMsg("[success] 镜像构建成功");
            }
            dockerResData.setTime(sw.elapsed(TimeUnit.MILLISECONDS));
            taskContext.notifyMsg(mqTags,  new Gson().toJson(dockerResData));

            dockerResData.setStep(3);
            dockerResData.setMsg("[info] 开始上传镜像仓库");
            dockerResData.setTime(sw.elapsed(TimeUnit.MILLISECONDS));
            taskContext.notifyMsg(mqTags,  new Gson().toJson(dockerResData));

            YpDockerClient.ins().pushImage(tag, new YPPushImageResultCallback(taskContext, mqTags, sw, dockerResData)).awaitCompletion();
            dockerResData.setMsg("[success] 上传镜像仓库成功");
            dockerResData.setStatus(DockerBuildTaskStatus.SUCCESS.getId());
            dockerResData.setTime(sw.elapsed(TimeUnit.MILLISECONDS));
            taskContext.notifyMsg(mqTags,  new Gson().toJson(dockerResData));
        } catch (Exception e) {
            log.warn("DockerServiceImpl#build Exception: " + e.getMessage(), e);
            dockerResData.setMsg("[error] 错误信息:" + e.getMessage());
            dockerResData.setStatus(DockerBuildTaskStatus.FAIL.getId());
            dockerResData.setTime(sw.elapsed(TimeUnit.MILLISECONDS));
            taskContext.notifyMsg(mqTags,  new Gson().toJson(dockerResData));
            return TaskResult.Failure(e.getMessage());
        }

        return TaskResult.Success();
    }

    private void renderDockerfile(File buildDir, Map params) {
        String Dockerfile = buildDir.getAbsolutePath() + (buildDir.isDirectory() ? (File.separator + "Dockerfile") : "");
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Dockerfile)));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        } catch (Exception e) {
            log.error("read docker file error: {}", e.toString());
            return;
        }

        String after = renderTemplate(sb.toString(), params);
        try {
            FileWriter fw = new FileWriter(Dockerfile);
            fw.write(after);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            log.error("write docker file error: {}", e.toString());
        }
    }

    private String renderTemplate(String template, Map<String, Object> params) {
        try {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            Template t = gt.getTemplate(template);
            params.forEach((k, v) -> t.binding(k, v));
            String str = t.render();
            return str;
        } catch (Throwable ex) {
            log.error("render docker file error: {}", ex.toString());
        }
        return "";
    }

    private File delTemp(String tempGitPath) {
        File tempGitDir = new File(tempGitPath);
        if (tempGitDir.exists()) {
            try {
                FileUtils.deleteDirectory(tempGitDir);
            } catch (Exception e) {
                log.error("delete temp files error: {}", e.getMessage());
            }
        }
        return tempGitDir;
    }
}
