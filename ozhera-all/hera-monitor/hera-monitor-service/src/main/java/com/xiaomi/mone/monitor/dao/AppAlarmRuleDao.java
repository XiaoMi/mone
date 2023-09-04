/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.mone.monitor.dao;

import com.google.common.collect.Lists;
import com.xiaomi.mone.monitor.dao.mapper.AppAlarmRuleMapper;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRuleExample;
import com.xiaomi.mone.monitor.dao.model.AppMonitorExample;
import com.xiaomi.mone.monitor.service.model.prometheus.AppWithAlarmRules;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class AppAlarmRuleDao {


    @Autowired
    private AppAlarmRuleMapper appAlarmRuleMapper;

    public List<AppWithAlarmRules> queryRulesByAppName(String userName, String appName, Integer page, Integer pageSize){
        if(page == null || page.intValue() == 0){
            page = 1;
        }

        if(pageSize == null || pageSize.intValue() == 0){
            pageSize = 10;
        }

        return  appAlarmRuleMapper.selectAlarmRuleByAppName(userName,appName, (page - 1) * pageSize, pageSize);
    }
    public Long countAlarmRuleByAppName(String userName,String appName){

        return  appAlarmRuleMapper.countAlarmRuleByAppName(userName,appName);
    }

    public List<AppWithAlarmRules> queryAppNoAlarmRulesConfig(String userName, String appName, Integer page, Integer pageSize){
        if(page == null || page.intValue() == 0){
            page = 1;
        }

        if(pageSize == null || pageSize.intValue() == 0){
            pageSize = 10;
        }

        return  appAlarmRuleMapper.selectAppNoRulesConfig(userName,appName, (page - 1) * pageSize, pageSize);
    }
    public Long countAppNoAlarmRulesConfig(String userName,String appName){

        return  appAlarmRuleMapper.countAppNoRulesConfig(userName,appName);
    }

    public Long getDataTotal(){
        AppAlarmRuleExample example = new AppAlarmRuleExample();
        AppAlarmRuleExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);

        return appAlarmRuleMapper.countByExample(example);
    }

    public List<AppAlarmRule> queryTeslaRules(){
        AppAlarmRuleExample example = new AppAlarmRuleExample();
        AppAlarmRuleExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);
        ca.andAlarmIdIsNotNull();
        ca.andAlertIn(Lists.newArrayList("china_intranet_tesla_p99_time_cost","tesla_intranet_availability","tesla_outnet_availability"));
        example.setOffset(0);
        example.setLimit(1000);
        return appAlarmRuleMapper.selectByExampleWithBLOBs(example);
    }


    public List<AppAlarmRule> query(AppAlarmRule appAlarmRule,Integer offset, Integer pageSize){
        AppAlarmRuleExample example = new AppAlarmRuleExample();
        AppAlarmRuleExample.Criteria ca = example.createCriteria();

        if(appAlarmRule.getTemplateId() != null){
            ca.andTemplateIdEqualTo(appAlarmRule.getTemplateId());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getCname())){
            ca.andCnameLike("%" + appAlarmRule.getCname() + "%");
        }

        if(appAlarmRule.getMetricType() != null){
            ca.andMetricTypeEqualTo(appAlarmRule.getMetricType());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getAlert())){
            ca.andAlertLike("%" + appAlarmRule.getAlert() + "%");
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getPriority())){
            ca.andPriorityEqualTo(appAlarmRule.getPriority());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getEnv())){
            ca.andEnvEqualTo(appAlarmRule.getEnv());
        }

        if(appAlarmRule.getIamId()!= null){
            ca.andIamIdEqualTo(appAlarmRule.getIamId());
        }

        if(appAlarmRule.getStrategyId() != null){
            ca.andStrategyIdEqualTo(appAlarmRule.getStrategyId());
        }

        if(appAlarmRule.getRuleType()!= null){
            ca.andRuleTypeEqualTo(appAlarmRule.getRuleType());
        }

        if(appAlarmRule.getRuleStatus()!= null){
            ca.andRuleStatusEqualTo(appAlarmRule.getRuleStatus());
        }

        if(appAlarmRule.getStatus()!= null){
            ca.andStatusEqualTo(appAlarmRule.getStatus());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getLabels())){
            ca.andLabelsLike("%" +appAlarmRule.getLabels() + "%");
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getRemark())){
            ca.andRemarkLike("%" +appAlarmRule.getRemark() + "%");
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getCreater())){
            ca.andCreaterLike("%" +appAlarmRule.getCreater() + "%");
        }

        example.setOffset(offset);
        example.setLimit(pageSize);
        example.setOrderByClause("id asc");
        return appAlarmRuleMapper.selectByExampleWithBLOBs(example);
    }

    public Long countByExample(AppAlarmRule appAlarmRule){
        AppAlarmRuleExample example = new AppAlarmRuleExample();
        AppAlarmRuleExample.Criteria ca = example.createCriteria();

        if(appAlarmRule.getTemplateId() != null){
            ca.andTemplateIdEqualTo(appAlarmRule.getTemplateId());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getCname())){
            ca.andCnameLike("%" + appAlarmRule.getCname() + "%");
        }

        if(appAlarmRule.getMetricType() != null){
            ca.andMetricTypeEqualTo(appAlarmRule.getMetricType());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getAlert())){
            ca.andAlertLike("%" + appAlarmRule.getAlert() + "%");
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getPriority())){
            ca.andPriorityEqualTo(appAlarmRule.getPriority());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getEnv())){
            ca.andEnvEqualTo(appAlarmRule.getEnv());
        }

        if(appAlarmRule.getIamId()!= null){
            ca.andIamIdEqualTo(appAlarmRule.getIamId());
        }

        if(appAlarmRule.getStrategyId() != null){
            ca.andStrategyIdEqualTo(appAlarmRule.getStrategyId());
        }

        if(appAlarmRule.getRuleType()!= null){
            ca.andRuleTypeEqualTo(appAlarmRule.getRuleType());
        }

        if(appAlarmRule.getRuleStatus()!= null){
            ca.andRuleStatusEqualTo(appAlarmRule.getRuleStatus());
        }

        if(appAlarmRule.getStatus()!= null){
            ca.andStatusEqualTo(appAlarmRule.getStatus());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getLabels())){
            ca.andLabelsLike("%" +appAlarmRule.getLabels() + "%");
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getRemark())){
            ca.andRemarkLike("%" +appAlarmRule.getRemark() + "%");
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getCreater())){
            ca.andCreaterLike("%" +appAlarmRule.getCreater() + "%");
        }

        return appAlarmRuleMapper.countByExample(example);
    }

    public int updateByIdSelective(AppAlarmRule rule){
        return appAlarmRuleMapper.updateByPrimaryKeySelective(rule);
    }

    public int update(AppAlarmRule condition,AppAlarmRule value){
        AppAlarmRuleExample example = new AppAlarmRuleExample();
        AppAlarmRuleExample.Criteria ca = example.createCriteria();

        if(condition.getTemplateId() != null){
            ca.andTemplateIdEqualTo(condition.getTemplateId());
        }

        if(StringUtils.isNoneBlank(condition.getCname())){
            ca.andCnameLike(condition.getCname());
        }

        if(condition.getMetricType() != null){
            ca.andMetricTypeEqualTo(condition.getMetricType());
        }

        if(StringUtils.isNoneBlank(condition.getAlert())){
            ca.andAlertLike(condition.getAlert());
        }

        if(StringUtils.isNoneBlank(condition.getPriority())){
            ca.andPriorityEqualTo(condition.getPriority());
        }

        if(StringUtils.isNoneBlank(condition.getEnv())){
            ca.andEnvEqualTo(condition.getEnv());
        }

        if(condition.getIamId()!= null){
            ca.andIamIdEqualTo(condition.getIamId());
        }

        if(condition.getRuleType()!= null){
            ca.andRuleTypeEqualTo(condition.getRuleType());
        }

        if(condition.getRuleStatus()!= null){
            ca.andRuleStatusEqualTo(condition.getRuleStatus());
        }

        if(condition.getStatus()!= null){
            ca.andStatusEqualTo(condition.getStatus());
        }

        if(StringUtils.isNoneBlank(condition.getRemark())){
            ca.andRemarkLike(condition.getRemark());
        }

        if(StringUtils.isNoneBlank(condition.getCreater())){
            ca.andCreaterLike(condition.getCreater());
        }

        value.setUpdateTime(new Date());

        return appAlarmRuleMapper.updateByExampleSelective(value,example);
    }

    public AppAlarmRule getById(Integer id){
        return appAlarmRuleMapper.selectByPrimaryKey(id);
    }

    public List<AppAlarmRule> selectByStrategyIdList(List<Integer> strategyIds){
        return appAlarmRuleMapper.selectByStrategyIdList(strategyIds);
    }

    public List<AppAlarmRule> selectByStrategyId(Integer strategyId){
        return appAlarmRuleMapper.selectByStrategyId(strategyId);
    }

    public int create(AppAlarmRule appAlarmRule) {
        if (null == appAlarmRule) {
            log.error("[AppAlarmRuleDao.create] null appAlarmRule");
            return 0;
        }

        appAlarmRule.setCreateTime(new Date());
        appAlarmRule.setUpdateTime(new Date());
        appAlarmRule.setStatus(0);

        try {
            int affected = appAlarmRuleMapper.insert(appAlarmRule);
            if (affected < 1) {
                log.warn("[AppAlarmRuleDao.create] failed to insert appAlarmRule: {}", appAlarmRule.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppAlarmRuleDao.create] failed to insert appMonitor: {}, err: {}", appAlarmRule.toString(), e);
            return 0;
        }
        return 1;
    }

    public int batchInsert(List<AppAlarmRule> list) {

        try {
            int affected = appAlarmRuleMapper.batchInsert(list);
            if (affected < 1) {
                log.warn("[AppAlarmRuleDao.batchInsert] failed to insert ruleList: {}", list);
                return 0;
            }
            return affected;
        } catch (Exception e) {
            log.error("[AppAlarmRuleDao.create] failed to insert ruleList: {}", list, e);
            return 0;
        }
    }

    public int delete(AppAlarmRule appAlarmRule){
        AppAlarmRuleExample example = new AppAlarmRuleExample();
        AppAlarmRuleExample.Criteria ca = example.createCriteria();

        if(appAlarmRule.getTemplateId() != null){
            ca.andTemplateIdEqualTo(appAlarmRule.getTemplateId());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getCname())){
            ca.andCnameLike(appAlarmRule.getCname());
        }

        if(appAlarmRule.getMetricType() != null){
            ca.andMetricTypeEqualTo(appAlarmRule.getMetricType());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getAlert())){
            ca.andAlertLike(appAlarmRule.getAlert());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getPriority())){
            ca.andPriorityEqualTo(appAlarmRule.getPriority());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getEnv())){
            ca.andEnvEqualTo(appAlarmRule.getEnv());
        }

        if(appAlarmRule.getIamId()!= null){
            ca.andIamIdEqualTo(appAlarmRule.getIamId());
        }

        if(appAlarmRule.getRuleType()!= null){
            ca.andRuleTypeEqualTo(appAlarmRule.getRuleType());
        }

        if(appAlarmRule.getRuleStatus()!= null){
            ca.andRuleStatusEqualTo(appAlarmRule.getRuleStatus());
        }

        if(appAlarmRule.getStatus()!= null){
            ca.andStatusEqualTo(appAlarmRule.getStatus());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getRemark())){
            ca.andRemarkLike(appAlarmRule.getRemark());
        }

        if(StringUtils.isNoneBlank(appAlarmRule.getCreater())){
            ca.andCreaterLike(appAlarmRule.getCreater());
        }
        if(appAlarmRule.getStrategyId() != null){
            ca.andStrategyIdEqualTo(appAlarmRule.getStrategyId());
        }
        try {
            return appAlarmRuleMapper.deleteByExample(example);
        } catch (Exception e) {
            log.error("AppAlarmRuleDao.delete error!message : {}",e.getMessage(),e);
            return 0;
        }
    }

    public int delById(Integer id){
        try {
            return appAlarmRuleMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            log.error("AppAlarmRuleDao.delById error!id : {}",id,e);
            return 0;
        }
    }



}
