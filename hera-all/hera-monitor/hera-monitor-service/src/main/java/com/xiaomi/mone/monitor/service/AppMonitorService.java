package com.xiaomi.mone.monitor.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaomi.mone.app.api.message.HeraAppInfoModifyMessage;
import com.xiaomi.mone.app.api.message.HeraAppModifyType;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.monitor.bo.AlarmStrategyInfo;
import com.xiaomi.mone.monitor.bo.AppViewType;
import com.xiaomi.mone.monitor.bo.RuleStatusType;
import com.xiaomi.mone.monitor.dao.AppAlarmRuleDao;
import com.xiaomi.mone.monitor.dao.AppAlarmStrategyDao;
import com.xiaomi.mone.monitor.dao.AppMonitorDao;
import com.xiaomi.mone.monitor.dao.HeraAppRoleDao;
import com.xiaomi.mone.monitor.dao.model.*;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.AppMonitorServiceExtension;
import com.xiaomi.mone.monitor.service.api.TeslaService;
import com.xiaomi.mone.monitor.service.extension.PlatFormTypeExtensionService;
import com.xiaomi.mone.monitor.service.model.AppMonitorModel;
import com.xiaomi.mone.monitor.service.model.AppMonitorRequest;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.ProjectInfo;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmRuleData;
import com.xiaomi.mone.monitor.service.model.prometheus.MetricData;
import com.xiaomi.mone.monitor.service.model.prometheus.MetricDataSet;
import com.xiaomi.mone.monitor.service.model.prometheus.MetricResponse;
import com.xiaomi.mone.monitor.service.prometheus.AlarmService;
import com.xiaomi.mone.monitor.service.prometheus.PrometheusService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 * @date 2021/8/13 11:07 上午
 */
@Slf4j
@Service
public class AppMonitorService {

    @Autowired
    AppMonitorDao appMonitorDao;

    @Autowired
    HeraBaseInfoService heraBaseInfoService;

    @Autowired
    AppGrafanaMappingService appGrafanaMappingService;

    @Autowired
    AppAlarmService appAlarmService;

    @Autowired
    private TeslaService teslaService;

    @Autowired
    PrometheusService prometheusService;

    @Autowired
    ResourceUsageService resourceUsageService;

    @Autowired
    HeraAppRoleDao heraAppRoleDao;

    @Autowired
    AppAlarmStrategyDao strategyDao;

    @Autowired
    AppAlarmRuleDao ruleDao;

    @Autowired
    AlarmService alarmService;

    @Autowired
    AppMonitorServiceExtension appMonitorServiceExtension;

    @Autowired
    PlatFormTypeExtensionService platFormTypeExtensionService;

    @Autowired
    AlarmStrategyService alarmStrategyService;

    private static final Gson gson = new Gson();

    @Reference(registry = "registryConfig", check = false, interfaceClass = HeraAppService.class, group = "${dubbo.group.heraapp}")
    HeraAppService hearAppService;

    public void appPlatMove(Integer OProjectId, Integer OPlat, Integer NProjectId, Integer Nplat, Integer newIamId, String NprojectName, Boolean rebuildRule) {


        log.info("appPlatMove OProjectId:{},OPlat:{},NProjectId:{},Nplat:{},NprojectName:{}", OProjectId, OPlat, NProjectId, Nplat, NprojectName);

        if (OProjectId == null || OPlat == null || NProjectId == null || Nplat == null || StringUtils.isBlank(NprojectName)) {
            log.error("appPlatMove has invalid param! OProjectId:{},OPlat:{},NProjectId:{},Nplat:{},NprojectName:{}", OProjectId, OPlat, NProjectId, Nplat, NprojectName);
            return;
        }

        HeraAppBaseInfoModel baseInfo = new HeraAppBaseInfoModel();
        baseInfo.setBindId(String.valueOf(OProjectId));
        baseInfo.setPlatformType(OPlat);

        List<HeraAppBaseInfoModel> query = heraBaseInfoService.query(baseInfo, null, null);
        if (CollectionUtils.isEmpty(query)) {
            log.info("appPlatMove nodata found!OProjectId:{},OPlat:{},NProjectId:{},Nplat:{},NprojectName:{}", OProjectId, OPlat, NProjectId, Nplat, NprojectName);
            return;
        }
        if (query.size() > 1) {
            log.info("appPlatMove more than one data found!OProjectId:{},OPlat:{},NProjectId:{},Nplat:{},NprojectName:{}", OProjectId, OPlat, NProjectId, Nplat, NprojectName);
            return;
        }


        HeraAppBaseInfoModel heraAppBaseInfo = query.get(0);

        String oldProjectName = heraAppBaseInfo.getAppName();


        heraAppBaseInfo.setPlatformType(Nplat);
        heraAppBaseInfo.setIamTreeId(newIamId);
        heraAppBaseInfo.setBindId(String.valueOf(NProjectId));
        heraAppBaseInfo.setAppName(NprojectName);

        int update = heraBaseInfoService.insertOrUpdate(heraAppBaseInfo);
        if (update < 1) {
            log.error("appPlatMove update heraBaseInfo fail!OProjectId:{},OPlat:{},NProjectId:{},Nplat:{},NprojectName:{}", OProjectId, OPlat, NProjectId, Nplat, NprojectName);
        }


        /**
         * 关联的角色信息
         */
        HeraAppRole heraAppRole = new HeraAppRole();
        heraAppRole.setAppPlatform(OPlat);
        heraAppRole.setAppId(String.valueOf(OProjectId));
        List<HeraAppRole> appRoles = heraAppRoleDao.query(heraAppRole, null, null);
        if (!CollectionUtils.isEmpty(appRoles)) {
            appRoles.forEach(t -> {
                t.setAppId(String.valueOf(NProjectId));
                t.setAppPlatform(Nplat);
                heraAppRoleDao.update(t);
            });
        }

        /**
         * 3、关注信息
         * -参与列表
         * -关注列表
         */
        List<AppMonitor> appMonitors = appMonitorDao.getByProjectIdAndPlat(OProjectId, OPlat);
        if (!CollectionUtils.isEmpty(appMonitors)) {
            appMonitors.forEach(t -> {

                t.setAppSource(Nplat);
                t.setProjectId(NProjectId);
                t.setIamTreeId(newIamId);
                t.setProjectName(NprojectName);
                appMonitorDao.update(t);
            });
        }


        if (rebuildRule) {
            AlarmStrategy strategy = new AlarmStrategy();
            strategy.setAppId(OProjectId);
            strategy.setAppName(oldProjectName);
            PageData<List<AlarmStrategyInfo>> listPageData = strategyDao.searchByCondNoUser(strategy, 1, 1000, null, null);
            List<AlarmStrategyInfo> list = listPageData.getList();
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(t -> {
                    List<AppAlarmRule> rules = ruleDao.selectByStrategyId(t.getId());
                    for (AppAlarmRule rule : rules) {
                        /**
                         * 1、app的projectId、Name、iamId都更新好
                         * 2、rule的iamId
                         * 3、user采用rule的creater
                         * 4、ruleData补数据
                         *
                         *   ruleData 标签数据来源：
                         *   1、strategy
                         *   2、rule的labels
                         *
                         *   使用到ruleData的数据：
                         *   AlarmCallbackUrl
                         *   ruleData.getAlarmDetailUrl()
                         *
                         *   getEnvLabels：
                         *   serverEnv（include、except）
                         *   service（include、except）
                         *   functionId（include、except）
                         *   methodName：http、dubbo
                         *   errorCode：http
                         *   serviceName：dubbo
                         *
                         * Tesla
                         *   rule.getTeslaGroup()
                         *   rule.getTeslaUrls()
                         *   rule.getExcludeTeslaUrls()
                         *
                         *
                         *   ruleData.getExpr() 自定义表达式
                         *   ruleData.getAlertMembers()
                         *
                         *
                         *
                         */

                        Integer oldAlarmId = rule.getAlarmId();
                        Integer oldIamId = rule.getIamId();

                        AppMonitor app = new AppMonitor();
                        app.setProjectId(NProjectId);
                        app.setProjectName(NprojectName);

                        rule.setIamId(newIamId);
                        rule.setProjectId(NProjectId);

                        StringBuilder cname = new StringBuilder();
                        cname.append(newIamId);
                        cname.append("-").append(rule.getAlert());
                        cname.append("-").append(System.currentTimeMillis());
                        rule.setCname(cname.toString());

                        AlarmRuleData ruleData = new AlarmRuleData();
                        BeanUtils.copyProperties(rule, ruleData);
                        ruleData.setLabels(rule.getLabels());
                        ruleData.convertLabels();

                        ruleData.setIncludeEnvs(t.getIncludeEnvs());
                        ruleData.setExceptEnvs(t.getExceptEnvs());
                        ruleData.setIncludeZones(t.getIncludeZones());
                        ruleData.setExceptZones(t.getExceptZones());
                        ruleData.setIncludeContainerName(t.getIncludeContainerName());
                        ruleData.setExceptContainerName(t.getExceptContainerName());
                        ruleData.setAlertMembers(t.getAlertMembers());

                        if (!CollectionUtils.isEmpty(t.getIncludeFunctions())) {
                            ruleData.setIncludeFunctions(t.getIncludeFunctions().stream().map(String::valueOf).collect(Collectors.toList()));
                        }

                        if (!CollectionUtils.isEmpty(t.getExceptFunctions())) {
                            ruleData.setExceptFunctions(t.getExceptFunctions().stream().map(String::valueOf).collect(Collectors.toList()));
                        }

                        ruleData.setIncludeModules(t.getIncludeModules());
                        ruleData.setExceptModules(t.getExceptModules());


                        Result result1 = alarmService.addRule(app, rule, rule.getCreater(), ruleData);
                        if (!result1.isSuccess()) {
                            log.error("appPlatMove add new rule fail!rule{}", rule.toString());
                            return;
                        }

                        JsonElement data = (JsonElement) result1.getData();
                        Integer alarmId = data.getAsJsonObject().get("id").getAsInt();
                        rule.setAlarmId(alarmId);

                        if (rule.getRuleStatus().equals(RuleStatusType.pause.getCode())) {
                            Result result = alarmService.enabledRule(alarmId, RuleStatusType.pause.getCode(), rule.getIamId(), rule.getCreater());
                            if (!result.isSuccess()) {
                                log.error("appPlatMove pause rule fail!rule{}", rule.toString());
                            }
                        }


                        Result result = alarmService.deleteRule(oldAlarmId, oldIamId, rule.getCreater());
                        if (!result.isSuccess()) {
                            log.error("appPlatMove del old rule fail!rule{}", rule.toString());
                        }

                        int i = ruleDao.updateByIdSelective(rule);
                        if (i < 1) {
                            log.error("appPlatMove update rule db fail! rule{}", rule.toString());
                        }

                    }

                    AlarmStrategy strategyUp = new AlarmStrategy();
                    strategyUp.setId(t.getId());
                    strategyUp.setAppId(NProjectId);
                    strategyUp.setAppName(NprojectName);
                    strategyUp.setIamId(newIamId);
                    boolean b = strategyDao.updateById(strategyUp);
                    if (!b) {
                        log.error("appPlatMove update strategy fail! oldP:{},new:{}", t.toString(), strategyUp.toString());
                    }

                });
            }
        }
    }

    public Result selectAppAlarmHealth(AlarmHealthQuery query) {
        try {
            List<AlarmHealthResult> alarmHealthResults = appMonitorDao.selectAppHealth(query);
            if (!CollectionUtils.isEmpty(alarmHealthResults)) {

                for (AlarmHealthResult alarmHealth : alarmHealthResults) {
                    /**
                     * 基础指标得分
                     */
                    alarmHealth.setBasicMetricScore((alarmHealth.getCpuUseRate().intValue() > 0 ? 2 : 0) +
                            (alarmHealth.getCpuLoad().intValue() > 0 ? 2 : 0) +
                            (alarmHealth.getMemUseRate().intValue() > 0 ? 2 : 0) +
                            (alarmHealth.getContainerNum().intValue() > 0 ? 2 : 0) +
                            (alarmHealth.getJvmThread().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getJvmGc().intValue() > 0 ? 1 : 0)
                    );

                    /**
                     * 接口指标得分
                     */
                    alarmHealth.setInterfaceMetricScore((alarmHealth.getHttpServerAvailability().intValue() > 0 ? 2 : 0) +
                            (alarmHealth.getHttpServerQps().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getHttpServerTimeCost().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getHttpClientAvailability().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getHttpClientQps().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getHttpClientTimeCost().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getDubboProviderAvailability().intValue() > 0 ? 2 : 0) +
                            (alarmHealth.getDubboProviderQps().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getDubboProviderTimeCost().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getDubboProviderSlowQuery().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getDubboConsumerAvailability().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getDubboConsumerQps().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getDubboConsumerTimeCost().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getDubboConsumerSlowQuery().intValue() > 0 ? 1 : 0) +
                            (alarmHealth.getDbAvailability().intValue() > 0 ? 2 : 0) +
                            (alarmHealth.getDbSlowQuery().intValue() > 0 ? 1 : 0)
                    );

                    /**
                     * 综合得分
                     */
                    alarmHealth.setComprehensiveScore(alarmHealth.getBasicMetricScore() + alarmHealth.getInterfaceMetricScore());
                }
            }
            return Result.success(alarmHealthResults);
        } catch (Exception e) {
            log.error("selectAppAlarmHealth Error!{}", e.getMessage(), e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    public Result getResourceUsageUrlForK8s(Integer appId, String appName) {
        return appMonitorServiceExtension.getResourceUsageUrlForK8s(appId, appName);
    }

    public Result initAppsByUsername(String userName){
        return appMonitorServiceExtension.initAppsByUsername(userName);
    }

    public List<ProjectInfo> getAppsByUserName(String username){
        return appMonitorServiceExtension.getAppsByUserName(username);
    }

    public Result<PageData> getProjectInfos(String userName, String appName, Integer page, Integer pageSize) {

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageData pd = new PageData();
        pd.setPage(page);
        pd.setPageSize(pageSize);

        HeraAppBaseInfoModel model = new HeraAppBaseInfoModel();
        model.setAppName(appName);

        Long total = hearAppService.count(model);
        pd.setTotal(total);

        List<HeraAppBaseInfoModel> query = hearAppService.query(model, page, pageSize);

        List list = new ArrayList<ProjectInfo>();
        if (CollectionUtils.isEmpty(query)) {
            pd.setList(list);
            return Result.success(pd);
        }

        query.forEach(t -> {
            ProjectInfo info = new ProjectInfo();
            info.setId(Long.valueOf(t.getBindId()));
            info.setName(t.getAppName());
            info.setIamTreeId(t.getIamTreeId() != null ? Long.valueOf(t.getIamTreeId()) : t.getIamTreeId());
            list.add(info);
        });

        pd.setList(list);
        return Result.success(pd);

    }

    public Result<String> createWithBaseInfo(AppMonitorModel appMonitorModel, String user) {

        HeraAppBaseInfoModel heraAppBaseInfo = appMonitorModel.baseInfo();
        Integer baseInfoId = createBaseInfo(heraAppBaseInfo);
        if (baseInfoId == null) {
            log.error("createBaseInfo fail!heraAppBaseInfo:{}", heraAppBaseInfo);
            return Result.fail(ErrorCode.unknownError);
        }

        AppMonitor appMonitor = appMonitorModel.appMonitor();
        appMonitor.setBaseInfoId(baseInfoId);

        if (!appMonitorServiceExtension.checkCreateParam(appMonitor)) {
            log.error("AppMonitorService.createWithBaseInfo 用户{}添加项目{}，参数不合法", user, appMonitor);
            return Result.fail(ErrorCode.invalidParamError);
        }
        if (StringUtils.isNotBlank(appMonitor.getOwner()) && appMonitor.getOwner().equals("yes")) {
            appMonitor.setOwner(user);
        } else {
            appMonitor.setCareUser(user);
        }

        Result<String> result = create(appMonitor);
        if (!result.isSuccess()) {
            log.error("AppMonitorController.addApp fail! user:{},appMonitorModel:{}", user, appMonitorModel);
            return Result.fail(ErrorCode.invalidParamError);
        }

        return Result.success(null);

    }

    public Integer createBaseInfo(HeraAppBaseInfoModel heraAppBaseInfo) {

        HeraAppBaseInfoModel queryCondition = new HeraAppBaseInfoModel();
        queryCondition.setBindId(heraAppBaseInfo.getBindId());
        queryCondition.setPlatformType(heraAppBaseInfo.getPlatformType());

        List<HeraAppBaseInfoModel> query = heraBaseInfoService.query(queryCondition, 1, 10);

        if (!CollectionUtils.isEmpty(query)) {
            log.info("createBaseInfo HeraAppBaseInfo has exist!heraAppBaseInfo:{},query Result:{}", heraAppBaseInfo, new Gson().toJson(query));
            return query.get(0).getId();
        }

        int i = heraBaseInfoService.insertOrUpdate(heraAppBaseInfo);

        if (i <= 0) {
            return null;
        }

        return heraAppBaseInfo.getId();
    }

    public Result<String> create(AppMonitor appMonitor) {

        if (appMonitor == null) {
            log.error("AppMonitorService.create param is null");
            return Result.fail(ErrorCode.invalidParamError);
        }
        if (appMonitor.getProjectId() == null || StringUtils.isEmpty(appMonitor.getProjectName())) {
            log.error("AppMonitorService.create param is avalid! projectId or projectName is empty!");
            return Result.fail(ErrorCode.invalidParamError);
        }
        if (StringUtils.isEmpty(appMonitor.getOwner()) && StringUtils.isEmpty(appMonitor.getCareUser())) {
            log.error("AppMonitorService.create param is avalid! owner and careUser can not both null at same time!");
            return Result.fail(ErrorCode.invalidParamError);
        }

        /**
         * owner、mycare只能存在一种，如果存在owner直接按owner处理，不再继续判断！正常的业务场景不会同时指定owner和mycare；
         */
        AppViewType viewType = AppViewType.MyApp;
        String userName = appMonitor.getOwner();
        if (StringUtils.isEmpty(appMonitor.getOwner())) {
            viewType = AppViewType.MyCareApp;
            userName = appMonitor.getCareUser();
        }
        AppMonitor app = appMonitorDao.getMyApp(appMonitor.getProjectId(), appMonitor.getIamTreeId(), userName, viewType);
        if (app != null) {
            log.info("AppMonitorService.create update Data appMonitor : {}", appMonitor);
            //如果指定的projectId，userName，viewType已经存在，则更新一下项目名称即可，无需重复创建数据！
            app.setProjectName(appMonitor.getProjectName());
            app.setBaseInfoId(appMonitor.getBaseInfoId());
            int update = appMonitorDao.update(app);
            if (update > 0) {
                log.info("AppMonitorService.create update Data success appMonitor : {}", appMonitor);
                return Result.success(null);
            } else {
                log.info("AppMonitorService.create database update Data failed appMonitor : {}", appMonitor);
                return Result.fail(ErrorCode.unknownError);
            }
        }
        //兼容不同类型的重复添加
        if (AppViewType.MyCareApp.equals(viewType)) {
            app = appMonitorDao.getMyApp(appMonitor.getProjectId(), appMonitor.getIamTreeId(), userName, AppViewType.MyApp);
            if (app != null) {
                log.error("AppMonitorService.create项目已经添加过参与项目， param={}", appMonitor);
                return Result.fail(ErrorCode.REPEAT_ADD_PROJECT);
            }
        } else {
            app = appMonitorDao.getMyApp(appMonitor.getProjectId(), appMonitor.getIamTreeId(), userName, AppViewType.MyCareApp);
            if (app != null) {
                //清除已经关注的数据
                Result<String> delResult = this.delete(app.getId());
                if (delResult.getCode() != ErrorCode.success.getCode()) {
                    return delResult;
                }
            }
        }
        try {
            int i = appMonitorDao.create(appMonitor);
            if (i > 0) {
                createGrafana(appMonitor);
                log.info("AppMonitorService.create success appMonitor : {}", appMonitor);
                return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), null);
            } else {
                log.info("AppMonitorService.create database create data failed appMonitor : {}", appMonitor);
                return Result.fail(ErrorCode.unknownError);
            }

        } catch (Exception e) {
            log.error("AppMonitorService.create error" + e.getMessage(), e);
            return Result.fail(ErrorCode.unknownError);
        }

    }

    /**
     * 创建grafana图表
     *
     * @param appMonitor
     */
    private void createGrafana(AppMonitor appMonitor) {
        if (appMonitor.getAppSource() == null) {
            return;
        }
        StringBuilder appName = new StringBuilder();
        appName.append(appMonitor.getProjectId()).append("_").append(appMonitor.getProjectName().replace('.', '_'));

        String area = platFormTypeExtensionService.getGrafanaDirByTypeCode(appMonitor.getAppSource());

        if (StringUtils.isBlank(area)) {
            log.error("invalid grafana area!appMonitor:{}", appMonitor);
            return;
        }

        HeraAppBaseInfoModel heraAppBaseInfo = new HeraAppBaseInfoModel();
        heraAppBaseInfo.setBindId(appMonitor.getProjectId() + "");
        heraAppBaseInfo.setPlatformType(appMonitor.getAppSource());

        List<HeraAppBaseInfoModel> query = heraBaseInfoService.query(heraAppBaseInfo, null, null);
        HeraAppBaseInfoModel baseInfo = CollectionUtils.isEmpty(query) ? null : query.get(0);
        if (baseInfo == null) {
            log.error("no base data found for app : {},stop generate grafana url", appMonitor.getProjectName());
            return;
        }

        appGrafanaMappingService.createTmpByAppBaseInfo(baseInfo);

    }


    public Result<String> delete(Integer id) {

        if (id == null) {
            log.error("AppMonitorService.delete error param id is null!");
            return Result.fail(ErrorCode.invalidParamError);
        }

        AppMonitor app = appMonitorDao.getById(id);
        if (app == null) {
            log.error("AppMonitorService.delete error cannot find data by id : {}", id);
            return Result.fail(ErrorCode.invalidParamError);
        }

        app.setStatus(1);
        int update = appMonitorDao.update(app);
        if (update < 1) {
            log.error("AppMonitorService.delete error delete database failed!");
            return Result.fail(ErrorCode.unknownError);
        }

        return Result.success(null);

    }

    public Result<String> deleteByUser(Integer projectId, Integer appSource, String userName) {


        try {
            List<AppMonitor> apps = appMonitorDao.getMyOwnerOrCareAppById(projectId, appSource, userName);

            if (CollectionUtils.isEmpty(apps)) {
                log.error("AppMonitorService.deleteByUser error cannot find data,projectId : {}", projectId);
                return Result.fail(ErrorCode.unknownError);
            }

            for (AppMonitor app : apps) {
                app.setStatus(1);
                int update = appMonitorDao.update(app);
                if (update < 1) {
                    log.error("AppMonitorService.deleteByUser error delete database failed!app:{}", app);
                    return Result.fail(ErrorCode.unknownError);
                }
            }

            return Result.success(null);

        } catch (Exception e) {
            log.error("deleteByUser error!" + e.getMessage(), e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    public Result<PageData<List<AppMonitor>>> listApp(String appName, String userName, Integer page, Integer pageSize_) {

        if (StringUtils.isEmpty(userName)) {
            log.error("AppMonitorService.listApp param is invalid userName is empty!");
            return Result.fail(ErrorCode.invalidParamError);
        }

        Integer pageNum = page;
        Integer pageSize = pageSize_;

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageData pd = new PageData();
        pd.setPage(pageNum);
        pd.setPageSize(pageSize);

        try {

            Long dataTotal = appMonitorDao.getDataTotalByOr(appName, userName, userName);
            pd.setTotal(dataTotal);

            if (dataTotal != null && dataTotal.intValue() > 0) {
                List<AppMonitor> apps = appMonitorDao.getMyOwnerOrCareApp(appName, userName, pageNum, pageSize);
                pd.setList(apps);
            }

            log.info("AppMonitorService.listApp success! param  appName : {}, userName : {},result Count : {} ", appName, userName, dataTotal);

            return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), pd);

        } catch (Exception e) {
            log.error("AppMonitorService.listApp error" + e.getMessage(), e);
            return Result.fail(ErrorCode.unknownError);
        }

    }

    public Result<PageData<List<AppMonitor>>> listAppDistinct(String userName, String appName, Integer page, Integer pageSize_) {

        if (StringUtils.isEmpty(userName)) {
            log.error("AppMonitorService.listAppDistinct param is invalid userName is empty!");
            return Result.fail(ErrorCode.invalidParamError);
        }

        Integer pageNum = page;
        Integer pageSize = pageSize_;

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageData pd = new PageData();
        pd.setPage(pageNum);
        pd.setPageSize(pageSize);

        try {

            Long dataTotal = appMonitorDao.countAllMyAppDistinct(userName, appName);
            pd.setTotal(dataTotal);

            if (dataTotal != null && dataTotal.intValue() > 0) {
                List<AppMonitor> apps = appMonitorDao.getAllMyAppDistinct(userName, appName, pageNum, pageSize);
                pd.setList(apps);
//                initAppAlarmData(apps);
            }

            log.info("AppMonitorService.listAppDistinct success! param  appName : {}, userName : {},result Count : {} ", appName, userName, dataTotal);

            return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), pd);

        } catch (Exception e) {
            log.error("AppMonitorService.listAppDistinct error" + e.getMessage(), e);
            return Result.fail(ErrorCode.unknownError);
        }

    }

    public Result<PageData<List<AppMonitor>>> listMyApp(AppMonitor appMonitor, String userName, Integer page, Integer pageSize_) {

        if (StringUtils.isEmpty(userName)) {
            log.error("AppMonitorService.listMyApp param is invalid userName is empty!");
            return Result.fail(ErrorCode.invalidParamError);
        }

        Integer pageNum = page;
        Integer pageSize = pageSize_;

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageData pd = new PageData();
        pd.setPage(pageNum);
        pd.setPageSize(pageSize);

        try {

            Long dataTotal = appMonitorDao.getDataTotal(appMonitor, userName, null);
            pd.setTotal(dataTotal);

            log.info("AppMonitorService.listMyApp success! param  appName : {}, userName : {},result Count : {} ", appMonitor.getProjectName(), userName, dataTotal);

            if (dataTotal != null && dataTotal.intValue() > 0) {
                List<AppMonitor> myCareApp = appMonitorDao.getMyOwnerApp(appMonitor, userName, pageNum, pageSize);
                pd.setList(myCareApp);
            }

            return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), pd);

        } catch (Exception e) {
            log.error("AppMonitorService.listMyApp error" + e.getMessage(), e);
            return Result.fail(ErrorCode.unknownError);
        }

    }

    /**
     * 查询我拥有的或关注的项目列表
     *
     * @param user
     * @param param
     * @return
     */
    public Result<PageData<List<AppMonitor>>> myAndCareAppList(String user, AppMonitorRequest param) {
        PageData<List<AppMonitor>> pageData = appMonitorDao.getMyAndCareAppList(user, param.getAppName(), param.getPage(), param.getPageSize(), param.isNeedPage());
        return Result.success(pageData);
    }

    public Result<PageData<List<AppMonitor>>> listMyCareApp(String appName, String careUser, Integer page, Integer pageSize_) {

        if (StringUtils.isEmpty(careUser)) {
            log.error("AppMonitorService.listMyCareApp param is invalid careUser is empty!");
            return Result.fail(ErrorCode.invalidParamError);
        }

        Integer pageNum = page;
        Integer pageSize = pageSize_;

        if (pageNum == null || pageNum.intValue() < 1) {
            pageNum = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageData pd = new PageData();
        pd.setPage(pageNum);
        pd.setPageSize(pageSize);

        try {

            AppMonitor appMonitor = new AppMonitor();
            appMonitor.setProjectName(appName);
            Long dataTotal = appMonitorDao.getDataTotal(appMonitor, null, careUser);
            pd.setTotal(dataTotal);

            log.info("AppMonitorService.listMyCareApp success! param  appName : {}, careUser : {},result Count : {} ", appName, careUser, dataTotal);

            if (dataTotal != null && dataTotal.intValue() > 0) {
                List<AppMonitor> myCareApp = appMonitorDao.getMyCareApp(appName, careUser, pageNum, pageSize);
                pd.setList(myCareApp);
            }

            return new Result<>(ErrorCode.success.getCode(), ErrorCode.success.getMessage(), pd);

        } catch (Exception e) {
            log.error("AppMonitorService.listMyCareApp error" + e.getMessage(), e);
            return Result.fail(ErrorCode.unknownError);
        }

    }

    public AppMonitor getByIamTreeId(Integer aimTreeId) {

        try {
            return appMonitorDao.getByIamTreeId(aimTreeId);
        } catch (Exception e) {
            log.error("AppMonitorService.getByIamTreeId error! {}", e.getMessage(), e);
            return null;
        }
    }

    public Result getTeslaAlarmHealthByUser(String user){
        return teslaService.getTeslaAlarmHealthByUser(user);
    }

    public void washBaseId() {
        Integer pageSize = 100;
        Integer page = 0;

        Long dataTotalL = appMonitorDao.getDataTotal(new AppMonitor(), null, null);
        log.info("washBaseId totalNum:{}", dataTotalL);
        Integer dataTotal = dataTotalL.intValue();
        page = dataTotal % pageSize == 0 ? (dataTotal / pageSize) : (dataTotal / pageSize + 1);

        for (int i = 1; i <= page; i++) {

            List<AppMonitor> allApps = appMonitorDao.getAllApps(i, pageSize);
            for (AppMonitor app : allApps) {

                HeraAppBaseInfoModel queryCondition = new HeraAppBaseInfoModel();
                queryCondition.setBindId(String.valueOf(app.getProjectId()));
                queryCondition.setPlatformType(app.getAppSource());
                List<HeraAppBaseInfoModel> appBase = heraBaseInfoService.query(queryCondition, 1, 1);

                if (CollectionUtils.isEmpty(appBase)) {
                    log.info("washBaseId no HeraAppBaseInfo found for app:{}", app.toString());
                    continue;
                }
                app.setBaseInfoId(appBase.get(0).getId());
                int update = appMonitorDao.update(app);
                log.info("wash baseId for app:{},result:{}", app.toString(), update);
            }
        }
    }

    public Result grafanaInterfaceList() {
        return appMonitorServiceExtension.grafanaInterfaceList();
    }

    public Result selectByIAMId(Integer iamId, Integer iamType, String userName){
        try {
            List<AppMonitor> appMonitors = appMonitorDao.selectByIAMId(iamId, iamType, userName);
            return Result.success(appMonitors);
        }catch (Throwable t){
            log.error("select by iamId error : ",t);
            return Result.fail(ErrorCode.unknownError);
        }
    }

    public void heraAppInfoModify(HeraAppInfoModifyMessage baseInfoModify) {

        if(HeraAppModifyType.create.equals(baseInfoModify.getModifyType())){
            HeraAppBaseInfoModel appBaseInfoModel = baseInfoModify.baseInfoModel();
            appGrafanaMappingService.createTmpByAppBaseInfo(appBaseInfoModel);
        }

        if(HeraAppModifyType.update.equals(baseInfoModify.getModifyType())){
            this.modifyAppAndAlarm(baseInfoModify);
        }

        if(HeraAppModifyType.delete.equals(baseInfoModify.getModifyType())){
            this.heraAppDelete(baseInfoModify);
        }
    }

    private void heraAppDelete(HeraAppInfoModifyMessage message){

        deleteByBaseInfoId(message.getId());

        alarmStrategyService.deleteByAppIdAndIamId(message.getAppId(),message.getIamTreeId());
    }

    private void deleteByBaseInfoId(Integer baseInfoId){

        List<AppMonitor> appMonitors = appMonitorDao.listAppsByBaseInfoId(baseInfoId);
        if(CollectionUtils.isEmpty(appMonitors)){
            log.info("deleteByBaseInfoId no data found! baseInfoId:{}",baseInfoId);
            return;
        }

        for(AppMonitor appMonitor : appMonitors){
            appMonitorDao.delete(appMonitor.getId());
        }
    }

    public void modifyAppAndAlarm(HeraAppInfoModifyMessage baseInfoModify) {

        /**
         * appMonitor 信息同步变更
         */
        List<AppMonitor> appMonitors = appMonitorDao.listAppsByBaseInfoId(baseInfoModify.getId());
        if (!CollectionUtils.isEmpty(appMonitors)) {
            appMonitors.forEach(t -> {
                t.setAppSource(baseInfoModify.getPlatformType());
                t.setProjectId(baseInfoModify.getAppId());
                t.setIamTreeId(baseInfoModify.getIamTreeId());
                t.setIamTreeType(baseInfoModify.getIamTreeType());
                t.setProjectName(baseInfoModify.getAppName());
                appMonitorDao.update(t);
            });
        }

        /**
         * appName变更，报警策略和报警规则同步变更
         */
        if (baseInfoModify.getIsNameChange()) {

            AlarmStrategy strategy = new AlarmStrategy();
            strategy.setAppId(baseInfoModify.getAppId());
            strategy.setIamId(baseInfoModify.getIamTreeId());

            if(!appMonitorServiceExtension.checkAppModifyStrategySearchCondition(baseInfoModify)){
                return;
            }

            PageData<List<AlarmStrategyInfo>> listPageData = strategyDao.searchByCondNoUser(strategy, 1, 1000, null, null);
            List<AlarmStrategyInfo> list = listPageData.getList();
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(t -> {
                    List<AppAlarmRule> rules = ruleDao.selectByStrategyId(t.getId());
                    for (AppAlarmRule rule : rules) {

                        AppMonitor app = new AppMonitor();
                        app.setProjectId(baseInfoModify.getAppId());
                        app.setProjectName(baseInfoModify.getAppName());

                        AlarmRuleData ruleData = new AlarmRuleData();
                        BeanUtils.copyProperties(rule, ruleData);
                        ruleData.setLabels(rule.getLabels());
                        ruleData.convertLabels();

                        ruleData.setIncludeEnvs(t.getIncludeEnvs());
                        ruleData.setExceptEnvs(t.getExceptEnvs());
                        ruleData.setIncludeZones(t.getIncludeZones());
                        ruleData.setExceptZones(t.getExceptZones());

                        ruleData.setIncludeContainerName(t.getIncludeContainerName());
                        ruleData.setExceptContainerName(t.getExceptContainerName());
                        ruleData.setAlertMembers(t.getAlertMembers());
                        ruleData.setAtMembers(t.getAtMembers());

                        if (!CollectionUtils.isEmpty(t.getIncludeFunctions())) {
                            ruleData.setIncludeFunctions(t.getIncludeFunctions().stream().map(String::valueOf).collect(Collectors.toList()));
                        }

                        if (!CollectionUtils.isEmpty(t.getExceptFunctions())) {
                            ruleData.setExceptFunctions(t.getExceptFunctions().stream().map(String::valueOf).collect(Collectors.toList()));
                        }

                        ruleData.setIncludeModules(t.getIncludeModules());
                        ruleData.setExceptModules(t.getExceptModules());

                        Result result = alarmService.editRule(rule, ruleData, app, rule.getCreater());
                        if(!result.isSuccess()){
                            log.error("heraAppInfoModify fail! rule : {} , result : {}",rule.toString(),new Gson().toJson(result));
                            continue;
                        }

                        int i = ruleDao.updateByIdSelective(rule);
                        if (i < 1) {
                            log.error("heraAppInfoModify update rule db fail! rule{}", rule.toString());
                        }

                    }

                    AlarmStrategy strategyUp = new AlarmStrategy();
                    strategyUp.setId(t.getId());
                    strategyUp.setAppId(baseInfoModify.getAppId());
                    strategyUp.setAppName(baseInfoModify.getAppName());
                    strategyUp.setIamId(baseInfoModify.getIamTreeId());
                    boolean b = strategyDao.updateById(strategyUp);
                    if (!b) {
                        log.error("heraAppInfoModify update strategy fail! old:{},new:{}", t.toString(), strategyUp.toString());
                    }

                });
            }
        }
    }

    public void washBugData(){
        AlarmStrategy strategy = new AlarmStrategy();
        PageData<List<AlarmStrategyInfo>> listPageData = strategyDao.searchByCondNoUser(strategy, 1, 1000, null, null);
        List<AlarmStrategyInfo> list = listPageData.getList();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(t -> {
                List<AppAlarmRule> rules = ruleDao.selectByStrategyId(t.getId());

                if(!CollectionUtils.isEmpty(rules)){
                    AppAlarmRule rule = rules.get(0);
                    AppMonitor appMonitor = appMonitorDao.getByIamTreeIdAndAppId(rule.getIamId(), rule.getProjectId());

                    if(appMonitor != null){

                        AlarmStrategy strategyUp = new AlarmStrategy();
                        strategyUp.setId(t.getId());
                        strategyUp.setAppId(appMonitor.getProjectId());
                        strategyUp.setAppName(appMonitor.getProjectName());
                        strategyUp.setIamId(appMonitor.getIamTreeId());
                        boolean b = strategyDao.updateById(strategyUp);
                        if (!b) {
                            log.error("heraAppInfoModify update strategy fail! old:{},new:{}", t.toString(), strategyUp.toString());
                        }

                        for (AppAlarmRule rule1 : rules) {

                            AlarmRuleData ruleData = new AlarmRuleData();
                            BeanUtils.copyProperties(rule1, ruleData);
                            ruleData.setLabels(rule1.getLabels());
                            ruleData.convertLabels();

                            ruleData.setIncludeEnvs(t.getIncludeEnvs());
                            ruleData.setExceptEnvs(t.getExceptEnvs());
                            ruleData.setIncludeZones(t.getIncludeZones());
                            ruleData.setExceptZones(t.getExceptZones());
                            ruleData.setIncludeContainerName(t.getIncludeContainerName());
                            ruleData.setExceptContainerName(t.getExceptContainerName());

//                        ruleData.setIncludeZones(t.getIncludeEnvs());
//                        ruleData.setExceptZones(t.getExceptEnvs());
                            ruleData.setAlertMembers(t.getAlertMembers());
                            ruleData.setAtMembers(t.getAtMembers());

                            if (!CollectionUtils.isEmpty(t.getIncludeFunctions())) {
                                ruleData.setIncludeFunctions(t.getIncludeFunctions().stream().map(String::valueOf).collect(Collectors.toList()));
                            }

                            if (!CollectionUtils.isEmpty(t.getExceptFunctions())) {
                                ruleData.setExceptFunctions(t.getExceptFunctions().stream().map(String::valueOf).collect(Collectors.toList()));
                            }

                            ruleData.setIncludeModules(t.getIncludeModules());
                            ruleData.setExceptModules(t.getExceptModules());

                            Result result = alarmService.editRule(rule1, ruleData, appMonitor, rule1.getCreater());
                            if(!result.isSuccess()){
                                log.error("washBugData fail! rule1 : {} , result : {}",rule1.toString(),new Gson().toJson(result));
                                continue;
                            }

                        }
                    }


                }


            });
        }
    }

    public void washBugDataForAppMonitor(){
        List<AppMonitor> allApps = appMonitorDao.getAllApps(1, 5000);
        for(AppMonitor appMonitor : allApps){
            if(appMonitor.getBaseInfoId() == null){
                log.error("update appMonitor no baseId found! appMonitor : {}" ,appMonitor.toString());
                continue;
            }
            HeraAppBaseInfoModel byId = heraBaseInfoService.getById(appMonitor.getBaseInfoId());

            if(byId == null || StringUtils.isBlank(byId.getBindId())){
                log.error("update appMonitor HeraAppBaseInfo error! appMonitor : {}" ,appMonitor.toString());
                continue;
            }

            try {
                appMonitor.setProjectId(Integer.valueOf(byId.getBindId()));
                appMonitor.setIamTreeId(byId.getIamTreeId());
                int update = appMonitorDao.update(appMonitor);
                if(update < 1){
                    log.error("update appMonitor fail! appMonitor : {}" ,appMonitor.toString());
                }
            } catch (NumberFormatException e) {
                log.error("update appMonitor error!" + e.getMessage(),e);
                continue;
            }
        }
    }

    public Result historyInstance(String application,Long startTime, Long endTime) {
        String promql = "count(jvm_classes_loaded_classes{application=\""+ application +"\"}) by (serverIp)";
        log.info("historyInstance promql : {}",promql);
        MetricResponse rangeMetricResponse = prometheusService.queryRangePrometheusByPromQl(promql, startTime, endTime, null,null);
        if(rangeMetricResponse == null || rangeMetricResponse.getData() == null){
            return Result.fail(ErrorCode.unknownError);
        }
        MetricData rangeMetricData = rangeMetricResponse.getData();
        List<MetricDataSet> rangeResult = rangeMetricData.getResult();
        List<String> rangeIps = new ArrayList<>();
        rangeResult.forEach(t -> {
            rangeIps.add(t.getMetric().getServerIp());
        });
        // 一期不做对比
//        List<String> momentIps = new ArrayList<>();
//        MetricResponse momentMetricResponse = prometheusService.queryRangePrometheusByPromQl(promql, startTime, endTime, null,PrometheusService.MOMENT_REQUEST_MODE);
//        if(momentMetricResponse == null || momentMetricResponse.getData() == null){
//            return Result.fail(ErrorCode.unknownError);
//        }
//        MetricData momentMetricData = momentMetricResponse.getData();
//        List<MetricDataSet> momentResult = momentMetricData.getResult();
//        momentResult.forEach(t -> {
//            momentIps.add(t.getMetric().getServerIp());
//        });
//        //momentIps和rangeIps的差集
//        List<String> ips = rangeIps.stream().filter(item -> !momentIps.contains(item)).collect(Collectors.toList());
//        //临时塞假数据
//        ips.add("1.1.1.1");
//        ips.add("1.1.1.2");
//        ips.add("1.1.1.3");
        return Result.success(rangeIps);
    }
}
