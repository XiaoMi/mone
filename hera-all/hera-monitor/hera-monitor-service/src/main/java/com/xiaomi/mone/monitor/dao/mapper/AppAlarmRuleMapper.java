package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRuleExample;
import com.xiaomi.mone.monitor.service.model.prometheus.AppWithAlarmRules;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Resource;
import java.util.List;

@Resource
public interface AppAlarmRuleMapper {
    long countByExample(AppAlarmRuleExample example);

    int deleteByExample(AppAlarmRuleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppAlarmRule record);

    int insertSelective(AppAlarmRule record);

    List<AppAlarmRule> selectByExampleWithBLOBs(AppAlarmRuleExample example);

    List<AppAlarmRule> selectByExample(AppAlarmRuleExample example);

    AppAlarmRule selectByPrimaryKey(Integer id);

    List<AppAlarmRule> selectByStrategyIdList(@Param("strategyIds") List<Integer> strategyIds);

    List<AppAlarmRule> selectByStrategyId(@Param("strategyId") Integer strategyId);

    List<AppAlarmRule> getRulesByIamId(@Param("iamId") Integer iamId);

    int updateByExampleSelective(@Param("record") AppAlarmRule record, @Param("example") AppAlarmRuleExample example);

    int updateByExampleWithBLOBs(@Param("record") AppAlarmRule record, @Param("example") AppAlarmRuleExample example);

    int updateByExample(@Param("record") AppAlarmRule record, @Param("example") AppAlarmRuleExample example);

    int updateByPrimaryKeySelective(AppAlarmRule record);

    int updateByPrimaryKeyWithBLOBs(AppAlarmRule record);

    int updateByPrimaryKey(AppAlarmRule record);

    int batchInsert(@Param("list") List<AppAlarmRule> list);

    int batchInsertSelective(@Param("list") List<AppAlarmRule> list, @Param("selective") AppAlarmRule.Column ... selective);

    List<AppWithAlarmRules> selectAlarmRuleByAppName(@Param("userName") String userName,@Param("appName") String appName, @Param("offset") Integer offset, @Param("limit") Integer limit);

    Long countAlarmRuleByAppName(@Param("userName") String userName,@Param("appName") String appName);

    List<AppWithAlarmRules> selectAppNoRulesConfig(@Param("userName") String userName, @Param("appName") String appName, @Param("offset") Integer offset, @Param("limit") Integer limit);

    Long countAppNoRulesConfig(@Param("userName") String userName,@Param("appName") String appName);
}