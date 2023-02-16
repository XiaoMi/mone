package com.xiaomi.youpin.prometheus.agent.controller;

import com.google.gson.Gson;
import com.xiaomi.youpin.prometheus.agent.Commons;
import com.xiaomi.youpin.prometheus.agent.result.Result;
import com.xiaomi.youpin.prometheus.agent.enums.ErrorCode;
import com.xiaomi.youpin.prometheus.agent.param.alert.RuleAlertParam;
import com.xiaomi.youpin.prometheus.agent.service.prometheus.RuleAlertService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//报警规则相关接口
/**
 * @author zhangxiaowei6
 */
@RestController
@Slf4j
@RequestMapping(value = "/api/v1/rules")
public class PrometheusAlertController {

    @Autowired
    RuleAlertService ruleAlertService;

    public static final Gson gson = new Gson();

    @RequestMapping(value = "/alert",method = RequestMethod.POST)
    public Result createRuleAlert(@RequestBody RuleAlertParam param) {
        if (param == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        Result result = ruleAlertService.CreateRuleAlert(param);
        return result;
    }

    @RequestMapping(value = "/alert/{id}",method = RequestMethod.PUT)
    public Result UpdateRuleAlert(@PathVariable String id ,@RequestBody RuleAlertParam param) {
        if (id == null || param == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        Result result = ruleAlertService.UpdateRuleAlert(id,param);
        return result;
    }

    @RequestMapping(value = "/alert/{id}",method = RequestMethod.DELETE)
    public Result DeleteRuleAlert(@PathVariable String id) {
        if (id == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        Result result = ruleAlertService.DeleteRuleAlert(id);
        return result;
    }

    @RequestMapping(value = "/alert/{id}",method = RequestMethod.GET)
    public Result GetRuleAlert(@PathVariable String id) {
        if (id == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        Result result = ruleAlertService.GetRuleAlert(id);
        return result;
    }

    @RequestMapping(value = "/alert/list",method = RequestMethod.GET)
    public Result GetRuleAlertList(Integer pageSize,Integer pageNo) {
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

    @RequestMapping(value = "/alert/enabled/{id}",method = RequestMethod.PUT)
    public Result EnabledRuleAlert(@PathVariable String id, String enabled) {
        if (id == null || StringUtils.isBlank(enabled) || ( !enabled.equals("0") && !enabled.equals("1"))) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        Result result = ruleAlertService.EnabledRuleAlert(id,enabled);
        return result;
    }

    @RequestMapping(value = "/alert/sendAlert",method = RequestMethod.POST)
    public Result sendAlert(@RequestBody String body) {
        Result result = ruleAlertService.SendAlert(body);
        return result;
    }

}
