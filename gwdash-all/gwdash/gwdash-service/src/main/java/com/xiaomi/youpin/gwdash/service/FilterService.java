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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.common.Keys;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.GitlabAccessToken;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.dao.model.ProjectCompileRecord;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileParam;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStep;
import com.youpin.xiaomi.tesla.service.TeslaGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import static com.xiaomi.youpin.gwdash.common.Consts.ROLE_ADMIN;

import static com.xiaomi.youpin.gitlab.GitlabConst.GIT_BASE;


/**
 * @author dp
 */

@Slf4j
@Service
public class FilterService {

    @Autowired
    private Dao dao;

    @Autowired
    private Redis redis;

    @Reference(check = false, group = "${dubbo.group}", interfaceClass = TeslaGatewayService.class, cluster = "broadcast")
    private TeslaGatewayService teslaGatewayService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private GitlabService gitlabService;

    @Autowired
    private ProjectCompilationService projectCompilationService;

    @Autowired
    private MyKsyunService myKsyunService;

    private static final String parseFileName = "src/main/resources/FilterDef";

    public Result<Boolean> createNewFilter(SessionAccount account, String group, String name, String commitId, String projectName) {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", false);
        }
        try {
            String gitUrl = GIT_BASE + group + "/" + name;
            Properties properties = gitlabService.getGitlabFile2Properties(gitUrl, commitId, parseFileName, gitlabAccessToken.getName(), gitlabAccessToken.getToken());
            if (null == properties) {
                return new Result<>(1, "解析FilterDef失败", false);
            }
            long projectId = dao.fetch(Project.class, Cnd.where("name", "=", projectName)).getId();
            FilterInfoBo filterInfoBo = new FilterInfoBo();
            filterInfoBo.setProjectId(projectId);
            filterInfoBo.setGitAddress(gitUrl);
            filterInfoBo.setGitGroup(group);
            filterInfoBo.setGitName(name);
            filterInfoBo.setCommitId(commitId);
            return handleProperties(true, filterInfoBo, account, properties);
        } catch (IOException e) {
            return new Result<>(1, e.getMessage(), false);
        }
    }

    public Result<Boolean> updateFilter(Integer id, SessionAccount account, String commitId, String projectName) {
        FilterInfoBo filterInfoBo = getFilterInfoBo(id);
        if (null == filterInfoBo) {
            return new Result<>(1, "该记录已经不存在", false);
        }
        // todo： 修正gitUrl，后期需要移除
        String gitUrl = GIT_BASE + filterInfoBo.getGitGroup() + "/" + filterInfoBo.getGitName();
        filterInfoBo.setGitAddress(gitUrl);
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", false);
        }
        if (filterInfoBo.getProjectId() == 0) {
            long projectId = dao.fetch(Project.class, Cnd.where("name", "=", projectName)).getId();
            filterInfoBo.setProjectId(projectId);
        }

        try {
            Properties properties = gitlabService.getGitlabFile2Properties(filterInfoBo.getGitAddress(), commitId, parseFileName, gitlabAccessToken.getName(), gitlabAccessToken.getToken());
            if (null == properties) {
                return new Result<>(1, "解析FilterDef失败", false);
            }
            filterInfoBo.setCommitId(commitId);
            return handleProperties(false, filterInfoBo, account, properties);
        } catch (IOException e) {
            return new Result<>(1, e.getMessage(), false);
        }
    }

    public Result<List<GitlabCommit>> getGitlabCommits(String group, String name, SessionAccount account) throws IOException {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }
        return gitlabService.getGitlabAllCommit(URLEncoder.encode(group + "/" + name, "UTF-8"), "master", gitlabAccessToken.getToken());
    }

    public Result<Boolean> deleteFilter(int id, SessionAccount account) {
        FilterInfoBo filterInfoBo = getFilterInfoBo(id);
        if (null == filterInfoBo) {
            return new Result<>(1, "该记录已经不存在", false);
        }
        dao.delete(filterInfoBo);
        return Result.success(true);
    }

    public Result<Boolean> buildFilter(int id, SessionAccount account) {
        GitlabAccessToken gitlabAccessToken =
                gitlabService.getAccessTokenByUsername(account.getUsername()).getData();
        if (null == gitlabAccessToken) {
            return new Result<>(1, "找不到授权的access token", null);
        }
        FilterInfoBo filterInfoBo = getFilterInfoBo(id);
        if (filterInfoBo == null) {
            return new Result<>(1, "该记录已经不存在", false);
        }
        long compileId = filterInfoBo.getCompileId();
        ProjectCompileRecord projectCompileRecord = dao.fetch(ProjectCompileRecord.class, Cnd.where("id", "=", compileId));
        if (0 != compileId && null != projectCompileRecord) {
            int status = projectCompileRecord.getStatus();
            if (status == TaskStatus.running.ordinal()) {
                return new Result<>(2, "已经构建中", false);
            }
        }

        CompileParam compileParam = new CompileParam();
        compileParam.setGitUrl(filterInfoBo.getGitAddress());
        compileParam.setBranch(filterInfoBo.getCommitId());
        compileParam.setGitName(gitlabAccessToken.getName());
        compileParam.setGitToken(gitlabAccessToken.getToken());
        compileParam.setBuildPath("");
        compileParam.setJarPath("");
        compileParam.setProfile("");
        compileParam.setCustomParams("");
        projectCompileRecord =
                projectCompilationService.startCloudCompile(compileParam);
        filterInfoBo.setCompileId(projectCompileRecord.getId());
        dao.update(filterInfoBo);
        return Result.success(true);
    }

    private Result<Boolean> handleProperties(Boolean isNewFilter,
                                             FilterInfoBo filterInfoBo,
                                             SessionAccount account,
                                             Properties properties) throws IOException {

        String nameProp = properties.getProperty("name", "");
        String cnameProp = properties.getProperty("cname", "");
        String authorProp = properties.getProperty("author", "");
        String defaultParamsProp = "[]";
        String paramsProp = properties.getProperty("params", defaultParamsProp);
        if (paramsProp == null || paramsProp.equals("")) {
            paramsProp = defaultParamsProp;
        }
        String isSystemProp = properties.getProperty("system", "0").trim();
        String versionProp = properties.getProperty("version", "").trim();
        String gitAddressProp = properties.getProperty("gitAddress", "").trim();
        String descProp = properties.getProperty("desc", "").trim();
        //插件所属于的网关分组(grou1,group2),多个逗号分隔  *是所有分组
        String groups = properties.getProperty("groups", "*").trim();
        if (isSystemProp.equals("")) {
            isSystemProp = "0";
        }
        if (!paramsProp.equals(defaultParamsProp)
                && !isJson(paramsProp)) {
            return new Result<>(2, "配置文件" + parseFileName + "中params需是json格式", false);
        }
        if (nameProp.equals("")) {
            return new Result<>(2, "配置文件" + parseFileName + "缺少name配置", false);
        }
        if (cnameProp.equals("")) {
            return new Result<>(2, "配置文件" + parseFileName + "缺少cname配置[过滤器中文名]", false);
        }
        if (gitAddressProp.equals("")) {
            return new Result<>(2, "配置文件" + parseFileName + "缺少gitAddress配置", false);
        }
        if (versionProp.equals("")) {
            return new Result<>(2, "配置文件" + parseFileName + "缺少version配置", false);
        }
        if (!versionProp.matches("^\\s*\\d+\\.\\d+\\.\\d+.*\\s*$")) {
            return new Result<>(2, "配置文件" + parseFileName + "version配置需要类似于1.0.0-SNAPSHOT", false);
        }
        if (isNewFilter) {
            FilterInfoWithoutDataBo filterInfoWithoutDataBo = getFilterInfoWithoutDataBo(nameProp);
            if (filterInfoWithoutDataBo != null) {
                return new Result<>(4, nameProp + "已经存在, 请更换名字", false);
            }
        } else {
            if (!nameProp.equals(filterInfoBo.getName())) {
                return new Result<>(4, "更新操作不允许修改name字段", false);
            }
        }

        long now = System.currentTimeMillis();
        filterInfoBo.setCname(cnameProp);
        filterInfoBo.setAuthor(authorProp);
        filterInfoBo.setParams(paramsProp);
        filterInfoBo.setVersion(versionProp);
        filterInfoBo.setDesc(descProp);
        filterInfoBo.setUtime(now);
        filterInfoBo.setIsSystem(Integer.parseInt(isSystemProp));
        filterInfoBo.setCompileId(0);
        filterInfoBo.setStatus(Consts.FILTER_STATUS_NEW);
        filterInfoBo.setGroups(groups);

        if (isNewFilter) {
            filterInfoBo.setName(nameProp);
            filterInfoBo.setCreator(account.getUsername());
            filterInfoBo.setCtime(now);
            dao.insert(filterInfoBo);
        } else {
            filterInfoBo.setCreator(account.getUsername());
            dao.update(filterInfoBo);
        }
        return Result.success(true);
    }

    public ResponseEntity<byte[]> downloadFilter(Integer id, SessionAccount account, HttpServletResponse response) throws IOException {
        FilterInfoBo filterInfoBo = getFilterInfoBo(id);
        if (filterInfoBo == null) {
            response.sendError(404, "jar包不存在");
            return null;
        }
        if (account.getRole() != ROLE_ADMIN
                && !account.getUsername().equals(filterInfoBo.getCreator())) {
            response.sendError(401, "您没有该权限");
            return null;
        }
        String fileName = filterInfoBo.getName() + ".jar";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=" + fileName);
        return new ResponseEntity<>(filterInfoBo.getData(), headers, HttpStatus.OK);
    }

    private boolean isJson(String str) {
        JsonElement jsonElement;
        try {
            jsonElement = new JsonParser().parse(str);
        } catch (Exception e) {
            return false;
        }
        if (null == jsonElement) {
            return false;
        }
        return jsonElement.isJsonArray();
    }

    private void notifyGateway(String name, String groups, String type) {
        if (StringUtils.isEmpty(groups)) {
            groups = "*";
        }
        try {
            teslaGatewayService.updateFilter(name, groups, type);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

    private void saveToCache(byte[] file, String nameProp) throws IOException {
        redis.sadd(Keys.systemFilterSetKey(), nameProp);
        redis.set(Keys.systemFilterKey(nameProp), file);
    }

    public Map<String, Object> getFilterList(int page, int pageSize, int status) {
        List<FilterInfoWithoutDataBo> list = dao.query(FilterInfoWithoutDataBo.class, Cnd.where("status", status == 0 ? ">" : "=", status), new Pager(page, pageSize));
        list.stream().forEach(it -> {
            dao.fetchLinks(it, null);
        });
        Map<String, Object> result = new HashMap<>();
        result.put("pluginList", list);
        result.put("total", dao.count("filter_info", Cnd.where("status", status == 0 ? ">" : "=", status)));
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    public List<FilterInfoWithoutDataBo> getAllEffectList() {
        return dao.query(FilterInfoWithoutDataBo.class,
                Cnd.where("onlineStatus", "=", Consts.FILTER_ONLINE),
                new Pager(1, 100));
    }

    public FilterInfoWithoutDataBo getFilterInfoWithoutDataBo(String name) {
        List<FilterInfoWithoutDataBo> list = dao.query(FilterInfoWithoutDataBo.class, Cnd.where("name", "=", name));
        return list.size() <= 0 ? null : list.get(0);
    }

    public FilterInfoBo getFilterInfoBo(Integer id) {
        FilterInfoBo filterInfoBo = dao.fetch(FilterInfoBo.class, Cnd.where("id", "=", id));
        // 回填gitName和gitGroup
        String gitAddress = filterInfoBo.getGitAddress();
        if (null != filterInfoBo
                && !StringUtils.isEmpty(gitAddress)
                && (StringUtils.isEmpty(filterInfoBo.getGitName())
                || StringUtils.isEmpty(filterInfoBo.getGitGroup()))) {
            String[] parts = gitAddress.split("/");
            int len = parts.length;
            filterInfoBo.setGitGroup(parts[len - 2]);
            filterInfoBo.setGitName(parts[len - 1]);
        }
        return filterInfoBo;
    }

    public Result<Boolean> reject(Integer id, Long accountId, String userName) {
        FilterInfoBo filterInfoBo = getFilterInfoBo(id);
        if (filterInfoBo == null) {
            return new Result<>(1, "filter不存在", false);
        }
        if (filterInfoBo.getStatus() == Consts.FILTER_STATUS_DELETE) {
            return new Result<>(2, "已经审核不通过", false);
        }

        long projectId = filterInfoBo.getProjectId();
        if (0 != projectId) {
            boolean isOwner = projectService.isOwner(projectId, accountId);
            if (!isOwner) {
                return new Result<>(3, "不是tesla项目owner", false);
            }
        }

        if (userName.equals(filterInfoBo.getCreator())) {
            return new Result<>(4, "不允许审核自己上传的过滤器", false);
        }

        // 更新plugin的信息
        dao.update(FilterInfoBo.class,
                Chain.make("status", Consts.FILTER_STATUS_DELETE),
                Cnd.where("id", "=", id));

        return Result.success(true);
    }

    public Result<Boolean> online(Integer id, long accountId) {
        FilterInfoBo filterInfoBo = getFilterInfoBo(id);
        if (filterInfoBo == null) {
            return new Result<>(1, "filter不存在", false);
        }
        if (filterInfoBo.getData() == null) {
            return new Result<>(2, "filter无审核过的版本", false);
        }
        if (filterInfoBo.getOnlineStatus() == Consts.FILTER_ONLINE) {
            return new Result<>(2, "filter已经开启", false);
        }

        long projectId = filterInfoBo.getProjectId();
        if (0 != projectId) {
            boolean isOwner = projectService.isOwner(projectId, accountId);
            if (!isOwner) {
                return new Result<>(3, "不是tesla项目owner", false);
            }
        }


        // 更新plugin的信息
        dao.update(FilterInfoBo.class,
                Chain.make("online_status", Consts.FILTER_ONLINE),
                Cnd.where("id", "=", id));

        // 插件审核通过则通知更新filter
        try {
            saveToCache(filterInfoBo.getData(), filterInfoBo.getName());
            notifyGateway(filterInfoBo.getName(), filterInfoBo.getGroups(), "add");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return Result.success(true);
    }

    public Result<Boolean> offline(Integer id, long accountId) {
        FilterInfoBo filterInfoBo = getFilterInfoBo(id);
        if (filterInfoBo == null) {
            return new Result<>(1, "filter不存在", false);
        }
        if (filterInfoBo.getOnlineStatus() == Consts.FILTER_OFFLINE) {
            return new Result<>(2, "filter已经关闭", false);
        }

        long projectId = filterInfoBo.getProjectId();
        if (0 != projectId) {
            boolean isOwner = projectService.isOwner(projectId, accountId);
            if (!isOwner) {
                return new Result<>(3, "不是tesla项目owner", false);
            }
        }


        // 更新plugin的信息
        dao.update(FilterInfoBo.class,
                Chain.make("online_status", Consts.FILTER_OFFLINE),
                Cnd.where("id", "=", id));

        // 插件下线则通知更新filter
        removeCache(filterInfoBo);
        notifyGateway(filterInfoBo.getName(), filterInfoBo.getGroups(), "remove");
        return Result.success(true);
    }

    public Result<Boolean> realDelete(Integer id, long accountId) {
        FilterInfoBo filterInfoBo = getFilterInfoBo(id);
        if (filterInfoBo == null) {
            return new Result<>(1, "该插件不存在", false);
        }
        long projectId = filterInfoBo.getProjectId();
        if (0 != projectId) {
            boolean isOwner = projectService.isOwner(projectId, accountId);
            if (!isOwner) {
                return new Result<>(3, "不是tesla项目owner", false);
            }
        }
        // 删除插件
        dao.delete(FilterInfoBo.class, id);
        // 插件删除通过则通知更新filter
        removeCache(filterInfoBo);
        notifyGateway(filterInfoBo.getName(),filterInfoBo.getGroups(), "remove");
        return Result.success(true);
    }

    private void removeCache(FilterInfoBo filterInfoBo) {
        redis.del(Keys.systemFilterKey(filterInfoBo.getName()));
        redis.srem(Keys.systemFilterSetKey(), filterInfoBo.getName());
    }

    public Result<Boolean> effectFilter(Integer id, long accountId, String userName) {
        FilterInfoBo filterInfoBo = getFilterInfoBo(id);
        if (filterInfoBo == null) {
            return new Result<>(1, "该插件不存在", false);
        }
        if (filterInfoBo.getStatus() == Consts.FILTER_STATUS_EFFECT) {
            return new Result<>(2, "已经审核通过", false);
        }

        long projectId = filterInfoBo.getProjectId();
        if (0 != projectId) {
            boolean isOwner = projectService.isOwner(projectId, accountId);
            if (!isOwner) {
                return new Result<>(3, "不是tesla项目owner", false);
            }
        }

        if (userName.equals(filterInfoBo.getCreator())) {
            return new Result<>(4, "不允许审核自己上传的过滤器", false);
        }

        long compileId = filterInfoBo.getCompileId();
        ProjectCompileRecord projectCompileRecord = null;
        if (0 != compileId) {
            projectCompileRecord = dao.fetch(ProjectCompileRecord.class, Cnd.where("id", "=", compileId));
        }
        if (null == projectCompileRecord) {
            return new Result<>(5, "请先编译filter，生成jar包", false);
        }
        int step = projectCompileRecord.getStep();
        int status = projectCompileRecord.getStatus();
        if (step != TaskStep.upload.ordinal()
                || status != TaskStatus.success.ordinal()) {

            if (status == TaskStatus.running.ordinal()) {
                return new Result<>(6, "构建中，稍后尝试启用", false);
            }
            return new Result<>(7, "构建失败，请重新尝试构建", false);
        }

        byte[] data = myKsyunService.getFileByDownloadKey(projectCompileRecord.getJarKey());
        filterInfoBo.setData(data);

        // 更新filter的信息
        dao.update(FilterInfoBo.class,
                Chain.make("status", Consts.FILTER_STATUS_EFFECT)
                        .add("online_status", Consts.FILTER_ONLINE)
                        .add("data", filterInfoBo.getData()),
                Cnd.where("id", "=", id));

        // 插件审核通过则通知更新filter
        try {
            saveToCache(filterInfoBo.getData(), filterInfoBo.getName());
            notifyGateway(filterInfoBo.getName(),filterInfoBo.getGroups(), "add");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return Result.success(true);
    }

    public Result<Boolean> filterRedisFlesh() {
        List<FilterInfoBo> list = dao.query(FilterInfoBo.class, Cnd.where("online_status", "=", 1));

        Set<String> keys = redis.smembers(Keys.systemFilterSetKey());

        keys.stream().forEach(it -> {
            redis.del(Keys.systemFilterKey(it));
        });
        redis.del(Keys.systemFilterSetKey());

        list.stream().forEach(it -> {
            redis.sadd(Keys.systemFilterSetKey(), it.getName());
            redis.set(Keys.systemFilterKey(it.getName()), it.getData());
        });

        return Result.success(true);
    }

    public Result<Boolean> rate(int theRateId, int type, int accountId, double rate) {
        UserRateBo userRateBo = dao.fetch(UserRateBo.class,
                Cnd.where("the_rate_id", "=", theRateId)
                        .and("type", "=", type)
                        .and("account_id", "=", accountId));
        if (null == userRateBo) {
            userRateBo = new UserRateBo();
            userRateBo.setTheRateId(theRateId);
            userRateBo.setType(type);
            userRateBo.setAccountId(accountId);
            userRateBo.setRate(rate);
            dao.insert(userRateBo);
        } else {
            userRateBo.setRate(rate);
            dao.update(userRateBo);
        }
        return Result.success(true);
    }
}
