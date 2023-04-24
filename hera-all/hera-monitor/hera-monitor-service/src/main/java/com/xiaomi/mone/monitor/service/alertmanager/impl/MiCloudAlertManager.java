package com.xiaomi.mone.monitor.service.alertmanager.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.UserInfo;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.ExceptionCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.alertmanager.AlertManager;
import com.xiaomi.mone.monitor.service.alertmanager.client.Client;
import com.xiaomi.mone.monitor.service.alertmanager.client.Request;
import com.xiaomi.mone.monitor.service.alertmanager.client.model.HttpMethodName;
import com.xiaomi.mone.monitor.service.helper.AlertHelper;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.prometheus.AlertTeamData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author gaoxihui
 * @date 2022/11/7 2:40 下午
 */
@Slf4j
@Service(value="miCloudAlertManager")
@ConditionalOnProperty(name = "service.selector.property", havingValue = "inner")
public class MiCloudAlertManager implements AlertManager {


    public static final String alarm_rule_group_uri = "/api/v1/rules/group";
    public static final String alarm_rule_alert_uri = "/api/v1/rules/alert";
    public static final String alarm_rule_alert_delete_uri = "/api/v1/rules/alert/";
    public static final String alarm_rule_alert_list_uri = "/api/v1/rules/alert/list";
    public static final String alarm_rule_alert_enabled_uri = "/api/v1/rules/alert/enabled/";
    public static final String alarm_alert_team_uri = "/api/v1/alert-team/oncall";
    public static final String alarm_alert_event_uri = "/api/v1/event/list";
    public static final String alarm_alert_event_latest_uri = "/api/v1/event/list/latest";
    public static final String alarm_alert_event_pre_uri = "/api/v1/event/";
    public static final String alarm_alert_event_resolve_uri = "/api/v1/silence";
    public static final String alarm_job_option_uri = "/api/v1/scrape-config";
    public static final String alarm_job_option_uri_list = "/api/v1/scrape-config/list";
    public static final String alarm_user_search = "/api/v1/user/list";
    public static final String alarm_alert_group = "/api/v1/alert-team";
    public static final String alarm_alert_group_list = "/api/v1/alert-team/list";

    @NacosValue(value = "${alarm.domain:}",autoRefreshed = true)
    private String alarmDomain;
    @NacosValue("${iam.ak:noconfig}")
    private String cloudAk;
    @NacosValue("${iam.sk:noconfig}")
    private String cloudSk;
    @NacosValue(value = "${prometheus.alarm.env:staging}",autoRefreshed = true)
    private String prometheusAlarmEnv;
    public static final String alarm_staging_env = "staging";
    public static final String alarm_online_env = "production";
    public static final String alarm_preview_env = "preview";
    public static final Integer stagingDefaultIamId = 16360;
    public static final Integer onlineDefaultIamId = 15272;

    @Autowired
    private AlertHelper alertHelper;

    @Override
    public Result addRule(JsonObject param,String identifyId,String user) {

        StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_rule_alert_uri);

        Request request = createRequest(HttpMethodName.POST, url.toString(), identifyId, user);
        request.setBody(param.toString());

        Result<JsonElement> jsonObjectResult = executeRequest(request);


        log.info("MiCloudAlertManager.addRuels request : {},response:{}", new Gson().toJson(request).toString(),new Gson().toJson(jsonObjectResult).toString());

        return jsonObjectResult;

    }

    @Override
    public Result editRule(Integer alertId,JsonObject param,String identifyId,String user) {

        StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_rule_alert_uri).append("/").append(alertId);

        Request request = createRequest(HttpMethodName.PUT, url.toString(), identifyId, user);

        request.setBody(param.toString());

        Result<JsonElement> jsonObjectResult = executeRequest(request);

        log.info("MiCloudAlertManager.editRuels request : {},response:{}", new Gson().toJson(request).toString(),new Gson().toJson(jsonObjectResult).toString());
        return jsonObjectResult;
    }

    @Override
    public Result delRule(Integer alertId,String identifyId,  String user) {

        StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_rule_alert_delete_uri)
                .append(alertId);
        Request request = createRequest(HttpMethodName.DELETE, url.toString(), identifyId, user);
        Result<JsonElement> jsonObjectResult = executeRequest(request);
        log.info("MiCloudAlertManager.deleteRule request : {},response:{}", new Gson().toJson(request).toString(),new Gson().toJson(jsonObjectResult).toString());
        return jsonObjectResult;
    }

    @Override
    public Result enableRule(Integer alertId,Integer pauseStatus,String identifyId, String user) {

        StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_rule_alert_enabled_uri)
                .append(alertId).append("?enabled=").append(pauseStatus);
        Request request = createRequest(HttpMethodName.PUT, url.toString(), identifyId, user);
        Result<JsonElement> jsonObjectResult = executeRequest(request);
        log.info("MiCloudAlertManager.enabledRule request : {},response:{}", new Gson().toJson(request).toString(),new Gson().toJson(jsonObjectResult).toString());
        return jsonObjectResult;
    }

    @Override
    public Result queryRuels(JsonObject params, String identifyId, String user) {
        StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_rule_alert_list_uri);
        Request request = createRequest(HttpMethodName.POST, url.toString(), identifyId, user);

        request.setBody(params.toString());

        Result<PageData> jsonObjectResult = queryList(request);
        log.info("MiCloudAlertManager.queryRuels request : {},response:{}", new Gson().toJson(request).toString(),new Gson().toJson(jsonObjectResult).toString());

        return jsonObjectResult;
    }

    public Result<JsonElement>  getAlarmRuleRemote(Integer alarmId,Integer iamId,String user){
        Request request = createRequest(HttpMethodName.GET, alarmDomain + alarm_rule_alert_uri+"/" + alarmId, iamId, user);

        JsonObject jsonObject = new JsonObject();
        request.setBody(jsonObject.toString());
        Result<JsonElement> jsonObjectResult = executeRequest(request);

        log.info("MiCloudAlertManager.getAlarmRuleRemote request : {},response:{}", new Gson().toJson(request).toString(),new Gson().toJson(jsonObjectResult).toString());

        return jsonObjectResult;
    }

    public Result updateAlarm(Integer alarmId,Integer iamId,String user,String body){

        StringBuilder url = new StringBuilder(alarmDomain).append(alarm_rule_alert_uri).append("/").append(alarmId);

        Request request = createRequest(HttpMethodName.PUT, url.toString(), iamId, user);

        request.setBody(body);

        Result<JsonElement> jsonObjectResult = executeRequest(request);

        log.info("MiCloudAlertManager.updateAlarm request : {},response:{}", new Gson().toJson(request).toString(),new Gson().toJson(jsonObjectResult).toString());

        return jsonObjectResult;
    }

    @Override
    public Result<JsonElement> addAlarmGroup(JsonObject params, String iamId, String user) {
        StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_rule_group_uri);
        Request request = createRequest(HttpMethodName.POST, url.toString(), iamId, user);

        request.setBody(params.toString());
        Result<JsonElement> jsonObjectResult = executeRequest(request);
        
        return jsonObjectResult;
    }

    @Override
    public Result<JsonElement> searchAlarmGroup(String alarmGroup, String identifyId, String user) {

        StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_rule_group_uri).append("?group=").append(alarmGroup);
        Request request = createRequest(HttpMethodName.GET, url.toString(), identifyId, user);

        Result<JsonElement> jsonObjectResult = executeRequest(request);

        return jsonObjectResult;
    }

    @Override
    public Result<PageData> searchAlertTeam(String name, String note, String manager, String oncallUser, String service, Integer iamId, String user, Integer page_no, Integer page_size) {
        if(iamId == null){
            iamId = getDefaultIamId();
        }

        if(page_no == null){
            page_no = 1;
        }
        if(page_size == null){
            page_size = 1;
        }
        StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_alert_team_uri);
        StringBuilder param = new StringBuilder();
        param.append("page_no=").append(page_no);
        param.append("&page_size=").append(page_size);
        if(StringUtils.isNotBlank(name)){
            param.append("&name=").append(name);
        }
        if(StringUtils.isNotBlank(note)){
            param.append("&note=").append(note);
        }
        if(StringUtils.isNotBlank(manager)){
            param.append("&manager=").append(manager);
        }
        if(StringUtils.isNotBlank(oncallUser)){
            param.append("&user=").append(oncallUser);
        }
        if(StringUtils.isNotBlank(service)){
            param.append("&service=").append(service);
        }

        String params = param.toString();
        if(StringUtils.isNotBlank(params)){
            url.append("?").append(params.substring(1));
        }

        Request request = createRequest(HttpMethodName.GET, url.toString(), iamId, user);

        Result<PageData> jsonObjectResult = queryList(request);
        JsonElement data = (JsonElement) jsonObjectResult.getData().getList();
        if(data != null){
            AlertTeamData[] list = new Gson().fromJson(data, AlertTeamData[].class);
            jsonObjectResult.getData().setList(list);
            jsonObjectResult.getData().setPageSize(page_size);
            jsonObjectResult.getData().setPage(page_no);
        }

        return jsonObjectResult;
    }

    @Override
    public Result<PageData> queryEvents(String user, Integer treeId, String alertLevel, Long startTime, Long endTime, Integer pageNo, Integer pageSize, JsonObject labels) {

        Request request = createRequest(HttpMethodName.POST, alarmDomain + MiCloudAlertManager.alarm_alert_event_uri, treeId, user);
        JsonObject params = new JsonObject();
        if (labels != null) {
            params.add("labels", labels);
        }
        if (StringUtils.isNotBlank(alertLevel)) {
            params.addProperty("priority", alertLevel);
        }
        if (startTime != null) {
            params.addProperty("start_time", startTime);
        }
        if (endTime != null) {
            params.addProperty("end_time", endTime);
        }
        if (pageNo != null) {
            params.addProperty("page_no", pageNo);
        }
        if (pageSize != null) {
            params.addProperty("page_size", pageSize);
        }
        request.setBody(params.toString());
        Result<PageData> jsonObjectResult = queryList(request);

        return jsonObjectResult;
    }

    @Override
    public Result<PageData> queryLatestEvents(Set<Integer> treeIdSet, String alertStat, String alertLevel, Long startTime, Long endTime, Integer pageNo, Integer pageSize, JsonObject labels) {
        Request request = createRequest(HttpMethodName.POST, alarmDomain + MiCloudAlertManager.alarm_alert_event_latest_uri, getDefaultIamId(), null);
        JsonObject params = new JsonObject();
        if (labels != null) {
            params.add("labels", labels);
        }
        if (!CollectionUtils.isEmpty(treeIdSet)) {
            params.add("tree_id_list", new Gson().toJsonTree(treeIdSet));
        }
        if (StringUtils.isNotBlank(alertLevel)) {
            params.addProperty("priority", alertLevel);
        }
        if (StringUtils.isNotBlank(alertStat)) {
            params.addProperty("status", alertStat);
        }
        if (startTime != null) {
            params.addProperty("start_time", startTime);
        }
        if (endTime != null) {
            params.addProperty("end_time", endTime);
        }
        if (pageNo != null) {
            params.addProperty("page_no", pageNo);
        }
        if (pageSize != null) {
            params.addProperty("page_size", pageSize);
        }
        request.setBody(params.toString());
        Result<PageData> jsonObjectResult = queryList(request);

        return jsonObjectResult;
    }

    @Override
    public Result<JsonObject> getEventById(String user, Integer treeId, String eventId) {
        Request request = createRequest(HttpMethodName.GET, alarmDomain + MiCloudAlertManager.alarm_alert_event_pre_uri + eventId, treeId, user);
        return exeRequest(request);
    }

    public Result<JsonElement> executeRequest(Request request){

        JsonElement data = null;

        CloseableHttpClient client = null;
        try {

            HttpRequestBase signedRequest = Client.sign(request);

            //Send the request.
            client = HttpClients.custom().build();
            HttpResponse response = client.execute(signedRequest);

            //Print the status line of the response.
            log.debug(response.getStatusLine().toString());

            //Print the header fields of the response.
            Header[] resHeaders = response.getAllHeaders();
            for (Header h : resHeaders) {
                log.debug(h.getName() + ":" + h.getValue());
            }

            //Print the body of the response.
            HttpEntity resEntity = response.getEntity();
            if (resEntity == null) {
                log.warn("executeRequest response no resEntity return! url : {}",request.getUrl());

                if(response.getStatusLine().getStatusCode() == 200){
                    log.warn("executeRequest success but no resEntity! url : {}",request.getUrl());
                    return Result.success(null);
                }else{
                    log.error("executeRequest error! response no resEntity return! req url : {},statusCode:{}",
                            request.getUrl(),response.getStatusLine().getStatusCode());

                    return Result.fail(ErrorCode.unknownError);
                }
            }

            JsonObject tokenJson = null;

            try {
                String result = EntityUtils.toString(resEntity, "UTF-8");

                log.info("executeRequest request:[{}] response : [{}]", new Gson().toJson(request), result);

                tokenJson = new Gson().fromJson(result, JsonObject.class);

                if(tokenJson.get("code") == null){
                    log.error("executeRequest error!resEntity no code return!");
                    return Result.fail(ErrorCode.unknownError);
                }

                int code = tokenJson.get("code").getAsInt();

                if(code == 200){
                    if(tokenJson.get("data")!=null){
                        log.info("executeRequest return data:{}",tokenJson.get("data").toString());
                        data = tokenJson.get("data");
                    }
                }else{
                    log.error("executeRequest result error! result : {}",result);
                    return Result.fail(new ExceptionCode(code,tokenJson.get("message").getAsString()));
                }
            }catch (Exception e){
                log.error(" executeRequest result parse failture ", e);
            }

        } catch (Exception e) {
            log.error(" executeRequest  failture ", e);
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                log.error("executeRequest client close error : {}",e.getMessage(),e);
            }
        }
        return Result.success(data);
    }

    public Request createRequest(HttpMethodName method, String url, String treeId, String user){
        log.debug("MiCloudAlertManager.createRequest sk:[{}], ak:[{}],url:{},treeId:{}", cloudSk, cloudAk,url,treeId);

        Request request = new Request();
        try {
            request.setKey(cloudAk);
            request.setSecret(cloudSk);
            request.setMethod(method.name());
            request.setUrl(url);
            request.addHeader("Content-Type", "text/plain");
            request.addHeader("iam-tree-id", treeId);
            if(StringUtils.isNoneBlank(user)){
                request.addHeader("user", user);
            }

        } catch (Exception e) {
            log.error("AlarmService request init error :{}",e.getMessage(),e);
        }
        return request;
    }

    @Override
    public Result<JsonObject> deleteAlertGroup(String user, long id) {
        StringBuilder reqUrl = new StringBuilder();
        reqUrl.append(alarmDomain).append(MiCloudAlertManager.alarm_alert_group).append("/").append(id);
        Request request = createRequest(HttpMethodName.DELETE, reqUrl.toString(), getDefaultIamId(), user);
        return exeRequest(request);
    }

    @Override
    public Result<JsonObject> editAlertGroup(String user, long id, String name, String note, String chatId, List<Long> memberIds) {
        StringBuilder reqUrl = new StringBuilder();
        reqUrl.append(alarmDomain).append(MiCloudAlertManager.alarm_alert_group).append("/").append(id);
        Request request = createRequest(HttpMethodName.PUT, reqUrl.toString(), getDefaultIamId(), user);
        JsonObject params = new JsonObject();
        params.addProperty("name", name);
        params.addProperty("note", note);
        if (StringUtils.isNotBlank(chatId)) {
            params.addProperty("chat_id", chatId);
        }
        JsonArray memberIdList = new JsonArray();
        for (Long memberId : memberIds) {
            memberIdList.add(memberId);
        }
        params.add("members", memberIdList);
        request.setBody(params.toString());
        return exeRequest(request);
    }

    @Override
    public Result<JsonObject> getAlertGroup(String user, long id) {
        StringBuilder reqUrl = new StringBuilder();
        reqUrl.append(alarmDomain).append(MiCloudAlertManager.alarm_alert_group).append("/").append(id);
        Request request = createRequest(HttpMethodName.GET, reqUrl.toString(), getDefaultIamId(), user);
        return exeRequest(request);
    }

    @Override
    public Result<JsonObject> createAlertGroup(String user, String name, String note, String chatId, List<Long> memberIds) {
        Request request = createRequest(HttpMethodName.POST, alarmDomain + MiCloudAlertManager.alarm_alert_group, getDefaultIamId(), user);
        JsonObject params = new JsonObject();
        params.addProperty("name", name);
        params.addProperty("note", note);
        if (StringUtils.isNotBlank(chatId)) {
            params.addProperty("chat_id", chatId);
        }
        JsonArray memberList = new JsonArray();
        for (Long memberId : memberIds) {
            memberList.add(memberId);
        }
        params.add("members", memberList);
        request.setBody(params.toString());
        return exeRequest(request);
    }

    public Result<JsonObject> exeRequest(Request request){

        CloseableHttpClient client = null;

        JsonObject resultJson = null;
        JsonElement data = null;

        try {

            HttpRequestBase signedRequest = Client.sign(request);

            //Send the request.
            client = HttpClients.custom().build();
            HttpResponse response = client.execute(signedRequest);

            //Print the status line of the response.
            log.debug(response.getStatusLine().toString());

            //Print the header fields of the response.
            Header[] resHeaders = response.getAllHeaders();
            for (Header h : resHeaders) {
                log.debug(h.getName() + ":" + h.getValue());
            }

            //Print the body of the response.
            HttpEntity resEntity = response.getEntity();
            if (resEntity == null) {
                log.warn("exeRequest response no resEntity return! url : {}",request.getUrl());

                if(response.getStatusLine().getStatusCode() == 200){
                    log.warn("exeRequest success but no resEntity! url : {}",request.getUrl());
                    return Result.success(null);
                }else{
                    log.error("exeRequest error! response no resEntity return! req url : {},statusCode:{}",
                            request.getUrl(),response.getStatusLine().getStatusCode());

                    return Result.fail(ErrorCode.unknownError);
                }
            }

            try {
                String result = EntityUtils.toString(resEntity, "UTF-8");

                log.info("exeRequest request:[{}] response : [{}]", new Gson().toJson(request), result);

                resultJson = new Gson().fromJson(result, JsonObject.class);

                if(resultJson.get("code") == null){
                    log.error("exeRequest error!resEntity no code return!");
                    return Result.fail(ErrorCode.unknownError);
                }

                int code = resultJson.get("code").getAsInt();

                if(code == 200){
                    if(resultJson.has("data") && !resultJson.get("data").isJsonPrimitive()){
                        log.info("exeRequest return data:{}",resultJson.get("data").toString());
                        data = resultJson.get("data");
                    }
                }else{
                    log.error("exeRequest result error! result : {}",result);
                    return Result.fail(new ExceptionCode(ErrorCode.OperFailed.getCode() ,ErrorCode.OperFailed.getConvertMsg(resultJson.get("message").getAsString())));
                }
            }catch (Exception e){
                log.error(" exeRequest result parse failture ", e);
            }

        } catch (Exception e) {
            log.error(" exeRequest failture ", e);
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (Exception e) {
                log.error("exeRequest client close error : {}",e.getMessage(),e);
            }
        }
        try {
            if (data == null) {
                return Result.success(null);
            }
            return Result.success(data.getAsJsonObject());
        } catch (Exception e) {
            log.error(" exeRequest result parse failture ", e);
            return null;
        }
    }

    @Override
    public Result<PageData<List<UserInfo>>> searchUser(String user, String searchName, int pageNo, int pageSize) {
        StringBuilder reqUrl = new StringBuilder();
        reqUrl.append(alarmDomain);
        reqUrl.append(MiCloudAlertManager.alarm_user_search).append("?page_no=").append(pageNo).append("&").append("page_size=").append(pageSize);
        if (StringUtils.isNotBlank(searchName)) {
            reqUrl.append("&name=").append(searchName);
        }
        Request request = createRequest(HttpMethodName.GET, reqUrl.toString(), getDefaultIamId(), user);
        Result<PageData> pageDataResult = queryList(request);
        log.info("MiCloudAlertManager.searchUser result={}", pageDataResult);
        PageData<List<UserInfo>> pageData = new PageData<>();
        pageData.setPage(pageNo);
        pageData.setPageSize(pageSize);
        pageData.setTotal(0L);
        if (pageDataResult != null && pageDataResult.getData() != null) {
            pageData.setTotal(pageDataResult.getData().getTotal());
            pageData.setList(alertHelper.buildUserInfoList((JsonElement) pageDataResult.getData().getList()));
        }
        return Result.success(pageData);
    }

    @Override
    public Result<PageData> getAlertGroupPageData(String user, String name, int pageNo, int pageSize) {
        StringBuilder reqUrl = new StringBuilder();
        reqUrl.append(alarmDomain).append(MiCloudAlertManager.alarm_alert_group_list).append("?page_no=").append(pageNo)
                .append("&page_size=").append(pageSize);
        if (StringUtils.isNotBlank(user)) {
            reqUrl.append("&member=").append(user);
        }
        if (StringUtils.isNotBlank(name)) {
            reqUrl.append("&name=").append(name);
        }
        Request request = createRequest(HttpMethodName.GET, reqUrl.toString(), getDefaultIamId(), user);
        return queryList(request);
    }

    @Override
    public Result<JsonObject> resolvedEvent(String user, Integer treeId, String alertName, String comment, Long startTime, Long endTime) {
        Request request = createRequest(HttpMethodName.POST, alarmDomain + MiCloudAlertManager.alarm_alert_event_resolve_uri, treeId, user);
        JsonObject params = new JsonObject();
        if (StringUtils.isNotBlank(comment)) {
            params.addProperty("comment", comment);
        }
        if (startTime != null) {
            params.addProperty("start_time", startTime);
        }
        if (endTime != null) {
            params.addProperty("end_time", endTime);
        }
        if (StringUtils.isNotBlank(alertName)) {
            JsonObject matcher = new JsonObject();
            matcher.addProperty("name", "alertname");
            matcher.addProperty("value", alertName);
            matcher.addProperty("is_equal", Boolean.TRUE);
            matcher.addProperty("is_regex", Boolean.FALSE);
            JsonArray matchers = new JsonArray();
            matchers.add(matcher);
            params.add("matchers", matchers);
        }
        request.setBody(params.toString());
        return exeRequest(request);
    }

    public Integer getDefaultIamId(){

        log.info("prometheusAlarmEnv ================ :{}",prometheusAlarmEnv);

        if(alarm_staging_env.equals(prometheusAlarmEnv)){
            return stagingDefaultIamId;
        }

        if(alarm_online_env.equals(prometheusAlarmEnv) || alarm_preview_env.equals(prometheusAlarmEnv)){
            return onlineDefaultIamId;
        }

        //默认返回staging环境
        return stagingDefaultIamId;
    }

    public Request createRequest(HttpMethodName method, String url,Integer treeId,String user){
        log.debug("MiCloudAlertManager.createRequest sk:[{}], ak:[{}],url:{},treeId:{}", cloudSk, cloudAk,url,treeId);

        Request request = new Request();
        try {
            request.setKey(cloudAk);
            request.setSecret(cloudSk);
            request.setMethod(method.name());
            request.setUrl(url);
            request.addHeader("Content-Type", "text/plain");
            request.addHeader("iam-tree-id", String.valueOf(treeId));
            if(StringUtils.isNoneBlank(user)){
                request.addHeader("user", user);
            }

        } catch (Exception e) {
            log.error("AlarmService request init error :{}",e.getMessage(),e);
        }
        return request;
    }

    public Result<PageData> queryList(Request request){

        CloseableHttpClient client = null;

        JsonObject resultJson = null;
        JsonElement data = null;

        try {

            HttpRequestBase signedRequest = Client.sign(request);

            //Send the request.
            client = HttpClients.custom().build();
            HttpResponse response = client.execute(signedRequest);

            //Print the status line of the response.
            log.debug(response.getStatusLine().toString());

            //Print the header fields of the response.
            Header[] resHeaders = response.getAllHeaders();
            for (Header h : resHeaders) {
                log.debug(h.getName() + ":" + h.getValue());
            }

            //Print the body of the response.
            HttpEntity resEntity = response.getEntity();
            if (resEntity == null) {
                log.warn("queryList response no resEntity return! url : {}",request.getUrl());

                if(response.getStatusLine().getStatusCode() == 200){
                    log.warn("queryList success but no resEntity! url : {}",request.getUrl());
                    return Result.success(null);
                }else{
                    log.error("queryList error! response no resEntity return! req url : {},statusCode:{}",
                            request.getUrl(),response.getStatusLine().getStatusCode());

                    return Result.fail(ErrorCode.unknownError);
                }
            }

            try {
                String result = EntityUtils.toString(resEntity, "UTF-8");

                log.info("queryList request:[{}] response : [{}]", new Gson().toJson(request), result);

                resultJson = new Gson().fromJson(result, JsonObject.class);

                if(resultJson.get("code") == null){
                    log.error("queryList error!resEntity no code return!");
                    return Result.fail(ErrorCode.unknownError);
                }

                int code = resultJson.get("code").getAsInt();

                if(code == 200){
                    if(resultJson.get("data")!=null){
                        log.info("queryList return data:{}",resultJson.get("data").toString());
                        data = resultJson.get("data");
                    }
                }else{
                    log.error("queryList result error! result : {}",result);
                    return Result.fail(new ExceptionCode(code,resultJson.get("message").getAsString()));
                }
            }catch (Exception e){
                log.error(" queryList result parse failture ", e);
            }

        } catch (Exception e) {
            log.error(" queryList failture ", e);
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                log.error("queryList client close error : {}",e.getMessage(),e);
            }
        }

        PageData pd = new PageData();
        if(data != null && data.isJsonArray()){
            int total = resultJson.get("total").getAsInt();
            pd.setTotal(Long.valueOf(total));
            pd.setList(data);
        }
        return Result.success(pd);
    }


}
