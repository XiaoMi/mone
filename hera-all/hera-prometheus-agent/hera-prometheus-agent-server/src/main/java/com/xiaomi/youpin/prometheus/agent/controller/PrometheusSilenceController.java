package com.xiaomi.youpin.prometheus.agent.controller;


import com.xiaomi.youpin.prometheus.agent.aop.ArgCheck;
import com.xiaomi.youpin.prometheus.agent.enums.ErrorCode;
import com.xiaomi.youpin.prometheus.agent.param.alert.RuleAlertParam;
import com.xiaomi.youpin.prometheus.agent.param.alert.RuleSilenceParam;
import com.xiaomi.youpin.prometheus.agent.service.prometheus.RuleSilenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.xiaomi.youpin.prometheus.agent.result.Result;

//告警屏蔽相关接口

/**
 * @author zhangxiaowei6
 */
@RestController
@Slf4j
@RequestMapping(value = "/api/v1")
public class PrometheusSilenceController {

    @Autowired
    RuleSilenceService ruleSilenceService;

    @ArgCheck
    @RequestMapping(value = "/silence", method = RequestMethod.POST)
    public Result createRuleSilence(@RequestBody RuleSilenceParam param) {
        if (param == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        Result result = ruleSilenceService.createRuleSilence(param);
        return result;
    }

    @RequestMapping(value = "/silence/{id}", method = RequestMethod.PUT)
    public Result updateRuleSilence() {
        return null;
    }

    @RequestMapping(value = "/silence/{id}", method = RequestMethod.DELETE)
    public Result deleteRuleSilence() {
        return null;
    }

    @RequestMapping(value = "/silence/cancel/{id}", method = RequestMethod.PUT)
    public Result cancelRuleSilence() {
        return null;
    }

    @RequestMapping(value = "/silence/{id}", method = RequestMethod.GET)
    public Result searchRuleSilence() {
        return null;
    }

    @RequestMapping(value = "/silence/list", method = RequestMethod.POST)
    public Result searchRuleSilenceList() {
        return null;
    }

}
