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

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.google.gson.Gson;
import com.xiaomi.youpin.gitlab.Gitlab;
import com.xiaomi.youpin.gitlab.bo.AccessLevel;
import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
import com.xiaomi.youpin.gitlab.bo.GitlabProject;
import com.xiaomi.youpin.gitlab.bo.GroupInfo;
import com.xiaomi.youpin.gwdash.bo.ProjectStatusEnum;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.service.AccountService;
import com.xiaomi.youpin.mischedule.STaskDef;
import com.xiaomi.youpin.mischedule.api.service.bo.RequestBo;
import com.xiaomi.youpin.tesla.billing.bo.ReportBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.gitlab.GitlabConst.GIT_BASE;

/**
 * @author tsingfu
 */
@Service
@Slf4j
public class ProjectService {

    @Autowired
    private Dao dao;

    @Autowired
    private GitlabService gitlabService;

    @Autowired
    private MyScheduleService myScheduleService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private ApiServerBillingService apiServerBillingService;

    @Autowired
    private UserService userService;

    @Autowired
    private Gson gson;

    @Reference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}")
    private AccountService accountService;

    private final String goType = "go";

    public Project getProjectByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        SqlExpression orSql = Cnd.exps("name", "=", name).or("desc", "=", name);
        List<Project> projects = dao.query(Project.class, Cnd.where("status", "!=", ProjectStatusEnum.DELETE.getId()).and(orSql));
        if (CollectionUtils.isEmpty(projects)) {
            return null;
        }
        return projects.get(0);
    }

    public Result<Project> getProjectById(long id) {
        return Result.success(dao.fetch(Project.class, Cnd.where("id", "=", id).and("status", "!=", ProjectStatusEnum.DELETE.getId())));
    }

    public Result<List<String>> getGitlabBranches(String group, String name, SessionAccount account, String search) throws IOException {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }

        return gitlabService.getGitlabBranch(URLEncoder.encode(group + "/" + name, "UTF-8"), gitlabAccessToken.getToken(), search);
    }

    public Result<List<GitlabCommit>> getGitlabCommits(String group, String name, String branch, SessionAccount account) throws IOException {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }

        return gitlabService.getGitlabAllCommit(URLEncoder.encode(group + "/" + name, "UTF-8"), branch, gitlabAccessToken.getToken());
    }

    public Result<List<GitlabCommit>> getGitlabBetweenCommits(String group, String name, String branch, SessionAccount account, String since, String until) throws GitAPIException, IOException {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }

        return gitlabService.getGitlabBetweenCommits(URLEncoder.encode(group + "/" + name, "UTF-8"), branch, gitlabAccessToken.getToken(), since, until);
    }

    public Result<GitlabCommit> getGitlabCommit(String group, String name, String sha, SessionAccount account) throws GitAPIException, IOException {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }

        return gitlabService.getGitlabCommit(URLEncoder.encode(group + "/" + name, "UTF-8"), sha, gitlabAccessToken.getToken());
    }
    public Project getProjectByEnvId(long envId){
        Sql sql =Sqls.create("SELECT P.id,P.name from project_env,project P WHERE project_env.id =@envId AND P.id = project_env.project_id");
        sql.setParam("envId",envId);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Project.class));
        List<Project> projects =dao.execute(sql).getList(Project.class);
        if(projects.size()>0){
            return  projects.get(0);
        }
        return null;
    }

    public Result<Boolean> createProject(SessionAccount account, Project param) {
        final String gitUrl = GIT_BASE + param.getGitGroup() + "/" + param.getGitName();
//        Project project = getFetch(param.getGitGroup(), param.getGitName());
//        if (null != project) {
//            return new Result<>(1, "项目已存在, 请联系ower添加", false);
//        }

        long now = System.currentTimeMillis();
        param.setGitAddress(gitUrl);
        param.setCtime(now);
        param.setUtime(now);
        param.setDeployLimit(3);
        dao.insert(param);

        ProjectRole projectRole = new ProjectRole();
        projectRole.setProjectId(param.getId());
        projectRole.setAccountId(account.getId().intValue());
        projectRole.setUtime(now);
        projectRole.setCtime(now);
        projectRole.setRoleType(RoleType.Owner.ordinal());
        dao.insert(projectRole);
        return Result.success(true);
    }

    private Project getFetch(String gitGroup, String gitName) {
        return dao.fetch(Project.class,
                Cnd.where("git_group", "=", gitGroup)
                        .and("git_name", "=", gitName)
                        .and("status", "!=", ProjectStatusEnum.DELETE.getId()));
    }

    public Result<Boolean> generateCode(ProjectGen param) {
        RequestBo requestBo = new RequestBo(STaskDef.GeneratorCodeTask.name());
        requestBo.setParam(new Gson().toJson(param));
        myScheduleService.submitTask(requestBo);
        return Result.success(true);
    }

    public Result<Boolean> generateCode(SessionAccount account, Project param) throws UnsupportedEncodingException {
        long id = param.getId();
        if (isOwner(id, account)) {
            Project project = getProjectById(param.getId()).getData();
            if (null == project) {
                return new Result<>(1, "项目未找到", false);
            }
            GitlabAccessToken gitlabAccessToken =
                    gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
            if (null == gitlabAccessToken) {
                return new Result<>(2, "需gitlab access token授权", false);
            }
            String gitGroup = project.getGitGroup();
            GroupInfo groupInfo = gitlabService.getNamespacesById(URLEncoder.encode(gitGroup, "UTF-8"), gitlabAccessToken.getToken()).getData();
            if (null == groupInfo || StringUtils.isEmpty(groupInfo.getKind())) {
                return new Result<>(3, "需git帐号权限为Maintainer或Owner", false);
            }
            boolean isGen = "user".equals(groupInfo.getKind()) || Gitlab.accessLevelMoreThan(AccessLevel.Maintainer, gitlabAccessToken.getToken(), project.getGitGroup());
            if (isGen == false) {
                return new Result<>(3, "需git帐号权限为Maintainer或Owner", false);
            }
            String gitName = project.getGitName();
            String gitUrl = GIT_BASE + gitGroup + "/" + gitName;
            GitlabProject gitlabProject = gitlabService.getTheProject(URLEncoder.encode(gitGroup + "/" + gitName, "UTF-8"), gitlabAccessToken.getToken()).getData();
            if (null != gitlabProject && StringUtils.isNotEmpty(gitlabProject.getWeb_url())) {
                return new Result<>(4, "需git仓库不存在才可创建", false);
            }
            project.setProjectGen(param.getProjectGen());
            ProjectGen projectGen = param.getProjectGen();
            projectGen.setProjectName(param.getGitName());
            projectGen.setGitUser(gitlabAccessToken.getName());
            projectGen.setGitToken(gitlabAccessToken.getToken());
            projectGen.setGitAddress(gitUrl);
            String params = projectGen.getParams();
            if (null == params || params.equals("")) {
                projectGen.setParams("[]");
            }
            generateCode(param.getProjectGen());
            dao.update(project);
            return Result.success(true);
        }

        return new Result<>(1, "需项目ower成员操作", false);
    }

    public Result<Boolean> updateProject(SessionAccount account, Project param) {
        long id = param.getId();

        if (isOwner(id, account)) {
            Project project = getProjectById(param.getId()).getData();
            if (null == project) {
                return new Result<>(1, "未找到要更新项目", false);
            }
//            String gitGroup = param.getGitGroup();
//            String gitName = param.getGitName();
//            Project existProject = getFetch(gitGroup, gitName);
//            if (null != existProject
//                    && existProject.getId() != project.getId()) {
//                return new Result<>(2, "项目更新失败, git地址已有对用项目", false);
//            }
            project.setName(param.getName());
            project.setGitGroup(param.getGitGroup());
            project.setGitName(param.getGitName());
            String gitUrl = GIT_BASE + param.getGitGroup() + "/" + param.getGitName();
            project.setGitAddress(gitUrl);
            project.setDesc(param.getDesc());
            project.setUtime(System.currentTimeMillis());
            ProjectGen projectGen = project.getProjectGen();
            ProjectGen paramGen = param.getProjectGen();
            if (null == projectGen) {
                project.setProjectGen(paramGen);
            } else if (null != paramGen) {
                projectGen.setType(paramGen.getType());
            }
            dao.update(project);
            return Result.success(true);
        }
        return new Result<>(2, "应用owner才能操作, 更新失败", false);
    }

    public boolean isOwner(long projectId, SessionAccount account) {
        long count = account.getRoles().stream().filter(role -> "project-superuser".equals(role.getName())).count();
        if (count > 0) {
            return true;
        }
        int accountId = account.getId().intValue();
        count = dao.count(ProjectRole.class, Cnd.where("projectId", "=", projectId).and("accountId", "=", accountId).and("roleType", "=", RoleType.Owner.ordinal()));
        return count > 0;
    }
    public List<Integer> getOwnerByProjectId(long projectId){
        List<ProjectRole> projectRoles = dao.query(ProjectRole.class,Cnd.where("projectId","=",projectId).and("roleType","=",RoleType.Owner.ordinal()));
        return projectRoles.stream().map(it->it.getAccountId()).collect(Collectors.toList());
    }

    public boolean isProjectSuperUser(SessionAccount account) {
        long count = account.getRoles().stream().filter(role -> "project-superuser".equals(role.getName())).count();
        if (count > 0) {
            return true;
        }
        return false;
    }

    public boolean isOwner(long projectId, long accountId) {
        long count = dao.count(ProjectRole.class, Cnd.where("projectId", "=", projectId).and("accountId", "=", accountId).and("roleType", "=", RoleType.Owner.ordinal()));
        return count > 0;
    }

    public boolean isMember(long projectId, SessionAccount account) {
        int accountId = account.getId().intValue();
        return isMember(projectId, accountId);
    }

    public List<ProjectRole> getTesters(long projectId) {
        return dao.query(ProjectRole.class, Cnd.where("projectId", "=", projectId).and("roleType", "=", RoleType.Tester.ordinal()));
    }

    public boolean isMember(long projectId, long accountId) {
        if (isOwner(projectId, accountId)) {
            return true;
        }
        long count = dao.count(ProjectRole.class, Cnd.where("projectId", "=", projectId).and("accountId", "=", accountId).and("roleType", "=", RoleType.Member.ordinal()));
        if (count > 0) {
            return true;
        }
        List<ProjectRole> groups = dao.query(ProjectRole.class, Cnd.where("projectId", "=", projectId).and("roleType", "=", RoleType.Member_Group.ordinal()));
        if (CollectionUtils.isEmpty(groups)) {
            return false;
        }
        List groupIds = groups.stream().map(e -> e.getAccountId()+"").collect(Collectors.toList());
        Account account = accountService.queryUserById(accountId);
        String[] gids = account.getGid().split("_");
        for (String gid : gids) {
            if (groupIds.contains(gid)) {
                return true;
            }
        }
        return false;
    }

    public Result<Boolean> deleteProject(SessionAccount account, Project param) {
        if (isOwner(param.getId(),account)) {
            Project project = getProjectById(param.getId()).getData();
            if (null == project) {
                return new Result<>(1, "项目不存在", false);
            }

            // project env check
            Result<List<ProjectEnv>> list = projectEnvService.getList(param.getId());
            if (!CollectionUtils.isEmpty(list.getData())) {
                return new Result<>(1, "需先删除所用部署环境", false);
            }

            project.setStatus(ProjectStatusEnum.DELETE.getId());
            dao.update(project);
            return Result.success(true);
        }
        return new Result<>(1, "项目owner才能操作", false);
    }

    public Result<Map<String, Object>> listProjects(SessionAccount account, Project param) {
        Cnd cnd = Cnd.where("status", "!=", ProjectStatusEnum.DELETE.getId());
        if (StringUtils.isNotBlank(param.getSearch())) {
            SqlExpressionGroup searchSql = Cnd.exps("name", "like", "%"+param.getSearch()+"%");
            cnd = cnd.and(searchSql);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        // 显示所有
        if (param.isShowAll()) {
            result.put("total", dao.count(Project.class, cnd));
            result.put("list", dao.query(Project.class, cnd, new Pager(param.getPage(), param.getPageSize())));
        } else {
            // 显示自己拥有的
            Account userInfo = userService.queryUserByName(account.getUsername());
            String sqlStr = "SELECT distinct(pr.projectId) FROM project_role as pr left outer join project as p on p.id = pr.projectId " +
                    "WHERE p.status != @status " +
                    "and ((accountId=@accountId and roleType in (" + RoleType.Owner.ordinal() + ", " + RoleType.Member.ordinal() + ")) " +
                    "or (accountId in (" + userInfo.getGid().replace("_", ",") + ") and roleType=" + RoleType.Member_Group.ordinal() + "))";
            Sql sql = Sqls.create(sqlStr);
            sql.params().set("status",  ProjectStatusEnum.DELETE.getId());
            sql.params().set("accountId", account.getId());
            sql.setCallback((Connection conn, ResultSet rs, Sql sql1) -> {
                List<String> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(rs.getString("pr.projectId"));
                }
                return list;
            });
            List<String> projectIds = dao.execute(sql).getList(String.class);
            result.put("total", projectIds.size());
            cnd.and("id", "in", projectIds.stream().reduce((a, b) -> a + "," + b).get());
            result.put("list", dao.query(Project.class, cnd, new Pager(param.getPage(), param.getPageSize())));
        }
        return Result.success(result);
    }

    public List<Project> getAllProjects(Long accountId) {

        StringBuilder searchSql = new StringBuilder("select * from project p join project_role pr on p.id = pr.projectId where p.status != @status and pr.accountId = @accountId ");
        Sql selectSql = Sqls.create(searchSql.toString());
        selectSql.params().set("status", ProjectStatusEnum.DELETE.getId());
        selectSql.params().set("accountId", accountId);
        selectSql.setCallback((Connection conn, ResultSet rs, Sql sql) -> {
            List<Project> list = new ArrayList<>();
            while (rs.next()) {
                Project project = new Project();
                project.setId(rs.getInt("p.id"));
                project.setGitAddress(rs.getString("p.gitAddress"));
                project.setGitName(rs.getString("p.git_name"));
                project.setGitGroup(rs.getString("p.git_group"));
                project.setDesc(rs.getString("p.desc"));
                project.setName(rs.getString("p.name"));
                project.setStatus(rs.getInt("p.status"));
                project.setCtime(rs.getLong("p.ctime"));
                project.setUtime(rs.getLong("p.utime"));
                list.add(project);
            }
            return list;
        });
        return dao.execute(selectSql).getList(Project.class);
    }

    public Result<Boolean> initBill(SessionAccount account, Project param) {
        if (isOwner(param.getId(),account)) {
            Project project = getProjectById(param.getId()).getData();
            if (null == project) {
                return new Result<>(1, "项目不存在", false);
            }
            List<ProjectEnv> list = projectEnvService.getList(param.getId()).getData();
            if (!CollectionUtils.isEmpty(list)) {
                ReportBo reportBo = new ReportBo();
                reportBo.setBizId(project.getId());
                reportBo.setName(project.getName());
                reportBo.setSubBizIdList(list.stream().map(it -> it.getId()).collect(Collectors.toList()));
                apiServerBillingService.initReport(reportBo);
            } else {
                log.info("initBill env list is empty: {}", param.getId());
            }
            return Result.success(true);
        }
        return new Result<>(1, "项目owner才能操作", false);
    }

    public Result<Object> statistics() {
        Map<String, Object> res = new HashMap<>();
        long endTime = System.currentTimeMillis();
        long startTime = endTime - (7 * 24 * 60 * 60 * 1000);
        List<ProjectPipeline> list = dao.query(ProjectPipeline.class, Cnd.where("ctime", "between", new Object[]{startTime, endTime}));
        Map<String, Integer> member = new HashMap<>();
        Map<Long, Map<String, Object>> project = new HashMap<>();
        for (ProjectPipeline pp: list) {
            long projectId = pp.getProjectId();
            String username = pp.getUsername();
            if (member.containsKey(username)) {
                member.put(username, member.get(username) + 1);
            } else {
                member.put(username, 1);
            }
            if (project.containsKey(projectId)) {
                Map<String, Object> map = project.get(projectId);
                map.put("total", (int) map.get("total") + 1);
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("total", 1);
                map.put("project", dao.fetch(Project.class, projectId));
                project.put(projectId, map);
            }
        }
        res.put("total", list.size());
        res.put("member", member);
        res.put("project", project);
        return Result.success(res);
    }
}
