package com.xiaomi.mone.monitor.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.AlarmCheckDataCount;
import com.xiaomi.mone.monitor.bo.AlarmRuleMetricType;
import com.xiaomi.mone.monitor.bo.AlarmRuleTemplateType;
import com.xiaomi.mone.monitor.bo.AlarmRuleType;
import com.xiaomi.mone.monitor.bo.AlarmSendInterval;
import com.xiaomi.mone.monitor.bo.AlarmStrategyType;
import com.xiaomi.mone.monitor.bo.AppViewType;
import com.xiaomi.mone.monitor.bo.RuleStatusType;
import com.xiaomi.mone.monitor.dao.AppAlarmRuleDao;
import com.xiaomi.mone.monitor.dao.AppAlarmRuleTemplateDao;
import com.xiaomi.mone.monitor.dao.AppMonitorDao;
import com.xiaomi.mone.monitor.dao.model.AlarmStrategy;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRuleTemplate;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.aop.context.HeraRequestMappingContext;
import com.xiaomi.mone.monitor.service.api.AlarmPresetMetricsService;
import com.xiaomi.mone.monitor.service.api.AppAlarmServiceExtension;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmRuleData;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmRuleRequest;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmRuleTemplateRequest;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmTemplateResponse;
import com.xiaomi.mone.monitor.service.model.prometheus.AppAlarmRuleTemplateQuery;
import com.xiaomi.mone.monitor.service.model.prometheus.AppWithAlarmRules;
import com.xiaomi.mone.monitor.service.prometheus.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gaoxihui
 */
@Slf4j
@Service
public class AppAlarmService {

    @Autowired
    AppAlarmRuleDao appAlarmRuleDao;

    @Autowired
    AppAlarmRuleTemplateDao appAlarmRuleTemplateDao;

    @Autowired
    AppMonitorService appMonitorService;

    @Autowired
    AlarmService alarmService;

    @Autowired
    private AppMonitorDao appMonitorDao;

    @Autowired
    private AlarmStrategyService alarmStrategyService;

    @Autowired
    private AlarmPresetMetricsService alarmPresetMetricsService;

    @Autowired
    private AppAlarmServiceExtension appAlarmServiceExtension;


    @NacosValue("${rule.evaluation.interval:20}")
    private Integer evaluationInterval;

    @NacosValue("${rule.evaluation.unit:s}")
    private String evaluationUnit;

    @Value("${prometheus.alarm.env:staging}")
    private String prometheusAlarmEnv;

    @Value("${server.type}")
    private String env;


    @Value("${alert.manager.env:staging}")
    private String alertManagerEnv;


    @Deprecated
    public void alarmRuleSwitchPlat(AppAlarmRule oldRule, Integer newProjectId, Integer newIamId, String oldProjectName, String newProjectName) {

        Result<JsonElement> alarmRuleRemote = alarmService.getAlarmRuleRemote(oldRule.getAlarmId(), oldRule.getProjectId(), oldRule.getCreater());
        if (!alarmRuleRemote.isSuccess()) {
            log.error("appPlatMove update get remote rule fail!oldRule:{},newProjectId:{},newIamId:{},newProjectName:{}", oldRule, newProjectId, newIamId, newProjectName);
            return;
        }
        JsonElement remoteRule = alarmRuleRemote.getData();

        if (remoteRule == null) {
            log.error("appPlatMove update no remote rule found!oldRule:{},newProjectId:{},newIamId:{},newProjectName:{}", oldRule, newProjectId, newIamId, newProjectName);
            return;
        }


        JsonObject asJsonObject = remoteRule.getAsJsonObject();

        /**
         * 表达式中的projectId、projectName替换
         */
        String expr = asJsonObject.get("expr").getAsString();

        String oldApplication = oldRule.getProjectId() + "_" + oldProjectName.replaceAll("-", "_");
        String newApplication = newProjectId + "_" + newProjectName.replaceAll("-", "_");
        String newExpr = expr.replace(oldApplication, newApplication);
        asJsonObject.remove("expr");
        asJsonObject.addProperty("expr", newExpr);


        /**
         * 替换iamId
         */
        asJsonObject.remove("tree_id");
        asJsonObject.addProperty("tree_id", newIamId);


        /**
         * labels内容替换
         */

        JsonObject labels = asJsonObject.getAsJsonObject("labels");
        labels.remove("project_id");
        labels.addProperty("project_id", newProjectId);
        labels.remove("project_name");
        labels.addProperty("project_name", newProjectName);
        labels.remove("app_iam_id");
        labels.addProperty("app_iam_id", newIamId);

        Result result = alarmService.updateAlarm(oldRule.getAlarmId(), oldRule.getIamId(), oldRule.getCreater(), asJsonObject.toString());

        log.info("alarmRuleSwitchPlat updateAlarm request body:{},response{}", asJsonObject.toString(), new Gson().toJson(result));
        if (result.isSuccess()) {

        }

    }

    public Result queryFunctionList(Integer projectId) {
        return appAlarmServiceExtension.queryFunctionList(projectId);
    }


    public Result queryRulesByAppName(String appName, String userName, Integer page, Integer pageSize) {

        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        PageData pd = new PageData();
        pd.setPage(page);
        pd.setPageSize(pageSize);

        Long aLong = appAlarmRuleDao.countAlarmRuleByAppName(userName, appName);
        pd.setTotal(aLong);

        List<AppWithAlarmRules> resultList = appAlarmRuleDao.queryRulesByAppName(userName, appName, page, pageSize);
        if (!CollectionUtils.isEmpty(resultList)) {
            for (AppWithAlarmRules appWithAlarmRule : resultList) {
                List<AppAlarmRule> alarmRules = appWithAlarmRule.getAlarmRules();

                appWithAlarmRule.setMetricMap(alarmPresetMetricsService.getEnumMap());
                appWithAlarmRule.setCheckDataMap(AlarmCheckDataCount.getEnumMap());
                appWithAlarmRule.setSendIntervalMap(AlarmSendInterval.getEnumMap());


                AppAlarmRule rule = new AppAlarmRule();
                rule.setIamId(appWithAlarmRule.getIamId());
                rule.setStatus(0);
                List<AppAlarmRule> rules = appAlarmRuleDao.query(rule, 0, Integer.MAX_VALUE);
                appWithAlarmRule.setAlarmRules(rules);
                if (!CollectionUtils.isEmpty(rules)) {
                    AppAlarmRule rule1 = rules.get(0);
                    appWithAlarmRule.setCreater(rule1.getCreater());
                    appWithAlarmRule.setLastUpdateTime(rule1.getUpdateTime());
                    appWithAlarmRule.setRuleStatus(rule1.getRuleStatus());
                }
            }

        }

//        Result<PageData<List<AppMonitor>>> appsResult = appMonitorService.listMyApp(appName, userName, page, pageSize);
//        if(appsResult.getData() == null || CollectionUtils.isEmpty(appsResult.getData().getList())){
//            pd.setTotal(0l);
//            return Result.success(pd);
//        }
//
//        pd.setTotal(appsResult.getData().getTotal());
//
//        List<AppMonitor> list = appsResult.getData().getList();
//
//        List<AppWithAlarmRules> resultList = new ArrayList<>();
//
//        for(AppMonitor app : list){
//
//            log.info("queryRulesByAppName : app id :{},appname:{},iamTreeId:{}",app.getId(),app.getProjectName(),app.getIamTreeId());
//            AppWithAlarmRules appAlarmRuleList = new AppWithAlarmRules();
//            appAlarmRuleList.setAppName(app.getProjectName());
//            appAlarmRuleList.setIamId(app.getIamTreeId());
//            appAlarmRuleList.setProjectId(app.getProjectId());
//
//            AppAlarmRule rule = new AppAlarmRule();
//            rule.setIamId(app.getIamTreeId());
//            rule.setStatus(0);
//            List<AppAlarmRule> rules = appAlarmRuleDao.query(rule, 0, Integer.MAX_VALUE);
//
//            appAlarmRuleList.setAlarmRules(rules);
//            if(!CollectionUtils.isEmpty(rules)){
//                AppAlarmRule rule1 = rules.get(0);
//                appAlarmRuleList.setCreater(rule1.getCreater());
//                appAlarmRuleList.setLastUpdateTime(rule1.getUpdateTime());
//                appAlarmRuleList.setRuleStatus(rule1.getRuleStatus());
//                appAlarmRuleList.setMetricMap(AlarmPresetMetrics.getEnumMap());
//                appAlarmRuleList.setCheckDataMap(AlarmCheckDataCount.getEnumMap());
//                appAlarmRuleList.setSendIntervalMap(AlarmSendInterval.getEnumMap());
//
//            }
//            resultList.add(appAlarmRuleList);
//        }

        pd.setList(resultList);

        return Result.success(pd);

    }

    public Result queryNoRulesConfig(String appName, String userName, Integer page, Integer pageSize) {

        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        PageData pd = new PageData();
        pd.setPage(page);
        pd.setPageSize(pageSize);

        Long aLong = appAlarmRuleDao.countAppNoAlarmRulesConfig(userName, appName);
        pd.setTotal(aLong);

        List<AppWithAlarmRules> resultList = appAlarmRuleDao.queryAppNoAlarmRulesConfig(userName, appName, page, pageSize);

        pd.setList(resultList);

        return Result.success(pd);

    }

    public Result queryRulesByIamId(Integer iamId, String userName){
        return appAlarmServiceExtension.queryRulesByIamId(iamId, userName);
    }

    public Integer getAlarmConfigNumByTeslaGroup(String group){
        AppAlarmRule rule = new AppAlarmRule();
        rule.setLabels(group);
        rule.setStatus(0);
        Long aLong = appAlarmRuleDao.countByExample(rule);
        return aLong == null ? 0 : aLong.intValue();
    }


//    public Result<PageData> queryRemoteRules(Integer iamId){
//
//        Map<String,String> labels = new HashMap();
//        labels.put("app_iam_id",String.valueOf(iamId));
//
//        Result<PageData> pageDataResult = alarmService.queryRuels(iamId, null, null, null, prometheusAlarmEnv, null, null, labels);
//        JsonElement data = (JsonElement) pageDataResult.getData().getList();
//        if(data != null){
//            AlarmRuleDataRemote[] list = new Gson().fromJson(data, AlarmRuleDataRemote[].class);
//            if(list.length == 0){
//                log.info("deleteRemoteRules no data found!iamId:{}",iamId);
//                return;
//            }
//        }
//    }

    public Result addRulesWithStrategy(AlarmRuleRequest param){

        /**
         * 校验当前操作人是否具有权限
         */
        AppMonitor app = null;
        if(param.getStrategyType().equals(AlarmStrategyType.TESLA.getCode())){
            app = appMonitorDao.getByIamTreeId(param.getIamId());
        }else{
            app = appMonitorDao.getMyApp(param.getProjectId(), param.getIamId(), param.getUser(), AppViewType.MyApp);
        }

        if (app == null) {
            log.error("不存在projectId={}的项目", param.getProjectId());
            return Result.fail(ErrorCode.NoOperPermission);
        }

        /**
         * 创建策略
         */
        AlarmStrategy strategy = alarmStrategyService.create(param,app);
        if (strategy == null) {
            log.error("规则策略创建失败; strategyResult={}", strategy);
            return Result.fail(ErrorCode.unknownError);
        }

        Integer strategyId = strategy.getId();
        HeraRequestMappingContext.set("strategyId", strategyId);

        param.setStrategyId(strategyId);

        return addRules(param,app);

    }


    public Result addRules(AlarmRuleRequest param,AppMonitor app){

        for(AlarmRuleData ruleData : param.getAlarmRules()){

            ruleData.setIncludeEnvs(param.getIncludeEnvs());
            ruleData.setExceptEnvs(param.getExceptEnvs());
            ruleData.setIncludeZones(param.getIncludeZones());
            ruleData.setExceptZones(param.getExceptZones());
            ruleData.setIncludeContainerName(param.getIncludeContainerName());
            ruleData.setExceptContainerName(param.getExceptContainerName());
            ruleData.setAlertMembers(param.getAlertMembers());
            ruleData.setAtMembers(param.getAtMembers());

            ruleData.setIncludeFunctions(param.getIncludeFunctions());
            ruleData.setExceptFunctions(param.getExceptFunctions());
            ruleData.setIncludeModules(param.getIncludeModules());
            ruleData.setExceptModules(param.getExceptModules());

            ruleData.convertLabels();
            AppAlarmRule rule = new AppAlarmRule();
            BeanUtils.copyProperties(ruleData,rule);
            StringBuilder cname = new StringBuilder();
            cname.append(param.getIamId() != null ? param.getIamId() : param.getProjectId());
            if (param.getStrategyType().intValue() == AlarmStrategyType.PAOMQL.getCode()) {
                if(rule.getMetricType() == null){
                    rule.setMetricType(AlarmRuleMetricType.customer_promql.getCode());
                }

                if(StringUtils.isBlank(ruleData.getAlert())){
                    cname.append("-").append(AlarmRuleMetricType.customer_promql.getDesc());
                }else{
                    cname.append("-").append(ruleData.getAlert());
                }

                cname.append("-").append(System.currentTimeMillis());
                rule.setCname(cname.toString());
                rule.setAlert(StringUtils.isBlank(rule.getAlert()) ? cname.toString() : rule.getAlert());
            } else {
                if(rule.getMetricType() == null){
                    rule.setMetricType(AlarmRuleMetricType.preset.getCode());
                }
                cname.append("-").append(ruleData.getAlert());
                cname.append("-").append(System.currentTimeMillis());
                rule.setCname(cname.toString());
            }

            rule.setRuleType(AlarmRuleType.app_config.getCode());
            rule.setProjectId(param.getProjectId());

            rule.setIamId(param.getIamId());
            int alarmForTime = evaluationInterval * ruleData.getDataCount();
            String alarmForTimeS = alarmForTime + evaluationUnit;
            rule.setForTime(alarmForTimeS);
            rule.setRuleGroup("group" + param.getIamId());
            rule.setEnv(prometheusAlarmEnv);
            rule.setStatus(0);
            rule.setCreater(param.getUser());
            rule.setRuleStatus(RuleStatusType.active.getCode());

            String remark = null;
            if(rule.getMetricType() == AlarmRuleMetricType.customer_promql.getCode()){
                remark = StringUtils.isBlank(rule.getRemark()) ? param.getStrategyDesc() : rule.getRemark();
            }else{
                remark = param.getStrategyDesc();
            }
            rule.setRemark(remark);

            rule.setCreateTime(new Date());
            rule.setUpdateTime(new Date());
            rule.setStrategyId(param.getStrategyId());

            Result result = alarmService.addRule(app, rule, param.getUser(),ruleData);
            if(result.getCode() != 0){
                log.error("AppAlarmService.addRules error! remote add ruleData fail!ruleData:{}",rule.toString());
                return Result.fail(ErrorCode.unknownError);
            }

            Integer alarmId = appAlarmServiceExtension.getAlarmIdByResult(result);
            rule.setAlarmId(alarmId);


            int i = appAlarmRuleDao.create(rule);
            if(i < 0){
                log.error("AppAlarmService.addRules error! add ruleData data fail!ruleData:{}",rule.toString());
                return Result.fail(ErrorCode.unknownError);
            }

        }
        return Result.success(null);

    }

    private static String getLabelJsonStr(AlarmRuleData ruleData){

        JsonObject labels = new JsonObject();
        labels.addProperty("includeMethods",ruleData.getIncludeMethods());
        labels.addProperty("exceptMethods",ruleData.getExceptMethods());
        labels.addProperty("includeErrorCodes",ruleData.getIncludeErrorCodes());
        labels.addProperty("exceptErrorCodes",ruleData.getExceptErrorCodes());
        labels.addProperty("includeDubboServices",ruleData.getIncludeDubboServices());
        labels.addProperty("exceptDubboServices",ruleData.getExceptDubboServices());
        labels.addProperty("teslaApiGroup",ruleData.getTeslaGroup());

        log.debug("labels : {}" , labels.toString());

        return labels.toString();

    }


    @Deprecated
    public Result editRules(List<AlarmRuleData> rules, AlarmRuleRequest param,String user, String userName){

        Result result = alarmStrategyService.deleteById(user, param.getStrategyId());
        if(result.getCode() != ErrorCode.success.getCode()){
            return result;
        }

        /**
         * 校验当前操作人是否具有权限
         */
        AppMonitor app = null;
        if(param.getStrategyType().equals(AlarmStrategyType.TESLA.getCode())){
            app = appMonitorDao.getByIamTreeId(param.getIamId());
        }else{
            app = appMonitorDao.getMyApp(param.getProjectId(), param.getIamId(), user, AppViewType.MyApp);
        }

        if (app == null) {
            log.error("不存在projectId={}的项目", param.getProjectId());
            return Result.fail(ErrorCode.NoOperPermission);
        }

        /**
         * 创建策略
         */
        AlarmStrategy strategy = alarmStrategyService.create(param,app);
        if (strategy == null) {
            log.error("规则策略创建失败; strategyResult={}", strategy);
            return Result.fail(ErrorCode.unknownError);
        }

        Integer strategyId = strategy.getId();
        HeraRequestMappingContext.set("strategyId", strategyId);

        param.setStrategyId(strategyId);

        return addRules(param,app);

    }

    public Result delAlarmRules(List<Integer> ids,String user){

        if(CollectionUtils.isEmpty(ids)){
            return Result.fail(ErrorCode.invalidParamError);
        }

        for(Integer id : ids){

            AppAlarmRule rule = appAlarmRuleDao.getById(id);
            if(rule == null){
                return Result.fail(ErrorCode.nonExistentAlarmRule);
            }

            Result result = alarmService.deleteRule(rule.getAlarmId(), rule.getIamId(), user);
            if(!result.isSuccess()){
                return Result.fail(ErrorCode.DeleteJobFail);
            }

            int i = appAlarmRuleDao.delById(id);
            if(i < 1){
                log.error("fail to delete rule in db,id:{}",id);
                return Result.fail(ErrorCode.FAIL_TO_DELETE_RULE_IN_DB);
            }
        }

        return Result.success();

    }

    public Result editAlarmRuleSingle(AlarmRuleData ruleData,String user){

        if(ruleData == null){
            log.error("editAlarmRuleSingle invalid ruleData : {} ",ruleData);
            return Result.fail(ErrorCode.invalidParamError);
        }

        /**
         * 检查策略是否存在
         */
        AlarmStrategy alarmStrategy = alarmStrategyService.getById(ruleData.getStrategyId());
        if(alarmStrategy == null){
            log.error("editAlarmRuleSingle strategy is not exist!ruleData:{}",ruleData);
            return Result.fail(ErrorCode.nonExistentStrategy);
        }


        /**
         * 校验当前操作人是否具有权限
         */
        AppMonitor app = null;
        if(alarmStrategy.getStrategyType().equals(AlarmStrategyType.TESLA.getCode())){
            app = appMonitorDao.getByIamTreeId(ruleData.getIamId());
        }else{
            app = appMonitorDao.getMyApp(ruleData.getProjectId(), ruleData.getIamId(), user, AppViewType.MyApp);
        }

        if (app == null) {
            log.error("不存在 owner 为 user : {}， projectId={}的项目", user,ruleData.getProjectId());
            return Result.fail(ErrorCode.NoOperPermission);
        }

        return editAlarmRule(ruleData,alarmStrategy,app,user);

    }

    public Result editRulesByStrategy(AlarmRuleRequest param){

        /**
         * 参数校验
         */
        List<AlarmRuleData> alarmRuleDatas = param.getAlarmRules();
        if(CollectionUtils.isEmpty(alarmRuleDatas)){
            log.error("editRules no rule data found!param:{}",param);
            return Result.fail(ErrorCode.invalidParamError);
        }

        /**
         * 校验当前操作人是否具有权限
         */
        AppMonitor app = null;
        if(param.getStrategyType().equals(AlarmStrategyType.TESLA.getCode())){
            app = appMonitorDao.getByIamTreeId(param.getIamId());
        }else{
            app = appMonitorDao.getMyApp(param.getProjectId(), param.getIamId(), param.getUser(), AppViewType.MyApp);
        }

        if (app == null) {
            log.error("不存在projectId={}的项目", param.getProjectId());
            return Result.fail(ErrorCode.NoOperPermission);
        }


        /**
         * 检查策略是否存在
         */
        AlarmStrategy alarmStrategy = alarmStrategyService.getById(param.getStrategyId());
        if(alarmStrategy == null){
            log.error("the strategy is not exist!param:{}",param.toString());
            return Result.fail(ErrorCode.nonExistentStrategy);
        }


        /**
         * 过滤参数中新增的规则（rule的id为null），进行创建
         */
        List<AlarmRuleData> addRules = alarmRuleDatas.stream().filter(t -> t.getId() == null).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(addRules)){

            //指定要添加的报警规则列表
            param.setAlarmRules(addRules);
            Result result1 = addRules(param, app);
            if(result1.getCode() != 0){
                return result1;
            }
        }


        /**
         * 检查报警组、通知人列表、选择的环境是否有更新，有更新则级联更新历史报警规则
         */
        if(!nullToEmpty(alarmStrategy.getAlertTeam()).equals(nullToEmpty(param.getAlertTeam()))
                ||!nullToEmpty(alarmStrategy.getAlertMembers()).equals(nullToEmpty(String.join(",", param.getAlertMembers())))
                ||!nullToEmpty(alarmStrategy.getAtMembers()).equals(nullToEmpty(String.join(",", param.getAtMembers() == null ? new ArrayList<>() : param.getAtMembers() )))
                ||!nullToEmpty(alarmStrategy.getEnvs()).equals(nullToEmpty(param.convertEnvs()))
                ||!nullToEmpty(alarmStrategy.getDesc()).equals(nullToEmpty(param.getStrategyDesc()))){

            alarmStrategy.setDesc(param.getStrategyDesc());

            List<AlarmRuleData> updateRules = alarmRuleDatas.stream().filter(t -> t.getId() != null).collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(updateRules)){

                AppMonitor appMonitor = app;

                Optional<Result> failResult = Optional.empty();

                updateRules.forEach(ruleData -> {

                    ruleData.setIncludeEnvs(param.getIncludeEnvs());
                    ruleData.setExceptEnvs(param.getExceptEnvs());
                    ruleData.setIncludeZones(param.getIncludeZones());
                    ruleData.setExceptZones(param.getExceptZones());
                    ruleData.setIncludeContainerName(param.getIncludeContainerName());
                    ruleData.setExceptContainerName(param.getExceptContainerName());
                    ruleData.setIncludeModules(param.getIncludeModules());
                    ruleData.setExceptModules(param.getExceptModules());
                    ruleData.setIncludeFunctions(param.getIncludeFunctions());
                    ruleData.setExceptFunctions(param.getExceptFunctions());

                    ruleData.setAlertMembers(param.getAlertMembers());
                    ruleData.setAtMembers(param.getAtMembers());

                    Result result = editAlarmRule(ruleData, alarmStrategy, appMonitor, param.getUser());
                    if(!result.isSuccess()){
                        failResult.orElse(result);
                        return;
                    }

                });

                if(failResult.isPresent()){
                    return failResult.get();
                }

            }

        }

        /**
         * 更新策略信息
         */
        Result<AlarmStrategy> strategyUpdateResult = alarmStrategyService.updateByParam(param);
        if(strategyUpdateResult.getCode() != ErrorCode.success.getCode()){
            log.error("update strategy in db fail!param:{}", param.toString());
            return Result.fail(ErrorCode.ALARM_STRATEGY_INFO_UPDATE_FAIL);
        }

        return Result.success(null);
    }

    public Result editAlarmRule(AlarmRuleData ruleData,AlarmStrategy alarmStrategy,AppMonitor app,String user){

        AppAlarmRule rule = appAlarmRuleDao.getById(ruleData.getId());
        if (rule == null) {
            log.info("edit alarm rule,no data found in db!ruleData:{}",ruleData);
            return Result.fail(ErrorCode.nonExistentAlarmRule);
        }

        ruleData.convertLabels();


        rule.setOp(ruleData.getOp());
        rule.setValue(ruleData.getValue());

        int alarmForTime = evaluationInterval * ruleData.getDataCount();
        String alarmForTimeS = alarmForTime + evaluationUnit;
        rule.setForTime(alarmForTimeS);
        rule.setDataCount(ruleData.getDataCount());

        rule.setPriority(ruleData.getPriority());
        rule.setSendInterval(ruleData.getSendInterval());

        rule.setAlertTeam(ruleData.getAlertTeam());

        rule.setLabels(ruleData.getLabels());

        /**
         * 兼容自定义报警单条填写描述信息
         */
        String remark = null;
        if(rule.getMetricType() == AlarmRuleMetricType.customer_promql.getCode()){
            remark = StringUtils.isNotBlank(ruleData.getRemark()) ? ruleData.getRemark(): alarmStrategy.getDesc();
        }else{
            remark = alarmStrategy.getDesc();
        }
        rule.setRemark(remark);

        rule.setUpdateTime(new Date());

        Result result = alarmService.editRule(rule,ruleData,app,user);

        if(result.getCode() != 0){
            log.error("AppAlarmService.editRules error! remote add ruleData fail!ruleData:{}",rule.toString());
            return Result.fail(ErrorCode.UpdateJobFail);
        }

        log.info("appAlarmRuleDao.updateByIdSelective rule:{}",new Gson().toJson(rule));
        int i = appAlarmRuleDao.updateByIdSelective(rule);
        if(i < 1){
            log.error("AppAlarmService.editRules save db fail!ruleData:{}",rule.toString());
            return Result.fail(ErrorCode.ALARM_RULE_INFO_UPDATE_FAIL);
        }

        return Result.success("");
    }

    private String nullToEmpty(String s){
        if(s == null){
            return "";
        }
        return s;
    }

    public Result deleteRulesByIamId(Integer iamId, Integer strategyId, String user){
        AppAlarmRule rulequery = new AppAlarmRule();
        rulequery.setStatus(0);
        if(iamId != null){
            rulequery.setIamId(iamId);
        }
        rulequery.setStrategyId(strategyId);
        List<AppAlarmRule> delRules = appAlarmRuleDao.query(rulequery, 0, Integer.MAX_VALUE);
        if(CollectionUtils.isEmpty(delRules)){
            log.info("AppAlarmService.deleteRulesByIamId no data found! iamId : {},user:{}",iamId,user);
            return Result.success(null);
        }

        /**
         * 同时删除远程接口的数据，和本地数据
         */
        for(AppAlarmRule rule : delRules){
            Result result = alarmService.deleteRule(rule.getAlarmId(), iamId, user);
            if(result.getCode()==0){
                int delete = appAlarmRuleDao.delete(rule);
                if(delete < 1){
                    log.error("AppAlarmService.deleteRulesByIamId delete local database fail!iamId : {},user:{}",iamId,user);
                    return Result.fail(ErrorCode.unknownError);
                }
            }else{

                if(result.getCode() == 404){
                    log.error("AppAlarmService.deleteRulesByIamId delete remote data,no data found!iamId : {},user:{}",iamId,user);

                    int delete = appAlarmRuleDao.delete(rule);
                    if(delete < 1){
                        log.error("AppAlarmService.deleteRulesByIamId delete local database fail!iamId : {},user:{}",iamId,user);
                        return Result.fail(ErrorCode.unknownError);
                    }
                    Result.success("");
                }
                log.error("AppAlarmService.deleteRulesByIamId delete remote data fail!iamId : {},user:{}",iamId,user);
                Result.fail(ErrorCode.unknownError);
            }
        }

        return Result.success("");
    }


    public Result enabledRules(Integer iamId, Integer strategyId, Integer pauseStatus,String user){


        AppAlarmRule rulequery = new AppAlarmRule();
        rulequery.setStatus(0);
        if(iamId != null){
            rulequery.setIamId(iamId);
        }
        rulequery.setStrategyId(strategyId);
        List<AppAlarmRule> delRules = appAlarmRuleDao.query(rulequery, 0, Integer.MAX_VALUE);
        if(CollectionUtils.isEmpty(delRules)){
            log.info("AppAlarmService.enabledRules no data found! iamId : {},user:{}",iamId,user);
            return Result.success(null);
        }

        /**
         * 同时暂停远程接口的数据，和本地数据
         */
        for(AppAlarmRule rule : delRules){
            Result result = alarmService.enabledRule(rule.getAlarmId(),pauseStatus,iamId, user);
            if(result.getCode()==0){
                AppAlarmRule condition = new AppAlarmRule();
                condition.setIamId(rule.getIamId());
                condition.setStatus(0);
                AppAlarmRule value = new AppAlarmRule();
                value.setRuleStatus(pauseStatus);
                try {
                    int update = appAlarmRuleDao.update(condition, value);
                    if(update < 1){
                        log.info("AppAlarmService.enabledRules update data failed!");
                        return Result.fail(ErrorCode.unknownError);
                    }
                } catch (Exception e) {
                    log.error("AppAlarmService.enabledRules error!{}",e.getMessage(),e);
                    return Result.fail(ErrorCode.unknownError);
                }
            }else{
                log.error("AppAlarmService.enabledRules update remote data fail!iamId : {},user:{}",iamId,user);
                Result.fail(ErrorCode.unknownError);
            }
        }


        return Result.success("");
    }

    public Result queryTemplate(AppAlarmRuleTemplateQuery query){
        if(StringUtils.isNotBlank(query.getCreater())){
            query.setCreater(query.getCreater());
        }

        Integer page = query.getPage();
        if(page == null || page.intValue() ==0){
            page = 1;
        }

        Integer pageSize = query.getPageSize();
        if(pageSize == null){
            pageSize = 10;
        }

        Integer start = (page-1) * pageSize;
        Long dataTotal = appAlarmRuleTemplateDao.getDataTotal(query);

        PageData pd = new PageData();
        pd.setPage(page);
        pd.setPageSize(pageSize);
        pd.setTotal(dataTotal);

        List<AlarmTemplateResponse> resultList = new ArrayList<>();

        /**
         * 模版查询
         */
        List<AppAlarmRuleTemplate> list = appAlarmRuleTemplateDao.query(query, start, pageSize);
        if(CollectionUtils.isEmpty(list)){
            pd.setList(resultList);
        }

        /**
         * 遍历附加规则
         */
        AppAlarmRule rule = new AppAlarmRule();
        rule.setRuleType(AlarmRuleType.template.getCode());
        rule.setStatus(0);
        for(AppAlarmRuleTemplate template : list){
            rule.setTemplateId(template.getId());
            List<AppAlarmRule> rules = appAlarmRuleDao.query(rule, 0, Integer.MAX_VALUE);

            AlarmTemplateResponse response = new AlarmTemplateResponse();
            response.setTemplate(template);
            response.setAlarmRules(rules);
            resultList.add(response);
        }

        pd.setList(resultList);

        return Result.success(pd);
    }

    public Result getTemplateById(Integer id){


        /**
         * 模版查询
         */
        AppAlarmRuleTemplate template = appAlarmRuleTemplateDao.getById(id);
        if(template == null){
            log.info("getTemplateById no data found id : {}",id);
            return Result.success(null);
        }

        /**
         * 遍历附加规则
         */
        AppAlarmRule rule = new AppAlarmRule();
        rule.setRuleType(AlarmRuleType.template.getCode());
        rule.setStatus(0);
        rule.setTemplateId(template.getId());
        List<AppAlarmRule> rules = appAlarmRuleDao.query(rule, 0, Integer.MAX_VALUE);

        AlarmTemplateResponse response = new AlarmTemplateResponse();
        response.setTemplate(template);
        response.setAlarmRules(rules);
        return Result.success(response);
    }

    public Result getTemplateByCreater(String user){

        AppAlarmRuleTemplateQuery query = new AppAlarmRuleTemplateQuery();
        query.setCreater(user);
        List<AppAlarmRuleTemplate> list = appAlarmRuleTemplateDao.query(query, 0, Integer.MAX_VALUE);
        return Result.success(list);
    }

    public Result addTemplate(AlarmRuleTemplateRequest request,String user){

        AppAlarmRuleTemplate alarmRuleTemplate = request.getTemplate();
        alarmRuleTemplate.setCreater(user);
        alarmRuleTemplate.setType(AlarmRuleTemplateType.user.getCode());
        int i = appAlarmRuleTemplateDao.create(alarmRuleTemplate);
        if(i < 1){
            log.error("AppAlarmService.addTemplate,insert template data failed!");
            return Result.fail(ErrorCode.unknownError);
        }

        List<AppAlarmRule> alarmRules = request.getAlarmRules();

        boolean b = saveTemplateRules(alarmRules, alarmRuleTemplate.getId(), user);
        if(!b){
            return Result.fail(ErrorCode.unknownError);
        }

        return Result.success("");

    }

    public Result editTemplate(AlarmRuleTemplateRequest request,String user){

        AppAlarmRuleTemplate alarmRuleTemplate = request.getTemplate();

        AppAlarmRuleTemplate update = new AppAlarmRuleTemplate();
        update.setId(alarmRuleTemplate.getId());
        update.setRemark(alarmRuleTemplate.getRemark());
        update.setName(alarmRuleTemplate.getName());
        update.setStrategyType(alarmRuleTemplate.getStrategyType());
        int i = appAlarmRuleTemplateDao.update(update);
        if(i < 1){
            log.error("AppAlarmService.editTemplate,update template data failed!");
            return Result.fail(ErrorCode.unknownError);
        }


        AppAlarmRule rule = new AppAlarmRule();
        rule.setTemplateId(alarmRuleTemplate.getId());
        rule.setRuleType(0);
        appAlarmRuleDao.delete(rule);

        List<AppAlarmRule> alarmRules = request.getAlarmRules();

        boolean b = saveTemplateRules(alarmRules, alarmRuleTemplate.getId(), user);
        if(!b){
            return Result.fail(ErrorCode.unknownError);
        }

        return Result.success("");

    }

    public Result deleteTemplate(Integer templateId){

        int i = appAlarmRuleTemplateDao.deleteById(templateId);
        if(i < 1){
            log.error("AppAlarmService.deleteTemplate,delete template data failed!");
            return Result.fail(ErrorCode.unknownError);
        }

        AppAlarmRule rule = new AppAlarmRule();
        rule.setTemplateId(templateId);
        rule.setRuleType(0);
        int delete = appAlarmRuleDao.delete(rule);
        if(delete < 1){
            log.info("AppAlarmService.deleteTemplate,delete template rules no data found!");
        }

        return Result.success("");

    }

    /**
     * 保存模版规则
     * @param alarmRules
     * @param templateId
     * @return
     */
    private boolean saveTemplateRules(List<AppAlarmRule> alarmRules,Integer templateId,String user){

        /**
         * 创建新的关联规则
         */
        if(CollectionUtils.isEmpty(alarmRules)){
            log.info("AppAlarmService.saveTemplateRules,alarmRules is empty!templateId : {}",templateId);
            return true;
        }
        Iterator<AppAlarmRule> iterator = alarmRules.iterator();
        while (iterator.hasNext()){
            AppAlarmRule next = iterator.next();
            next.setCreater(user);

            next.setTemplateId(templateId);
            //没有和任何应用关联配置，单纯隶属于一个模版的模版规则
            next.setRuleType(AlarmRuleType.template.getCode());
            //因为是模版，模版的数据不关联任何应用，默认不生效
            next.setRuleStatus(0);
            next.setForTime(next.getDataCount() * 30 + "s");
            next.setStatus(0);
            next.setCreateTime(new Date());
            next.setUpdateTime(new Date());
        }

        int i1 = appAlarmRuleDao.batchInsert(alarmRules);
        if (i1 < 1) {
            log.error("AppAlarmService.saveTemplateRules,insert rules data failed! templateId:{},alarmRules:{}",
                    templateId,alarmRules);
            return false;
        }
        return true;
    }


}
