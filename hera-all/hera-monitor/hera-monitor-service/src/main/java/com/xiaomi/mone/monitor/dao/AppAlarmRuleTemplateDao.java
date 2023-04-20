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

import com.xiaomi.mone.monitor.bo.AlarmStrategyType;
import com.xiaomi.mone.monitor.dao.mapper.AppAlarmRuleTemplateMapper;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRuleTemplate;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRuleTemplateExample;
import com.xiaomi.mone.monitor.service.model.prometheus.AppAlarmRuleTemplateQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class AppAlarmRuleTemplateDao {


    @Autowired
    private AppAlarmRuleTemplateMapper templateMapper;

    public Long getDataTotal(AppAlarmRuleTemplateQuery appAlarmRuleTemplate){
        AppAlarmRuleTemplateExample example = new AppAlarmRuleTemplateExample();
        AppAlarmRuleTemplateExample.Criteria ca = example.createCriteria();

        if(appAlarmRuleTemplate.getStatus() != null){
            ca.andStatusEqualTo(appAlarmRuleTemplate.getStatus());
        }

        if(StringUtils.isNotBlank(appAlarmRuleTemplate.getRemark())){
            ca.andRemarkLike("%" + appAlarmRuleTemplate.getRemark() + "%");
        }

        if(StringUtils.isNotBlank(appAlarmRuleTemplate.getName())){
            ca.andNameLike("%" + appAlarmRuleTemplate.getName() + "%");
        }

        if(StringUtils.isNotBlank(appAlarmRuleTemplate.getCreater())){
            ca.andCreaterEqualTo(appAlarmRuleTemplate.getCreater());
        }

        if(appAlarmRuleTemplate.getType() != null){
            ca.andTypeEqualTo(appAlarmRuleTemplate.getType());
        }

        if(appAlarmRuleTemplate.getStrategyType() != null){
            ca.andStrategyTypeEqualTo(appAlarmRuleTemplate.getStrategyType());
        }

        return templateMapper.countByExample(example);
    }


    public List<AppAlarmRuleTemplate> query(AppAlarmRuleTemplateQuery appAlarmRuleTemplate,Integer offset, Integer pageSize){
        AppAlarmRuleTemplateExample example = new AppAlarmRuleTemplateExample();
        AppAlarmRuleTemplateExample.Criteria ca = example.createCriteria();

        if(appAlarmRuleTemplate.getStatus() != null){
            ca.andStatusEqualTo(appAlarmRuleTemplate.getStatus());
        }

        if(StringUtils.isNotBlank(appAlarmRuleTemplate.getRemark())){
            ca.andRemarkLike("%" + appAlarmRuleTemplate.getRemark() +"%");
        }

        if(StringUtils.isNotBlank(appAlarmRuleTemplate.getName())){
            ca.andNameLike("%" + appAlarmRuleTemplate.getName() + "%");
        }

        if(StringUtils.isNotBlank(appAlarmRuleTemplate.getCreater())){
            ca.andCreaterEqualTo(appAlarmRuleTemplate.getCreater());
        }

        if(appAlarmRuleTemplate.getType() != null){
            ca.andTypeEqualTo(appAlarmRuleTemplate.getType());
        }

        if(appAlarmRuleTemplate.getStrategyType() != null){
            ca.andStrategyTypeEqualTo(appAlarmRuleTemplate.getStrategyType());
        }

        example.setOffset(offset);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");
        return templateMapper.selectByExample(example);
    }

    public AppAlarmRuleTemplate getById(Integer id){
        return templateMapper.selectByPrimaryKey(id);
    }

    public int create(AppAlarmRuleTemplate appAlarmRuleTemplate) {
        if (null == appAlarmRuleTemplate) {
            log.error("[AppAlarmRuleTemplateDao.create] null appAlarmRuleTemplate");
            return 0;
        }

        appAlarmRuleTemplate.setCreateTime(new Date());
        appAlarmRuleTemplate.setUpdateTime(new Date());
        appAlarmRuleTemplate.setStatus(0);
        if (appAlarmRuleTemplate.getStrategyType() == null) {
            appAlarmRuleTemplate.setStrategyType(AlarmStrategyType.SYSTEM.getCode());
        }
        try {
            int affected = templateMapper.insert(appAlarmRuleTemplate);
            if (affected < 1) {
                log.warn("[AppAlarmRuleTemplateDao.create] failed to insert appAlarmRuleTemplate: {}", appAlarmRuleTemplate.toString());
                return 0;
            }

            return affected;
        } catch (Exception e) {
            log.error("[AppAlarmRuleTemplateDao.create] failed to insert appAlarmRuleTemplate: {}, err: {}", appAlarmRuleTemplate.toString(), e);
            return 0;
        }
    }

    public int update(AppAlarmRuleTemplate appAlarmRuleTemplate) {
        if (null == appAlarmRuleTemplate) {
            log.error("[AppAlarmRuleTemplateDao.update] null appAlarmRuleTemplate");
            return 0;
        }

        appAlarmRuleTemplate.setUpdateTime(new Date());

        try {
            int affected = templateMapper.updateByPrimaryKeySelective(appAlarmRuleTemplate);
            if (affected < 1) {
                log.warn("[AppAlarmRuleTemplateDao.update] failed to insert appAlarmRuleTemplate: {}", appAlarmRuleTemplate.toString());
                return 0;
            }

            return affected;
        } catch (Exception e) {
            log.error("[AppAlarmRuleTemplateDao.update] failed to insert appAlarmRuleTemplate: {}, err: {}", appAlarmRuleTemplate.toString(), e);
            return 0;
        }
    }

    public void batchInsert(List<AppAlarmRuleTemplate> list){
        int i = templateMapper.batchInsert(list);
    }

    public int deleteById(Integer id){
        try {
            return templateMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            log.error("deleteById error! message:{}",e.getMessage(),e);
            return 0;
        }
    }



}
