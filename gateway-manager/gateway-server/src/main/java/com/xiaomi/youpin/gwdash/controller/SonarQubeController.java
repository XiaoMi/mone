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
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.bo.SonarQubeBo;
//import com.xiaomi.youpin.gwdash.bo.SonarQubeParam;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.GitlabAccessToken;
//import com.xiaomi.youpin.gwdash.dao.model.Project;
//import com.xiaomi.youpin.gwdash.service.GitlabService;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import com.xiaomi.youpin.gwdash.service.ProjectService;
//import com.xiaomi.youpin.gwdash.service.SonarQubeService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
///**
// * @author Zheng Xu zheng.xucn@outlook.com
// * <p>
// * 上传项目到SonarQube做代码分析
// */
//
//
//@RestController
//@Slf4j
//public class SonarQubeController {
//
//    @Autowired
//    private LoginService loginService;
//
//    @Autowired
//    private SonarQubeService sonarQubeService;
//
//    @Autowired
//    private GitlabService gitlabService;
//
//    @Autowired
//    private ProjectService projectService;
//
//
//    // This method is used for local testing
//    @RequestMapping(value = "/sonarqube/members", method = RequestMethod.GET)
//    public Result<List<String>> getProjectMembers(HttpServletRequest request, @RequestParam long projectId) {
//        return Result.success(sonarQubeService.getProjectMembersUsername(projectId));
//    }
//
//
//    @RequestMapping(value = "/api/sonarqube/delete", method = RequestMethod.DELETE)
//    public Result<Boolean> deleteConfig(HttpServletRequest request, @RequestParam long projectId) {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (account == null) {
//            return new Result<>(1, "用户不存在", null);
//        }
//        sonarQubeService.deleteConfig(projectId);
//        return Result.success(true);
//    }
//
//    @RequestMapping(value = "/api/sonarqube/metrics", method = RequestMethod.GET)
//    public Result<String> getApiMetrics(HttpServletRequest request, @RequestParam String projectKey) {
//
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (account == null) {
//            return new Result<>(1, "用户不存在", null);
//        }
//
//        return Result.success(sonarQubeService.getApiMetrics(projectKey));
//    }
//
//
//    @RequestMapping(value = "/api/sonarqube/allmetrics", method = RequestMethod.GET)
//    public Result<String> getAllApiMetrics(HttpServletRequest request) {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (account == null) {
//            return new Result<>(1, "用户不存在", null);
//        }
//
//        return Result.success(sonarQubeService.getAllApiMetrics());
//    }
//
//
//    @RequestMapping(value = "/api/sonarqube/config", method = RequestMethod.GET)
//
//    public Result<SonarQubeBo> getConfig(HttpServletRequest request, @RequestParam long projectId) {
//
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (account == null) {
//            return new Result<>(1, "用户不存在", null);
//        }
//
//        return Result.success(sonarQubeService.getConfig(projectId));
//    }
//
//
//    @RequestMapping(value = "/api/sonarqube/allconfigs", method = RequestMethod.GET)
//    public Result<List<SonarQubeBo>> getAllConfigs(HttpServletRequest request) {
//
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (account == null) {
//            return new Result<>(1, "用户不存在", null);
//        }
//        return Result.success(sonarQubeService.getAllConfigs());
//    }
//
//
//    @RequestMapping(value = "/api/sonarqube/start", method = RequestMethod.GET)
//    public Result<Boolean> startSonarQubeTask(HttpServletRequest request, @RequestParam long projectId) {
//
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (account == null) {
//            return new Result<>(1, "用户不存在", false);
//        }
//
//        return Result.success(sonarQubeService.startSonarQubeTask(projectId));
//    }
//
//    @RequestMapping(value = "/api/sonarqube/pause", method = RequestMethod.GET)
//    public Result<Boolean> pauseSonarQubeTask(HttpServletRequest request, @RequestParam long projectId) {
//
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (account == null) {
//            return new Result<>(1, "用户不存在", false);
//        }
//
//        return Result.success(sonarQubeService.pauseSonarQubeTask(projectId));
//    }
//
//    @RequestMapping(value = "/api/sonarqube/update", method = RequestMethod.PUT)
//    public Result<Boolean> updateConfig(
//        @RequestBody SonarQubeParam param,
//        HttpServletRequest request) {
//
//        SessionAccount account = loginService.getAccountFromSession(request);
//
//        if (account == null) {
//            return new Result<>(1, "用户不存在", false);
//        }
//
//        Result<Project> projectResult = projectService.getProjectById(param.getProjectId());
//        if (projectResult == null || projectResult.getData() == null) {
//            return new Result<>(1, "项目不存在", false);
//        }
//
//        GitlabAccessToken gitlabAccessToken = getGitLabInfo(account.getUsername(), param.getProjectId());
//        if (null == gitlabAccessToken) {
//            return new Result<>(1, "没有足够git权限", false);
//        }
//
//
//        Project project = projectResult.getData();
//
//        return Result.success(sonarQubeService.updateConfig(gitlabAccessToken, project, param));
//    }
//
//
//    @RequestMapping(value = "/api/sonarqube/create", method = RequestMethod.POST)
//    public Result<Boolean> createConfig(
//        @RequestBody SonarQubeParam param,
//        HttpServletRequest request) {
//        SessionAccount account = loginService.getAccountFromSession(request);
//
//        if (account == null) {
//            return new Result<>(1, "用户不存在", false);
//        }
//        Result<Project> projectResult = projectService.getProjectById(param.getProjectId());
//        if (projectResult == null || projectResult.getData() == null) {
//            return new Result<>(1, "项目不存在", false);
//        }
//
//
//        GitlabAccessToken gitlabAccessToken = getGitLabInfo(account.getUsername(), param.getProjectId());
//        if (null == gitlabAccessToken) {
//            return new Result<>(1, "没有足够git权限", false);
//        }
//
//
//        Project project = projectResult.getData();
//        sonarQubeService.createConfig(gitlabAccessToken, project, param);
//
//        return Result.success(true);
//    }
//
//    @RequestMapping(value = "/api/sonarqube/permission/add", method = RequestMethod.POST)
//    public Result<Boolean> addPermission(HttpServletRequest request, @RequestParam long projectId,
//                                         @RequestParam String projectKey) {
//
//        sonarQubeService.addPermission(projectId, projectKey);
//        return Result.success(true);
//    }
//
//    @RequestMapping(value = "/api/sonarqube/upload", method = RequestMethod.POST)
//    public Result<Boolean> uploadProject(
//        @RequestBody SonarQubeParam param,
//        HttpServletRequest request) {
//
//        // FilterConfiguration.java
//        // registrationBean.addInitParameter("IGNORE_URL", "/sonarqube/upload");
//
//        SessionAccount account = loginService.getAccountFromSession(request);
//
//        if (account == null) {
//            return new Result<>(1, "用户不存在", false);
//        }
//        Result<Project> projectResult = projectService.getProjectById(param.getProjectId());
//        if (projectResult == null || projectResult.getData() == null) {
//            return new Result<>(1, "项目不存在", false);
//        }
//
//        GitlabAccessToken gitlabAccessToken = getGitLabInfo(account.getUsername(), param.getProjectId());
//
//        if (null == gitlabAccessToken) {
//            return new Result<>(1, "没有足够git权限", false);
//        }
//
//        Project project = projectResult.getData();
//
//        log.info("upload project to sonarqube");
//        return sonarQubeService.uploadProject(gitlabAccessToken, project, param.getBranch(), param.getProjectKey(),
//            param.getProfile(), param.getBuildPath());
//    }
//
//
//    private GitlabAccessToken getGitLabInfo(String username, long projectId) {
//        GitlabAccessToken accessToken = gitlabService.getAccessTokenByUsername(sonarQubeService.getProjectOneMemberUsername(username, projectId)).getData();
//        if (accessToken != null) {
//            return accessToken;
//        }
//        return gitlabService.getAccessTokenByUsername(username).getData();
//    }
//}
