package com.xiaomi.mone.monitor.service.aop.action;

import com.xiaomi.mone.monitor.bo.HeraReqInfo;
import com.xiaomi.mone.monitor.bo.OperLogAction;
import com.xiaomi.mone.monitor.dao.model.AlarmStrategy;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.dao.model.HeraOperLog;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.aop.context.HeraRequestMappingContext;
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
public class HeraRequestMappingActionRuleDelete extends HeraRequestMappingActionArg2<HttpServletRequest, List<Integer>, Result>{

    @Autowired
    private HeraRequestMappingActionStrategyHelper heraRequestMappingActionStrategyHelper;

    @Override
    public void beforeAction(HttpServletRequest arg1, List<Integer> arg2, HeraReqInfo heraReqInfo) {
        if (arg2 == null || arg2.isEmpty()) {
            return;
        }
        Integer strategyId = heraRequestMappingActionStrategyHelper.getStrategyIdByRuleId(arg2.get(0));
        if (strategyId == null) {
            return;
        }
        HeraRequestMappingContext.set("strategyId", strategyId);
        Pair<AlarmStrategy, List<AppAlarmRule>> pair = heraRequestMappingActionStrategyHelper.queryStrategyById(strategyId);
        HeraOperLog operLog = new HeraOperLog();
        operLog.setOperName(heraReqInfo.getUser());
        operLog.setModuleName(heraReqInfo.getModuleName());
        operLog.setInterfaceName(heraReqInfo.getInterfaceName());
        operLog.setInterfaceUrl(heraReqInfo.getReqUrl());
        operLog.setAction(OperLogAction.STRATEGY_EDIT.getAction());
        heraRequestMappingActionStrategyHelper.saveHeraOperLogs(pair, operLog, heraReqInfo);
        if (operLog.getId() != null) {
            heraReqInfo.setOperLog(operLog);
        }
    }

    @Override
    public void afterAction(HttpServletRequest arg1, List<Integer> arg2, HeraReqInfo heraReqInfo, Result result) {
        if (arg2 == null || arg2.isEmpty()) {
            return;
        }
        Integer strategyId = HeraRequestMappingContext.get("strategyId");
        if (strategyId == null) {
            return;
        }
        Pair<AlarmStrategy, List<AppAlarmRule>> pair = heraRequestMappingActionStrategyHelper.queryStrategyById(strategyId);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", result.getCode());
        resultMap.put("message", result.getMessage());
        heraReqInfo.getOperLog().setResultDesc(Json.toJson(resultMap));
        heraRequestMappingActionStrategyHelper.saveHeraOperLogs(pair, heraReqInfo.getOperLog(), heraReqInfo);
    }
}
