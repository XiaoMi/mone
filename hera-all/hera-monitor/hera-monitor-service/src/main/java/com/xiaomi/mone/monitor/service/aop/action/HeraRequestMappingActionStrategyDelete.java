package com.xiaomi.mone.monitor.service.aop.action;

import com.xiaomi.mone.monitor.bo.AlarmStrategyParam;
import com.xiaomi.mone.monitor.bo.HeraReqInfo;
import com.xiaomi.mone.monitor.bo.OperLogAction;
import com.xiaomi.mone.monitor.dao.model.AlarmStrategy;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.dao.model.HeraOperLog;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.aop.helper.HeraRequestMappingActionStrategyHelper;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/13 16:01
 */
@Slf4j
@Service
public class HeraRequestMappingActionStrategyDelete extends HeraRequestMappingActionArg2<HttpServletRequest, AlarmStrategyParam, Result>{

    @Autowired
    private HeraRequestMappingActionStrategyHelper heraRequestMappingActionStrategyHelper;

    @Override
    public void beforeAction(HttpServletRequest arg1, AlarmStrategyParam arg2, HeraReqInfo heraReqInfo) {
        if (arg2.getId() <= 0) {
            return;
        }
        Pair<AlarmStrategy, List<AppAlarmRule>> pair = heraRequestMappingActionStrategyHelper.queryStrategyById(arg2.getId());
        HeraOperLog operLog = new HeraOperLog();
        operLog.setOperName(heraReqInfo.getUser());
        operLog.setModuleName(heraReqInfo.getModuleName());
        operLog.setInterfaceName(heraReqInfo.getInterfaceName());
        operLog.setInterfaceUrl(heraReqInfo.getReqUrl());
        operLog.setAction(OperLogAction.STRATEGY_DELETE.getAction());
        heraRequestMappingActionStrategyHelper.saveHeraOperLogs(pair, operLog, heraReqInfo);
        if (operLog.getId() != null) {
            heraReqInfo.setOperLog(operLog);
        }
    }

    @Override
    public void afterAction(HttpServletRequest arg1, AlarmStrategyParam arg2, HeraReqInfo heraReqInfo, Result result) {
        Pair<AlarmStrategy, List<AppAlarmRule>> pair = heraRequestMappingActionStrategyHelper.queryStrategyById(arg2.getId());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", result.getCode());
        resultMap.put("message", result.getMessage());
        heraReqInfo.getOperLog().setResultDesc(Json.toJson(resultMap));
        heraRequestMappingActionStrategyHelper.saveHeraOperLogs(pair, heraReqInfo.getOperLog(), heraReqInfo);
    }
}
