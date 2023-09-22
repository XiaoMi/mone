package com.xiaomi.youpin.prometheus.agent.api.service;

import com.xiaomi.youpin.prometheus.agent.param.alert.RuleAlertParam;
import com.xiaomi.youpin.prometheus.agent.result.Result;

/**
 * @author zhangxiaowei6
 */

//Provide alertManager with alarm-related dubbo interfaces.
public interface PrometheusAlertService {
    Result createRuleAlert(RuleAlertParam param);
    Result UpdateRuleAlert(String id ,RuleAlertParam param);

    Result DeleteRuleAlert(String id);

    Result GetRuleAlert(String id);

    Result GetRuleAlertList(Integer pageSize,Integer pageNo);

    Result EnabledRuleAlert(String id, String enabled);
}
