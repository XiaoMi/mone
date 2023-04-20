package com.xiaomi.mone.monitor.dao;

import com.xiaomi.mone.monitor.dao.mapper.AppTeslaAlarmRuleMapper;
import com.xiaomi.mone.monitor.dao.model.AppTeslaAlarmRule;
import com.xiaomi.mone.monitor.dao.model.AppTeslaAlarmRuleExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/11/19 3:11 下午
 */
@Slf4j
@Repository
public class AppTeslaAlarmRuleDao {

    @Autowired
    AppTeslaAlarmRuleMapper appTeslaAlarmRuleMapper;

    public Long count(String name,String teslaGroupName,Integer type,String remark){
        AppTeslaAlarmRuleExample example = new AppTeslaAlarmRuleExample();
        AppTeslaAlarmRuleExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);

        if (StringUtils.isNotBlank(teslaGroupName)){
            ca.andTeslaGroupEqualTo( teslaGroupName);
        }

        if (StringUtils.isNotBlank(name)){
            ca.andNameEqualTo(name);
        }

        if (StringUtils.isNotBlank(remark)){
            ca.andRemarkEqualTo(remark);
        }

        if (type != null){
            ca.andTypeEqualTo(type);
        }

        return appTeslaAlarmRuleMapper.countByExample(example);
    }


    public List<AppTeslaAlarmRule> list(String name,String teslaGroupName,Integer type,String remark,Integer page, Integer pageSize){

        AppTeslaAlarmRuleExample example = new AppTeslaAlarmRuleExample();

        example.setOffset((page-1) * pageSize);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");

        AppTeslaAlarmRuleExample.Criteria ca = example.createCriteria();
        ca.andStatusEqualTo(0);

        if (StringUtils.isNotBlank(teslaGroupName)){
            ca.andTeslaGroupEqualTo( teslaGroupName);
        }

        if (StringUtils.isNotBlank(name)){
            ca.andNameEqualTo(name);
        }

        if (StringUtils.isNotBlank(remark)){
            ca.andRemarkEqualTo(remark);
        }

        if (type != null){
            ca.andTypeEqualTo(type);
        }

        return appTeslaAlarmRuleMapper.selectByExample(example);

    }

    public int insert(AppTeslaAlarmRule appTeslaAlarmRule) {
        if (null == appTeslaAlarmRule) {
            log.error("[AppTeslaAlarmRuleDao.create] null appMonitor");
            return 0;
        }

        appTeslaAlarmRule.setCreateTime(new Date());
        appTeslaAlarmRule.setUpdateTime(new Date());
        appTeslaAlarmRule.setStatus(0);

        try {
            int affected = appTeslaAlarmRuleMapper.insert(appTeslaAlarmRule);
            if (affected < 1) {
                log.warn("[AppTeslaAlarmRuleDao.create] failed to insert appTeslaAlarmRule: {}", appTeslaAlarmRule.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppTeslaAlarmRuleDao.create] failed to insert appTeslaAlarmRule: {}, err: {}", appTeslaAlarmRule.toString(), e);
            return 0;
        }
        return 1;
    }

    public int update(AppTeslaAlarmRule appTeslaAlarmRule) {
        if (null == appTeslaAlarmRule) {
            log.error("[AppTeslaAlarmRuleDao.update] null appTeslaAlarmRule");
            return 0;
        }

        appTeslaAlarmRule.setUpdateTime(new Date());

        try {
            int affected = appTeslaAlarmRuleMapper.updateByPrimaryKey(appTeslaAlarmRule);
            if (affected < 1) {
                log.warn("[AppTeslaAlarmRuleDao.update] failed to update appTeslaAlarmRule: {}", appTeslaAlarmRule.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppTeslaAlarmRuleDao.update] failed to update appTeslaAlarmRule: {}, err: {}", appTeslaAlarmRule.toString(), e);
            return 0;
        }
        return 1;
    }
    public int delete(Integer id) {
        if (null == id) {
            log.error("[AppTeslaAlarmRuleDao.delete] null id");
            return 0;
        }

        int affected = 0;
        try {
            affected = appTeslaAlarmRuleMapper.deleteByPrimaryKey(id);
            if (affected < 1) {
                log.warn("[AppTeslaAlarmRuleDao.delete] failed to delete id: {}", id);
                return 0;
            }
        } catch (Exception e) {
            log.error("[AppTeslaAlarmRuleDao.delete] failed to delete id: {}, err: {}", id, e);
            return 0;
        }
        return affected;
    }


}
