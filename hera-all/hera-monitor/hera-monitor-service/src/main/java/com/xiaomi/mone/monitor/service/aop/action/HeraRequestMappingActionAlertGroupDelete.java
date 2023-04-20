package com.xiaomi.mone.monitor.service.aop.action;

import com.xiaomi.mone.monitor.bo.AlertGroupInfo;
import com.xiaomi.mone.monitor.bo.AlertGroupParam;
import com.xiaomi.mone.monitor.bo.HeraReqInfo;
import com.xiaomi.mone.monitor.bo.OperLogAction;
import com.xiaomi.mone.monitor.dao.HeraOperLogDao;
import com.xiaomi.mone.monitor.dao.model.HeraOperLog;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.aop.helper.HeraRequestMappingActionAlertGroupHelper;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/13 16:01
 */
@Slf4j
@Service
public class HeraRequestMappingActionAlertGroupDelete extends HeraRequestMappingActionArg2<HttpServletRequest, AlertGroupParam, Result<AlertGroupInfo>>{

    @Autowired
    private HeraRequestMappingActionAlertGroupHelper heraRequestMappingActionAlertGroupHelper;
    @Autowired
    private HeraOperLogDao heraOperLogDao;

    @Override
    public void beforeAction(HttpServletRequest arg1, AlertGroupParam arg2, HeraReqInfo heraReqInfo) {
        if (arg2.getId() <= 0) {
            return;
        }
        Result<AlertGroupInfo> alertData = heraRequestMappingActionAlertGroupHelper.alertGroupDetailed(heraReqInfo.getUser(), arg2.getId());
        HeraOperLog operLog = new HeraOperLog();
        operLog.setOperName(heraReqInfo.getUser());
        operLog.setModuleName(heraReqInfo.getModuleName());
        operLog.setInterfaceName(heraReqInfo.getInterfaceName());
        operLog.setInterfaceUrl(heraReqInfo.getReqUrl());
        operLog.setAction(OperLogAction.ALERT_GROUP_ADD.getAction());
        heraRequestMappingActionAlertGroupHelper.saveHeraOperLogs(alertData, operLog, heraReqInfo);
        if (operLog.getId() != null) {
            heraReqInfo.setOperLog(operLog);
        }
    }

    @Override
    public void afterAction(HttpServletRequest arg1, AlertGroupParam arg2, HeraReqInfo heraReqInfo, Result<AlertGroupInfo> result) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", result.getCode());
        resultMap.put("message", result.getMessage());
        heraReqInfo.getOperLog().setResultDesc(Json.toJson(resultMap));
        heraRequestMappingActionAlertGroupHelper.saveHeraOperLogs(null, heraReqInfo.getOperLog(), heraReqInfo);
    }
}
