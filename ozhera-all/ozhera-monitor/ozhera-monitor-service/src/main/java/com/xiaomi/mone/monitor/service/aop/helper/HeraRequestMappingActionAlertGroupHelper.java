package com.xiaomi.mone.monitor.service.aop.helper;

import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.AlertGroupInfo;
import com.xiaomi.mone.monitor.bo.AlertGroupParam;
import com.xiaomi.mone.monitor.bo.HeraReqInfo;
import com.xiaomi.mone.monitor.dao.AppAlarmRuleDao;
import com.xiaomi.mone.monitor.dao.AppAlarmStrategyDao;
import com.xiaomi.mone.monitor.dao.HeraOperLogDao;
import com.xiaomi.mone.monitor.dao.model.AlarmStrategy;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.dao.model.HeraOperLog;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AlertGroupService;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingAction;
import com.xiaomi.mone.monitor.service.prometheus.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/13 16:01
 */
@Slf4j
@Service
public class HeraRequestMappingActionAlertGroupHelper {

    @Autowired
    private HeraOperLogDao heraOperLogDao;
    @Autowired
    private AlertGroupService alertGroupService;

    public Result<AlertGroupInfo> alertGroupDetailed(String user, Long id) {
        AlertGroupParam param = new AlertGroupParam();
        param.setId(id);
        return alertGroupService.alertGroupDetailed(user, param);
    }


    public void saveHeraOperLogs(Result<AlertGroupInfo> alertData, HeraOperLog operLog, HeraReqInfo heraReqInfo) {
        boolean beforeAction = operLog.getId() == null ? true : false;
        if (alertData != null) {
            if (beforeAction) {
                operLog.setBeforeData(Json.toJson(alertData));
            } else {
                operLog.setAfterData(Json.toJson(alertData));
            }
        }
        operLog.setDataType(HeraRequestMappingAction.DATA_ALERT_GROUP);
        operLog.setLogType(HeraRequestMappingAction.LOG_TYPE_PARENT);
        if(!heraOperLogDao.insertOrUpdate(operLog)) {
            log.error("操作日志AOP拦截插入或更新异常; heraReqInfo={},operLog={}", heraReqInfo, operLog);
            return;
        }
    }
}
