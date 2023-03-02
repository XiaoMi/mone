package com.xiaomi.youpin.prometheus.agent.service.Impl;

import com.xiaomi.youpin.prometheus.agent.Commons;
import com.xiaomi.youpin.prometheus.agent.api.service.PrometheusAlertService;
import com.xiaomi.youpin.prometheus.agent.enums.ErrorCode;
import com.xiaomi.youpin.prometheus.agent.param.alert.RuleAlertParam;
import com.xiaomi.youpin.prometheus.agent.result.Result;
import com.xiaomi.youpin.prometheus.agent.service.prometheus.RuleAlertService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service(timeout = 5000, group = "${dubbo.group}")
public class PrometheusAlertImpl implements PrometheusAlertService {
    @Autowired
    RuleAlertService ruleAlertService;

    @Override
    public Result createRuleAlert(RuleAlertParam param) {
        Result result = ruleAlertService.CreateRuleAlert(param);
        return result;
    }

    @Override
    public Result UpdateRuleAlert(String id, RuleAlertParam param) {
        if (id == null || param == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        Result result = ruleAlertService.UpdateRuleAlert(id,param);
        return result;
    }

    @Override
    public Result DeleteRuleAlert(String id) {
        if (id == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        Result result = ruleAlertService.DeleteRuleAlert(id);
        return result;
    }

    @Override
    public Result GetRuleAlert(String id) {
        if (id == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        Result result = ruleAlertService.GetRuleAlert(id);
        return result;
    }

    @Override
    public Result GetRuleAlertList(Integer pageSize, Integer pageNo) {
        if (pageSize == null && pageNo == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        if (pageSize == null) {
            pageSize = Commons.COMMON_PAGE_SIZE;
        }
        if (pageNo == null) {
            pageNo = Commons.COMMON_PAGE_NO;
        }
        Result result = ruleAlertService.GetRuleAlertList(pageSize, pageNo);
        return result;
    }

    @Override
    public Result EnabledRuleAlert(String id, String enabled) {
        if (id == null || StringUtils.isBlank(enabled) || ( !enabled.equals("0") && !enabled.equals("1"))) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        Result result = ruleAlertService.EnabledRuleAlert(id,enabled);
        return result;
    }
}
