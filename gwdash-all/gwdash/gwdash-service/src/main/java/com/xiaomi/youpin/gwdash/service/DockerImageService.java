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
import com.xiaomi.youpin.gwdash.bo.DockerImageInfoBo;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.GitlabAccessToken;
import com.xiaomi.youpin.gwdash.dao.model.ProjectCompileRecord;
import com.xiaomi.youpin.mischedule.api.service.bo.DockerParam;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author dp
 */

@Slf4j
@Service
public class DockerImageService {

    @Autowired
    private Dao dao;

    @Autowired
    private GitlabService gitlabService;

    @Autowired
    private DockerfileService dockerfileService;

    public Result<Boolean> createDockerImage(SessionAccount account, String gitUrl, String groupName, String projectName, String commitId, String desc) {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", false);
        }
        try {
            DockerImageInfoBo dockerImageInfo = dao.fetch(DockerImageInfoBo.class, Cnd.where("commit_id", "=", commitId));
            if (dockerImageInfo != null) {
                return new Result<>(1, "git commit id重复", false);
            }
            DockerImageInfoBo dockerImageInfoBo = new DockerImageInfoBo();
            dockerImageInfoBo.setGitAddress(gitUrl);
            dockerImageInfoBo.setGroupName(groupName);
            dockerImageInfoBo.setProjectName(projectName);
            dockerImageInfoBo.setDesc(desc);
            dockerImageInfoBo.setCommitId(commitId);
            dockerImageInfoBo.setStatus(0);
            dockerImageInfoBo.setCreator(account.getName());
            dockerImageInfoBo.setUpdater(account.getName());
            dockerImageInfoBo.setCtime(System.currentTimeMillis());
            dockerImageInfoBo.setUtime(System.currentTimeMillis());
            dao.insert(dockerImageInfoBo);
            return Result.success(true);
        } catch (Exception e) {
            return new Result<>(1, e.getMessage(), false);
        }
    }

    public Result<Boolean> updateDockerImage(Integer id, SessionAccount account, String desc) {
        DockerImageInfoBo dockerImageInfoBo = dao.fetch(DockerImageInfoBo.class, Cnd.where("id", "=", id));
        if (null == dockerImageInfoBo) {
            return new Result<>(1, "该记录已经不存在", false);
        }

        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", false);
        }

        try {
            dockerImageInfoBo.setDesc(desc);
            dockerImageInfoBo.setUpdater(account.getName());
            dockerImageInfoBo.setUtime(System.currentTimeMillis());
            dao.update(dockerImageInfoBo);
            return Result.success(true);
        } catch (Exception e) {
            return new Result<>(1, e.getMessage(), false);
        }
    }

    public Result<List<GitlabCommit>> getGitlabCommits(String gitAddress, SessionAccount account) throws IOException {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }
        String[] strings = gitAddress.split("/");
        if (strings.length < 2) {
            return new Result<>(1, "git地址错误", null);
        }
        String group = strings[strings.length - 2];
        String name = strings[strings.length - 1];
        return gitlabService.getGitlabAllCommit(URLEncoder.encode(group + "/" + name, "UTF-8"), "master", gitlabAccessToken.getToken());
    }

    public Result<Boolean> deleteDcokerImage(int id, SessionAccount account) {
        DockerImageInfoBo dockerImageInfoBo = dao.fetch(DockerImageInfoBo.class, Cnd.where("id", "=", id));
        if (null == dockerImageInfoBo) {
            return new Result<>(1, "该记录已经不存在", false);
        }
        dao.delete(dockerImageInfoBo);
        return Result.success(true);
    }

    public Result<Boolean> buildDockerImage(int id, SessionAccount account) {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }
        DockerImageInfoBo dockerImageInfoBo = dao.fetch(DockerImageInfoBo.class, Cnd.where("id", "=", id));
        if (dockerImageInfoBo == null) {
            return new Result<>(1, "该记录已经不存在", false);
        }
        if (dockerImageInfoBo.getStatus() != Consts.DOCKER_IMAGE_STATUS_EFFECT) {
            return new Result<>(1, "只有审核通过的记录才允许docker build", false);
        }

        DockerParam dockerParam = new DockerParam();
        dockerParam.setGitUrl(dockerImageInfoBo.getGitAddress() + ".git");
        dockerParam.setGitUser(gitlabAccessToken.getUsername());
        dockerParam.setGitToken(gitlabAccessToken.getToken());
        StringBuffer appName = new StringBuffer();
        appName.append(dockerImageInfoBo.getGroupName());
        appName.append(".");
        appName.append(dockerImageInfoBo.getProjectName());
        dockerParam.setAppName(appName.toString().toLowerCase());
        dockerParam.setBranch(dockerImageInfoBo.getCommitId());
        ProjectCompileRecord projectCompileRecord = dockerfileService.startDockerBuild(dockerParam).getData();
        dockerImageInfoBo.setCompilationId(projectCompileRecord.getId());
        dao.update(dockerImageInfoBo);
        return Result.success(true);
    }

    public Map<String, Object> getDockerImageList(int page, int pageSize, int status) {
        List<DockerImageInfoBo> list = dao.query(DockerImageInfoBo.class,
                Cnd.where("status", status == 0 ? ">=" : "=", status)
                        .desc("ctime"),
                new Pager(page, pageSize));
        list.stream().forEach(it -> {
            dao.fetchLinks(it, null);
        });
        Map<String, Object> result = new HashMap<>();
        result.put("dockerImageList", list);
        result.put("total", dao.count("docker_image_info", Cnd.where("status", status == 0 ? ">=" : "=", status)));
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    public Result<Boolean> rejectDockerImage(Integer id, String userName) {
        DockerImageInfoBo dockerImageInfoBo = dao.fetch(DockerImageInfoBo.class, Cnd.where("id", "=", id));
        if (dockerImageInfoBo == null
                || dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_DELETE) {
            return new Result<>(1, "该记录不存在", false);
        }
        if (dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_REJECT
                || dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_EFFECT) {
            return new Result<>(2, "已经审核过", false);
        }

        if (userName.equals(dockerImageInfoBo.getCreator()) || userName.equals(dockerImageInfoBo.getUpdater())) {
            return new Result<>(4, "不允许审核自己创建或更新的", false);
        }

        // 更新信息
        dao.update(DockerImageInfoBo.class,
                Chain.make("status", Consts.DOCKER_IMAGE_STATUS_REJECT),
                Cnd.where("id", "=", id));

        return Result.success(true);
    }

    public Result<Boolean> effectDockerImage(Integer id, String userName) {
        DockerImageInfoBo dockerImageInfoBo = dao.fetch(DockerImageInfoBo.class, Cnd.where("id", "=", id));
        if (dockerImageInfoBo == null
                || dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_DELETE) {
            return new Result<>(1, "该记录不存在", false);
        }
        if (dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_EFFECT
                || dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_REJECT) {
            return new Result<>(2, "已经审核过", false);
        }

        if (userName.equals(dockerImageInfoBo.getCreator()) || userName.equals(dockerImageInfoBo.getUpdater())) {
            return new Result<>(4, "不允许审核自己创建或更新的", false);
        }

        // 更新信息
        dao.update(DockerImageInfoBo.class,
                Chain.make("status", Consts.DOCKER_IMAGE_STATUS_EFFECT),
                Cnd.where("id", "=", id));

        return Result.success(true);
    }

}
