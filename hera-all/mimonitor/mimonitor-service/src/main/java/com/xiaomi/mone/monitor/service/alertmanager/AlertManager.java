package com.xiaomi.mone.monitor.service.alertmanager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.UserInfo;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.PageData;

import java.util.List;
import java.util.Set;

/**
 * @author gaoxihui
 * @date 2022/11/7 2:57 下午
 */
public interface AlertManager {

    public Result addRule(JsonObject param,String identifyId, String user);

    public Result editRule(Integer alertId,JsonObject param,String identifyId, String user);

    public Result delRule(Integer alertId,String identifyId, String user);

    public Result enableRule(Integer alertId,Integer pauseStatus,String identifyId, String user);

    public Result  queryRuels(JsonObject params, String identifyId, String user);

    public Result<JsonElement>  getAlarmRuleRemote(Integer alarmId,Integer iamId,String user);

    public Result updateAlarm(Integer alarmId,Integer iamId,String user,String body);

    Result<JsonElement> addAlarmGroup(JsonObject params, String identifyId, String user);

    Result<JsonElement> searchAlarmGroup(String alarmGroup,String identifyId,String user);

    Result<PageData> searchAlertTeam(String name,String note,String manager,String oncallUser,String service,Integer iamId,String user,Integer page_no,Integer page_size);

    Result<PageData> queryEvents(String user, Integer treeId, String alertLevel, Long startTime, Long endTime, Integer pageNo, Integer pageSize, JsonObject labels);

    Result<PageData> queryLatestEvents(Set<Integer> treeIdSet, String alertStat, String alertLevel, Long startTime, Long endTime, Integer pageNo, Integer pageSize, JsonObject labels);

    Result<JsonObject> getEventById(String user, Integer treeId, String eventId);

    Result<PageData> getAlertGroupPageData(String user, String name, int pageNo, int pageSize);

    Result<JsonObject> resolvedEvent(String user, Integer treeId, String alertName, String comment, Long startTime, Long endTime);

    Result<PageData<List<UserInfo>>> searchUser(String user, String searchName, int pageNo, int pageSize);

    Result<JsonObject> createAlertGroup(String user, String name, String note, String chatId, List<Long> memberIds);

    Result<JsonObject> getAlertGroup(String user, long id);

    Result<JsonObject> editAlertGroup(String user, long id, String name, String note, String chatId, List<Long> memberIds);

    Result<JsonObject> deleteAlertGroup(String user, long id);

    Integer getDefaultIamId();
}
