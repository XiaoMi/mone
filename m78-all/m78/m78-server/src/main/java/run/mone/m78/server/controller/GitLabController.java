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

package run.mone.m78.server.controller;

import com.google.common.base.Preconditions;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.gitlab.GitLabReq;
import run.mone.m78.api.bo.gitlab.GitTreeItem;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.service.git.InnerGitLabService;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;

/**
 * @author shanwb
 * @date 2024-02-26
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/gitlab")
@HttpApiModule(value = "GitLabController", apiController = GitLabController.class)
public class GitLabController {

    @Autowired
    private InnerGitLabService innerGitLabService;

    @PostMapping("/getFileContent")
    @ResponseBody
    public Result<String> getFileContent(HttpServletRequest request, @RequestBody GitLabReq gitLabReq) {
        log.info("getFileContent in :{}", GsonUtils.gson.toJson(gitLabReq));

        Preconditions.checkArgument(null != gitLabReq, "request param can not be null");
        Preconditions.checkArgument(null != gitLabReq.getGitDomain(), "gitDomain can not be null");
        Preconditions.checkArgument(null != gitLabReq.getProjectId(), "projectId can not be null");
        Preconditions.checkArgument(null != gitLabReq.getFilePath(), "filePath can not be null");
        Preconditions.checkArgument(null != gitLabReq.getGitToken(), "gitToken can not be null");

        String content = innerGitLabService.getFileContent(gitLabReq.getBranch(), gitLabReq.getGitDomain(), gitLabReq.getGitToken(), gitLabReq.getProjectId(), gitLabReq.getFilePath());
        return Result.success(content);
    }

    @PostMapping("/getProjectStructureTree")
    @ResponseBody
    public Result<List<GitTreeItem>> getProjectStructureTree(HttpServletRequest request, @RequestBody GitLabReq gitLabReq) {
        log.info("getProjectStructureTree in :{}", GsonUtils.gson.toJson(gitLabReq));

        Preconditions.checkArgument(null != gitLabReq, "request param can not be null");
        Preconditions.checkArgument(null != gitLabReq.getGitDomain(), "gitDomain can not be null");
        Preconditions.checkArgument(null != gitLabReq.getProjectId(), "projectId can not be null");
        Preconditions.checkArgument(null != gitLabReq.getFilePath(), "filePath can not be null");
        Preconditions.checkArgument(null != gitLabReq.getGitToken(), "gitToken can not be null");

        List<GitTreeItem> treeItemList = innerGitLabService.getProjectStructureTree(gitLabReq.getBranch(), gitLabReq.getGitDomain(), gitLabReq.getGitToken(), gitLabReq.getProjectId());
        return Result.success(treeItemList);
    }

}