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

package run.mone.m78.service.service.git.dubbo;

import com.alibaba.excel.util.StringUtils;
import com.google.common.base.Preconditions;
import com.xiaomi.data.push.antlr.java.JavaExprDTO;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import run.mone.m78.api.GitLabService;
import run.mone.m78.api.bo.gitlab.GitLabReq;
import run.mone.m78.api.bo.gitlab.GitTreeItem;
import run.mone.m78.api.constant.CommonConstant;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.service.git.InnerGitLabService;

import java.util.List;

/**
 * @author shanwb
 * @date 2024-02-24
 */
@DubboService(timeout = CommonConstant.DEF_DUBBO_TIMEOUT, group = "${dubbo.group}", version="1.0")
@Slf4j
public class GitLabServiceImpl implements GitLabService {

    @Autowired
    private InnerGitLabService innerGitLabService;

    @Override
    public Result<String> getFileContent(GitLabReq gitLabReq) {
        log.info("getFileContent in :{}", GsonUtils.gson.toJson(gitLabReq));
        Preconditions.checkArgument(null != gitLabReq.getGitDomain(), "gitDomain can not be null");
        Preconditions.checkArgument(null != gitLabReq.getProjectId(), "projectId can not be null");
        Preconditions.checkArgument(null != gitLabReq.getFilePath(), "filePath can not be null");
        Preconditions.checkArgument(null != gitLabReq.getGitToken(), "gitToken can not be null");
        String branch = gitLabReq.getBranch();
        if (StringUtils.isBlank(branch)) {
            branch = "master";
        }

        String content = innerGitLabService.getFileContent(branch, gitLabReq.getGitDomain(), gitLabReq.getGitToken(), gitLabReq.getProjectId(), gitLabReq.getFilePath());

        return Result.success(content);
    }

    @Override
    public Result<String> parseProjectJavaFile(GitLabReq gitLabReq) {
        log.info("parseProjectJavaFile in :{}", GsonUtils.gson.toJson(gitLabReq));
        Preconditions.checkArgument(null != gitLabReq.getGitDomain(), "gitDomain can not be null");
        Preconditions.checkArgument(null != gitLabReq.getProjectId(), "projectId can not be null");
        Preconditions.checkArgument(null != gitLabReq.getFilePath(), "filePath can not be null");
        Preconditions.checkArgument(null != gitLabReq.getGitToken(), "gitToken can not be null");
        String filePath = gitLabReq.getFilePath();
        Preconditions.checkArgument(filePath.endsWith(".java"), "only support java file");

        String branch = gitLabReq.getBranch();
        if (StringUtils.isBlank(branch)) {
            branch = "master";
        }

        JavaExprDTO javaExprDTO = innerGitLabService.parseProjectJavaFile(branch, gitLabReq.getGitDomain(), gitLabReq.getGitToken(), gitLabReq.getProjectId(), filePath);
        String result = GsonUtils.gson.toJson(javaExprDTO);

        return Result.success(result);
    }

    @Override
    public Result<List<GitTreeItem>> getProjectStructureTree(GitLabReq gitLabReq) {
        log.info("getProjectStructureTree in :{}", GsonUtils.gson.toJson(gitLabReq));
        Preconditions.checkArgument(null != gitLabReq.getGitDomain(), "gitDomain can not be null");
        Preconditions.checkArgument(null != gitLabReq.getProjectId(), "projectId can not be null");
        Preconditions.checkArgument(null != gitLabReq.getGitToken(), "gitToken can not be null");

        String branch = gitLabReq.getBranch();
        if (StringUtils.isBlank(branch)) {
            branch = "master";
        }

        List<GitTreeItem> treeItemList = innerGitLabService.getProjectStructureTree(branch, gitLabReq.getGitDomain(), gitLabReq.getGitToken(), gitLabReq.getProjectId());

        return Result.success(treeItemList);
    }

}
