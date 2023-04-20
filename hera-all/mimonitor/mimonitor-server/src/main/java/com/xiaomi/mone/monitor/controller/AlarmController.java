package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.monitor.aop.HeraRequestMapping;
import com.xiaomi.mone.monitor.bo.*;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRuleTemplate;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AppAlarmService;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingActionRuleDelete;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingActionRuleEdit;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingActionStrategyAdd;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingActionStrategyEdit;
import com.xiaomi.mone.monitor.service.api.AlarmPresetMetricsService;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.prometheus.*;
import com.xiaomi.mone.monitor.service.prometheus.AlarmService;
import com.xiaomi.mone.monitor.service.user.UserConfigService;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/9/9 10:10 上午
 */
@Slf4j
@RestController
public class AlarmController {

    @Autowired
    AppAlarmService appAlarmService;
    @Autowired
    AlarmService alarmService;
    @Value("${server.type}")
    private String env;

    @Autowired
    UserConfigService userConfigService;

    @Autowired
    private AlarmPresetMetricsService alarmPresetMetricsService;


    @ResponseBody
    @GetMapping("/alarm/ruleSelectDataInfo")
    public Result alarmSelectDataInfo(){
        Map<String,List> map = new HashMap<>();
        map.put("alarmLevels",AlarmAlertLevel.getEnumList());
        map.put("presetMetrics",alarmPresetMetricsService.getEnumList());
        map.put("checkDataCount",AlarmCheckDataCount.getEnumList());
        map.put("sendInterval",AlarmSendInterval.getEnumList());
        return Result.success(map);
    }

    @ResponseBody
    @PostMapping("/alarm/template/add")
    public Result addAlarmTemplate(HttpServletRequest request,@RequestBody AlarmRuleTemplateRequest param){

        try {
            log.info("AlarmController.addAlarmTemplate param : {} " , param.toString());

            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AlarmController.addAlarmTemplate request info error no user info found! param : {} ", param.toString());
                return Result.fail(ErrorCode.unknownError);
            }

            String user = userInfo.genFullAccount();

            log.info("AlarmController.addAlarmTemplate param : {} ,user : {}", param.toString(),user);
            return appAlarmService.addTemplate(param,user);
        } catch (Exception e) {
            log.error("AlarmController.addAlarmTemplate param : {} ,exception :{}", param.toString(),e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @ResponseBody
    @PostMapping("/alarm/template/edit")
    public Result editAlarmTemplate(HttpServletRequest request,@RequestBody AlarmRuleTemplateRequest param){
        try {
            log.info("AlarmController.editAlarmTemplate param : {} " , param.toString());

            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AlarmController.editAlarmTemplate request info error no user info found! param : {} ", param.toString());
                return Result.fail(ErrorCode.unknownError);
            }

            String user = userInfo.genFullAccount();

            log.info("AlarmController.editAlarmTemplate param : {} ,user : {}", param.toString(),user);
            return appAlarmService.editTemplate(param,user);
        } catch (Exception e) {
            log.error("AlarmController.editAlarmTemplate param : {} ,exception :{}", param.toString(),e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @ResponseBody
    @PostMapping("/alarm/template/delete")
    public Result deleteAlarmTemplate(HttpServletRequest request, @RequestBody AppAlarmRuleTemplate template){

        try {
            log.info("AlarmController.deleteAlarmTemplate id : {} " , template.getId());
            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AlarmController.deleteAlarmTemplate request info error no user info found! param : {} ", template.getId());
                return Result.fail(ErrorCode.unknownError);
            }

            String user = userInfo.genFullAccount();

            log.info("AlarmController.deleteAlarmTemplate id : {} ,user : {}", template.getId(),user);
            return appAlarmService.deleteTemplate(template.getId());
        } catch (Exception e) {
            log.error("AlarmController.deleteAlarmTemplate param : {} ,exception :{}", template.getId(),e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @ResponseBody
    @PostMapping("/alarm/template/query")
    public Result queryAlarmTemplate(HttpServletRequest request, @RequestBody AppAlarmRuleTemplateQuery param){

        return appAlarmService.queryTemplate(param);
    }

    @ResponseBody
    @GetMapping("/alarm/template/get")
    public Result getAlarmTemplateById(Integer id){
        return appAlarmService.getTemplateById(id);
    }

    @ResponseBody
    @GetMapping("/alarm/template/list")
    public Result listAlarmTemplate(HttpServletRequest request){

        try {
            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AlarmController.listAlarmTemplate request info error no user info found! ");
                return Result.fail(ErrorCode.unknownError);
            }

            String user = userInfo.genFullAccount();

            log.info("AlarmController.listAlarmTemplate user : {}",user);

            return appAlarmService.getTemplateByCreater(user);
        } catch (Exception e) {
            log.error("AlarmController.listAlarmTemplate exception :{}",e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @HeraRequestMapping(value = "/alarm/rule/add", interfaceName = InterfaceNameEnum.STRATEGY_ADD, actionClass = HeraRequestMappingActionStrategyAdd.class)
    public Result addAlarmRule(HttpServletRequest request, @RequestBody AlarmRuleRequest param){
        try {
            log.info("AlarmController.addAlarmRule param : {} " , param.toString());

            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AlarmController.addAlarmRule request info error no user info found! param : {} ", param.toString());
                return Result.fail(ErrorCode.unknownError);
            }

            String user = userInfo.genFullAccount();
            user = userConfigService.getAssignUser(user);
            param.setUser(user);

            return appAlarmService.addRulesWithStrategy(param);

        } catch (Exception e) {
            log.error("AlarmController.addAlarmRule param : {} ,exception :{}", param.toString(),e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @Deprecated
    @HeraRequestMapping(value = "/alarm/rule/edit", interfaceName = InterfaceNameEnum.STRATEGY_EDIT, actionClass = HeraRequestMappingActionStrategyEdit.class)
    public Result editAlarmRule(HttpServletRequest request, @RequestBody AlarmRuleRequest param){
        try {
            log.info("AlarmController.editAlarmRule param : {} " , param.toString());

            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AlarmController.editAlarmRule request info error no user info found! param : {} ", param.toString());
                return Result.fail(ErrorCode.unknownError);
            }

            String user = userInfo.genFullAccount();

            user = userConfigService.getAssignUser(user);

            log.info("AlarmController.editAlarmRule param : {} ,user : {}", param.toString(),user);

            return appAlarmService.editRules(param.getAlarmRules(),param,user,userInfo.getName());

        } catch (Exception e) {
            log.error("AlarmController.editAlarmRule param : {} ,exception :{}", param.toString(),e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @HeraRequestMapping(value = "/alarm/strategy/edit", interfaceName = InterfaceNameEnum.STRATEGY_EDIT, actionClass = HeraRequestMappingActionStrategyEdit.class)
    public Result editAlarmStrategy(HttpServletRequest request, @RequestBody AlarmRuleRequest param){
        try {
            log.info("AlarmController.editAlarmStrategy param : {} " , param.toString());

            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AlarmController.editAlarmStrategy no user info found! param : {} ", param.toString());
                return Result.fail(ErrorCode.INVALID_USER);
            }

            String user = userInfo.genFullAccount();

            user = userConfigService.getAssignUser(user);

            log.info("AlarmController.editAlarmStrategy param : {} ,user : {}", param.toString(),user);

            param.setUser(user);

            return appAlarmService.editRulesByStrategy(param);

        } catch (Exception e) {
            log.error("AlarmController.editAlarmStrategy param : {}", param.toString(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @HeraRequestMapping(value = "/alarm/rule/delete",interfaceName = InterfaceNameEnum.RULE_DELETE, actionClass = HeraRequestMappingActionRuleDelete.class)
    public Result deleteRules(HttpServletRequest request, @RequestBody List<Integer> ids){

        try {
            log.info("AlarmController.deleteRules ids : {} " ,ids);

            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AlarmController.deleteRules no user info found! ids : {} ", ids);
                return Result.fail(ErrorCode.INVALID_USER);
            }

            String user = userInfo.genFullAccount();

            user = userConfigService.getAssignUser(user);

            log.info("AlarmController.deleteRules ids : {} ,user : {}", ids,user);

            return appAlarmService.delAlarmRules(ids,user);

        } catch (Exception e) {
            log.error("AlarmController.editAlarmRuleSingle ids : {}", ids,e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @HeraRequestMapping(value = "/alarm/rule/edit/single", interfaceName = InterfaceNameEnum.RULE_EDIT, actionClass = HeraRequestMappingActionRuleEdit.class)
    public Result editAlarmRuleSingle(HttpServletRequest request, @RequestBody AlarmRuleData ruleData){
        try {
            log.info("AlarmController.editAlarmRuleSingle param : {} " ,ruleData);

            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AlarmController.editAlarmRuleSingle no user info found! param : {} ", ruleData);
                return Result.fail(ErrorCode.INVALID_USER);
            }

            String user = userInfo.genFullAccount();

            user = userConfigService.getAssignUser(user);

            log.info("AlarmController.editAlarmRuleSingle param : {} ,user : {}", ruleData,user);

            return appAlarmService.editAlarmRuleSingle(ruleData,user);

        } catch (Exception e) {
            log.error("AlarmController.editAlarmRuleSingle param : {}", ruleData,e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @PostMapping("/alarm/appAlarmRules/noConfigApp")
    public Result noConfigApp(HttpServletRequest request, @RequestBody AppRulesQuery param){

        try {
            log.info("AlarmController.noConfigApp param : {} " , param.toString());
            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AlarmController.noConfigApp request info error no user info found! param : {} ", param.toString());
                return Result.fail(ErrorCode.unknownError);
            }

            String user = userInfo.genFullAccount();

            log.info("AlarmController.noConfigApp param : {} ,user : {}", param.toString(),user);

            return appAlarmService.queryNoRulesConfig(param.getAppName(),user,param.getPage(),param.getPageSize());

        } catch (Exception e) {
            log.error("AlarmController.noConfigApp param : {} ,exception :{}", param.toString(),e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }

    }

    @ResponseBody
    @PostMapping("/alarm/alertTeam/query")
    public Result<PageData> alertTeamQuery(HttpServletRequest request,@RequestBody AlertTeamQuery param){
        try {
            log.info("AlarmController.alertTeamQuery param : {} ",param.toString());
            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.error("AlarmController.alertTeamQuery request info error no user info found! " +
                                "param name : {} ",param.toString());
                return Result.fail(ErrorCode.unknownError);
            }

            String user = userInfo.genFullAccount();

            param.setOncallUser(user);

            return alarmService.searchAlertTeam(param.getName(),param.getNote(),param.getManager(),param.getOncallUser(),param.getService(),param.getIamId(), user, param.getPage(), param.getPageSize());

        } catch (Exception e) {
            log.error("AlarmController.alertTeamQuery param : {} ,exception :{}", param,e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
//        return alarmService.searchAlertTeam("", "", "", "", "", 16360, "gaoxihui", 1, 10);
    }


    @ResponseBody
    @GetMapping("/alarm/alarmLevels")
    public Result<Map<String, String>> alarmLevels(){
        return Result.success(AlarmAlertLevel.getEnumMap());
    }
    @ResponseBody
    @GetMapping("/alarm/presetMetrics")
    public Result<Map<String, String>> presetMetrics(){
        return Result.success(alarmPresetMetricsService.getEnumMap());
    }
    @ResponseBody
    @GetMapping("/alarm/checkDataCount")
    public Result<Map<String, String>> checkDataCount(){
        return Result.success(AlarmCheckDataCount.getEnumMap());
    }
    @ResponseBody
    @GetMapping("/alarm/sendInterval")
    public Result<Map<String, String>> sendInterval(){
        return Result.success(AlarmSendInterval.getEnumMap());
    }

}
