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

package com.xiaomi.youpin.gwdash.bo;

import java.util.Objects;

public class GitScriptInfo {

    private String gitProjectId;

    private String gitToken;

    private String gitPath;

    private String gitBranch;

    private String commit;

    public GitScriptInfo() {
    }

    public GitScriptInfo(String gitProjectId, String gitToken, String gitPath, String gitBranch, String commit) {
        this.gitProjectId = gitProjectId;
        this.gitToken = gitToken;
        this.gitPath = gitPath;
        this.gitBranch = gitBranch;
        this.commit = commit;
    }

    public String getGitProjectId() {
        return gitProjectId;
    }

    public void setGitProjectId(String gitProjectId) {
        this.gitProjectId = gitProjectId;
    }

    public String getGitToken() {
        return gitToken;
    }

    public void setGitToken(String gitToken) {
        this.gitToken = gitToken;
    }

    public String getGitPath() {
        return gitPath;
    }

    public void setGitPath(String gitPath) {
        this.gitPath = gitPath;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    @Override
    public String toString() {
        return "GitScriptInfo{" +
                "gitProjectId='" + gitProjectId + '\'' +
                ", gitToken='" + gitToken + '\'' +
                ", gitPath='" + gitPath + '\'' +
                ", gitBranch='" + gitBranch + '\'' +
                ", commit='" + commit + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitScriptInfo that = (GitScriptInfo) o;
        return Objects.equals(gitProjectId, that.gitProjectId) &&
                Objects.equals(gitToken, that.gitToken) &&
                Objects.equals(gitPath, that.gitPath) &&
                Objects.equals(gitBranch, that.gitBranch) &&
                Objects.equals(commit, that.commit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gitProjectId, gitToken, gitPath, gitBranch, commit);
    }
}
