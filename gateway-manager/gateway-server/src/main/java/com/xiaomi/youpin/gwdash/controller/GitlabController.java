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
//import com.xiaomi.mone.miflow.api.model.vo.gitlab.*;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.*;
//import com.xiaomi.youpin.gwdash.exception.CommonError;
//import com.xiaomi.youpin.gwdash.service.GitlabService;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.ListUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.lib.Ref;
//import org.eclipse.jgit.transport.CredentialsProvider;
//import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//import java.io.File;
//import java.io.IOException;
//import java.util.*;
//
///**
// * @author tsingfu
// */
//@RestController
//@Slf4j
//public class GitlabController {
//
//    @Autowired
//    private GitlabService gitlabService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @RequestMapping(value = "/api/gitlab/token/new", method = RequestMethod.POST)
//    public Result<Boolean> createAccessToken(
//            HttpServletRequest request,
//            @RequestParam("name") String name,
//            @RequestParam("token") String token,
//            @RequestParam(value = "desc", defaultValue = "", required = false) String desc,
//            @RequestParam(value = "domain", defaultValue = "git.n.xiaomi.com", required = false) String domain
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return gitlabService.createAccessToken(sessionAccount.getUsername(), name, token, desc, domain);
//    }
//
//    @RequestMapping(value = "/api/gitlab/token/edit", method = RequestMethod.POST)
//    public Result<Boolean> editAccessToken(
//            HttpServletRequest request,
//            @RequestParam("id") long id,
//            @RequestParam("name") String name,
//            @RequestParam("token") String token,
//            @RequestParam(value = "desc", defaultValue = "", required = false) String desc
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return gitlabService.editAccessToken(sessionAccount.getUsername(), id, name, token, desc);
//    }
//
//    @RequestMapping(value = "/api/gitlab/token/del", method = RequestMethod.GET)
//    public Result<Boolean> delAccessToken(
//            HttpServletRequest request,
//            @RequestParam("id") long id
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return gitlabService.delAccessToken(sessionAccount.getUsername(), id);
//    }
//
//    @RequestMapping(value = "/api/gitlab/token/list", method = RequestMethod.GET)
//    public Result<Map<String, Object>> getAccessToken(
//            HttpServletRequest request,
//            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
//            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return gitlabService.getAccessToken(sessionAccount.getUsername(), page, pageSize);
//    }
//
//    @RequestMapping(value = "/api/gitlab/branch", method = RequestMethod.GET)
//    public Result<List<String>> getGitlabBranch(
//            HttpServletRequest request,
//            @RequestParam(value = "group") String group,
//            @RequestParam(value = "name") String name
//    ) throws IOException, GitAPIException {
//        SessionAccount session = loginService.getAccountFromSession(request);
//        GitlabAccessToken gitlabAccessToken = gitlabService.getAccessTokenByUsername(session.getUsername()).getData();
//        if (null != gitlabAccessToken) {
//            return new Result<>(1, "gitlab access token不存在，需要添加", null);
//        }
//        String path = System.getProperty("java.io.tmpdir") + File.separator + group + File.separator + name + File.separator;
//        CredentialsProvider cp = new UsernamePasswordCredentialsProvider("test", "");
//        Git git = Git.cloneRepository().setURI("youpin-server/mytest.git").setCredentialsProvider(cp).setDirectory(new File(path)).call();
//        Collection<Ref> refs = git.lsRemote().setRemote("origin").call();
//        List<String> list = new ArrayList<>();
//        refs.stream().forEach(it -> {
//            list.add(it.getName());
//        });
//        return Result.success(list);
//    }
//
//    @RequestMapping(value = "/api/gitlab/branch/add", method = RequestMethod.POST)
//    public Result<BranchBo> createBranch(
//            HttpServletRequest request,
//            @RequestBody @Valid BranchBo bo
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return gitlabService.createBranch(sessionAccount.getUsername(), bo.getProjectId(), bo.getBranchName(), bo.getRefBranchName());
//    }
//
//    @RequestMapping(value = "/api/gitlab/branch/delete", method = RequestMethod.DELETE)
//    public Result<Boolean> deleteBranch(
//            HttpServletRequest request,
//            @RequestParam("branchId") long branchId
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return gitlabService.deleteBranch(sessionAccount.getUsername(), branchId);
//    }
//
//    @RequestMapping(value = "/api/gitlab/branch/qry", method = RequestMethod.POST)
//    public Result<BranchListResult> qryBranch(
//            HttpServletRequest request,
//            @RequestBody @Valid BranchListParam param
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return gitlabService.getProjectBranches(param);
//    }
//
//    /*@RequestMapping(value = "/api/gitlab/branch/release/qry", method = RequestMethod.POST)
//    public Result<List<ProjectGitlabBranch>> qryReleaseBranch(
//            HttpServletRequest request,
//            @RequestBody @Valid BranchListParam param
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return gitlabService.getProjectReleaseBranches(param);
//    }*/
//
//    @RequestMapping(value = "/api/gitlab/branchIntegration", method = RequestMethod.POST)
//    public Result<BranchIntegrationBo> createBranchIntegration(
//            HttpServletRequest request,
//            @RequestBody @Valid BranchIntegrationBo branchIntegrationBo
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        if (branchIntegrationBo.getProjectId() <= 0 || branchIntegrationBo.getEnvId() <= 0){
//            return new Result<>(CommonError.InvalidParamError.getCode(), "缺少入参");
//        }
//        return gitlabService.createMergeIntegration(branchIntegrationBo, sessionAccount.getUsername());
//    }
//
//    @RequestMapping(value = "/api/gitlab/branchIntegration/qry", method = RequestMethod.POST)
//    public Result<BranchIntegrationListResult> qryBranchIntegration(
//            HttpServletRequest request,
//            @RequestBody @Valid BranchListParam param
//    ) {
//        if (param.getEnvId() <= 0 ){
//            return new Result<>(CommonError.InvalidParamError.getCode(), "envId is Invalid");
//        }
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return gitlabService.getProjectBrancheIntegrations(param);
//    }
//
//    @RequestMapping(value = "/api/gitlab/merge", method = RequestMethod.POST)
//    public Result<BranchIntegrationRunLogBo> createMerge(
//            HttpServletRequest request,
//            @RequestParam("branchIntegrationId") long branchIntegrationId
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return gitlabService.mergeBranch(sessionAccount.getUsername(), branchIntegrationId);
//    }
//
//    /**
//     * 集成分支运行历史列表
//     * @param request
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/api/gitlab/mergeLog/qry", method = RequestMethod.GET)
//    public Result<IntegrationRunLogListResult> qryMergeLog(
//            HttpServletRequest request,
//            @Valid BranchListParam param
//    ) {
//        if (param.getEnvId() <= 0 || param.getProjectId() <= 0 || param.getBranchIntegrationId() <=0){
//            return new Result<>(CommonError.InvalidParamError.getCode(), "param is invalid");
//        }
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return gitlabService.qryMergeLog(param);
//    }
//
//}
