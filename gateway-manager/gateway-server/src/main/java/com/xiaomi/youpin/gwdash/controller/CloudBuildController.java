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
//package com.xiaomi.youpin.gwdash.controller;
//
//import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.CloudBuildTypeEnum;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectCompileRecord;
//import com.xiaomi.youpin.gwdash.service.CloudBuildService;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import com.xiaomi.youpin.gwdash.service.ProjectService;
//import lombok.extern.slf4j.Slf4j;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import static com.xiaomi.youpin.gwdash.common.Consts.ROLE_ADMIN;
//
///**
// * @author dp
// */
//@RestController
//@Slf4j
//@RequestMapping("/api/cloud/build")
//public class CloudBuildController {
//
//    @Autowired
//    private CloudBuildService dockerImageService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @RequestMapping(value = "/jar/list", method = RequestMethod.GET)
//    public Result<List<ProjectCompileRecord>> getDockerImageList1(HttpServletRequest request,
//                                                           HttpServletResponse response,
//                                                           @RequestParam("gitName") String gitName,
//                                                           @RequestParam("gitGroup") String gitGroup,
//                                                           @RequestParam(required = false, value = "page", defaultValue = "1") int page,
//                                                           @RequestParam(required = false, value = "pageSize", defaultValue = "10") int pageSize) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[DockerImageController.getImageList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        List<ProjectCompileRecord> result = dockerImageService.getBuildList(gitName, gitGroup, CloudBuildTypeEnum.JAR.getId(), page, pageSize);
//        return Result.success(result);
//    }
//
//    /**
//     * 展示 image 列表
//     **/
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    public Result<Map<String, Object>> getDockerImageList(HttpServletRequest request,
//                                                          HttpServletResponse response,
//                                                          @RequestParam("type") int type,
//                                                          @RequestParam(required = false, value = "status", defaultValue = "0") int status,
//                                                          @RequestParam("page") int page,
//                                                          @RequestParam("pageSize") int pageSize) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[DockerImageController.getImageList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        Map<String, Object> result = dockerImageService.getListByType(page, pageSize, status, type);
//        return Result.success(result);
//    }
//
//    /**
//     * 创建新的docker image
//     * 新建
//     */
//    @RequestMapping(value = "/new", method = RequestMethod.POST)
//    public Result<Boolean> createDockerImage(
//            HttpServletRequest request,
//            @RequestParam("type") int type,
//            @RequestParam("projectId") long projectId,
//            @RequestParam("gitAddress") String gitAddress,
//            @RequestParam("groupName") String groupName,
//            @RequestParam("projectName") String projectName,
//            @RequestParam("commitId") String commitId,
//            @RequestParam("desc") String desc,
//            @RequestParam("branch") String branch
//    ) {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return dockerImageService.createDockerImage(account, projectId, gitAddress, groupName, projectName, commitId, desc, type,branch);
//    }
//
//    /**
//     * 更新 docker image (update)
//     */
//    @RequestMapping(value = "/update", method = RequestMethod.POST)
//    public Result<Boolean> updateDockerImage(
//            HttpServletRequest request,
//            @RequestParam("id") int id,
//            @RequestParam("desc") String desc
//    ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return dockerImageService.updateDockerImage(id, account, desc);
//    }
//
//    @RequestMapping(value = "/commits", method = RequestMethod.GET)
//    public Result<List<GitlabCommit>> getGitlabCommits(
//            HttpServletRequest request,
//            @RequestParam("gitAddress") String gitAddress
//    ) throws GitAPIException, IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return dockerImageService.getGitlabCommits(gitAddress, account);
//    }
//
//    /**
//     * 删除docker image
//     */
//    @RequestMapping(value = "/del", method = RequestMethod.POST)
//    public Result<Boolean> deleteFilter(
//            HttpServletRequest request,
//            @RequestParam("id") int id
//    ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return dockerImageService.deleteDcokerImage(id, account);
//    }
//
//    /**
//     * 构建docker image
//     * 构建
//     */
//    @RequestMapping(value = "/build", method = RequestMethod.POST)
//    public Result<Boolean> buildDockerImage(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestParam("type") int type,
//            @RequestParam("id") int id
//    ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return dockerImageService.buildDockerImage(id, account, type);
//    }
//
//    /**
//     * 审核通过
//     */
//    @RequestMapping(value = "/effect", method = RequestMethod.POST)
//    public Result<Boolean> effect(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return dockerImageService.effectDockerImage(id, account);
//    }
//
//    /**
//     * 审核不通过
//     */
//    @RequestMapping(value = "/reject", method = RequestMethod.POST)
//    public Result<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") int id) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        return dockerImageService.rejectDockerImage(id, account);
//    }
//}
