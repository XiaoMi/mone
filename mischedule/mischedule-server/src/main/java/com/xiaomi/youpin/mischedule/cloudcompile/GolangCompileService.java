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

package com.xiaomi.youpin.mischedule.cloudcompile;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.impl.shell.ProcessUtils;
import com.xiaomi.youpin.ks3.KsyunService;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileTaskStatus;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileTaskStep;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * <p>
 * golang 编译服务
 */
@Service
@Slf4j
public class GolangCompileService extends AbstractCompile {

    private KsyunService ksyunService;

    @Value("${go.path}")
    protected String goPath;

    private String goBinPath = "go";


    @PostConstruct
    private void init() {
        ksyunService = new KsyunService();
        ksyunService.setAccessKeyID(accesskey);
        ksyunService.setAccessKeySecret(accessSecret);
        ksyunService.init();
    }


    public void compile(final CompileParam request) {
        log.info("golang compile:{}", new Gson().toJson(request));

        Stopwatch sw = Stopwatch.createStarted();
        long id = request.getId();
        String gitUrl = request.getGitUrl();
        String branch = request.getBranch();
        String tags = request.getTags();
        String gitName = request.getGitName();
        String gitToken = request.getGitToken();
        String ks3Key = getKs3Key(request, request.getGitUrl(), request.getBranch());
        String buildPath = request.getBuildPath();
        String binName = request.getJarPath();

        // 配置参数检查
        if (!check(tags, id, gitUrl, branch, gitName, gitToken, sw)) {
            return;
        }


        // clone仓库
        String projectName = getProjectName(gitUrl);
        String tempGitPath = BASE_GIT_PATH + projectName + File.separator + branch;
        File tempGitDir = delTemp(tempGitPath);

        if (!clone(tags, id, sw, gitUrl, branch, gitName, gitToken, tempGitDir)) {
            delTemp(tempGitPath);
            return;
        }

        boolean res = build(tags, id, sw, gitUrl, branch, tempGitDir, buildPath, binName);
        if (!res) {
            return;
        }


        // 检索&上传打包文件
        if (StringUtils.isNotEmpty(buildPath)) {
            tempGitDir = new File(tempGitPath + "/" + buildPath);
        }

        upload(tags, id, sw, tempGitDir, ks3Key, binName);
        delTemp(tempGitPath);
        log.info("totally finished {} time:{}", id, sw.elapsed(TimeUnit.MICROSECONDS));
    }


    private boolean build(String tags, long id, Stopwatch sw, String gitUrl, String branch, File tempGitDir, String buildPath, String binName) {
        sendMessage(tags, id, CompileTaskStep.build, CompileTaskStatus.running, sw, null, null,
                "[INFO] 开始打包" + System.lineSeparator());
        boolean isPass = true;
        log.info("build begin");
        Pair<Boolean, String> result = build(tempGitDir.getPath(), buildPath, binName);

        if (!result.getKey()) {
            log.error("build error {} {} {}", gitUrl, branch, result);
            isPass = false;
            sendMessage(tags, id, CompileTaskStep.build, CompileTaskStatus.failure, sw, "", null,
                    "[ERROR] 构建失败" + System.lineSeparator());
        } else {
            log.info("build successfully {} {}", gitUrl, branch);
            sendMessage(tags, id, CompileTaskStep.build, CompileTaskStatus.running, sw, "", null,
                    "[SUCCESS] 构建成功" + System.lineSeparator());
        }
        return isPass;
    }


    private Pair<Boolean, String> build(String path, String buildPath, String binName) {
        if (StringUtils.isNotEmpty(buildPath)) {
            path = path + "/" + buildPath;
        }
        log.info("--->go version{}", ProcessUtils.process(path, "go version"));
        log.info("--->go env{}", ProcessUtils.process(path, "go env"));
        log.info("--->go build: {} {} {}", path, buildPath, binName);
        Pair<Integer, List<String>> res = ProcessUtils.process(path, "go build -o " + binName);
        return Pair.of(res.getKey() == 0, res.toString());
    }


    private boolean upload(String tags, long id, Stopwatch sw, File tempGitDir, String ks3Key, String binName) {
        sendMessage(tags, id, CompileTaskStep.findJar, CompileTaskStatus.running, sw, null, null,
                "[INFO] 搜索打包文件" + System.lineSeparator());
        Pair<Boolean, String> res = findBinFile(tempGitDir, binName);
        if (!res.getKey()) {
            sendMessage(tags, id, CompileTaskStep.findJar, CompileTaskStatus.failure, sw, null, null,
                    "[ERROR] 未发现打包文件" + System.lineSeparator());
            return false;
        }
        sendMessage(tags, id, CompileTaskStep.findJar, CompileTaskStatus.running, sw, null, null,
                "[SUCCESS] 找到打包文件" + System.lineSeparator());
        sendMessage(tags, id, CompileTaskStep.upload, CompileTaskStatus.running, sw, null, null,
                "[INFO] 上传打包文件" + System.lineSeparator());
        String jarPath = res.getValue();
        try {
            log.info("upload file begin");
            String url = ksyunService.uploadFile(ks3Key, new File(jarPath), ONE_MONTH_SECONDS);
            log.info("upload file finish use time:{}", sw.elapsed(TimeUnit.MILLISECONDS));
            FileUtils.forceDelete(tempGitDir);
            sendMessage(tags, id, CompileTaskStep.upload, CompileTaskStatus.success, sw, url, ks3Key,
                    "[SUCCESS] 上传成功" + System.lineSeparator());
        } catch (IOException e) {
            log.error("{}", e);
            sendMessage(tags, id, CompileTaskStep.upload, CompileTaskStatus.failure, sw, null, null,
                    "[SUCCESS] 上传失败" + e.getMessage() + System.lineSeparator());
            return false;
        }
        return true;
    }

    /**
     * 寻找jar 文件
     *
     * @param tempGitDir
     * @param binName
     * @return
     */
    private Pair<Boolean, String> findBinFile(File tempGitDir, String binName) {
        File file = new File(tempGitDir.getPath() + File.separator + binName);
        return Pair.of(file.exists(), file.exists() ? file.getPath() : "");
    }


}
