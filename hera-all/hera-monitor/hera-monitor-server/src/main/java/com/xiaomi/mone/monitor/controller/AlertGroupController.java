package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.monitor.aop.HeraRequestMapping;
import com.xiaomi.mone.monitor.bo.AlertGroupInfo;
import com.xiaomi.mone.monitor.bo.AlertGroupParam;
import com.xiaomi.mone.monitor.bo.InterfaceNameEnum;
import com.xiaomi.mone.monitor.bo.UserInfo;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AlertGroupService;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingActionAlertGroupAdd;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingActionAlertGroupDelete;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingActionAlertGroupEdit;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.user.UserConfigService;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zhanggaofeng1
 */
@Slf4j
@RestController
@RequestMapping(value = "/alertGroup")
public class AlertGroupController {

    @Autowired
    private AlertGroupService alertGroupService;

    @Autowired
    private UserConfigService userconfigService;

    @RequestMapping(value = "/test")
    public Result<PageData<List<UserInfo>>> test() {
        UserInfoVO userInfo = null;
        try {
            return alertGroupService.initAlertGroupData();
        } catch (Exception e) {
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @RequestMapping(value = "/user/search")
    public Result<PageData<List<UserInfo>>> userSearch(HttpServletRequest request, @RequestBody AlertGroupParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlertGroupController.userSearch param : {} ", param);
            param.pageQryInit();
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlertGroupController.userSearch request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            user = userconfigService.getAssignUser(user);
            log.info("AlertGroupController.userSearch param : {} ,user : {}", param, user);
            return alertGroupService.userSearch(user, param);
        } catch (Exception e) {
            log.error("AlertGroupController.userSearch异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @RequestMapping(value = "/sync/{type}")
    public Result sync(HttpServletRequest request, @PathVariable(name = "type") String type) {
        AuthUserVo userInfo = null;
        try {
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            user = userconfigService.getAssignUser(user);
            log.info("AlertGroupController.sync type={}, user : {}", type, user);
            return alertGroupService.sync(user, type);
        } catch (Exception e) {
            log.error("AlertGroupController.sync异常 type : {} ,userInfo :{}", type, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @RequestMapping(value = "/search")
    public Result<PageData<List<AlertGroupInfo>>> alertGroupSearch(HttpServletRequest request, @RequestBody AlertGroupParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlertGroupController.alertGroupSearch param : {} ", param);
            param.pageQryInit();
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlertGroupController.alertGroupSearch request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            user = userconfigService.getAssignUser(user);
            log.info("AlertGroupController.alertGroupSearch param : {} ,user : {}", param, user);
            return alertGroupService.alertGroupSearch(user, param);
        } catch (Exception e) {
            log.error("AlertGroupController.alertGroupSearch异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @RequestMapping(value = "/searchByIds")
    public Result<Map<Long, AlertGroupInfo>> alertGroupSearchByIds(HttpServletRequest request, @RequestBody AlertGroupParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlertGroupController.alertGroupSearchByIds param : {} ", param);
            if (param.getRelIds() == null || param.getRelIds().isEmpty() || param.getRelIds().size() > 10) {
                log.info("AlertGroupController.alertGroupSearchByIds request info error no arg error! param : {} ", param);
                return Result.fail(ErrorCode.invalidParamError);
            }
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlertGroupController.alertGroupSearchByIds request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            user = userconfigService.getAssignUser(user);
            log.info("AlertGroupController.alertGroupSearchByIds param : {} ,user : {}", param, user);
            return alertGroupService.alertGroupSearchByIds(user, param);
        } catch (Exception e) {
            log.error("AlertGroupController.alertGroupSearchByIds异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @RequestMapping(value = "/detailed")
    public Result<AlertGroupInfo> alertGroupDetailed(HttpServletRequest request, @RequestBody AlertGroupParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlertGroupController.alertGroupDetailed param : {} ", param);
            if (param.getId() <= 0) {
                log.info("AlertGroupController.alertGroupDetailed request info error no arg error! param : {} ", param);
                return Result.fail(ErrorCode.invalidParamError);
            }
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlertGroupController.alertGroupDetailed request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            user = userconfigService.getAssignUser(user);
            log.info("AlertGroupController.alertGroupDetailed param : {} ,user : {}", param, user);
            return alertGroupService.alertGroupDetailed(user, param);
        } catch (Exception e) {
            log.error("AlertGroupController.alertGroupDetailed异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @HeraRequestMapping(value = "/create", interfaceName = InterfaceNameEnum.ALERT_GROUP_ADD, actionClass = HeraRequestMappingActionAlertGroupAdd.class)
    public Result<AlertGroupInfo> alertGroupCreate(HttpServletRequest request, @RequestBody AlertGroupParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlertGroupController.alertGroupCreate param : {} ", param);
            if (!param.createArgCheck()) {
                log.info("AlertGroupController.alertGroupCreate request info error no arg error! param : {} ", param);
                return Result.fail(ErrorCode.invalidParamError);
            }
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlertGroupController.alertGroupCreate request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            user = userconfigService.getAssignUser(user);
            log.info("AlertGroupController.alertGroupCreate param : {} ,user : {}", param, user);
            return alertGroupService.alertGroupCreate(user, param);
        } catch (Exception e) {
            log.error("AlertGroupController.alertGroupCreate异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @HeraRequestMapping(value = "/edit", interfaceName = InterfaceNameEnum.ALERT_GROUP_EDIT, actionClass = HeraRequestMappingActionAlertGroupEdit.class)
    public Result alertGroupEdit(HttpServletRequest request, @RequestBody AlertGroupParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlertGroupController.alertGroupEdit param : {} ", param);
            if (!param.createArgCheck() || param.getId() <= 0) {
                log.info("AlertGroupController.alertGroupEdit request info error no arg error! param : {} ", param);
                return Result.fail(ErrorCode.invalidParamError);
            }
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlertGroupController.alertGroupEdit request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            user = userconfigService.getAssignUser(user);
            log.info("AlertGroupController.alertGroupEdit param : {} ,user : {}", param, user);
            return alertGroupService.alertGroupEdit(user, param);
        } catch (Exception e) {
            log.error("AlertGroupController.alertGroupEdit异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @HeraRequestMapping(value = "/delete", interfaceName = InterfaceNameEnum.ALERT_GROUP_EDIT, actionClass = HeraRequestMappingActionAlertGroupDelete.class)
    public Result alertGroupDelete(HttpServletRequest request, @RequestBody AlertGroupParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlertGroupController.alertGroupDelete param : {} ", param);
            if (param.getId() <= 0) {
                log.info("AlertGroupController.alertGroupDelete request info error no arg error! param : {} ", param);
                return Result.fail(ErrorCode.invalidParamError);
            }
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlertGroupController.alertGroupDelete request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            user = userconfigService.getAssignUser(user);
            log.info("AlertGroupController.alertGroupDelete param : {} ,user : {}", param, user);
            return alertGroupService.alertGroupDelete(user, param);
        } catch (Exception e) {
            log.error("AlertGroupController.alertGroupDelete异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @ResponseBody
    @PostMapping("/duty_list/")
    public Result dutyInfoList(HttpServletRequest request, @RequestBody AlertGroupParam param) {
        AuthUserVo userInfo = null;
        try {
            log.info("AlertGroupController.dutyInfoList param : {} ", param);
            if (param.getId() <= 0) {
                log.info("AlertGroupController.dutyInfoList request info error no arg error! param : {} ", param);
                return Result.fail(ErrorCode.invalidParamError);
            }
            userInfo = UserUtil.getUser();
            if (userInfo == null) {
                log.info("AlertGroupController.dutyInfoList request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.INVALID_USER);
            }
            String user = userInfo.genFullAccount();
            user = userconfigService.getAssignUser(user);
            log.info("AlertGroupController.dutyInfoList param : {} ,user : {}", param, user);
            return alertGroupService.alertGroupDelete(user, param);
        } catch (Exception e) {
            log.error("AlertGroupController.alertGroupDelete异常 param : {} ,userInfo :{}", param, userInfo, e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

}
