package com.xiaomi.mone.monitor.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.monitor.bo.*;
import com.xiaomi.mone.monitor.dao.*;
import com.xiaomi.mone.monitor.dao.model.*;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.api.AppMonitorServiceExtension;
import com.xiaomi.mone.monitor.service.extension.PlatFormTypeExtensionService;
import com.xiaomi.mone.monitor.service.model.AppMonitorModel;
import com.xiaomi.mone.monitor.service.model.AppMonitorRequest;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.ProjectInfo;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmRuleData;
import com.xiaomi.mone.monitor.service.model.prometheus.Metric;
import com.xiaomi.mone.monitor.service.prometheus.AlarmService;
import com.xiaomi.mone.monitor.service.prometheus.PrometheusService;
import com.xiaomi.mone.monitor.utils.FreeMarkerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private GrafanaService grafanaService;

    @Value("${server.type}")
    private String serverType;

    @NacosValue(value = "${resource.use.rate.url}", autoRefreshed = true)
    private String resourceUseRateUrl;

    @NacosValue(value = "${resource.use.rate.url.k8s:nocinfig}", autoRefreshed = true)
    private String resourceUseRateUrlk8s;

    @NacosValue(value = "${resource.use.rate.nodata.url}", autoRefreshed = true)
    private String resourceUseRateNoDataUrl;

    @NacosValue(value = "${grafana.domain}", autoRefreshed = true)
    private String grafanaDomain;

    private String resourceUrl = "/d/hera-resource-utilization/hera-k8szi-yuan-shi-yong-lu-da-pan?orgId=1&var-application=";

    private String dubboProviderOverview = "/d/hera-dubboprovider-overview/hera-dubboproviderzong-lan?orgId=1&kiosk&theme=light";
    private String dubboConsumerOverview = "/d/hera-dubboconsumer-overview/hera-dubboconsumerzong-lan?orgId=1&kiosk&theme=light";
    private String dubboProviderMarket = "/d/Hera-DubboProviderMarket/hera-dubboproviderda-pan?orgId=1&kiosk&theme=light";
    private String dubboConsumerMarket = "/d/Hera-DubboConsumerMarket/hera-dubboconsumerda-pan?orgId=1&kiosk&theme=light";
    private String httpOverview = "/d/Hera-HTTPServer-overview/hera-httpserver-zong-lan?orgId=1&kiosk&theme=light";
    private String httpMarket = "/d/Hera-HTTPServerMarket/hera-httpserverda-pan?orgId=1&kiosk&theme=light";

    @Autowired
    private AppGrafanaMappingDao appGrafanaMappingDao;

    @Autowired
    HeraBaseInfoService heraBaseInfoService;

    @Autowired
    AppGrafanaMappingService appGrafanaMappingService;

    @Autowired
    AppAlarmService appAlarmService;

//    @Reference(registry = "registryConfig",check = false, interfaceClass = IGatewayOpenApi.class,group="${dubbo.group.gateway}")
//    IGatewayOpenApi iGatewayOpenApi;
//
//    @Reference(registry = "registryConfig",check = false, interfaceClass = GwdashApiService.class,group="${dubbo.group}")
//    GwdashApiService gwdashApiService;
//
//    @Reference(registry = "registryConfigYoupin",check = false, interfaceClass = GwdashApiService.class,group="${dubbo.group.youpin}")
//    GwdashApiService gwdashApiServiceYP;
//
//    @Reference(registry = "registryConfig",check = false, interfaceClass = MilogProviderService.class,version = "1.0",group="${dubbo.group.miline}",timeout = 5000)
//    MilogProviderService milogProviderService;

    @Autowired
    PrometheusService prometheusService;

    @Autowired
    ResourceUsageService resourceUsageService;

//    @Autowired
//    FeishuService feishuService;

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
                        ruleData.setIncludeServices(t.getIncludeServices());
                        ruleData.setExceptServices(t.getExceptServices());
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


//    //用于给应用报警不健康的服务发送定时飞书
//    public Result selectAllAppAlarmHealth(String token){
//        String createUrl = getAlarmCreateUrl();
//        String envStr = "";
//        if(!StringUtils.isNotBlank(token) || !token.equals(FEISHU_ALARM_TOKEN)){
//            return  Result.fail(ErrorCode.invalidParamError);
//        }
//        if (serverType.equals("staging") || serverType.equals("dev")) {
//            envStr = "staging";
//        } else if (serverType.equals("online")) {
//            envStr = "online";
//        }
//        if ( !envStr.equals("online")) {
//            //测试不发飞书，线上发飞书
//            return Result.success("测试环境不发送飞书");
//        }
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 50, 5, TimeUnit.MINUTES, new LinkedBlockingQueue(100),
//                (Runnable r) -> new Thread(r, "compute-execute-thread-v2"), new ThreadPoolExecutor.CallerRunsPolicy());
//        try {
//            String finalEnvStr1 = envStr;
//            executor.execute(() -> {
//            AlarmHealthQuery query = new AlarmHealthQuery();
//                List<AlarmHealthResult> alarmHealthResults = appMonitorDao.selectAppHealth(query);
//            String finalEnvStr = finalEnvStr1;
//            alarmHealthResults.forEach(result -> {
//                if (result.getAppId() == null) {
//                    //空的跳过
//                    return;
//                }
//                //筛选不合格的报警并发送飞书
//                String baseContent = "[P1][Hera][应用健康度不达标]\n您在" + finalEnvStr +"环境的应用" + result.getAppName() + "没有配置基础指标报警,因此报警健康度为不合格，请在Hera中进行配置，感谢您的配合,配置链接：" + createUrl;
//                String appContent = "[P1][Hera][应用健康度不达标]\n您在" + finalEnvStr + "环境的应用" + result.getAppName() + "没有配置接口指标报警,因此报警健康度为不合格，请在Hera中进行配置，感谢您的配合,配置链接：" + createUrl;
//                if (result.getBaseAlarmNum() == 0){
//                    String[] owner = new String[]{result.getOwner()};
//                    feishuService.sendFeishu(baseContent,owner,null);
//                }
//                if (result.getAppAlarmNum() == 0){
//                    String[] owner = new String[]{result.getOwner()};
//                    feishuService.sendFeishu(appContent,owner,null);
//                }
//            });
//            });
//            return Result.success("发送飞书成功");
//        } catch (Exception e) {
//            log.error("selectAppAlarmHealth Error!{}",e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//    }
//
//    //改造成飞书卡片形式
//    public Result selectAllResourceUtilizationUnhealthy(String token){
//        String envStr = "";
//        if(!StringUtils.isNotBlank(token) || !token.equals(FEISHU_ALARM_TOKEN)){
//            return  Result.fail(ErrorCode.invalidParamError);
//        }
//
//        if (serverType.equals("staging") || serverType.equals("dev")) {
//            envStr = "staging";
//        } else if (serverType.equals("online")) {
//            envStr = "online";
//        }
//       /* if ( !envStr.equals("online")) {
//            //测试不发飞书，线上发飞书
//            return Result.success("测试环境不发送飞书");
//        }*/
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 50, 5, TimeUnit.MINUTES, new LinkedBlockingQueue(100),
//                (Runnable r) -> new Thread(r, "compute-execute-thread-v2"), new ThreadPoolExecutor.CallerRunsPolicy());
//        try{
//            Map<String, Object> map = new HashMap<>();
//            map.put("env",envStr);
//            String finalEnvStr1 = envStr;
//            String domain = grafanaDomain.split("://")[1];
//            executor.execute(() -> {
//                List<ResourceUsageMessage> cpuUsageData = resourceUsageService.getCpuUsageData();
//                List<ResourceUsageMessage> memUsageData = resourceUsageService.getMemUsageData();
//               // log.info("cpuUsageData is: {}" ,cpuUsageData);
//               // log.info("memUsageData is: {}" ,memUsageData);
//                String finalEnvStr = finalEnvStr1;
//                cpuUsageData.forEach(cpuUsage -> {
//                    if (cpuUsage.getProjectId() == null || cpuUsage.getProjectId().equals("")){
//                        return;
//                    }
//                    map.put("application",cpuUsage.getProjectName());
//                    String ip = String.format(domain +  "/d/khkvf66Gk/zhong-guo-qu-onlinerong-qi-jian-kong?orgId=1&refresh=10s&viewPanel=2&var-Node=%s&var-total_instance=295&var-name=All", cpuUsage.getIp());
//                    map.put("ipAddr",ip);
//                    map.put("ip",cpuUsage.getIp());
//                    map.put("type","CPU");
//                    String content = "";
//                    try {
//                        content = FreeMarkerUtil.getContent("/", "feishu_resource_alert.ftl", map);
//                    } catch (Exception e) {
//                        log.error("FreeMarkerUtil.getContent Error!{}",e.getMessage(),e);
//                    }
//                    //TODO:上线测试后去除if判断
//                    //if (cpuUsage.getMembers().contains("zhangxiaowei6")){
//                        String[] owner = cpuUsage.getMembers().toArray(new String[0]);
//                        feishuService.sendFeishu(content,owner,null,true);
//                  //  }
//                });
//                memUsageData.forEach(memUsage -> {
//                    if (memUsage.getProjectId() == null || memUsage.getProjectId().equals("")){
//                        return;
//                    }
//                    map.put("application",memUsage.getProjectName());
//                    String ip = String.format(domain + "/d/khkvf66Gk/zhong-guo-qu-onlinerong-qi-jian-kong?orgId=1&refresh=10s&var-Node=%s&var-total_instance=295&var-name=All&var-pod=&viewPanel=11", memUsage.getIp());
//                    map.put("ipAddr",ip);
//                    map.put("ip",memUsage.getIp());
//                    map.put("type","内存");
//                    String content = "";
//                    try {
//                        content = FreeMarkerUtil.getContent("/", "feishu_resource_alert.ftl", map);
//                    } catch (Exception e) {
//                        log.error("FreeMarkerUtil.getContent Error!{}",e.getMessage(),e);
//                    }
//                    //TODO:上线测试后去除if判断
//                   // if (memUsage.getMembers().contains("zhangxiaowei6")){
//                        String[] owner = memUsage.getMembers().toArray(new String[0]);
//                        feishuService.sendFeishu(content,owner,null,true);
//                 //   }
//                });
//            });
//
//        } catch(Exception e){
//            log.error("selectAllResourceUtilizationUnhealthy Error!{}",e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//        return Result.success("发送资源使用率低的飞书成功");
//    }

//    private String getAlarmCreateUrl() {
//        if (serverType.equals("dev") || serverType.equals("staging")) {
//            return STAGING_HERA_ALARM_CREATE;
//        } else if (serverType.equals("online")) {
//            return ONLINE_HERA_ALARM_CREATE;
//        } else {
//            return STAGING_HERA_ALARM_CREATE;
//        }
//    }

    public String getAppGitName(Integer appId, String appName) {

        return null;
//        if(appId == null){
//            return null;
//        }
//        String gitName = null;
//        List<ProjectInfo> appInfos = this.getAppsByName(appName);
//        List<ProjectInfo> appYpInfos = this.getAppsByNameYp(appName);
//        if(CollectionUtils.isEmpty(appInfos) && CollectionUtils.isEmpty(appYpInfos) ){
//            //Todo 后续改成按id查询的
//            log.error("getAppGitName no data found for app:{}",appName);
//        }else{
//
//            for(ProjectInfo appInfo : appInfos){
//                if(appInfo.getId().intValue() == appId.intValue()){
//                    gitName = appInfo.getGitName();
//                    break;
//                }
//            }
//
//            for(ProjectInfo appInfo : appYpInfos){
//                if(appInfo.getId().intValue() == appId.intValue()){
//                    gitName = appInfo.getGitName();
//                    break;
//                }
//            }
//        }
//
//        return gitName;
    }


    public Result getResourceUsageUrl(Integer appId, String appName) {

        StringBuffer buffer = new StringBuffer();
        buffer.append(resourceUseRateUrl);

        String appGitName = getAppGitName(appId, appName);
        if (StringUtils.isNotBlank(appGitName)) {
            appName = appGitName;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("sum(container_cpu_usage_seconds_total{system='mione'")
                .append(",name=~'").append(appName.trim()).append("-202.*").append("'")
                .append(",container_label_PROJECT_ID='").append(appId).append("'")
                .append("}) by (name)");
        Result<PageData> pageDataResult = prometheusService.queryByMetric(builder.toString());
        if (pageDataResult.getCode() != ErrorCode.success.getCode() || pageDataResult.getData() == null) {
            log.error("queryByMetric error! projectId :{},projectName:{}", appId, appName);
            return Result.success(resourceUseRateNoDataUrl);
        }


        List<Metric> list = (List<Metric>) pageDataResult.getData().getList();
        log.info("getContainerInstance param : appId:{}, projectName:{},result:{}", appId, appName, list);

        if (CollectionUtils.isEmpty(list)) {
            log.info("getContainerInstance no data found! param : appId:{}, projectName:{},result:{}", appId, appName, list);
            return Result.success(resourceUseRateNoDataUrl);
        }

//        Metric metric = list.stream().sorted((a1, a2) -> a2.getName().compareTo(a1.getName())).collect(Collectors.toList()).get(0);

        List<String> collect = list.stream().map(t -> t.getName()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return Result.success(resourceUseRateNoDataUrl);
        }
        for (String name : collect) {
            buffer.append("&var-name=").append(name);
        }


        return Result.success(buffer.toString());
    }

    public Result getResourceUsageUrlForK8s(Integer appId, String appName) {
        return appMonitorServiceExtension.getResourceUsageUrlForK8s(appId, appName);
    }

//    public Result getEnvByProjectId(Integer projectId,Integer appSource){
//        if(projectId == null || appSource == null){
//            log.error("getEnvByProjectId param error! projectId : {},appSource:{}",projectId,appSource);
//            return Result.fail(ErrorCode.invalidParamError);
//        }
//
//        GwdashApiService gwdashApiServiceExe = PlatFormType.china.getCode().equals(appSource) ? gwdashApiService : PlatFormType.youpin.getCode().equals(appSource) ? gwdashApiServiceYP : null;
//        if(gwdashApiServiceExe == null){
//            log.error("getEnvByProjectId appSource error! appSource value is : {}",appSource);
//            return Result.fail(ErrorCode.UNKNOWN_TYPE);
//        }
//
//        com.xiaomi.youpin.infra.rpc.Result<List<ProjectEnvBo>> envsRes = gwdashApiServiceExe.getProjectEnvListByProjectId(Long.valueOf(projectId));
//
//        if(envsRes == null || envsRes.getCode() != 0){
//            log.error("getEnvByProjectId error! result : {}",new Gson().toJson(envsRes));
//            return Result.fail(ErrorCode.unknownError);
//        }
//
//        return Result.success(envsRes.getData());
//
//    }

//    public Result initAppsByUsername(String userName){
//        if(StringUtils.isEmpty(userName)){
//            log.info("AppMonitorService.initAppsByUsername error!param userName is empty!");
//            return Result.fail(ErrorCode.invalidParamError);
//        }
//
//        try {
//
//            List<ProjectInfo> apps = this.getAppsByUserName(userName);
//            if (CollectionUtils.isEmpty(apps)) {
//                log.info("AppMonitorService.initAppsByUsername success! no init apps found! userName : {}", userName);
//                return Result.success(null);
//            }
//
//            for (ProjectInfo projectInfo : apps) {
//                AppMonitor appMonitor = new AppMonitor();
//                appMonitor.setProjectId(projectInfo.getId().intValue());
//                appMonitor.setProjectName(projectInfo.getName());
//                appMonitor.setOwner(userName);
//                Result<String> stringResult = this.create(appMonitor);
//                if (ErrorCode.success.getCode() == stringResult.getCode()) {
//                    log.info("AppMonitorService.initAppsByUsername success! app : {}", appMonitor.toString());
//                } else {
//                    log.error("AppMonitorService.initAppsByUsername failed! app : {}", appMonitor.toString());
//                }
//            }
//        } catch (Exception e) {
//            log.error("AppMonitorService.initAppsByUsername error : {}", e.getMessage());
//            return Result.fail(ErrorCode.unknownError);
//        }
//
//        return Result.success(null);
//
//    }

//    public List<ProjectInfo> getAppsByUserName(String username){
//       com.xiaomi.youpin.infra.rpc.Result<Map<String, Object>> result = gwdashApiService.getAppsByUserName(username,null,false,0,0);
//        log.info("AppMonitorService.getAppsByUsername param username : {},result : {}",username,new Gson().toJson(result));
//        if(result.getCode() != 0){
//            log.error("AppMonitorService.getAppsByUsername error! param username : {}, result : {}",username,new Gson().toJson(result));
//            return null;
//        }
//
//        Map<String, Object> data = result.getData();
//        if(CollectionUtils.isEmpty(data)){
//            log.info("AppMonitorService.getAppsByUsername no map data found param username : {}",username);
//            return new ArrayList<ProjectInfo>();
//        }
//
//        Integer total = (Integer) data.get("total");
//        log.info("AppMonitorService.getAppsByUsername username : {}, data total : {}",username,total);
//
//        List list = (List) data.get("list");
//
//        if(CollectionUtils.isEmpty(list)){
//            log.info("AppMonitorService.getAppsByUsername no data found param username : {}",username);
//            return new ArrayList<ProjectInfo>();
//        }
//
//        return Arrays.asList(new Gson().fromJson(new Gson().toJson(list),ProjectInfo[].class));
//    }

//    public List<ProjectInfo> getAppsByName(String appName){
//       com.xiaomi.youpin.infra.rpc.Result<Map<String, Object>> result = gwdashApiService.getAppsByUserName(null,appName,true,0,50);
//        log.info("AppMonitorService.getAppsByName param appName : {},result : {}",appName,new Gson().toJson(result));
//        if(result.getCode() != 0){
//            log.error("AppMonitorService.getAppsByName error! param appName : {}, result : {}",appName,new Gson().toJson(result));
//            return null;
//        }
//
//        Map<String, Object> data = result.getData();
//        if(CollectionUtils.isEmpty(data)){
//            log.info("AppMonitorService.getAppsByName no map data found param appName : {}",appName);
//            return new ArrayList<ProjectInfo>();
//        }
//
//        Integer total = (Integer) data.get("total");
//        log.info("AppMonitorService.getAppsByName appName : {}, data total : {}",appName,total);
//
//        List list = (List) data.get("list");
//
//        if(CollectionUtils.isEmpty(list)){
//            log.info("AppMonitorService.getAppsByName no data found param appName : {}",appName);
//            return new ArrayList<ProjectInfo>();
//        }
//
//        return Arrays.asList(new Gson().fromJson(new Gson().toJson(list),ProjectInfo[].class));
//    }
//
//    public List<ProjectInfo> getAppsByNameYp(String appName){
//       com.xiaomi.youpin.infra.rpc.Result<Map<String, Object>> result = gwdashApiServiceYP.getAppsByUserName(null,appName,true,0,50);
//        log.info("AppMonitorService.getAppsByName(youpin) param appName : {},result : {}",appName,new Gson().toJson(result));
//        if(result.getCode() != 0){
//            log.error("AppMonitorService.getAppsByName(youpin) error! param appName : {}, result : {}",appName,new Gson().toJson(result));
//            return null;
//        }
//
//        Map<String, Object> data = result.getData();
//        if(CollectionUtils.isEmpty(data)){
//            log.info("AppMonitorService.getAppsByName(youpin) no map data found param appName : {}",appName);
//            return new ArrayList<ProjectInfo>();
//        }
//
//        Integer total = (Integer) data.get("total");
//        log.info("AppMonitorService.getAppsByName(youpin) appName : {}, data total : {}",appName,total);
//
//        List list = (List) data.get("list");
//
//        if(CollectionUtils.isEmpty(list)){
//            log.info("AppMonitorService.getAppsByName(youpin) no data found param appName : {}",appName);
//            return new ArrayList<ProjectInfo>();
//        }
//
//        return Arrays.asList(new Gson().fromJson(new Gson().toJson(list),ProjectInfo[].class));
//    }

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
//            info.setIamTreeId(); //iamTreeId后续不再提供
            list.add(info);
        });

        pd.setList(list);

        //todo 旧逻辑对比
//        boolean isShowAll = StringUtils.isBlank(userName) ? true : false;
//
//        log.info("AppMonitorService.getProjectInfos param appName : {},page : {},pageSize : {}",appName,page,pageSize);
//        com.xiaomi.youpin.infra.rpc.Result<Map<String, Object>> result = gwdashApiService.getAppsByUserName(userName,appName,isShowAll,page,pageSize);
//        log.info("AppMonitorService.getProjectInfos param appName : {},result : {}",appName,new Gson().toJson(result));
//        if(result.getCode() != 0){
//            log.error("AppMonitorService.getProjectInfos error! param appName : {}, result : {}",appName,new Gson().toJson(result));
//            return null;
//        }
//
//        Map<String, Object> data = result.getData();
//        if(CollectionUtils.isEmpty(data)){
//            log.error("AppMonitorService.getProjectInfos no map data found param appName : {}",appName);
//            return Result.fail(ErrorCode.unknownError);
//        }
//
//        Integer total = (Integer) data.get("total");
//        log.info("AppMonitorService.getProjectInfos appName : {}, data total : {}",appName,total);
//        pd.setTotal(total.longValue());
//
//        List list = (List) data.get("list");
//
//        if(CollectionUtils.isEmpty(list)){
//            log.info("AppMonitorService.getProjectInfos no data found param appName : {}",appName);
//            pd.setList(new ArrayList<ProjectInfo>());
//        }else{
//            pd.setList(Arrays.asList(new Gson().fromJson(new Gson().toJson(list),ProjectInfo[].class)));
//        }


        return Result.success(pd);

    }

//    public Result<PageData> getYPProjectInfos(String userName,String appName,Integer page,Integer pageSize){
//
//        if(page == null){
//            page = 1;
//        }
//
//        if(pageSize == null){
//            pageSize = 10;
//        }
//
//        PageData pd = new PageData();
//        pd.setPage(page);
//        pd.setPageSize(pageSize);
//
//        boolean isShowAll = StringUtils.isBlank(userName) ? true : false;
//
//        log.info("AppMonitorService.getYPProjectInfos param appName : {},page : {},pageSize : {}",appName,page,pageSize);
//        com.xiaomi.youpin.infra.rpc.Result<Map<String, Object>> result = gwdashApiServiceYP.getAppsByUserName(userName,appName,isShowAll,page,pageSize);
//        log.info("AppMonitorService.getYPProjectInfos param appName : {},result : {}",appName,new Gson().toJson(result));
//        if(result.getCode() != 0){
//            log.error("AppMonitorService.getYPProjectInfos error! param appName : {}, result : {}",appName,new Gson().toJson(result));
//            return Result.success(pd);
//        }
//
//        Map<String, Object> data = result.getData();
//        if(CollectionUtils.isEmpty(data)){
//            log.error("AppMonitorService.getYPProjectInfos no map data found param appName : {}",appName);
//            return Result.fail(ErrorCode.unknownError);
//        }
//
//        Integer total = (Integer) data.get("total");
//        log.info("AppMonitorService.getYPProjectInfos appName : {}, data total : {}",appName,total);
//        pd.setTotal(total.longValue());
//
//        List list = (List) data.get("list");
//
//        if(CollectionUtils.isEmpty(list)){
//            log.info("AppMonitorService.getYPProjectInfos no data found param appName : {}",appName);
//            pd.setList(new ArrayList<ProjectInfo>());
//        }else{
//            pd.setList(Arrays.asList(new Gson().fromJson(new Gson().toJson(list),ProjectInfo[].class)));
//        }
//
//        return Result.success(pd);
//    }


    public Result<String> createWithBaseInfo(AppMonitorModel appMonitorModel, String user) {

        HeraAppBaseInfoModel heraAppBaseInfo = appMonitorModel.baseInfo();
        Integer baseInfoId = createBaseInfo(heraAppBaseInfo);
        if (baseInfoId == null) {
            log.error("createBaseInfo fail!heraAppBaseInfo:{}", heraAppBaseInfo);
            return Result.fail(ErrorCode.unknownError);
        }

        AppMonitor appMonitor = appMonitorModel.appMonitor();
        appMonitor.setBaseInfoId(baseInfoId);

        //TODO mione项目添加默认iamTreeId
        //需要兼容内网判断 appMonitor.getIamTreeId() == null 的逻辑
        //if (appMonitor.getIamTreeId() == null || appMonitor.getProjectId() == null || StringUtils.isBlank(appMonitor.getProjectName())) {

        if (appMonitor.getProjectId() == null || StringUtils.isBlank(appMonitor.getProjectName())) {
            log.error("AppMonitorController.createWithBaseInfo 用户{}添加项目{}，参数不合法", user, appMonitor);
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

        /**
         * todo 旧逻辑对比
         */
        //String area = PlatFormType.china.getGrafanaDirByCode(appMonitor.getAppSource());


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

//    public Result getTeslaAlarmHealthByUser(String user){
//        try {
//            Result<PageData> teslaGroupByUserName = this.getTeslaGroupByUserName(user);
//
//            List<TeslaAlarmHealthResult> list = new ArrayList<>();
//
//            if(teslaGroupByUserName == null || CollectionUtils.isEmpty((List)teslaGroupByUserName.getData().getList())){
//                return Result.success(list);
//            }
//            List<TeslaApiGroupInfo> myGroups = (List<TeslaApiGroupInfo>) teslaGroupByUserName.getData().getList();
//            for(TeslaApiGroupInfo groupInfo : myGroups){
//                TeslaAlarmHealthResult teslaHealth = new TeslaAlarmHealthResult();
//                teslaHealth.setGroupName(groupInfo.getName());
//                teslaHealth.setBaseUrl(groupInfo.getBaseUrl());
//                Integer alarmConfigNumByTeslaGroup = appAlarmService.getAlarmConfigNumByTeslaGroup(groupInfo.getBaseUrl());
//                teslaHealth.setAlarmNum(alarmConfigNumByTeslaGroup);
//                list.add(teslaHealth);
//            }
//
//            return Result.success(list);
//
//        } catch (Exception e) {
//            log.error("getTeslaAlarmHealthByUser error!{}",e.getMessage(),e);
//            return Result.fail(ErrorCode.unknownError);
//        }
//    }
//
//    public Result<PageData> getTeslaGroupByUserName(String username){
//
//        List<TeslaApiGroupInfo> listAll = null;
//        PageData pd = new PageData();
//
//        com.xiaomi.youpin.infra.rpc.Result<Map<String,Object>> result = iGatewayOpenApi.getApiGroupsByUserName(username,"1");
//        log.info("AppMonitorService.getTeslaGroupByUserName param username : {},result : {}",username,new Gson().toJson(result));
//        if(result.getCode() != 0){
//            log.error("AppMonitorService.getTeslaGroupByUserName error! param username : {}, result : {}",username,new Gson().toJson(result));
//            return Result.success(pd);
//        }
//
//        Map<String,Object> data = result.getData();
//        if(CollectionUtils.isEmpty(data)){
//            log.info("AppMonitorService.getTeslaGroupByUserName no data found param username : {}",username);
//            pd.setTotal(0l);
//            return Result.success(pd);
//        }
//
//        Object apiGroups = data.get("apiGroups");
//        List<Integer> myGids = ((List<String>) data.get("myGids")).stream().map(Integer::parseInt).collect(Collectors.toList());
//
//        listAll = Arrays.asList(new Gson().fromJson(new Gson().toJson(apiGroups), TeslaApiGroupInfo[].class));
//
//        List<TeslaApiGroupInfo> myGroups = listAll.stream().filter(gid -> myGids.contains(gid.getGid())).collect(Collectors.toList());
//
//
//        Map map = new HashMap();
//        map.put("allGroups",listAll);
//        pd.setSummary(map);
//
//        pd.setTotal(CollectionUtils.isEmpty(myGroups) ? 0l : Long.valueOf(myGroups.size()));
//        pd.setList(myGroups);
//        return Result.success(pd);
//
//    }

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
        Map<String, Object> map = new HashMap<>();
        map.put("dubboProviderOverview", grafanaDomain + dubboProviderOverview);
        map.put("dubboConsumerOverview", grafanaDomain + dubboConsumerOverview);
        map.put("dubboProviderMarket", grafanaDomain + dubboProviderMarket);
        map.put("dubboConsumerMarket", grafanaDomain + dubboConsumerMarket);
        map.put("httpOverview", grafanaDomain + httpOverview);
        map.put("httpMarket", grafanaDomain + httpMarket);
        try {
            log.info("grafanaInterfaceList map:{}", map);
            String data = FreeMarkerUtil.getContentExceptJson("/heraGrafanaTemplate", "grafanaInterfaceList.ftl", map);
            JsonArray jsonElements = gson.fromJson(data, JsonArray.class);
            log.info(jsonElements.toString());
            List<GrafanaInterfaceRes> resList = new ArrayList<>();
            jsonElements.forEach(it -> {
                GrafanaInterfaceRes grafanaInterfaceRes = gson.fromJson(it, GrafanaInterfaceRes.class);
                resList.add(grafanaInterfaceRes);
            });
            log.info("grafanaInterfaceList success! data:{}", resList);
            return Result.success(resList);
        } catch (Exception e) {
            log.error("grafanaInterfaceList error! {}", e);
            return Result.fail(ErrorCode.unknownError);
        }
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
}
