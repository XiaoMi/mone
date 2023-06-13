package com.xiaomi.mone.monitor.service.alertmanager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.UserInfo;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.alarm.duty.DutyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author gaoxihui
 * @date 2022/11/7 2:23 下午
 */
@Service
public class AlertServiceAdapt {

    @Autowired
    private AlertManager alertManager;


    public Result addRule(JsonObject param,String identifyId,  String user){

        return alertManager.addRule(param,identifyId,user);

    }

    public Result editRule(Integer alertId,JsonObject param,String identifyId, String user){

        return alertManager.editRule(alertId,param,identifyId,user);

    }

    public Result delRule(Integer alertId,String identifyId, String user){

        return alertManager.delRule(alertId,identifyId,user);

    }

    public Result enableRule(Integer alertId,Integer pauseStatus,String identifyId, String user){

        return alertManager.enableRule(alertId,pauseStatus,identifyId,user);
    }

    public Result queryRuels(JsonObject params, String identifyId, String user){
        return alertManager.queryRuels(params,identifyId,user);
    }

    public Result<JsonElement>  getAlarmRuleRemote(Integer alarmId,Integer iamId,String user){
        return alertManager.getAlarmRuleRemote(alarmId,iamId,user);
    }

    public Result updateAlarm(Integer alarmId,Integer iamId,String user,String body){
        return alertManager.updateAlarm(alarmId,iamId,user,body);
    }

    public Result<JsonElement> addAlarmGroup(JsonObject params, String iamId, String user){
        return alertManager.addAlarmGroup(params,iamId,user);
    }

    public Result<JsonElement> searchAlarmGroup(String alarmGroup,String identifyId,String user){
        return alertManager.searchAlarmGroup(alarmGroup,identifyId,user);
    }

    public Result<PageData> searchAlertTeam(String name,String note,String manager,String oncallUser,String service,Integer iamId,String user,Integer page_no,Integer page_size){
        return alertManager.searchAlertTeam(name,note,manager,oncallUser,service,iamId,user,page_no,page_size);
    }
    public Result<PageData> queryEvents(String user, Integer treeId, String alertLevel, Long startTime, Long endTime, Integer pageNo, Integer pageSize, JsonObject labels){
        return alertManager.queryEvents(user,treeId,alertLevel,startTime,endTime,pageNo,pageSize,labels);
    }

    public Result<PageData> queryLatestEvents(Set<Integer> treeIdSet, String alertStat, String alertLevel, Long startTime, Long endTime, Integer pageNo, Integer pageSize, JsonObject labels){
        return alertManager.queryLatestEvents(treeIdSet,alertStat,alertLevel,startTime,endTime,pageNo,pageSize,labels);
    }

    public Result<JsonObject> getEventById(String user, Integer treeId, String eventId) {
        return alertManager.getEventById(user,treeId,eventId);
    }

    public Result<JsonObject> resolvedEvent(String user, Integer treeId, String alertName, String comment, Long startTime, Long endTime) {
        return alertManager.resolvedEvent(user,treeId,alertName,comment,startTime,endTime);
    }

    public Result<PageData> getAlertGroupPageData(String user, String name, int pageNo, int pageSize) {
        return alertManager.getAlertGroupPageData(user, name, pageNo, pageSize);
    }

    public Result<PageData<List<UserInfo>>> searchUser(String user, String searchName, int pageNo, int pageSize) {
        return alertManager.searchUser(user, searchName, pageNo, pageSize);
    }

    public Result<JsonObject> createAlertGroup(String user, String name, String note, String chatId, List<Long> memberIds, DutyInfo dutyInfo) {
        return alertManager.createAlertGroup(user, name, note, chatId, memberIds,dutyInfo);
    }

    public Result<JsonObject> getAlertGroup(String user, long id) {
        return alertManager.getAlertGroup(user, id);
    }

    public Result<JsonObject> editAlertGroup(String user, long id, String name, String note, String chatId, List<Long> memberIds,DutyInfo dutyInfo) {
        return alertManager.editAlertGroup(user, id, name, note, chatId, memberIds,dutyInfo);
    }

    public Result<JsonObject> deleteAlertGroup(String user, long id) {
        return alertManager.deleteAlertGroup(user, id);
    }

    public Integer getDefaultIamId() {
        return alertManager.getDefaultIamId();
    }


    public Result<JsonObject> dutyInfoList(String user, long id,Long start,Long end) {
        return alertManager.dutyInfoList(user, id,start,end);
    }
}
