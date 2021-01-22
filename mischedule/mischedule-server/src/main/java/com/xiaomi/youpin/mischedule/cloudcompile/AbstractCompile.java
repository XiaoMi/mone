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

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.jcraft.jsch.Session;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileEnum;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileTaskStatus;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileTaskStep;
import com.xiaomi.youpin.mischedule.bo.VulcanusData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public abstract class AbstractCompile {


    @Autowired
    protected DefaultMQProducer defaultMQProducer;


    @Value("${maven.path}")
    protected String MAVEN_HOME;

    @Value("${maven.repo}")
    protected String MAVEN_REPO;

    @Value("${maven.nexus.settings.path}")
    protected String nexus;

    @Value("${maven.pkgs.settings.path}")
    protected String pkgs;

    @Value("${git.base.path}")
    protected String BASE_GIT_PATH;

    @Value("${ks3.AccessKeyID}")
    protected String accesskey;

    @NacosValue(value = "${ks3_AccessKeySecret}", autoRefreshed = true)
    protected String accessSecret;


    protected static final String START_FLAG = "xiaomi.com";
    protected static final String END_FLAG = ".git";
    protected static final String KEY_PREFIX = "vulcanus";
    public static final int ONE_MONTH_SECONDS = 3600 * 24 * 30;


    public String getProjectName(String gitUrl) {
        int start = gitUrl.indexOf(START_FLAG);
        int end = gitUrl.lastIndexOf(END_FLAG);
        String substring = gitUrl.substring(start + 1 + START_FLAG.length(), end);
        return substring.replaceAll("/", Matcher.quoteReplacement(File.separator));
    }


    public String getProjectName(String alias, String gitUrl) {
        if (StringUtils.isNotEmpty(alias)) {
            return alias;
        }
        return getProjectName(gitUrl);
    }


    public String getKs3Key(CompileParam request, String gitUrl, String branch) {
        String projectName = getProjectName(request.getAlias(), gitUrl);
        String[] strs = projectName.split(Matcher.quoteReplacement(File.separator));
        String serviceName = strs[strs.length - 1];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timeStamp = sdf.format(new Date());
        return serviceName + "-" + timeStamp + ".jar";
    }


    public boolean check(String tags, long id, String gitUrl, String branch, String gitName, String gitToken, Stopwatch sw) {
        sendMessage(tags, id, CompileTaskStep.check, CompileTaskStatus.running, sw, null, null,
                "[INFO] 配置信息检查" + System.lineSeparator());
        boolean isPass = true;
        String errMsg = "";

        if (StringUtils.isEmpty(branch) || StringUtils.isEmpty(gitUrl)) {
            errMsg = "分支或git地址不能为空";
            isPass = false;
        }

        if (StringUtils.isEmpty(gitName) || StringUtils.isEmpty(gitToken)) {
            errMsg = "git授权账户不能为空";
            isPass = false;
        }

        if (isPass && !gitUrl.endsWith(".git")) {
            errMsg = "git地址需以.git结尾";
            isPass = false;
        }

        if (isPass && !gitUrl.startsWith("http")) {
            errMsg = "git地址需以http开头";
            isPass = false;
        }

        if (isPass && StringUtils.isBlank(branch)) {
            errMsg = "分支名不能存在空格字符";
            isPass = false;
        }

        if (isPass) {
            sendMessage(tags, id, CompileTaskStep.check, CompileTaskStatus.running, sw, null, null,
                    "[SUCCESS] 配置信息检查完成");
        } else {
            sendMessage(tags, id, CompileTaskStep.check, CompileTaskStatus.failure, sw, null, null,
                    "[ERROR] 配置信息检查未通过" + errMsg + System.lineSeparator());
        }

        return isPass;
    }


    protected void sendMessage(String tags, long id, CompileTaskStep step,
                               CompileTaskStatus status, Stopwatch sw, String url, String key, String message) {
        log.info("sendMessage id:{} msg:{}", id, message);
        try {
            Gson gson = new Gson();
            if (null != message) {
                defaultMQProducer.send(new Message(
                        CompileEnum.CompileLog.getName(),
                        tags,
                        gson.toJson(VulcanusData.builder().id(id).message(message).build()).getBytes()
                ));
            }
            if (null != step && null != status) {
                defaultMQProducer.send(
                        new Message(CompileEnum.CompileStatus.getName(),
                                tags,
                                gson.toJson(VulcanusData.builder().id(id)
                                        .time(sw.elapsed(TimeUnit.MILLISECONDS))
                                        .step(step.ordinal())
                                        .url(url)
                                        .key(key)
                                        .status(status.ordinal()).build()).getBytes()
                        )
                );
            }
        } catch (MQClientException | RemotingException | InterruptedException | MQBrokerException e) {
            log.warn("cloudCompileServiceImpl.sendMessage: {}", e);
        }
    }


    protected File delTemp(String tempGitPath) {
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


    protected boolean clone(String tags, long id, Stopwatch sw, String gitUrl, String branch, String username, String password, File tempGitDir) {
        sendMessage(tags, id, CompileTaskStep.clone, CompileTaskStatus.running, sw, null, null,
                "[INFO] 开始克隆仓库" + System.lineSeparator());

        boolean isPass = true;
        log.info("clone begin");
        Pair<Boolean, String> result = cloneRepository(gitUrl, branch, username, password, tempGitDir);
        if (!result.getKey()) {
            log.error("clone error {} {}, {}", gitUrl, branch, result.getValue());
            isPass = false;
            sendMessage(tags, id, CompileTaskStep.clone, CompileTaskStatus.failure, sw, null, null,
                    "[ERROR] 克隆仓库失败" + result.getValue() + System.lineSeparator());
        } else {
            sendMessage(tags, id, CompileTaskStep.clone, CompileTaskStatus.running, sw, null, null,
                    "[SUCCESS] 克隆仓库成功" + System.lineSeparator());
        }
        return isPass;
    }

    private TransportConfigCallback getTransportConfigCallback() {
        final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        };

        return new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                if (transport instanceof TransportHttp) {
                    return;
                }
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        };
    }


    /**
     * 根据git地址{gitUrl}和分支{branch}，把代码pull至指定目录{gitPath}
     */
    private Pair<Boolean, String> cloneRepository(String gitUrl, String branch, String username, String password, File gitPath) {
        try {

            SshSessionFactory.setInstance(new JschConfigSessionFactory() {
                @Override
                public void configure(OpenSshConfig.Host hc, Session session) {
                    session.setConfig("StrictHostKeyChecking", "no");
                }
            });


            Git git = Git.cloneRepository()
                    .setURI(gitUrl)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                    .setDirectory(gitPath)
                    .setCloneAllBranches(true)
                    .call();

            Repository repository = git.getRepository();
            String branchName = repository.getBranch();

            ObjectId objectId = null;
            if (!branch.equals(branchName)) {
                List<Ref> branchList = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
                branchList = branchList.stream().filter(it -> it.getName().endsWith("/" + branch)).collect(Collectors.toList());
                int len = branchList.size();
                if (len > 0) {
                    objectId = branchList.get(0).getObjectId();
                } else {
                    objectId = repository.resolve(branch);
                }

                if (null != objectId) {
                    RevWalk revWalk = new RevWalk(repository);
                    RevCommit commit = revWalk.parseCommit(objectId);
                    git.checkout().setStartPoint(commit).setCreateBranch(true).setName(commit.getName()).call();
                } else {
                    return Pair.of(false, "分支不存在");
                }
            }

            // 释放资源
            git.getRepository().close();
            log.info("download successfully for {} branch:{}", gitUrl, branch);
            return Pair.of(true, "");
        } catch (Throwable e) {
            log.error("cloneRepository error:{}", e.getMessage());
            return Pair.of(false, e.getMessage());
        }
    }

}
