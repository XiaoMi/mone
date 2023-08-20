package com.xiaomi.mone.monitor.controller;

import com.google.common.collect.Lists;
import com.xiaomi.mone.monitor.bo.*;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AlarmStrategyService;
import com.xiaomi.mone.monitor.aop.HeraRequestMapping;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingActionStrategyDelete;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingActionStrategyEnable;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.user.UserConfigService;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * @author zhanggaofeng1
 */
@Slf4j
@RestController
@RequestMapping(value = "/alarm/strategy")
public class AlarmStrategyController {

    @Autowired
    private AlarmStrategyService alarmStrategyService;

    @Autowired
    UserConfigService userConfigService;

    /**
     * 策略类型列表查询
     * @param request
     * @param param
     * @return
     */
    @RequestMapping(value = "/type_list")
    public Result typeList(HttpServletRequest request, @RequestBody AlarmStrategyParam param) {
        if (param.isTemplateNeed()) {
            return Result.success(AlarmStrategyType.getTemplateStrategyTypeList());
        } if (param.isRuleNeed()) {
            return Result.success(AlarmStrategyType.getRuleStrategyTypeList());
        } else {
            return Result.success(null);
        }
    }

    @HeraRequestMapping(value = "/enabled", interfaceName = InterfaceNameEnum.STRATEGY_ENABLE, actionClass = HeraRequestMappingActionStrategyEnable.class)
    public Result enabled(HttpServletRequest request, @RequestBody AlarmStrategyParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlarmStrategyController.enabled param : {} ", param);
            if (param.getId() <= 0 || (param.getStatus() != 0 && param.getStatus() != 1)) {
                return Result.fail(ErrorCode.invalidParamError);
            }
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlarmStrategyController.enabled request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            log.info("AlarmStrategyController.enabled param : {} ,user : {}", param, user);
            return alarmStrategyService.enabled(user, param);
        } catch (Exception e) {
            log.error("AlarmStrategyController.enabled异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @HeraRequestMapping(value = "/deleteById", interfaceName = InterfaceNameEnum.STRATEGY_DELETE, actionClass = HeraRequestMappingActionStrategyDelete.class)
    public Result deleteById(HttpServletRequest request, @RequestBody AlarmStrategyParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlarmStrategyController.deleteById param : {} ", param);
//            if (param.getId() <= 0) {
//                return Result.fail(ErrorCode.invalidParamError);
//            }
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlarmStrategyController.deleteById request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            log.info("AlarmStrategyController.deleteById param : {} ,user : {}", param, user);
            if(param.getIds() == null){
                param.setIds(Lists.newArrayList());
            }

            //兼容单条删除的旧逻辑
            if(param.getId() != null){
                if(!param.getIds().contains(param.getId())){
                    param.getIds().add(param.getId());
                }
            }
            return alarmStrategyService.batchDeleteStrategy(user, param.getIds());
        } catch (Exception e) {
            log.error("AlarmStrategyController.deleteById异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @GetMapping("/deleteByStrategyId")
    public Result deleteByStrategyId(HttpServletRequest request, Integer strategyId) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlarmStrategyController.deleteByStrategyId strategyId : {} ", strategyId);
            if (strategyId.intValue() <= 0) {
                return Result.fail(ErrorCode.invalidParamError);
            }
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlarmStrategyController.deleteByStrategyId request info error no user info found! strategyId : {} ", strategyId);
                return Result.fail(ErrorCode.unknownError);
            }

            if(!userConfigService.isSuperAdmin(userInfo.genFullAccount())){
                return Result.fail(ErrorCode.NoOperPermission);
            }

            String user = userInfo.genFullAccount();
            log.info("AlarmStrategyController.deleteByStrategyId strategyId : {} ,user : {}", strategyId, user);
            return alarmStrategyService.deleteByStrategyId(user,strategyId);
        } catch (Exception e) {
            log.error("AlarmStrategyController.deleteByStrategyId异常 strategyId : {} ,userInfo :{}", strategyId, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @RequestMapping(value = "/detailed")
    public Result<AlarmStrategyInfo> detailed(HttpServletRequest request, @RequestBody AlarmStrategyParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlarmStrategyController.detailed param : {} ", param);
            if (param.getId() <= 0) {
                return Result.fail(ErrorCode.invalidParamError);
            }
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlarmStrategyController.detailed request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            log.info("AlarmStrategyController.detailed param : {} ,user : {}", param, user);
            return alarmStrategyService.detailed(user, param);
        } catch (Exception e) {
            log.error("AlarmStrategyController.detailed异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/search")
    public Result<PageData<List<AlarmStrategyInfo>>> search(HttpServletRequest request, @RequestBody AlarmStrategyParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlarmStrategyController.search param : {} ", param);
            param.pageQryInit();
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlarmStrategyController.search request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            Result<PageData<List<AlarmStrategyInfo>>> result = alarmStrategyService.search(user, param);
            log.info("AlarmStrategyController.search param : {} ,user : {},result:{}", param, user,result);
            return result;
        } catch (Exception e) {
            log.error("AlarmStrategyController.search异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @RequestMapping(value = "/dubbo_search")
    public Result<PageData> dubboSearch(HttpServletRequest request, @RequestBody AlarmStrategyParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlarmStrategyController.dubboSearch param : {} ", param);
            if (StringUtils.isBlank(param.getAppName()) || param.getAppId() == null) {
                return Result.fail(ErrorCode.invalidParamError);
            }
            param.pageQryInit();
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlarmStrategyController.dubboSearch request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            log.info("AlarmStrategyController.dubboSearch param : {} ,user : {}", param, user);
            return alarmStrategyService.dubboSearch(user, param);
        } catch (Exception e) {
            log.error("AlarmStrategyController.dubboSearch异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }
}
