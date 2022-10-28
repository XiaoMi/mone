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

import com.xiaomi.youpin.gitlab.bo.GitlabCommit;

import java.util.List;
import java.util.Objects;

public class CommitHistoryResult {

    private List<GitlabCommit> commits;

    public CommitHistoryResult() {
    }

    public CommitHistoryResult(List<GitlabCommit> commits) {
        this.commits = commits;
    }

    public List<GitlabCommit> getCommits() {
        return commits;
    }

    public void setCommits(List<GitlabCommit> commits) {
        this.commits = commits;
    }

    @Override
    public String toString() {
        return "CommitHistoryResult{" +
                "commits=" + commits +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommitHistoryResult that = (CommitHistoryResult) o;
        return Objects.equals(commits, that.commits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commits);
    }
}
