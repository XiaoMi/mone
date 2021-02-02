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

package com.xiaomi.youpin.mischedule.task;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.TaskResult;
import com.xiaomi.data.push.schedule.task.impl.AbstractTask;
import com.xiaomi.youpin.codecheck.CodeCheck;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import com.xiaomi.youpin.mischedule.bo.CodeCheckData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * <p>
 * 负责代码检测的任务
 */
@Component
@Slf4j
public class CodeCheckTask extends AbstractTask {

    @Value("${git.base.path}")
    private String BASE_GIT_PATH;

    @Override
    public TaskResult execute(TaskParam taskParam, TaskContext taskContext) {
        Stopwatch sw = Stopwatch.createStarted();
        String id = taskParam.param.get("id");
        String tag = taskParam.param.get("tag");
        String gitUrl = taskParam.param.get("gitUrl");
        String branch = taskParam.param.get("branch");
        String gitUser = taskParam.param.get("gitUser");
        String gitPw = taskParam.param.get("gitPw");

        String tempGitPath = BASE_GIT_PATH + gitUrl + File.separator + branch;
        File tempGitDir = delTemp(tempGitPath);
        Pair<Boolean, String> result = cloneRepository(gitUrl, branch, tempGitDir, gitUser, gitPw);
        Map<String, List<CheckResult>> checkRes;
        if (result.getKey()) {
            checkRes = new CodeCheck().check(tempGitPath);
        } else {
            checkRes = new HashMap<>();
            List<CheckResult> list = new ArrayList<>(1);
            list.add(CheckResult.getErrorRes("", "", result.getValue()));
            checkRes.put(branch, list);
        }
        taskContext.notifyMsg(tag, new Gson().toJson(CodeCheckData
            .builder()
            .id(Long.valueOf(id))
            .checkRes(checkRes)
            .time(sw.elapsed(TimeUnit.MILLISECONDS))
            .build()));

        delTemp(tempGitPath);
        log.info("\t-->CodeCheckTask#execute: {}, {}", result, checkRes);
        return TaskResult.Success();
    }


    /**
     * 根据git地址{gitUrl}和分支{branch}，把代码pull至指定目录{gitPath}
     */
    private Pair<Boolean, String> cloneRepository(String gitUrl, final String branch, File gitPath, String username, String password) {
        try {
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
                branchList = branchList.stream().filter(it -> {
                    return it.getName().endsWith("/" + branch);
                }).collect(Collectors.toList());
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
            log.info("cloneRepository {} branch:{}", gitUrl, branch);
            return Pair.of(true, "");
        } catch (Exception e) {
            log.error("cloneRepository {}", e);
            return Pair.of(false, e.getMessage());
        }
    }


    private File delTemp(String tempGitPath) {
        File tempGitDir = new File(tempGitPath);
        if (tempGitDir.exists()) {
            try {
                FileUtils.deleteDirectory(tempGitDir);
            } catch (Exception e) {
                log.error("delTemp {}", e.getMessage());
            }
        }
        return tempGitDir;
    }
}
