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

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.google.common.base.Stopwatch;
import com.xiaomi.youpin.ks3.KsyunService;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileTaskStatus;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileTaskStep;
import com.xiaomi.youpin.mischedule.enums.XmlSettingEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.Service;
import org.apache.maven.shared.invoker.*;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;


/**
 * @author gaoyibo
 * @author goodjava@qq.com
 */
@Slf4j
@Service
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySource(dataId = "mischedule", autoRefreshed = true)
public class CloudCompileServiceImpl extends AbstractCompile implements CloudCompileService {

    private String debug = "false";


    @Value("${use.file.server}")
    private boolean useFileServer;


    @Value("${file.server.url}")
    private String fileServerUrl;


    private KsyunService ksyunService;


    @PostConstruct
    private void init() {
        if (useFileServer) {
            log.info("useFileServer");
            ksyunService = new KsyunService(fileServerUrl);
            ksyunService.setToken("dprqfzzy123!");
        } else {
            ksyunService = new KsyunService();
            ksyunService.setAccessKeyID(accesskey);
            ksyunService.setAccessKeySecret(accessSecret);
            ksyunService.init();
        }
    }

    @Override
    public void compile(final CompileParam request) {
        log.info("cloudCompileServiceImpl.compile params:{}", request);

        Stopwatch sw = Stopwatch.createStarted();
        long id = request.getId();
        String gitUrl = request.getGitUrl();
        String branch = request.getBranch();
        String profile = request.getProfile();
        String tags = request.getTags();
        String gitName = request.getGitName();
        String gitToken = request.getGitToken();
        String jarPath = request.getJarPath();
        String buildPath = request.getBuildPath();
        String ks3Key = getKs3Key(request,request.getGitUrl(), request.getBranch());

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

        // 打包
        if (!build(tags, id, sw, gitUrl, branch, tempGitPath, buildPath, profile, request.getRepoType(), request.getCustomParams())) {
            delTemp(tempGitPath);
            return;
        }

        // 检索&上传打包文件
        boolean res = upload(tags, id, sw, tempGitPath, jarPath, ks3Key);
        if (!res) {
            return;
        }
        delTemp(tempGitPath);
        log.info("totally finished {} time:{}", id, sw.elapsed(TimeUnit.MICROSECONDS));
    }

    private String getProjectName(CompileParam request, String gitUrl) {
        return this.getProjectName(request.getAlias(), gitUrl);
    }


    private boolean upload(String tags, long id, Stopwatch sw, String tempGitPath, String jarPath, String ks3Key) {
        try {
            log.info("upload:{} begin", id);
            sendMessage(tags, id, CompileTaskStep.findJar, CompileTaskStatus.running, sw, null, null,
                    "[INFO] 搜索打包文件" + System.lineSeparator());
            jarPath = findDir(tempGitPath, jarPath);
            FindJar findJar = new FindJar(new File(jarPath)).invoke();
            if (!findJar.isSuccess()) {
                sendMessage(tags, id, CompileTaskStep.findJar, CompileTaskStatus.failure, sw, null, null,
                        "[ERROR] 未发现打包文件, jarPath: " + jarPath + System.lineSeparator());
                return false;
            }
            sendMessage(tags, id, CompileTaskStep.findJar, CompileTaskStatus.running, sw, null, null,
                    "[SUCCESS] 找到打包文件, jarPath: " + jarPath + System.lineSeparator());
            sendMessage(tags, id, CompileTaskStep.upload, CompileTaskStatus.running, sw, null, null,
                    "[INFO] 上传打包文件" + System.lineSeparator());
            jarPath = findJar.getJarPath();
            log.info("upload file begin");
            String url = ksyunService.uploadFile(ks3Key, new File(jarPath), ONE_MONTH_SECONDS);
            log.info("upload:{} file finish use time:{}", id, sw.elapsed(TimeUnit.MILLISECONDS));
            sendMessage(tags, id, CompileTaskStep.upload, CompileTaskStatus.success, sw, url, ks3Key,
                    "[SUCCESS] 上传成功" + System.lineSeparator());
        } catch (Throwable e) {
            log.error("upload id:" + id + " error:" + e.getMessage(), e);
            sendMessage(tags, id, CompileTaskStep.upload, CompileTaskStatus.failure, sw, null, null,
                    "[ERROR] 上传失败" + e.getMessage() + System.lineSeparator());
            return false;
        }
        return true;
    }

    private boolean build(String tags, long id, Stopwatch sw, String gitUrl, String branch, String tempGitPath, String buildPath, String profile, int repoType, String customParams) {
        sendMessage(tags, id, CompileTaskStep.build, CompileTaskStatus.running, sw, null, null,
                "[INFO] 开始打包" + System.lineSeparator());
        boolean isPass = true;
        log.info("build begin");
        buildPath = findDir(tempGitPath, buildPath);
        Pair<Boolean, String> result = build(tags, id, new File(buildPath + File.separator + "pom.xml"), profile, repoType, customParams);
        // String buildLogKey = getKs3BuildLogKey(gitUrl, branch);
        // String buildLogFile = buildPath + File.separator + "build.log";
        // write(buildLogFile, result.getValue());
        // String buildUrl = ksyunService.uploadFile(buildLogKey, new File(buildLogFile), ONE_MONTH_SECONDS);

        if (!result.getKey()) {
            log.error("build error {} {} {}", gitUrl, branch, result);
            isPass = false;
            sendMessage(tags, id, CompileTaskStep.build, CompileTaskStatus.failure, sw, null, null,
                    "[ERROR] 构建失败, 构建路径： " + buildPath + System.lineSeparator());
        } else {
            log.info("build successfully {} {}", gitUrl, branch);
            sendMessage(tags, id, CompileTaskStep.build, CompileTaskStatus.running, sw, null, null,
                    "[SUCCESS] 构建成功, 构建路径: " + buildPath + System.lineSeparator());
        }
        return isPass;
    }

    private String findDir(String tempGitPath, String buildPath) {
        if (StringUtils.isEmpty(buildPath)) {
            return tempGitPath;
        }
        if ("/".equals(buildPath)) {
            return tempGitPath;
        }
        if (buildPath.startsWith("/")) {
            return findDir(tempGitPath, buildPath.substring(1));
        }
        File file = new File(tempGitPath);
        if (!file.isDirectory()) {
            return tempGitPath;
        }
        return file.getAbsolutePath() + File.separator + buildPath;
    }

    private String getKs3BuildLogKey(String gitUrl, String branch) {
        String projectName = getProjectName(gitUrl);
        String[] strs = projectName.split(Matcher.quoteReplacement(File.separator));
        String serviceName = strs[strs.length - 1];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timeStamp = sdf.format(new Date());
        return KEY_PREFIX + "/" + projectName + "/" + branch + "/" + serviceName + "-" + timeStamp + "-build.log";
    }

    /**
     * 编译指定pom目录的文件
     */
    private Pair<Boolean, String> build(String tags, long id, File pomPath, String profile, int repoType, String customParams) {
        log.info("build begin pomPath:{}", pomPath);
        File mavenRepo = new File(MAVEN_REPO);
        if (!mavenRepo.exists()) {
            mavenRepo.mkdirs();
        }

        Invoker invoker = new DefaultInvoker();
        invoker.setLocalRepositoryDirectory(new File(MAVEN_REPO));
        invoker.setMavenHome(new File(MAVEN_HOME));
        StringBuilder buildInfo = new StringBuilder("---- build info ----" + System.lineSeparator());
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pomPath);
        List commandList = Arrays.asList("-U", "clean", "package", "-Dmaven.test.skip=true");
        if (StringUtils.isNotBlank(profile)) {
            commandList = new ArrayList(commandList);
            if (!profile.equals("")) {
                commandList.add(" -P " + profile);
            }
        }
        if (StringUtils.isNotBlank(customParams)) {
            commandList = new ArrayList(commandList);
            Arrays.asList(customParams.split(" ")).stream().filter(e -> StringUtils.isNotBlank(e)).forEach(commandList::add);
        }
        String settingXmlPath = repoType == XmlSettingEnums.PKGS.getCode() ? pkgs : nexus;
        sendMessage(tags, id, null, null, null, null, null, "settings file is: " + settingXmlPath);
        sendMessage(tags, id, null, null, null, null, null, "mvn command list is: " + commandList);
        File settingFile = new File(settingXmlPath);
        if (settingFile.exists()) {
            request.setGlobalSettingsFile(settingFile);
        } else {
            log.error("setting file not exits");
        }
        request.setGoals(commandList);
        request.setMavenOpts("-Xms400m -Xmx400m");
        StringBuilder tmp = new StringBuilder();
        request.setOutputHandler(s ->
        {
            buildInfo.append(s).append(System.lineSeparator());
            tmp.append(s).append(System.lineSeparator());
            if (tmp.toString().split(System.lineSeparator()).length > 5) {
                if (debug != null && Boolean.parseBoolean(debug) == true) {
                    log.info(tmp.toString());
                }
                sendMessage(tags, id, null, null, null, null, null, tmp.toString());
                tmp.delete(0, tmp.length());
            }
        });
        if (tmp.length() > 0) {
            sendMessage(tags, id, null, null, null, null, null, tmp.toString());
        }
        InvocationResult result = null;
        try {
            result = invoker.execute(request);
        } catch (MavenInvocationException e) {
            log.error(e.getMessage());
            return Pair.of(false, buildInfo.toString());
        }
        if (result == null || result.getExitCode() != 0) {
            log.error("build failed, code = {}, exception = {}", result.getExitCode(), result.getExecutionException());
            return Pair.of(false, buildInfo.toString());
        }
        return Pair.of(true, buildInfo.toString());
    }

    private void write(String file, String msg) {
        try {
            FileOutputStream fo = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fo);
            BufferedWriter fw = new BufferedWriter(osw);
            fw.write(msg);
            fw.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private class FindJar {
        private boolean success;
        private File tempGitDir;
        private String jarPath;

        public FindJar(File tempGitDir) {
            this.tempGitDir = tempGitDir;
        }


        private String getJarPath(String dir) {
            File[] files = new File(dir + File.separator + "target").listFiles();
            if (files == null || files.length == 0) {
                return null;
            }
            for (File e : files) {
                if (e.getName().endsWith(".jar")
                        && !e.getName().endsWith("-sources.jar")
                        && !e.getName().startsWith("original")) {
                    log.info("jar path: {}", e);
                    return e.getAbsolutePath();
                }
            }
            return null;
        }


        boolean isSuccess() {
            return success;
        }

        public String getJarPath() {
            return jarPath;
        }

        public FindJar invoke() {
            jarPath = getJarPath(tempGitDir.getAbsolutePath());
            if (jarPath == null) {
                log.error("no file found for upload!");
                success = false;
                return this;
            }
            success = true;
            return this;
        }
    }
}
