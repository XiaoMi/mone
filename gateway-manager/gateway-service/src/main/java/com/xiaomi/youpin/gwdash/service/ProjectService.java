package com.xiaomi.youpin.gwdash.service;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.bo.ProjectBo;
import com.xiaomi.youpin.gwdash.bo.ProjectStatusEnum;
import com.xiaomi.youpin.gwdash.bo.SearchAppNameParam;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author tsingfu
 */
@Service
@Slf4j
public class ProjectService {

    @Autowired
    private Dao dao;

    @Reference(check = false, interfaceClass = IProjectService.class, group = "${gwdash.dubbo.group}")
    private IProjectService projectService;

    @Autowired
    private UserService userService;

    @Reference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}")
    private AccountService accountService;

    /**
     * 用于信息校验
     */
    private static Cache<String, Boolean> appNameCache = newCache();

    private static synchronized Cache<String, Boolean> newCache() {
        if (appNameCache != null) {
            return appNameCache;
        }
        log.info("gw manager project service created new app name cache");

        // current size is about 1100
        return CacheBuilder.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
    }

    public static Cache<String, Boolean> getAppNameCache() {
        if (appNameCache == null) {
            appNameCache = newCache();
        }
        return appNameCache;
    }

    private synchronized void reloadCache() {
        if (!getAppNameCache().asMap().isEmpty()) {
            return;
        }
        Set<String> appNames = this.projectService.getAllAppNames().getData();
        appNames.forEach(appName -> getAppNameCache().put(appName, true));
        log.info("gw manager project service reloaded app name cache, size is {}", getAppNameCache().size());
    }

    public boolean existedAppName(String input) {
        if (1==1){
            return true;
        }
        if (getAppNameCache().asMap().isEmpty()) {
            this.reloadCache();
        }
        try {
            if (getAppNameCache().asMap().containsKey(input)) {
                return getAppNameCache().asMap().get(input);
            }
            return getAppNameCache().get(input, () -> {
                List<ProjectBo> projects = this.projectService.getProjectByName(input, true);
                boolean res = projects != null && !projects.isEmpty();
                getAppNameCache().put(input, res);

                if (!res) {
                    log.warn("ProjectService existedAppName invalid app name {}, cache size of {}",
                            input, getAppNameCache().size());
                }
                return res;
            });
        } catch (ExecutionException e) {
            log.error("ProjectService existedAppName error", e);
            return false;
        }
    }

    public Result<Set<String>> getApplicationNames() {
        Set<String> appNames = projectService.getAllAppNames().getData();
        log.info("gwdash appnames : [{}]", new Gson().toJson(appNames));
        return Result.success(appNames);
    }

    public Result<List<String>> getApplicationNames(SearchAppNameParam param) {
        return Result.success(projectService.searchAppNames(param).getData());
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
        if (isProjectSuperUser(account)) {
            return true;
        }
        int accountId = account.getId().intValue();
        return isMember(projectId, accountId);
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
        List groupIds = groups.stream().map(e -> e.getAccountId() + "").collect(Collectors.toList());
        Account account = accountService.queryUserById(accountId);
        String[] gids = account.getGid().split("_");
        for (String gid : gids) {
            if (groupIds.contains(gid)) {
                return true;
            }
        }
        return false;
    }

    public Result<Map<String, Object>> listProjects(SessionAccount account, Project param) {
        Cnd cnd = Cnd.where("status", "!=", ProjectStatusEnum.DELETE.getId());
        if (StringUtils.isNotBlank(param.getSearch())) {
            SqlExpressionGroup searchSql = Cnd.exps("name", "like", "%" + param.getSearch() + "%");
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
            sql.params().set("status", ProjectStatusEnum.DELETE.getId());
            sql.params().set("accountId", account.getId());
            sql.setCallback((Connection conn, ResultSet rs, Sql sql1) -> {
                List<String> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(rs.getString("pr.projectId"));
                }
                return list;
            });
            List<String> projectIds = dao.execute(sql).getList(String.class);
            int size = projectIds.size();
            result.put("total", size);
            if (size > 0) {
                cnd.and("id", "in", projectIds.stream().reduce((a, b) -> a + "," + b).get());
                result.put("list", dao.query(Project.class, cnd, new Pager(param.getPage(), param.getPageSize())));
            } else {
                result.put("list", Lists.newArrayList());
            }
        }
        return Result.success(result);
    }

    public List<Project> getProjects() {
        return dao.query(Project.class, Cnd.where("status", "!=", ProjectStatusEnum.DELETE.getId()));
    }

    static protected String parseGitDomain(String gitURL) {
        try {
            return new URL(gitURL).getHost();
        } catch (Exception e) {
            return "";
        }

    }

}
