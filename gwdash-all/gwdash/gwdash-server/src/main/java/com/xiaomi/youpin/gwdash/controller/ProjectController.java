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

package com.xiaomi.youpin.gwdash.controller;

import com.ksyun.ks3.utils.StringUtils;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
import com.xiaomi.youpin.gwdash.bo.ProjectMemberBo;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Keys;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.dao.model.ProjectRole;
import com.xiaomi.youpin.gwdash.dao.model.RoleType;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.ApiServerBillingService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.ProjectService;
import com.xiaomi.youpin.tesla.billing.bo.BResult;
import com.xiaomi.youpin.tesla.billing.bo.ReportRes;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * 项目管理
 */
@RestController
@Slf4j
public class ProjectController {

    @Autowired
    private Dao dao;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApiServerBillingService apiServerBillingService;

    @Autowired
    private Redis redis;

    /**
     * 创建项目
     *
     * @return
     */
    @RequestMapping(value = "/api/project/create", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> create(HttpServletRequest request, @RequestBody Project param) throws UnsupportedEncodingException {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectService.createProject(account, param);
    }

    @RequestMapping(value = "/api/project/info", method = RequestMethod.GET)
    public Result<Project> getProjectById(@RequestParam("id") long id) {
        return projectService.getProjectById(id);
    }

    /**
     * 生成代码
     * @param param
     * @return
     */
    @RequestMapping(value = "/api/project/generateCode", method =
            RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> generateCode (
            HttpServletRequest request,
            @RequestBody Project param) throws UnsupportedEncodingException {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectService.generateCode(account, param);
    }

    /**
     * 获取项目列表
     *
     * @param request
     * @param param
     * @return
     */
    @RequestMapping(value = "/api/project/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Map<String, Object>> list(HttpServletRequest request, @RequestBody Project param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectService.listProjects(account, param);
    }

    @RequestMapping(value = "/api/project/delete", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> delete(HttpServletRequest request, @RequestBody Project param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectService.deleteProject(account, param);
    }

    @RequestMapping(value = "/api/project/branch", method = RequestMethod.POST)
    public Result<List<String>> projectBranch(
            HttpServletRequest request,
            @RequestParam("group") String group,
            @RequestParam("name") String name,
            @RequestParam("search") String search
    ) throws GitAPIException, IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectService.getGitlabBranches(group, name, account, search);
    }

    @RequestMapping(value = "/api/project/commits", method = RequestMethod.POST)
    public Result<List<GitlabCommit>> projectCommit(
            HttpServletRequest request,
            @RequestParam("group") String group,
            @RequestParam("name") String name,
            @RequestParam("branch") String branch
    ) throws GitAPIException, IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectService.getGitlabCommits(group, name, branch, account);
    }

    @RequestMapping(value = "/api/project/update", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> update(HttpServletRequest request, @RequestBody Project param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectService.updateProject(account, param);
    }

    /**
     * 添加拥有者
     *
     * @param request
     * @param param
     * @return
     */
    @RequestMapping(value = "/api/project/addMembers", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> addMembers(HttpServletRequest request, @RequestBody ProjectMemberBo param) {
        long now = System.currentTimeMillis();
        log.info("addMembers 0.0.3");
        if (param.getProjectId() <= 0 || param.getRoleType() == null || param.getRoleType() < 0) {
            return Result.fail(CommonError.InvalidParamError);
        }

        SessionAccount account = loginService.getAccountFromSession(request);
        long id = param.getProjectId();

        if (param.getRoleType() == RoleType.Tester.ordinal() && (account == null || CollectionUtils.isEmpty(account.getRoles()) || account.getRoles().stream().filter(e -> e.getName().equals("tester")).findAny().isPresent() == false)) {
            //tester才可以编辑测试人员
            return new Result<>(2, "测试管理员才能操作", true);
        } else if (!projectService.isOwner(id, account)) {
            return new Result<>(2, "应用owner才能操作", true);
        }
        List<Integer> members = param.getMembers();
        dao.clear(ProjectRole.class, Cnd.where("projectId", "=", param.getProjectId()).and("roleType", "=", param.getRoleType()));
        if (!CollectionUtils.isEmpty(members)) {
            members.stream().forEach(memeberId -> {
                ProjectRole projectRole = new ProjectRole();
                projectRole.setProjectId(param.getProjectId());
                projectRole.setAccountId(memeberId);
                projectRole.setRoleType(param.getRoleType());
                projectRole.setCtime(now);
                projectRole.setUtime(now);

                int count = dao.count(ProjectRole.class, Cnd.where("projectId", "=", param.getProjectId()).and("accountId", "=", memeberId).and("roleType", "=", param.getRoleType()));
                if (count <= 0) {
                    dao.insert(projectRole);
                } else {
                    dao.clear(ProjectRole.class, Cnd.where("projectId", "=", param.getProjectId()).and("accountId", "=", memeberId).and("roleType", "=", param.getRoleType()));
                    dao.insert(projectRole);
                }

            });
        }
        return Result.success(true);
    }


    @RequestMapping(value = "/api/project/members", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<List<ProjectRole>> members(HttpServletRequest request, @RequestBody Project param) {
        List<ProjectRole> data = dao.query(ProjectRole.class, Cnd.where("projectId", "=", param.getId()));
        return Result.success(data);
    }

    @RequestMapping(value = "/api/project/bill/report/test", method = RequestMethod.GET)
    public BResult<ReportRes> getBillReport(
            @RequestParam("projectId") long projectId,
            @RequestParam("envId") long envId) {
        return apiServerBillingService.report(projectId, envId);
    }

    @RequestMapping(value = "/api/project/leftDeployNum", method = RequestMethod.GET)
    public Result getLeftDeployNum(
            @RequestParam("projectId") long projectId) {
        Result<Project> project = projectService.getProjectById(projectId);
        if (project.getData() == null) {
            return Result.fail(CommonError.InvalidParamError);
        }

        String deployedNum = redis.get(Keys.deployKey(projectId));
        int depNum;
        if (StringUtils.isBlank(deployedNum)||"null".equals(deployedNum)) {
            depNum = 0;
        } else {
            depNum = Integer.parseInt(deployedNum);
        }
        return Result.success(project.getData().getDeployLimit() - depNum);
    }

    @RequestMapping(value = "/api/project/init/bill", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> initBill(HttpServletRequest request, @RequestBody Project param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        return projectService.initBill(account, param);
    }

    @RequestMapping(value = "/api/project/statistics", method = RequestMethod.GET)
    public Result<Object> statistics() {
        return projectService.statistics();
    }

}
