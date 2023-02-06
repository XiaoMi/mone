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

import com.xiaomi.youpin.gitlab.bo.GitlabBranch;
import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
import com.xiaomi.youpin.gitlab.bo.GitlabProject;
import com.xiaomi.youpin.gitlab.Gitlab;
import com.xiaomi.youpin.gitlab.bo.GroupInfo;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.FileUtil;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.GitlabAccessToken;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.exception.CommonException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.util.ByteInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * gitlab操作服务
 */
@Service
public class GitlabService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitlabService.class);

    @Autowired
    @Qualifier("gitlabHandler")
    private Gitlab gitlabHandler;

    @Autowired
    private Dao dao;


    /**
     * 获取指定项目的提交历史
     * @param projectId
     * @param token
     * @param path
     * @param branch
     * @return
     */
    public Result<CommitHistoryResult> getCommits(String projectId, String token, String path, String branch) {

        List<GitlabCommit> commits;

        try {
            commits = gitlabHandler.fetchFileVersion(projectId, token, path, branch);
        } catch (Exception e) {
            LOGGER.error("[GitlabService.getCommits] failed to get commit history, projectId: {}, token: {}, path: {}, branch: {}",
                    projectId, token, path, branch);
            throw new CommonException(CommonError.GitlabOptError);
        }

        return Result.success(new CommitHistoryResult(commits));
    }

    /**
     * 获取gitlab上的指定文档内容
     * @param projectId
     * @param token
     * @param path
     * @param branch
     * @return
     */
    public Result<FileContentResult> getFileContent(String projectId, String token, String path, String branch) {

        String content;

        try {
            content = gitlabHandler.fetchFile(projectId, token, path, branch);
        } catch (Exception e) {
            LOGGER.error("[GitlabService.getFileContent] failed to get commit history, projectId: {}, token: {}, path: {}, branch: {}",
                    projectId, token, path, branch);
            throw new CommonException(CommonError.GitlabOptError);
        }

        return Result.success(new FileContentResult(content));
    }

    public Result<Boolean> createAccessToken(String username, String name, String token, String desc) {
        Cnd cnd = Cnd.where("username", "=", username);
        GitlabAccessToken gitlabAccessToken = dao.fetch(GitlabAccessToken.class, cnd);
        if (null != gitlabAccessToken) {
            return new Result<>(1, "至多能创建一个token帐号", false);
        }
        gitlabAccessToken = new GitlabAccessToken();
        gitlabAccessToken.setUsername(username);
        gitlabAccessToken.setName(name);
        gitlabAccessToken.setToken(token);
        gitlabAccessToken.setDesc(desc);
        dao.insert(gitlabAccessToken);
        return Result.success(true);
    }

    public Result<Boolean> editAccessToken(String username, long id, String name, String token, String desc) {
        Cnd cnd = Cnd.where("username", "=", username).and("id", "=", id);
        GitlabAccessToken gitlabAccessToken = dao.fetch(GitlabAccessToken.class, cnd);
        if (null != gitlabAccessToken) {
            gitlabAccessToken.setDesc(desc);
            gitlabAccessToken.setName(name);
            gitlabAccessToken.setToken(token);
            dao.update(gitlabAccessToken);
            return Result.success(true);
        }
        return Result.success(false);
    }

    public Result<Map<String, Object>> getAccessToken(String username, int page, int pageSize) {
        Cnd cnd = Cnd.where("username", "=", username);
        Map<String, Object> map = new HashMap<>();
        map.put("total", dao.count(GitlabAccessToken.class, cnd));
        map.put("list", dao.query(GitlabAccessToken.class, cnd, new Pager(page, pageSize)));
        return Result.success(map);
    }

    public Result<Boolean> delAccessToken(String username, long id) {
        Cnd cnd = Cnd.where("username", "=", username).and("id", "=", id);
        GitlabAccessToken gitlabAccessToken = dao.fetch(GitlabAccessToken.class, cnd);
        if (null != gitlabAccessToken) {
            dao.delete(gitlabAccessToken);
            return Result.success(true);
        }
        return Result.success(true);
    }

    public Result<GitlabAccessToken> getAccessTokenByUsername(String username) {
        return Result.success(dao.fetch(GitlabAccessToken.class,
                Cnd.where("username", "=", username)));
    }

    public Properties getGitlabFile2Properties (String gitUrl, String commitId, String filePath, String username, String password) {
        String path = System.getProperty("java.io.tmpdir") + File.separator + gitUrl + File.separator;
        try {
            CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password);
            Git git = Git.cloneRepository()
                    .setURI(gitUrl)
                    .setCredentialsProvider(cp)
                    .setDirectory(new File(path))
                    .setCloneAllBranches(true)
                    .call();

            Repository repository = git.getRepository();
            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(repository.resolve(commitId));
                RevTree tree = commit.getTree();

                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    treeWalk.setFilter(PathFilter.create(filePath));

                    if (!treeWalk.next()) {
                        return null;
                    }

                    ObjectId objectId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId);
                    Properties properties = new Properties();
                    properties.load(new InputStreamReader(new ByteInputStream(loader.getBytes()), "UTF-8"));
                    return properties;
                }
            }
        } catch (GitAPIException | IOException e) {
        } finally {
            FileUtil.deleteDirectory(path);
        }
        return null;
    }

    public Result<List<GitlabProject>> getAllProjects (String search, String token) {
        Map<String, String> params = new HashMap<>();
        params.put("search", search);
        List<GitlabProject> list = gitlabHandler.fetchProjects(token, params);
        return Result.success(list);
    }

    public Result<GitlabProject> getTheProject (String projectId, String token) {
        GitlabProject gitlabProject = gitlabHandler.fetchProject(projectId, token, null);
        return Result.success(gitlabProject);
    }

    public Result<GroupInfo> getNamespacesById (String id, String token) {
        GroupInfo groupInfo = gitlabHandler.getNamespacesById(id, token);
        return Result.success(groupInfo);
    }

    public Result<List<GitlabCommit>> getGitlabAllCommit (String projectId, String branch, String token) {
        return Result.success(gitlabHandler.fetchCommits(projectId, branch, token, null));
    }

    public Result<List<GitlabCommit>> getGitlabBetweenCommits (String projectId, String branch, String token, String since, String until) {
        Map<String, String> params = new HashMap<>();
        params.put("ref_name", branch);
        params.put("since", since);
        params.put("until", until);
        return Result.success(gitlabHandler.fetchCommits(projectId, branch, token, params));
    }

    public Result<GitlabCommit> getGitlabCommit (String projectId, String sha, String token) {
        return Result.success(gitlabHandler.fetchCommit(projectId, sha, token));
    }

    public Result<List<String>> getGitlabBranch (String projectId, String token, String search) {
        List<String> list;
        Map<String, String> params = new HashMap<>();
        params.put("search", search);
        List<GitlabBranch> gitlabBranches = gitlabHandler.fetchBranches(projectId, token, params);
        list = gitlabBranches.stream().map(it -> {
            return it.getName();
        }).collect(Collectors.toList());
        return Result.success(list);
    }
}
