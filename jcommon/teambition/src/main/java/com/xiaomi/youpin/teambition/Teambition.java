package com.xiaomi.youpin.teambition;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.youpin.teambition.bo.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

@Slf4j
public class Teambition {

    private String appId;
    private String appSecret;

    private TeambitionConfig config;


    public static final Long EXPIRES_IN = 1 * 3600 * 1000L;
    public static final String TOKEN_APPID = "_appId";
    private AtomicReference<AppTokenInfo> tr = new AtomicReference<>();

    public Teambition(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
        rereshToken();
    }

    public Teambition(String appId, String appSecretm, TeambitionConfig config) {
        this.config = config;
        this.appId = appId;
        this.appSecret = appSecret;
        rereshToken();
    }

    /**
     * 获取用户id
     *
     * @param email
     * @param tenantId
     * @return
     */
    public String getUserId(String email, String tenantId) {
        String getIdUrl = config.getGetIdUrl();
        String strResult = HttpClientV2.get(getIdUrl + email, getHeaders(tenantId), 100000);
        BaseRst<UserInfo> result = null;
        try {
            result = new Gson().fromJson(strResult, new TypeToken<BaseRst<UserInfo>>() {
            }.getType());
        } catch (Exception e) {
            log.error("getUserId json parse error, msg:{}", e.getStackTrace());
        }
        if (result == null || result.getCode() != 200 || result.getResult() == null) {
            return null;
        }
        return result.getResult().getId();
    }

    /**
     * 查询用户信息
     *
     * @param ids
     * @param tenantId
     * @return
     */
    public List<UserInfo> batchQryUserinfo(List<String> ids, String tenantId) {
        if (CollectionUtils.isEmpty(ids)) {

        }
        Map<String, List<String>> param = ImmutableMap.of("ids", ids);
        String batchQryUserUrl = config.getBatchQryUserUrl();
        String strResult = HttpClientV2.post(batchQryUserUrl, new Gson().toJson(param), getHeaders(tenantId), 100000);
        BaseRst<List<UserInfo>> result = null;
        try {
            result = new Gson().fromJson(strResult, new TypeToken<BaseRst<List<UserInfo>>>() {
            }.getType());
        } catch (Exception e) {
            log.error("json parse error, msg:{}", e.getMessage());
        }
        if (result == null || result.getCode() != 200 || result.getResult() == null) {
            return null;
        }
        return result.getResult();
    }

    /**
     * 获取用户项目列表
     *
     * @param param
     * @param tenantId
     * @return
     */
    public BaseRst<List<ProjectInfo>> getProjectsByUserId(ProjectParam param, String tenantId) {
        String body = new Gson().toJson(param);
        String qryProjectUrl = config.getQryProjectUrl();
        String response = HttpClientV2.post(qryProjectUrl, body, getHeaders(tenantId), 2000);
        BaseRst<List<ProjectInfo>> result = null;
        try {
            result = new Gson().fromJson(response, new TypeToken<BaseRst<List<ProjectInfo>>>() {
            }.getType());
        } catch (Exception e) {
            log.error("getProjectsByUserId json parse error, msg:{}", e.getStackTrace());
        }
        if (result == null || result.getCode() != 200 || result.getResult() == null) {
            log.error("Teambition.getProjectsByUserId {}", result);
            return null;
        }
        return result;
    }

    private String createTql(TqlParam param) {
        StringBuilder tql = new StringBuilder("isArchived = " + param.isArchived());
        if (StringUtils.isNotBlank(param.getProjectId())) {
            tql.append(" AND projectId = " + param.getProjectId());
        }
        if (StringUtils.isNotBlank(param.getParentId())) {
            tql.append(" AND parentId = " + param.getParentId());
        } else {
            tql.append(" AND onlyTopTask = true");
        }
        if ("participant".equals(param.getCondition())) {
            tql.append(" AND involveMember = " + param.getUserId());
        } else if (StringUtils.isNotBlank(param.getUserId())) {
            tql.append(" AND " + (StringUtils.isBlank(param.getCondition()) ? "executor" : param.getCondition()) + "Id" + " = " + param.getUserId());
        }
        if (StringUtils.isNotBlank(param.getDueDateStart()) && StringUtils.isNotBlank(param.getDueDateEnd())) {
            tql.append(" AND dueDate >=" + param.getDueDateStart() + " AND dueDate <=" + param.getDueDateEnd());
        }
        if (param.getIsDone() != null) {
            tql.append(" AND isDone = " + param.getIsDone());
        }
        if (param.getPriority() != null) {
            tql.append(" AND priority = " + param.getPriority());
        }
        return tql.toString();
    }

    /**
     * tql查询任务列表
     */
    public BaseRst<List<TaskInfo>> qryTaskListByTql(String tenantId, int pageSize, String pageToken, String orderBy, TqlParam param) {
        TqlRequest tqlRequest = new TqlRequest();
        tqlRequest.setPageSize(pageSize);
        tqlRequest.setPageToken(pageToken);
        if (StringUtils.isNotBlank(orderBy)) {
            tqlRequest.setOrderBy("dueDate");
        }
        tqlRequest.setTql(createTql(param));
        log.info("qryTaskListByTql.tql {}", tqlRequest.getTql());
        String tqlsearchUrl = config.getTqlsearchUrl();
        String response = HttpClientV2.post(tqlsearchUrl, new Gson().toJson(tqlRequest), getHeaders(tenantId), 20000);
        BaseRst<List<TaskInfo>> result = null;
        try {
            result = new Gson().fromJson(response, new TypeToken<BaseRst<List<TaskInfo>>>() {
            }.getType());
        } catch (Exception e) {
            log.error("json parse error, msg:{}", e.getMessage());
        }
        if (result == null || result.getCode() != 200 || result.getResult() == null) {
            log.error("Teambition.qryTaskListByTql {}", result);
            return null;
        }
        result.getResult().stream().forEach(it -> {
            if (StringUtils.isNotBlank(it.getExecutorId())) {
                it.setExecutorName(batchQryUserinfo(Collections.singletonList(it.getExecutorId()), tenantId).get(0).getName());
                it.setCreatorName(batchQryUserinfo(Collections.singletonList(it.getCreatorId()), tenantId).get(0).getName());
            }
        });
        return result;
    }

    /**
     * 查询任务列表
     *
     * @param param
     * @param tenantId 企业id
     * @return
     */
    public BaseRst<List<TaskInfo>> qryTaskList(TaskParam param, String tenantId) {
        String strResult = "";
        String qryTaskUrl = config.getQryTaskUrl();
        if (StringUtils.isNotBlank(param.getTaskId())) {
            strResult = HttpClientV2.get(qryTaskUrl + "?taskId=" + param.getTaskId(), getHeaders(tenantId), 100000);
        } else if (StringUtils.isNotBlank(param.getParentTaskId())) {
            strResult = HttpClientV2.get(qryTaskUrl + "?parentTaskId=" + param.getParentTaskId(), getHeaders(tenantId), 100000);
        } else if (param != null) {
            StringBuilder paramStr = new StringBuilder("?userId=");
            paramStr.append(param.getUserId()).append("&condition=").append(param.getCondition());
            paramStr.append("&organizationId=" + param.getOrganizationId());
            if (param.getPageSize() > 0) {
                paramStr.append("&pageSize=" + param.getPageSize());
            }
            if (StringUtils.isNotBlank(param.getPageToken())) {
                paramStr.append("&pageToken=" + param.getPageToken());
            }
            if (StringUtils.isNotBlank(param.getProjectId())) {
                paramStr.append("&projectId=" + param.getProjectId());
            }
            if (StringUtils.isNotBlank(param.getIsDone())) {
                paramStr.append("&isDone=" + param.getIsDone());
            }
            strResult = HttpClientV2.get(qryTaskUrl + paramStr, getHeaders(tenantId), 100000);
        } else {
            return null;
        }
        BaseRst<List<TaskInfo>> result = null;
        try {
            result = new Gson().fromJson(strResult, new TypeToken<BaseRst<List<TaskInfo>>>() {
            }.getType());
        } catch (Exception e) {
            log.error("json parse error, msg:{}", e.getMessage());
        }
        if (result == null || result.getCode() != 200 || result.getResult() == null) {
            log.error("Teambition.qryTaskList {}", result);
            return null;
        }
        result.getResult().stream().forEach(it -> {
            if (StringUtils.isNotBlank(it.getExecutorId())) {
                it.setExecutorName(batchQryUserinfo(Collections.singletonList(it.getExecutorId()), tenantId).get(0).getName());
                it.setCreatorName(batchQryUserinfo(Collections.singletonList(it.getCreatorId()), tenantId).get(0).getName());
            }
        });
        return result;
    }

    /**
     * 更新任务
     *
     * @param taskInfo
     * @param tenantId
     * @return
     */
    public TaskInfo updateTask(TaskInfo taskInfo, String tenantId) {
        String updateTaskUrl = config.getUpdateTaskUrl();
        String strResult = HttpClientV2.post(updateTaskUrl, new Gson().toJson(taskInfo), getHeaders(tenantId), 100000);
        BaseRst<TaskInfo> result = null;
        try {
            result = new Gson().fromJson(strResult, new TypeToken<BaseRst<TaskInfo>>() {
            }.getType());
        } catch (Exception e) {
            log.error("json parse error, msg:{}", e.getMessage());
        }
        if (result == null || result.getCode() != 200 || result.getResult() == null) {
            log.error("Teambition.updateTask {}", result);
            return null;
        }
        return result.getResult();
    }

    /**
     * 创建项目
     *
     * @return
     */
    public boolean createProject(String url, String tenantId, String name) {
        OldProjectInfo projectInfo = new OldProjectInfo();
        projectInfo.set_organizationId(tenantId);
        projectInfo.setName(name);
        //projectInfo.setType("normal");
        projectInfo.setVisibility("project");
        String response = HttpClientV2.post(url, new Gson().toJson(projectInfo), getHeaders(tenantId), 2000);
        return true;
    }

    /**
     * 新建任务
     *
     * @return
     */
    public TaskInfo createTask(TaskInfo taskInfo, String tenantId) {
        String createTaskUrl = config.getCreateTaskUrl();
        String response = HttpClientV2.post(createTaskUrl, new Gson().toJson(taskInfo), getHeaders(tenantId), 2000);
        log.info("Teambition.createTask:{}", response);
        BaseRst<TaskInfo> result = null;
        try {
            result = new Gson().fromJson(response, new TypeToken<BaseRst<TaskInfo>>() {
            }.getType());
        } catch (Exception e) {
            log.error("createTask json parse error, msg:{}", e.getMessage());
        }
        if (result == null || result.getCode() != 200 || result.getResult() == null) {
            return null;
        }
        return result.getResult();
    }

    public BaseRst<List<UserInfo>> getMemberListByProjectId(String projectId, String tenantId, String pageToken, int pageSize) {
        String memberListUrl = config.getMemberListUrl();
        String url = memberListUrl + projectId;
        if (StringUtils.isNotBlank(pageToken)) {
            url += "&pageToken=" + pageToken;
        }
        if (pageSize > 0) {
            url += "&pageSize=" + pageSize;
        }
        String strResult = HttpClientV2.get(url, getHeaders(tenantId), 100000);
        log.info("Teambition.getMemberListByProjectId:{}", strResult);
        BaseRst<List<UserInfo>> result = null;
        try {
            result = new Gson().fromJson(strResult, new TypeToken<BaseRst<List<UserInfo>>>() {
            }.getType());
        } catch (Exception e) {
            log.error("getMemberListByProjectId json parse error, msg:{}", e.getStackTrace());
        }
        if (result == null || result.getCode() != 200 || result.getResult() == null) {
            return null;
        }
        return result;
    }


    public TaskFlow qryTaskFlow(String projectId, String tenantId) {
        String templateQryUrl = config.getTemplateQryUrl();
        String templateResult = HttpClientV2.get(templateQryUrl + projectId, getHeaders(tenantId), 10000);
        BaseRst<TaskFlow> result = null;
        try {
            BaseRst<List<Template>> templateRst = new Gson().fromJson(templateResult, new TypeToken<BaseRst<List<Template>>>() {
            }.getType());
            if (templateRst == null || templateRst.getCode() != 200 || templateRst.getResult() == null) {
                log.error("Teambition.qryTemplate {}", templateRst);
                return null;
            }
            String taskflowQryUrl = config.getTaskflowQryUrl();
            String strResult = HttpClientV2.get(taskflowQryUrl + templateRst.getResult().get(0).getTaskflowId(), getHeaders(tenantId), 10000);
            result = new Gson().fromJson(strResult, new TypeToken<BaseRst<TaskFlow>>() {
            }.getType());
            if (result == null || result.getCode() != 200 || result.getResult() == null) {
                log.error("Teambition.qryTaskflow {}", result);
                return null;
            }
        } catch (Exception e) {
            log.error("qryTaskFlow json parse error, msg:{}", e.getStackTrace());
            return null;
        }
        return result.getResult();
    }

    private String getToken() {
        AppTokenInfo token = this.tr.get();
        long now = System.currentTimeMillis();
        if (token == null || (now - token.getUtime() >= (token.getExpire() - 60 * 10 * 1000))) {
            log.error("start rereshToken");
            rereshToken();
        }
        return this.tr.get().getAppToken();
    }

    private void rereshToken() {
        try {
            String token = getAppToken();
            if (StringUtils.isBlank(token)) {
                log.error("get token failed");
            }
            AppTokenInfo tokenResult = new AppTokenInfo();
            tokenResult.setAppToken(token);
            tokenResult.setUtime(System.currentTimeMillis());
            tr.set(tokenResult);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

    public String genAppToken(String appId, String appSecret) {
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(appSecret)) {
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256(appSecret);
        long timestamp = System.currentTimeMillis();
        Date issuedAt = new Date(timestamp);
        Date expiresAt = new Date(timestamp + EXPIRES_IN);
        return JWT.create()
                .withClaim(TOKEN_APPID, appId)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public String getAppToken() {
        return genAppToken(appId, appSecret);
    }

    private Map getHeaders(String tenantId) {
        Map headers = new HashMap();
        headers.put("Authorization", "Bearer " + getToken());
        headers.put("content-type", "application/json");
        headers.put("X-Tenant-Id", tenantId);
        headers.put("X-Tenant-Type", "organization");
        return headers;
    }
}
