///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.service;
//
//import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
//import com.xiaomi.youpin.gwdash.bo.CloudBuildInfo;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.CloudBuildTypeEnum;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.GitlabAccessToken;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectCompileRecord;
//import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
//import com.xiaomi.youpin.mischedule.api.service.bo.DockerParam;
//import lombok.extern.slf4j.Slf4j;
//import org.nutz.dao.Chain;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.nutz.dao.pager.Pager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//
///**
// * @author dp
// */
//
//@Slf4j
//@Service
//public class CloudBuildService {
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private GitlabService gitlabService;
//
//    @Autowired
//    private DockerfileService dockerfileService;
//
//    @Autowired
//    private ProjectCompilationService compilationService;
//
//    public Result<Boolean> createDockerImage(SessionAccount account,
//                                             long projectId,
//                                             String gitUrl,
//                                             String groupName,
//                                             String projectName,
//                                             String commitId,
//                                             String desc,
//                                             int type,
//                                             String branch) {
//        if (!CloudBuildTypeEnum.isValidity(type)) {
//            return new Result<>(1, "不支持的构建类型", false);
//        }
//        GitlabAccessToken gitlabAccessToken =gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
//        if (null == gitlabAccessToken) {
//            return new Result<>(1, "找不到授权的access token", false);
//        }
//        try {
//            CloudBuildInfo cloudBuildInfo = dao.fetch(CloudBuildInfo.class, Cnd.where("commit_id", "=", commitId));
//            if (cloudBuildInfo != null) {
//                return new Result<>(1, "git commit id重复", false);
//            }
//            cloudBuildInfo = new CloudBuildInfo();
//            cloudBuildInfo.setGitAddress(gitUrl);
//            cloudBuildInfo.setGroupName(groupName);
//            cloudBuildInfo.setProjectName(projectName);
//            cloudBuildInfo.setProjectId(projectId);
//            cloudBuildInfo.setDesc(desc);
//            cloudBuildInfo.setCommitId(commitId);
//            cloudBuildInfo.setStatus(0);
//            cloudBuildInfo.setCreator(account.getUsername());
//            cloudBuildInfo.setUpdater(account.getUsername());
//            cloudBuildInfo.setCtime(System.currentTimeMillis());
//            cloudBuildInfo.setUtime(System.currentTimeMillis());
//            cloudBuildInfo.setType(type);
//            cloudBuildInfo.setBranch(branch);
//            dao.insert(cloudBuildInfo);
//            return Result.success(true);
//        } catch (Exception e) {
//            return new Result<>(1, e.getMessage(), false);
//        }
//    }
//
//    public Result<Boolean> updateDockerImage(Integer id, SessionAccount account, String desc) {
//        CloudBuildInfo cloudBuildInfo = dao.fetch(CloudBuildInfo.class, Cnd.where("id", "=", id));
//        if (null == cloudBuildInfo) {
//            return new Result<>(1, "该记录已经不存在", false);
//        }
//
//        GitlabAccessToken gitlabAccessToken =
//                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
//        if (null == gitlabAccessToken) {
//            return new Result<>(1, "找不到授权的access token", false);
//        }
//
//        try {
//            cloudBuildInfo.setDesc(desc);
//            cloudBuildInfo.setUpdater(account.getUsername());
//            cloudBuildInfo.setUtime(System.currentTimeMillis());
//            dao.update(cloudBuildInfo);
//            return Result.success(true);
//        } catch (Exception e) {
//            return new Result<>(1, e.getMessage(), false);
//        }
//    }
//
//    public Result<List<GitlabCommit>> getGitlabCommits(String gitAddress, SessionAccount account) throws IOException {
//        GitlabAccessToken gitlabAccessToken =
//                gitlabService.getAccessTokenByUsername(account.getUsername(), ProjectService.parseGitDomain(gitAddress)).getData();
//        if (null == gitlabAccessToken) {
//            return new Result<>(1, "找不到授权的access token", null);
//        }
//        String[] strings = gitAddress.split("/");
//        if (strings.length < 2) {
//            return new Result<>(1, "git地址错误", null);
//        }
//        String group = strings[strings.length - 2];
//        String name = strings[strings.length - 1];
//        return gitlabService.getGitlabAllCommit(ProjectService.parseGitHost(gitAddress), URLEncoder.encode(group + "/" + name, "UTF-8"), "master", gitlabAccessToken.getToken());
//    }
//
//    public Result<Boolean> deleteDcokerImage(int id, SessionAccount account) {
//        CloudBuildInfo cloudBuildInfo = dao.fetch(CloudBuildInfo.class, Cnd.where("id", "=", id));
//        if (null == cloudBuildInfo) {
//            return new Result<>(1, "该记录已经不存在", false);
//        }
//        dao.delete(cloudBuildInfo);
//        return Result.success(true);
//    }
//
//    public Result<Boolean> buildDockerImage(int id, SessionAccount account, int type) {
//        CloudBuildInfo cloudBuildInfo = dao.fetch(CloudBuildInfo.class, Cnd.where("id", "=", id));
//        if (cloudBuildInfo == null) {
//            return new Result<>(1, "该记录已经不存在", false);
//        }
//        GitlabAccessToken gitlabAccessToken =
//                gitlabService.getAccessTokenByUsername(account.getUsername(), ProjectService.parseGitDomain(cloudBuildInfo.getGitAddress())).getData();
//        if (null == gitlabAccessToken) {
//            return new Result<>(1, "找不到授权的access token", null);
//        }
//        if (cloudBuildInfo.getStatus() != Consts.DOCKER_IMAGE_STATUS_EFFECT) {
//            return new Result<>(1, "只有审核通过的记录才允许docker build", false);
//        }
//        if (!CloudBuildTypeEnum.isValidity(type)) {
//            return new Result<>(1, "不支持的构建类型", false);
//        }
//
//        if (type == CloudBuildTypeEnum.JAR.getId()) {
//            CompileParam compileParam = new CompileParam();
//            compileParam.setGitUrl(cloudBuildInfo.getGitAddress() + ".git");
//            compileParam.setBranch(cloudBuildInfo.getCommitId());
//            compileParam.setGitName(gitlabAccessToken.getName());
//            compileParam.setGitToken(gitlabAccessToken.getToken());
//            compileParam.setBuildPath("");
//            compileParam.setJarPath("");
//            compileParam.setProfile("");
//            compileParam.setCustomParams("");
//            ProjectCompileRecord projectCompileRecord =
//                    compilationService.startCloudCompile(compileParam);
//            cloudBuildInfo.setCompilationId(projectCompileRecord.getId());
//            dao.update(cloudBuildInfo);
//        } else if (type == CloudBuildTypeEnum.MIRROR.getId()) {
//            DockerParam dockerParam = new DockerParam();
//            dockerParam.setGitUrl(cloudBuildInfo.getGitAddress() + ".git");
//            dockerParam.setGitUser(gitlabAccessToken.getUsername());
//            dockerParam.setGitToken(gitlabAccessToken.getToken());
//            StringBuffer appName = new StringBuffer();
//            appName.append(cloudBuildInfo.getGroupName());
//            appName.append(".");
//            appName.append(cloudBuildInfo.getProjectName());
//            dockerParam.setAppName(appName.toString().toLowerCase());
//            dockerParam.setBranch(cloudBuildInfo.getCommitId());
//            ProjectCompileRecord projectCompileRecord = dockerfileService.startDockerBuild(dockerParam).getData();
//            cloudBuildInfo.setCompilationId(projectCompileRecord.getId());
//            dao.update(cloudBuildInfo);
//        }
//        return Result.success(true);
//    }
//
//    public Map<String, Object> getListByType(int page, int pageSize, int status, int type) {
//        List<CloudBuildInfo> list = dao.query(CloudBuildInfo.class,
//                Cnd.where("status", status == 0 ? ">=" : "=", status)
//                        .and("type", "=", type)
//                        .desc("ctime"),
//                new Pager(page, pageSize));
//        list.stream().forEach(it -> {
//            dao.fetchLinks(it, null);
//        });
//        Map<String, Object> result = new HashMap<>();
//        result.put("list", list);
//        result.put("total", dao.count("docker_image_info", Cnd.where("status", status == 0 ? ">=" : "=", status).and("type", "=", type)));
//        result.put("page", page);
//        result.put("pageSize", pageSize);
//        return result;
//    }
//
//    public Result<Boolean> rejectDockerImage(Integer id, SessionAccount account) {
//        CloudBuildInfo dockerImageInfoBo = dao.fetch(CloudBuildInfo.class, Cnd.where("id", "=", id));
//        if (dockerImageInfoBo == null
//                || dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_DELETE) {
//            return new Result<>(1, "该记录不存在", false);
//        }
//        if (dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_REJECT
//                || dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_EFFECT) {
//            return new Result<>(2, "已经审核过", false);
//        }
//
//        if (!projectService.isOwner(dockerImageInfoBo.getProjectId(), account)) {
//            return new Result<>(4, "您没有权限审核", false);
//        }
////        String userName = account.getUsername();
////        if (userName.equals(dockerImageInfoBo.getCreator()) || userName.equals(dockerImageInfoBo.getUpdater())) {
////            return new Result<>(4, "不允许审核自己创建或更新的", false);
////        }
//        // 更新信息
//        dao.update(CloudBuildInfo.class,
//                Chain.make("status", Consts.DOCKER_IMAGE_STATUS_REJECT),
//                Cnd.where("id", "=", id));
//
//        return Result.success(true);
//    }
//
//    public Result<Boolean> effectDockerImage(Integer id, SessionAccount account) {
//        CloudBuildInfo dockerImageInfoBo = dao.fetch(CloudBuildInfo.class, Cnd.where("id", "=", id));
//        if (dockerImageInfoBo == null
//                || dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_DELETE) {
//            return new Result<>(1, "该记录不存在", false);
//        }
//        if (dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_EFFECT
//                || dockerImageInfoBo.getStatus() == Consts.DOCKER_IMAGE_STATUS_REJECT) {
//            return new Result<>(2, "已经审核过", false);
//        }
//        if (!projectService.isOwner(dockerImageInfoBo.getProjectId(), account)) {
//            return new Result<>(4, "您没有权限审核", false);
//        }
////        String userName = account.getUsername();
////        if (userName.equals(dockerImageInfoBo.getCreator()) || userName.equals(dockerImageInfoBo.getUpdater())) {
////            return new Result<>(4, "不允许审核自己创建或更新的", false);
////        }
//
//        // 更新信息
//        dao.update(CloudBuildInfo.class,
//                Chain.make("status", Consts.DOCKER_IMAGE_STATUS_EFFECT),
//                Cnd.where("id", "=", id));
//
//        return Result.success(true);
//    }
//
//    public List<ProjectCompileRecord> getBuildList(String gitName, String gitGroup, int type, int page, int pageSize) {
//        List<CloudBuildInfo> list = dao.query(CloudBuildInfo.class,
//                Cnd.where("group_name", "=", gitGroup)
//                        .and("project_name", "=", gitName)
//                        .and("type", "=", type)
//                        .and("status", "=", 2)
//                        .desc("ctime"),
//                new Pager(page, pageSize));
//
//        return list.stream().map(it -> {
//            dao.fetchLinks(it, null);
//            return it.getBuildRecord();
//        }).filter(it -> {
//            if (null != it && it.getStatus() == 1) {
//                return true;
//            }
//            return false;
//        }).collect(Collectors.toList());
//    }
//}
