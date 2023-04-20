/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.bo.AlarmStrategyInfo;
import com.xiaomi.mone.monitor.bo.AlarmStrategyParam;
import com.xiaomi.mone.monitor.bo.AlarmStrategyType;
import com.xiaomi.mone.monitor.bo.AppViewType;
import com.xiaomi.mone.monitor.dao.AppAlarmRuleDao;
import com.xiaomi.mone.monitor.dao.AppAlarmRuleTemplateDao;
import com.xiaomi.mone.monitor.dao.AppAlarmStrategyDao;
import com.xiaomi.mone.monitor.dao.AppMonitorDao;
import com.xiaomi.mone.monitor.dao.model.AlarmStrategy;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.prometheus.AlarmRuleRequest;
import com.xiaomi.mone.monitor.service.prometheus.PrometheusService;
import com.xiaomi.mone.monitor.service.user.LocalUser;
import com.xiaomi.mone.monitor.service.user.MoneUserDetailService;
import com.xiaomi.mone.monitor.service.user.UseDetailInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author zhanggaofeng1
 */
@Slf4j
@Service
public class AlarmStrategyService {

    @Autowired
    private AppAlarmStrategyDao appAlarmStrategyDao;
    @Autowired
    private AppAlarmRuleDao appAlarmRuleDao;
    @Autowired
    private AppAlarmService appAlarmService;
    @Autowired
    private AppMonitorDao appMonitorDao;
    @Autowired
    private PrometheusService prometheusService;
    @Autowired
    private AppAlarmRuleTemplateDao appAlarmRuleTemplateDao;
    @Autowired
    private MoneUserDetailService moneUserDetailService;

    public AlarmStrategy getById(Integer id){
        return appAlarmStrategyDao.getById(id);
    }

    public boolean updateById(AlarmStrategy strategy){
        return appAlarmStrategyDao.updateById(strategy);
    }

    /**
     * 创建策略
     * @param param
     * @param app
     * @return
     */
    public AlarmStrategy create(AlarmRuleRequest param, AppMonitor app) {
        if(StringUtils.isBlank(param.getStrategyName()) || param.getStrategyType() == null) {
            throw new IllegalArgumentException("strategy name or type is null");
        }
        AlarmStrategy strategy = new AlarmStrategy();
        strategy.setCreater(param.getUser());
        strategy.setStrategyName(param.getStrategyName());
        strategy.setStrategyType(param.getStrategyType());
        strategy.setAppId(app.getProjectId());
        strategy.setIamId(app.getIamTreeId());
        strategy.setAppName(param.getStrategyType().equals(AlarmStrategyType.TESLA.getCode()) ? param.getAppAlias() : app.getProjectName());
        strategy.setDesc(param.getStrategyDesc());
        strategy.setStatus(0);
        if (StringUtils.isNotBlank(param.getAlertTeam())) {
            strategy.setAlertTeam(param.getAlertTeam());
        } else if (!CollectionUtils.isEmpty(param.getAlarmRules())) {
            strategy.setAlertTeam(param.getAlarmRules().get(0).getAlertTeam());
        }
        Map<Integer, UseDetailInfo.DeptDescr> deptMap = LocalUser.getDepts();
        if (deptMap.containsKey(3)) {
            strategy.setGroup3(deptMap.get(3).getDeptName());
        }
        if (deptMap.containsKey(4)) {
            strategy.setGroup4(deptMap.get(4).getDeptName());
        }
        if (deptMap.containsKey(5)) {
            strategy.setGroup5(deptMap.get(5).getDeptName());
        }

        JsonObject envs = new JsonObject();
        if(!CollectionUtils.isEmpty(param.getIncludeEnvs())){
            envs.addProperty("includeEnvs",String.join(",", param.getIncludeEnvs()));
        }
        if(!CollectionUtils.isEmpty(param.getExceptEnvs())){
            envs.addProperty("exceptEnvs",String.join(",", param.getExceptEnvs()));
        }

        if(!CollectionUtils.isEmpty(param.getIncludeServices())){
            envs.addProperty("includeServices",String.join(",", param.getIncludeServices()));
        }
        if(!CollectionUtils.isEmpty(param.getExceptEnvs())){
            envs.addProperty("exceptServices",String.join(",", param.getExceptServices()));
        }

        if(!CollectionUtils.isEmpty(param.getIncludeModules())){
            envs.addProperty("includeModules",String.join(",", param.getIncludeModules()));
        }
        if(!CollectionUtils.isEmpty(param.getExceptModules())){
            envs.addProperty("exceptModules",String.join(",", param.getExceptModules()));
        }

        if(!CollectionUtils.isEmpty(param.getIncludeFunctions())){
            envs.addProperty("includeFunctions",String.join(",", param.getIncludeFunctions()));
        }
        if(!CollectionUtils.isEmpty(param.getExceptFunctions())){
            envs.addProperty("exceptFunctions",String.join(",", param.getExceptFunctions()));
        }
        strategy.setEnvs(envs.toString());

        if(!CollectionUtils.isEmpty(param.getAlertMembers())){
            strategy.setAlertMembers(String.join(",", param.getAlertMembers()));
        }

        if(!CollectionUtils.isEmpty(param.getAtMembers())){
            strategy.setAtMembers(String.join(",", param.getAtMembers()));
        }


        if (!appAlarmStrategyDao.insert(strategy)) {
            return null;
        }

        log.info("插入规则策略成功：strategy={}",strategy);

        return strategy;
    }

    public Result<AlarmStrategy> updateByParam(AlarmRuleRequest param) {

        AlarmStrategy strategy = new AlarmStrategy();

        strategy.setId(param.getStrategyId());
        strategy.setStrategyName(param.getStrategyName());
        strategy.setDesc(param.getStrategyDesc());

        if (StringUtils.isNotBlank(param.getAlertTeam())) {
            strategy.setAlertTeam(param.getAlertTeam());
        }

        JsonObject envs = new JsonObject();
        if(!CollectionUtils.isEmpty(param.getIncludeEnvs())){
            envs.addProperty("includeEnvs",String.join(",", param.getIncludeEnvs()));
        }
        if(!CollectionUtils.isEmpty(param.getExceptEnvs())){
            envs.addProperty("exceptEnvs",String.join(",", param.getExceptEnvs()));
        }

        if(!CollectionUtils.isEmpty(param.getIncludeServices())){
            envs.addProperty("includeServices",String.join(",", param.getIncludeServices()));
        }
        if(!CollectionUtils.isEmpty(param.getExceptEnvs())){
            envs.addProperty("exceptServices",String.join(",", param.getExceptServices()));
        }

        if(!CollectionUtils.isEmpty(param.getIncludeModules())){
            envs.addProperty("includeModules",String.join(",", param.getIncludeModules()));
        }
        if(!CollectionUtils.isEmpty(param.getExceptModules())){
            envs.addProperty("exceptModules",String.join(",", param.getExceptModules()));
        }

        if(!CollectionUtils.isEmpty(param.getIncludeFunctions())){
            envs.addProperty("includeFunctions",String.join(",", param.getIncludeFunctions()));
        }
        if(!CollectionUtils.isEmpty(param.getExceptFunctions())){
            envs.addProperty("exceptFunctions",String.join(",", param.getExceptFunctions()));
        }

        strategy.setEnvs(envs.toString());

        strategy.setAlertMembers(String.join(",", param.getAlertMembers() == null ? new ArrayList<>() : param.getAlertMembers()));
        strategy.setAtMembers(String.join(",", param.getAtMembers() == null ? new ArrayList<>() : param.getAtMembers()));

        if (!appAlarmStrategyDao.updateById(strategy)) {
            return Result.fail(ErrorCode.unknownError);
        }

        log.info("插入规则策略成功：strategy={}",strategy);
        return Result.success(strategy);
    }

    public Result enabled(String user, AlarmStrategyParam param) {
        AlarmStrategy strategy = appAlarmStrategyDao.getById(param.getId());
        if (strategy == null) {
            return Result.fail(ErrorCode.nonExistentStrategy);
        }
        AppMonitor app = AlarmStrategyType.TESLA.getCode().equals(strategy.getStrategyType()) ? appMonitorDao.getByIamTreeId(strategy.getIamId()) : appMonitorDao.getMyApp(strategy.getAppId(),strategy.getIamId(), user, AppViewType.MyApp);
        if (app == null) {
            return Result.fail(ErrorCode.NoOperPermission);
        }
        Integer ruleStat = param.getStatus() == 0 ? 1 : 0;
        Result result = appAlarmService.enabledRules(strategy.getIamId(), strategy.getId(), ruleStat, user);
        if (result.getCode() != ErrorCode.success.getCode()) {
            return result;
        }
        strategy = new AlarmStrategy();
        strategy.setId(param.getId());
        strategy.setStatus(param.getStatus());
        if (!appAlarmStrategyDao.updateById(strategy)) {
            return Result.fail(ErrorCode.unknownError);
        }
        log.info("enabled规则策略成功：strategy={}",strategy);
        return Result.success(null);
    }


    public Result deleteById(String user, Integer strategyId) {
        if (strategyId == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        AlarmStrategy strategy = appAlarmStrategyDao.getById(strategyId);
        if (strategy == null) {
            return Result.fail(ErrorCode.nonExistentStrategy);
        }
        log.info("AlarmStrategyService.deleteById strategy : {}",new Gson().toJson(strategy));
        AppMonitor app = AlarmStrategyType.TESLA.getCode().equals(strategy.getStrategyType()) ? appMonitorDao.getByIamTreeId(strategy.getIamId()) : appMonitorDao.getMyApp(strategy.getAppId(),strategy.getIamId(), user, AppViewType.MyApp);
        if (app == null) {
            return Result.fail(ErrorCode.NoOperPermission);
        }
        Result result = appAlarmService.deleteRulesByIamId(strategy.getIamId() ,strategyId, user);
        if (result.getCode() != ErrorCode.success.getCode()) {
            return result;
        }
        if (!appAlarmStrategyDao.deleteById(strategyId)) {
            return Result.fail(ErrorCode.unknownError);
        }
        log.info("删除规则策略成功：strategy={}",strategy);
        return Result.success(null);
    }

    public Result deleteByStrategyId(String user, Integer strategyId) {
        if (strategyId == null) {
            return Result.fail(ErrorCode.invalidParamError);
        }
        AlarmStrategy strategy = appAlarmStrategyDao.getById(strategyId);
        if (strategy == null) {
            return Result.fail(ErrorCode.nonExistentStrategy);
        }
        log.info("AlarmStrategyService.deleteById strategy : {}",new Gson().toJson(strategy));

        Result result = appAlarmService.deleteRulesByIamId(strategy.getIamId() ,strategyId, user);
        if (result.getCode() != ErrorCode.success.getCode()) {
            return result;
        }
        if (!appAlarmStrategyDao.deleteById(strategyId)) {
            return Result.fail(ErrorCode.unknownError);
        }
        log.info("删除规则策略成功：strategy={}",strategy);
        return Result.success(null);
    }

    public Result<AlarmStrategyInfo> detailed(String user, AlarmStrategyParam param) {
        AlarmStrategyInfo info = appAlarmStrategyDao.getInfoById(param.getId());
        if (info != null) {
            setRuleListData(info);
        }
        return Result.success(info);
    }

    private void setRuleListData(AlarmStrategyInfo info) {
        if (info == null) {
            return;
        }
        List<AppAlarmRule> ruleList = appAlarmRuleDao.selectByStrategyId(info.getId());
        info.setAlarmRules(ruleList);
        if (StringUtils.isBlank(info.getAlertTeam()) && !CollectionUtils.isEmpty(ruleList)) {
            info.setAlertTeam(ruleList.get(0).getAlertTeam());
        }
    }

    public Result<PageData<List<AlarmStrategyInfo>>> search(String user, AlarmStrategyParam param) {
        AlarmStrategy strategy = new AlarmStrategy();
        strategy.setStrategyName(param.getStrategyName());
        strategy.setStrategyType(param.getStrategyType());
        strategy.setAppId(param.getAppId());
        strategy.setAppName(param.getAppName());
        strategy.setStatus(param.getStatus());
        Map<Integer, UseDetailInfo.DeptDescr> deptMap = LocalUser.getDepts();
        if (deptMap.containsKey(3)) {
            strategy.setGroup3(deptMap.get(3).getDeptName());
        }
        if (deptMap.containsKey(4)) {
            strategy.setGroup4(deptMap.get(4).getDeptName());
        }
        if (deptMap.containsKey(5)) {
            strategy.setGroup5(deptMap.get(5).getDeptName());
        }
        PageData<List<AlarmStrategyInfo>> pageData = appAlarmStrategyDao.searchByCond(user, param.isOwner(),strategy, param.getPage(), param.getPageSize(),param.getSortBy(),param.getSortOrder());
        ruleDataHandler(pageData.getList());
        return Result.success(pageData);
    }

    public Result<PageData> dubboSearch(String user, AlarmStrategyParam param) {
        StringBuilder metricStr = new StringBuilder();
        metricStr.append("sum(sum_over_time(staging_").append(param.getAppId()).append("_").append(param.getAppName())
                .append("_jaeger_dubboBisTotalCount_total{}[24h])) by (serviceName)");
        return  prometheusService.queryByMetric(metricStr.toString());
    }

    private void ruleDataHandler(List<AlarmStrategyInfo> infoList) {
        if (infoList == null || infoList.isEmpty()) {
            return;
        }
        Map<Integer, AlarmStrategyInfo> infoMap = listToMap(infoList);
        List<AppAlarmRule> ruleList = appAlarmRuleDao.selectByStrategyIdList(infoMap.keySet().stream().collect(Collectors.toList()));
        if (ruleList == null) {
            return;
        }
        AlarmStrategyInfo info = null;
        for (AppAlarmRule rule : ruleList) {
            if (rule.getStrategyId() == null) {
                continue;
            }
            info = infoMap.get(rule.getStrategyId());
            if (info == null) {
                continue;
            }
            if (info.getAlarmRules() == null) {
                info.setAlarmRules(new ArrayList<>());
            }
            info.getAlarmRules().add(rule);
            if (StringUtils.isBlank(info.getAlertTeam())) {
                info.setAlertTeam(rule.getAlertTeam());
            }
        }
    }

    private Map<Integer, AlarmStrategyInfo> listToMap(List<AlarmStrategyInfo> infoList) {
        if (CollectionUtils.isEmpty(infoList)) {
            return null;
        }
        Map<Integer, AlarmStrategyInfo> infoMap = new HashMap<>();
        for (AlarmStrategyInfo info : infoList) {
            if (infoMap.containsKey(info.getId()) && !info.isOwner()) {
                continue;
            }
            infoMap.put(info.getId(), info);
        }
        return infoMap;
    }

}
