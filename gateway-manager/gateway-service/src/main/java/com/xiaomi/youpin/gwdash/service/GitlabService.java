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

package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
import com.xiaomi.youpin.gwdash.bo.CommitHistoryResult;
import com.xiaomi.youpin.gwdash.bo.FileContentResult;
import com.xiaomi.youpin.gwdash.bo.GitlabToken;
import com.xiaomi.youpin.gwdash.common.Result;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

/**
 * gitlab操作服务
 */
@Service
public class GitlabService {

    @Reference(group = "${ref.gwdash.service.group}", interfaceClass = IGitlabService.class, check = false)
    private IGitlabService gwdashGitlabService;

    public GitlabToken getAccessTokenByUsername(String username, String domain) {
        return gwdashGitlabService.getGitlabTokenByUsername(username, domain);
    }

    public Properties getGitlabFile2Properties(String gitUrl, String commitId, String filePath, String username, String password) {
        return gwdashGitlabService.getGitlabFile2Properties(gitUrl, commitId, filePath, username, password);
    }

    public List<GitlabCommit> getGitlabAllCommit(String gitHost, String projectId, String branch, String token){
        return gwdashGitlabService.getAllGitlabCommit(gitHost, projectId, branch, token);
    }

    public Result<CommitHistoryResult> getCommits(String gitHost, String projectId, String token, String path, String branch) {
        List<GitlabCommit> commits = gwdashGitlabService.getCommitsV2(gitHost, projectId, token, path, branch);
        return Result.success(new CommitHistoryResult(commits));
    }

    public Result<FileContentResult> getFileContent(String gitHost, String projectId, String token, String path, String branch) {
        String content = gwdashGitlabService.getFileContentV2(gitHost, projectId, token, path, branch);
        return Result.success(new FileContentResult(content));
    }
}
