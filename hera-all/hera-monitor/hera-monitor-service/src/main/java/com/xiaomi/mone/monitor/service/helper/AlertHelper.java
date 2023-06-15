/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor.service.helper;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.*;
import com.xiaomi.mone.monitor.dao.model.AlertGroup;
import com.xiaomi.mone.monitor.dao.model.AlertGroupMember;
import com.xiaomi.mone.monitor.pojo.ReqErrorMetricsPOJO;
import com.xiaomi.mone.monitor.pojo.ReqSlowMetricsPOJO;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.AlertHelperExtension;
import com.xiaomi.mone.monitor.service.api.ReqErrorMetricsService;
import com.xiaomi.mone.monitor.service.api.ReqSlowMetricsService;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.alarm.duty.DutyInfo;
import com.xiaomi.mone.monitor.service.prometheus.AlarmService;
import com.xiaomi.mone.monitor.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhanggaofeng1
 */
@Slf4j
@Component
public class AlertHelper {

    private final long TEN_MINUTES = 10 * 60 * 1000L;//毫秒
    private final long FIVE_MINUTES = 10 * 60 * 1000L * 1000L;//微妙
    @Autowired
    private AlarmService alarmService;
    @Autowired
    private AlertHelperExtension alertHelperExtension;
    @Autowired
    private ReqErrorMetricsService reqErrorMetricsService;
    @Autowired
    private ReqSlowMetricsService reqSlowMetricsService;
    @NacosValue(value = "${hera.url}",autoRefreshed = true)
    private String heraUrl;


    /**
     * 查询告警事件数量
     *
     * @param projectId
     * @param treeId
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer queryAlertEventNum(Integer projectId, Integer treeId, Long startTime, Long endTime) {
        try {
            JsonObject labels = new JsonObject();
            labels.addProperty("project_id", projectId.toString());
            Set<Integer> treeIdSet = new HashSet<>();
            treeIdSet.add(treeId);
            Result<PageData> result = alarmService.queryLatestEvents(treeIdSet, "firing", null, startTime, endTime, 1, 1, labels);
            if (result == null || result.getData() == null || result.getData().getTotal() == null) {
                return 0;
            }
            return result.getData().getTotal().intValue();
        } catch (Exception e) {
            log.error("查询最近告警事件异常 projectId={}", projectId, e);
            return 0;
        }
    }

    public JsonObject buildLabels(AlertHistoryParam param) {
        JsonObject labels = new JsonObject();
        if (StringUtils.isNotBlank(param.getServerIp())) {
            labels.addProperty("serverIp", param.getServerIp());
        }
        if (StringUtils.isNotBlank(param.getInstance())) {
            labels.addProperty("instance", param.getInstance());
        }
        if (StringUtils.isNotBlank(param.getMethodName())) {
            labels.addProperty("methodName", param.getMethodName());
        }
        return labels;
    }

    /**
     * @param list
     * @return
     */
    public List<AlertHistory> buildAlertHistoryList(JsonElement list) {
        if (list == null || !list.isJsonArray()) {
            return null;
        }
        List<AlertHistory> historyList = new ArrayList<>();
        list.getAsJsonArray().forEach((ele) -> {
            JsonObject data = ele.getAsJsonObject();
            AlertHistory history = new AlertHistory();
            historyList.add(history);
            if (data.has("id")) {
                history.setId(data.get("id").getAsString());
            }
            if (data.has("alert_id")) {
                history.setAlertId(data.get("alert_id").getAsString());
            }
            if (data.has("tree_id")) {
                history.setIamTreeId(data.get("tree_id").getAsInt());
            }
            if (data.has("alert_name")) {
                history.setAlertName(data.get("alert_name").getAsString());
            }
            if (data.has("alert_cname")) {
                history.setAlertCName(data.get("alert_cname").getAsString());
            }
            if (data.has("alert_time")) {
                history.setAlertDate(CommonUtil.toMillis(data.get("alert_time").getAsLong()));
            } else {
                history.setAlertDate(System.currentTimeMillis());
            }
            if (data.has("priority")) {
                history.setAlertLevel(data.get("priority").getAsString());
            }
            if (data.has("start_time")) {
                history.setStartTime(CommonUtil.toMillis(data.get("start_time").getAsLong()));
            }
            if (data.has("end_time")) {
                history.setEndTime(CommonUtil.toMillis(data.get("end_time").getAsLong()));
            }
            //resolved firing
            if (data.has("status")) {
                history.setAlertStat(data.get("status").getAsString());
            }
            history.setDurationTime(buildDurationTime(data));
            // 拼接异常详情
            StringBuilder url = new StringBuilder();
            if (data.has("labels")) {
                JsonObject labels = data.get("labels").getAsJsonObject();
                StringBuilder content = new StringBuilder();
                buildAlertContent(content, labels);
                buildDataCountContent(content, labels);
                buildAlarmSendInterval(content, labels);
                history.setAlertContent(content.toString());
                if (labels.has("project_name")) {
                    history.setAlertApp(labels.get("project_name").getAsString());
                }
                if (labels.has("project_id")) {
                    history.setAlertAppId(labels.get("project_id").getAsString());
                }
                if (labels.has("instance")) {
                    history.setAlertIntance(labels.get("instance").getAsString());
                }
                if (labels.has("methodName")) {
                    history.setMethodName(labels.get("methodName").getAsString());
                }
                if (labels.has("ip")) {
                    history.setAlertIp(labels.get("ip").getAsString());
                }
                if (labels.has("serverIp")) {
                    history.setAlertIp(labels.get("serverIp").getAsString());
                }
                //特殊url处理
                if (labels.has("detailRedirectUrl")) {
                    url.append(labels.get("detailRedirectUrl").getAsString());
                    //时间单位是微秒
                    url.append("&start=").append(history.getAlertDate() * 1000L - FIVE_MINUTES);
                    url.append("&end=").append(history.getAlertDate() * 1000L + FIVE_MINUTES);
                    if (labels.has("group") && labels.has("url")) {
                        url.append("&operation=").append("/mtop/").append(labels.get("group").getAsString()).append("/").append(labels.get("url").getAsString());
                    }
                    history.setDetailedUrl(url.toString());
                    return;
                }
            }
            url.append(heraUrl).append("?id=").append(history.getAlertAppId())
                    .append("&name=").append(history.getAlertApp())
                    .append("&start_time=").append(history.getAlertDate() - TEN_MINUTES)
                    .append("&end_time=").append(history.getAlertDate() + TEN_MINUTES);
                    if (StringUtils.isNotBlank(history.getAlertIp())) {
                        url.append("&var-instance=").append(history.getAlertIp());
                    }
                    if (StringUtils.isNotBlank(history.getMethodName())) {
                        url.append("&method_name=").append(history.getMethodName());
                    }
            ReqErrorMetricsPOJO errMetrics = reqErrorMetricsService.getErrorMetricsByMetrics(history.getAlertName());
            if (errMetrics != null) {
                url.append("&activeTab=exception").append("&metric=").append(errMetrics.getCode());
                history.setDetailedUrl(url.toString());
                return;
            }
            ReqSlowMetricsPOJO slowMetrics = reqSlowMetricsService.getSlowMetricsByMetric(history.getAlertName());
            if (slowMetrics != null) {
                url.append("&activeTab=slowQuery").append("&metric=").append(slowMetrics.getCode());
                history.setDetailedUrl(url.toString());
                return;
            }
            url.append("&activeTab=dashboard");
            history.setDetailedUrl(url.toString());
        });
        return historyList;
    }

    public AlertHistoryDetailed buildAlertHistoryDetailed(JsonObject data) {
        AlertHistoryDetailed detailed = new AlertHistoryDetailed();
        if (data.has("id")) {
            detailed.setId(data.get("id").getAsString());
        }
        if (data.has("alert_id")) {
            detailed.setAlertId(data.get("alert_id").getAsString());
        }
        if (data.has("tree_id")) {
            detailed.setIamTreeId(data.get("tree_id").getAsInt());
        }
        if (data.has("alert_name")) {
            detailed.setAlertName(data.get("alert_name").getAsString());
        }
        if (data.has("alert_cname")) {
            detailed.setAlertCName(data.get("alert_cname").getAsString());
        }
        if (data.has("alert_time")) {
            detailed.setAlertDate(CommonUtil.toMillis(data.get("alert_time").getAsLong()));
        }
        if (data.has("priority")) {
            detailed.setAlertLevel(data.get("priority").getAsString());
        }
        //resolved firing
        if (data.has("status")) {
            detailed.setAlertStat(data.get("status").getAsString());
        }
        if (data.has("end_time")) {
            detailed.setAlertEndTime(CommonUtil.toMillis(data.get("end_time").getAsLong()));
        }
        if (data.has("start_time")) {
            detailed.setAlertStartTime(CommonUtil.toMillis(data.get("start_time").getAsLong()));
        }
        detailed.setDurationTime(buildDurationTime(data));
        if (data.has("labels")) {
            JsonObject labels = data.get("labels").getAsJsonObject();
            StringBuilder content = new StringBuilder();
            buildAlertContent(content, labels);
            buildDataCountContent(content, labels);
            buildAlarmSendInterval(content, labels);
            detailed.setAlertContent(content.toString());
            if (labels.has("project_name")) {
                detailed.setAlertApp(labels.get("project_name").getAsString());
            }
            if (labels.has("project_id")) {
                detailed.setAlertAppId(labels.get("project_id").getAsString());
            }
            if (labels.has("instance")) {
                detailed.setAlertIntance(labels.get("instance").getAsString());
            }
            if (labels.has("ip")) {
                detailed.setAlertIp(labels.get("ip").getAsString());
            }
            if (labels.has("serverIp")) {
                detailed.setAlertIp(labels.get("serverIp").getAsString());
            }
        }
        return detailed;
    }

    private void buildAlertContent(StringBuilder content, JsonObject data) {
        alertHelperExtension.buildAlertContent(content, data);
    }

    private String buildDurationTime(JsonObject data) {
        if (!data.has("duration")) {
            return null;
        }
        StringBuilder content = new StringBuilder();
        Long duration = data.get("duration").getAsLong();
        if (duration < 0L) {
            duration = 0L;
        }
        if (duration < 60L) {
            content.append("持续").append(duration).append("秒");
        } else {
            content.append("持续").append(duration / 60L).append("分钟").append(duration % 60L).append("秒");
        }
        return content.toString();
    }

    private void buildDataCountContent(StringBuilder content, JsonObject data) {
        if (!data.has("data_count")) {
            return;
        }
        AlarmCheckDataCount count = AlarmCheckDataCount.getByCode(data.get("data_count").getAsString());
        if (AlarmCheckDataCount.zero.equals(count)) {
            content.append("立即触发").append(", ");
            return;
        }
        content.append(count.getMessage()).append(", ");
    }

    private void buildAlarmSendInterval(StringBuilder content, JsonObject data) {
        if (!data.has("send_interval")) {
            return;
        }
        AlarmSendInterval interval = AlarmSendInterval.getEnum(data.get("send_interval").getAsString());
        if (interval == null) {
            return;
        }
        content.append("每").append(interval.getMessage());
    }

    /**
     * @param list
     * @return
     */
    public List<AlertGroupMember> buildAlertGroupMemberList(JsonElement list) {
        if (list == null || !list.isJsonArray()) {
            return null;
        }
        List<AlertGroupMember> memberList = new ArrayList<>();
        list.getAsJsonArray().forEach((ele) -> {
            JsonObject data = ele.getAsJsonObject();
            AlertGroupMember member = new AlertGroupMember();
            memberList.add(member);
            if (data.has("id")) {
                member.setMemberId(data.get("id").getAsLong());
            }
            if (data.has("name")) {
                member.setMember(data.get("name").getAsString());
            }
        });
        return memberList;
    }

    /**
     *
     * @param user
     * @param agList
     * @return
     */
    public List<AlertGroupInfo> buildAlertGroupInfoList(String user, List<AlertGroup> agList) {
        if (agList == null || agList.isEmpty() ) {
            return null;
        }
        List<AlertGroupInfo> groupList = new ArrayList<>();
        agList.forEach((ele) -> {
            groupList.add(buildAlertGroupInfo(user, ele));
        });
        return groupList;
    }

    public AlertGroupInfo buildAlertGroupInfo(String user, AlertGroup ag) {
        if (ag == null) {
            return null;
        }
        AlertGroupInfo agInfo = new AlertGroupInfo();
        agInfo.setId(ag.getId());
        agInfo.setName(ag.getName());
        agInfo.setCreatedTime(ag.getCreateTime().getTime());
        agInfo.setCreatedBy(ag.getCreater());
        agInfo.setNote(ag.getDesc());
        agInfo.setChatId(ag.getChatId());
        agInfo.setMembers(buildUserInfoList(ag.getMembers()));
        agInfo.setType(ag.getType());
        agInfo.setRelId(ag.getRelId());
        if (user != null && agInfo.getMembers() != null &&
                agInfo.getMembers().stream().filter(m -> user.equals(m.getName())).findAny().isPresent()) {
            agInfo.setDelete(true);
            agInfo.setEdit(true);
        }
        agInfo.setDutyInfo(ag.getDutyInfo() == null ? null : new Gson().fromJson(ag.getDutyInfo(), DutyInfo.class));
        return agInfo;
    }

    public static void main(String[] args) {
        String test = "{\"manager\":\"liyandi\",\n" +
                "    \"child_groups\":[\n" +
                "        {\n" +
                "            \"name\":\"miaoshu\",\n" +
                "            \"oncall_parent_group_id\":1,\n" +
                "            \"rotation_type\":0,\n" +
                "            \"shift_length\":0,\n" +
                "            \"shift_length_unit\":\"\",\n" +
                "            \"duty_start_time\":1677664398,\n" +
                "            \"handoff_time\":43200,\n" +
                "            \"preset_vacation\":0,\n" +
                "            \"oncall_users\":[\n" +
                "                {\n" +
                "                    \"user\":\"liyandi\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"model_type\":0,\n" +
                "    \"chat_only\":0}";
        DutyInfo dutyInfo = new Gson().fromJson(test, DutyInfo.class);
        System.out.println(dutyInfo);
    }

    public List<UserInfo> buildUserInfoList(List<AlertGroupMember> agmList) {
        if (CollectionUtils.isEmpty(agmList)) {
            return null;
        }
        List<UserInfo> userList = new ArrayList<>();
        agmList.forEach((ele) -> {
            UserInfo user = new UserInfo();
            user.setId(ele.getMemberId());
            user.setName(ele.getMember());
            userList.add(user);
        });
        return userList;
    }

    /**
     * @param list
     * @return
     */
    public List<UserInfo> buildUserInfoList(JsonElement list) {
        if (list == null || !list.isJsonArray()) {
            return null;
        }
        List<UserInfo> userList = new ArrayList<>();
        list.getAsJsonArray().forEach((ele) -> {
            JsonObject data = ele.getAsJsonObject();
            UserInfo user = new UserInfo();
            userList.add(user);
            if (data.has("id")) {
                user.setId(data.get("id").getAsInt());
            }
            if (data.has("name")) {
                user.setName(data.get("name").getAsString());
            }
            if (data.has("cname")) {
                user.setCname(data.get("cname").getAsString());
            }
            if (data.has("email")) {
                user.setEmail(data.get("email").getAsString());
            }
        });
        return userList;
    }


    /**
     *
     * @param data
     * @return
     */
    public AlertGroup buildAlertGroup(JsonObject data) {
        if (data == null) {
            return null;
        }
        AlertGroup ag = new AlertGroup();
        if (data.has("id")) {
            ag.setRelId(data.get("id").getAsLong());
        }
        if (data.has("name")) {
            ag.setName(data.get("name").getAsString());
        }
        if (data.has("note")) {
            ag.setDesc(data.get("note").getAsString());
        }
        if (data.has("chat_id")) {
            ag.setChatId(data.get("chat_id").getAsString());
        }
        if (data.has("created_by")) {
            ag.setCreater(data.get("created_by").getAsString());
        }
        if (data.has("members")) {
            ag.setMembers(buildAlertGroupMemberList(data.get("members")));
        }
        if (data.has("duty_info")) {
            ag.setDutyInfo(data.get("duty_info").getAsJsonObject().toString());
        }
        return ag;
    }

    public List<AlertGroupMember> getDiffAgMember(List<AlertGroupMember> oldMembers, List<AlertGroupMember> newMembers) {
        if (CollectionUtils.isEmpty(oldMembers)) {
            return newMembers;
        }
        List<AlertGroupMember> lastNewMembers = new ArrayList<>();
        for (AlertGroupMember newMember : newMembers) {
            boolean find = false;
            for (AlertGroupMember oldMember : oldMembers) {
                if (oldMember.getMemberId().equals(newMember.getMemberId())) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                lastNewMembers.add(newMember);
            }
        }
        return lastNewMembers;
    }

}
