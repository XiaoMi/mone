package com.xiaomi.mone.monitor.controller;

import com.google.common.collect.Lists;
import com.xiaomi.mone.app.api.model.HeraAppRoleModel;
import com.xiaomi.mone.monitor.bo.AppType;
import com.xiaomi.mone.monitor.bo.Pair;
import com.xiaomi.mone.monitor.bo.PlatFormType;
import com.xiaomi.mone.monitor.dao.HeraAppRoleDao;
import com.xiaomi.mone.monitor.dao.model.AlarmHealthQuery;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.dao.model.HeraAppBaseInfo;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AppMonitorService;
import com.xiaomi.mone.monitor.service.ComputeTimerService;
import com.xiaomi.mone.monitor.service.HeraBaseInfoService;
import com.xiaomi.mone.monitor.service.kubernetes.CapacityService;
import com.xiaomi.mone.monitor.service.model.*;
import com.xiaomi.mone.monitor.service.model.redis.AppAlarmData;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 */
@Slf4j
@RestController
public class AppMonitorController {

    @Autowired
    AppMonitorService appMonitorService;
    @Autowired
    ComputeTimerService computeTimerService;
    @Autowired
    HeraBaseInfoService heraBaseInfoService;
    @Autowired
    CapacityService capacityService;

    @ResponseBody
    @PostMapping("/mimonitor/capacityAdjustRecord")
    public Result selectAppAlarmHealth(@RequestBody CapacityAdjustRecordRequest request){
        return capacityService.listCapacityAdjustRecord(request);
    }

    @GetMapping("/mimonitor/resourceUsage")
    public Result getResourceUsageUrl(Integer appId,String appName) {
        return appMonitorService.getResourceUsageUrl(appId,appName);
    }

    @GetMapping("/mimonitor/resourceUsagek8s")
    public Result resourceUsagek8s(Integer appId,String appName) {
        return appMonitorService.getResourceUsageUrlForK8s(appId,appName);
    }

    @ResponseBody
    @GetMapping("/mimonitor/getAppType")
    public Result getAppType(Integer id){

        HeraAppBaseInfo byId = heraBaseInfoService.getById(id);

        log.info("getAppType id : {},result :{}",id,byId);
        Map<String,Integer> map = new HashMap<>();

        map.put("type",byId == null ? AppType.businessType.getCode() : byId.getAppType());

        return Result.success(map);
    }

    @Autowired
    HeraAppRoleDao heraAppRoleDao;

    @ResponseBody
    @GetMapping("/mimonitor/addHeraRoleM")
    public Result addRoleByAppIdAndPlat(String appId,Integer plat,String user){

        HeraAppRoleModel role = new HeraAppRoleModel();
        role.setAppId(appId);
        role.setAppPlatform(plat);
        role.setUser(user);
        role.setStatus(1);
        role.setRole(0);
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());

        return heraBaseInfoService.addRole(role);
    }

    @ResponseBody
    @GetMapping("/mimonitor/addHeraRole")
    public Result addHeraRole(HeraAppRoleModel role){

        return heraBaseInfoService.addRole(role);

    }

    @ResponseBody
    @GetMapping("/mimonitor/delHeraRole")
    public Result delHeraRole(Integer id){

        return heraBaseInfoService.delRole(id);

    }

    @ResponseBody
    @GetMapping("/mimonitor/queryHeraRole")
    public Result queryHeraRole(HeraAppRoleQuery query){

        log.info("queryHeraRole query:{}",query);
        return heraBaseInfoService.queryRole(query.getModel(), query.getPage(), query.getPageSize());
    }

    @ResponseBody
    @GetMapping("/mimonitor/getAppTypeByName")
    public Result getAppType(Integer projectId,String projectName){

        HeraAppBaseInfo byBindIdAndName = heraBaseInfoService.getByBindIdAndName(String.valueOf(projectId), projectName);

        log.info("getAppType projectId : {},projectName{},result :{}",projectId,projectName,byBindIdAndName);
        Map<String,Integer> map = new HashMap<>();

        map.put("type",byBindIdAndName == null ? AppType.businessType.getCode() : byBindIdAndName.getAppType());

        return Result.success(map);
    }

    @ResponseBody
    @PostMapping("/mimonitor/appAlarmHealth")
    public Result selectAppAlarmHealth(HttpServletRequest request, @RequestBody AlarmHealthQuery param){
        log.info("AppMonitorController.selectAppAlarmHealth param : {}",param);
        AuthUserVo userInfo = UserUtil.getUser();
        if(userInfo == null){
            log.info("AppMonitorController.selectAppAlarmHealth request info error no user info found! ");
            return Result.fail(ErrorCode.unknownError);
        }

        param.setOwner(userInfo.genFullAccount());

        return appMonitorService.selectAppAlarmHealth(param);
    }

    @ResponseBody
    @PostMapping("/mimonitor/statistics")
    public Result<List<AppAlarmData>> getProjectStatistics(HttpServletRequest request, @RequestBody AppMonitorRequest param){
        log.info("AppMonitorController.getProjectStatistics param : {}",param);
        if(param.getDuration() <= 0 || CollectionUtils.isEmpty(param.getProjectList())){
            log.error("AppMonitorController.getProjectStatistics error! invalid param! param : {}",param);
            return Result.fail(ErrorCode.invalidParamError);
        }
        return computeTimerService.getProjectStatistics(param);
    }

    @ResponseBody
    @PostMapping("/mimonitor/titlenum/statistics")
    public Result<AppAlarmData> titlenumStatistics(HttpServletRequest request, @RequestBody AppMonitorRequest param){

        log.info("AppMonitorController.titlenumStatistics param : {}",param);

        if(CollectionUtils.isEmpty(param.getProjectList()) || param.getStartTime() == null || param.getEndTime() == null){
            log.error("AppMonitorController.titlenumStatistics error! invalid param! param : {}",param);
            return Result.fail(ErrorCode.invalidParamError);
        }

        AppAlarmData appAlarmData = computeTimerService.countAppMetricData(param);

        log.info("AppMonitorController.titlenumStatistics param : {},result : {}",param,appAlarmData);

        return Result.success(appAlarmData);
    }

    @ResponseBody
    @PostMapping("/mimonitor/heraApps")
    public Result<PageData> getHeraApps(HttpServletRequest request,@RequestBody HeraAppBaseQuery query){

        if(query == null){
            log.error("AppMonitorController.getHeraApps error! invalid param! param : {}",query);
            return Result.fail(ErrorCode.invalidParamError);
        }

        log.info("AppMonitorController.getHeraApps param : {}",query);


        AuthUserVo userInfo = UserUtil.getUser();
        if(userInfo == null){
            log.info("AppMonitorController.getHeraApps no user info found! param : {} ", query);
            return Result.fail(ErrorCode.unknownError);
        }
        query.setParticipant(userInfo.genFullAccount());

        return heraBaseInfoService.queryByParticipant(query);

    }

    @ResponseBody
    @PostMapping("/mimonitor/getProjects")
    public Result<PageData> getProjectInfos(HttpServletRequest request,@RequestBody AppMonitorRequest param){
        log.info("AppMonitorController.getProjectInfos param : {}",param);

        if(param == null || param.getViewType() == null){
            log.error("AppMonitorController.getProjectInfos error! invalid param! param : {}",param);
            return Result.fail(ErrorCode.invalidParamError);
        }

        if(param.getArea() == null){
            log.error("AppMonitorController.getProjectInfos error! no area param! param : {}",param);
            return Result.fail(ErrorCode.invalidParamError);
        }

        AuthUserVo userInfo = UserUtil.getUser();
        if(userInfo == null){
            log.info("AppMonitorController.getProjectInfos for user request info error no user info found! param : {} ", param);
            return Result.fail(ErrorCode.unknownError);
        }

        /**
         * 不再区分区域及是否参与角色，替换为全量的应用查询，并适配原有参数类型
         */
        return appMonitorService.getProjectInfos(userInfo.genFullAccount(),param.getAppName(),param.getPage(),param.getPageSize());

    }

    @ResponseBody
    @GetMapping("/mimonitor/getMyProjectIds")
    public Result<PageData> getMyProjectIds(HttpServletRequest request,Integer area){

        AuthUserVo userInfo = UserUtil.getUser();
        if(userInfo == null){
            log.info("AppMonitorController.getMyProjectIds request info error no user info found! ");
            return Result.fail(ErrorCode.unknownError);
        }

        Result<PageData> result = null;

        result = appMonitorService.getProjectInfos(userInfo.genFullAccount(), null, 1, Integer.MAX_VALUE);

        log.debug("getMyProjectIds,area:{},result:{}",area,result);

        if(ErrorCode.success.getCode() != result.getCode()){
            return Result.fail(ErrorCode.unknownError);
        }

        List<ProjectInfo> list = (List<ProjectInfo>) result.getData().getList();

        PageData<Object> objectPageData = new PageData<>();

        if(CollectionUtils.isEmpty(list)){
            objectPageData.setList(Lists.newArrayList());
            return Result.success(objectPageData);
        }

        List<Long> projectIds = list.stream().map(it -> {
            return it.getId();
        }).collect(Collectors.toList());


        objectPageData.setList(projectIds);
        return Result.success(objectPageData);
    }

    @PostMapping("/mimonitor/listApp")
    public Result<PageData<List<AppMonitor>>> listMyApp(HttpServletRequest request,@RequestBody AppMonitorRequest param){

        try {
            if(param.getPageSize() == null){
                //默认最大显示 1000
                param.setPageSize(1000);
            }
            log.info("AppMonitorController.listApp param : {} " , param);

            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AppMonitorController.listApp request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }

            String user = userInfo.genFullAccount();

            log.info("AppMonitorController.listApp param : {} ,user : {}", param,user);

            if(param.getViewType() == null){
                if(param.getDistinct() != null && param.getDistinct() == 1){
                    return appMonitorService.listAppDistinct(user,param.getAppName(),param.getPage(),param.getPageSize());
                }
                return appMonitorService.listApp(param.getAppName(),user,param.getPage(),param.getPageSize());
            }

            //指定了查询我关注的应用，返回我关注的应用列表！
            if(param.getViewType() != null && param.getViewType().intValue() == 1){
                return appMonitorService.listMyCareApp(param.getAppName(),user,param.getPage(),param.getPageSize());
            }

            AppMonitor appMonitor = new AppMonitor();
            appMonitor.setProjectName(param.getAppName());
            appMonitor.setAppSource(param.getPlatFormType());

            return appMonitorService.listMyApp(appMonitor,user,param.getPage(),param.getPageSize());
        } catch (Exception e) {
            log.error("AppMonitorController.listApp param : {} ,exception :{}", param,e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @PostMapping("/mimonitor/my_and_care_app_list")
    public Result<PageData<List<AppMonitor>>> myAndCareAppList(HttpServletRequest request,@RequestBody AppMonitorRequest param){
        try {
            param.qryInit();
            log.info("AppMonitorController.myAndCareAppList param : {} " , param);
            AuthUserVo userInfo = UserUtil.getUser();
            if(userInfo == null){
                log.info("AppMonitorController.myAndCareAppList request info error no user info found! param : {} ", param);
                return Result.fail(ErrorCode.unknownError);
            }
            String user = userInfo.genFullAccount();
            log.info("AppMonitorController.myAndCareAppList param : {} ,user : {}", param,user);
            return appMonitorService.myAndCareAppList(user, param);
        } catch (Exception e) {
            log.error("AppMonitorController.myAndCareAppList param : {} ,exception :{}", param,e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    @PostMapping("/mimonitor/addApp")
    public Result<String> addApp(HttpServletRequest request,@RequestBody List<AppMonitorModel> params){

        log.info("AppMonitorController.addApp param : {} ", params);

        AuthUserVo userInfo = UserUtil.getUser();
        if(userInfo == null){
            log.info("AppMonitorController.addApp request info error no user info found! param : {} ", params);
            return Result.fail(ErrorCode.unknownError);
        }

        String user = userInfo.genFullAccount();
        // 只允许添加一条（单个应用）
        AppMonitorModel param = params.get(0);
        if (param.getProjectId() == null || StringUtils.isBlank(param.getProjectName())) {
            log.error("AppMonitorController.addApp 用户{}添加项目{}，参数不合法", user, param);
            return Result.fail(ErrorCode.invalidParamError);
        }

        log.info("AppMonitorController.addApp param : {} ,user : {}", param, user);
        Result<String> result = appMonitorService.createWithBaseInfo(param,user);
        log.info("AppMonitorController.addApp param : {} ,user : {} , result : {}", param, user,result);
        return result;

    }

    @GetMapping("/mimonitor/delApp")
    public Result<String> delApp(HttpServletRequest request,Integer id){

        log.info("AppMonitorController.addApp id : {} ", id);
        return appMonitorService.delete(id);
    }

    @GetMapping("/mimonitor/delAppByProjectId")
    public Result<String> delAppByProjectId(HttpServletRequest request,Integer projectId,Integer appSource){

        log.info("AppMonitorController.delAppByProjectId projectId : {} ,appSource : {}", projectId,appSource);
        AuthUserVo userInfo = UserUtil.getUser();
        if(userInfo == null){
            log.info("AppMonitorController.delAppByProjectId request error no user info found! projectId : {} ,appSource : {}", projectId,appSource);
            return Result.fail(ErrorCode.INVALID_USER);
        }

        return appMonitorService.deleteByUser(projectId,appSource,userInfo.genFullAccount());
    }


    @GetMapping("/mimonitor/platFormList")
    public Result<List<Pair>> platFormList(HttpServletRequest request){

        return Result.success(PlatFormType.getCodeDescList());
    }

    @GetMapping("/api-manual/deleteHeraApp")
    public Result<String> deleteHeraApp(HttpServletRequest request,Integer id){
        AuthUserVo userInfo = UserUtil.getUser();
        if(userInfo == null){
            log.info("AppMonitorController.deleteHeraApp request error no user info found! id : {}", id);
            return Result.fail(ErrorCode.INVALID_USER);
        }

        log.info("AppMonitorController.deleteHeraApp id : {}" ,id);

        if(!userInfo.genFullAccount().equals("gaoxihui")){
            return Result.fail(ErrorCode.NoOperPermission);
        }

        heraBaseInfoService.deleAppById(id);

        return Result.success("sucess");
    }

    @GetMapping("/mimonitor/appMembers")
    public Result<List<String>> appMembers(HttpServletRequest request,String appId,Integer platForm){
        AuthUserVo userInfo = UserUtil.getUser();
        if(userInfo == null){
            log.info("AppMonitorController.appMembers request error no user info found! appId : {}", appId);
            return Result.fail(ErrorCode.INVALID_USER);
        }


        return heraBaseInfoService.getAppMembersByAppId(appId, platForm,userInfo.genFullAccount());
    }



    @GetMapping("/mimonitor/appTypeList")
    public Result<List<Pair>> appTypeList(HttpServletRequest request){

        return Result.success(AppType.getCodeDescList());
    }

    @GetMapping("/mimonitor/washBaseId")
    public Result washBaseId(HttpServletRequest request){

        appMonitorService.washBaseId();
        return Result.success("washBaseId OOOK!");
    }

    @GetMapping("/mimonitor/grafanaInterfaceList")
    public Result grafanaInterfaceList() {
        return appMonitorService.grafanaInterfaceList();
    }
}
