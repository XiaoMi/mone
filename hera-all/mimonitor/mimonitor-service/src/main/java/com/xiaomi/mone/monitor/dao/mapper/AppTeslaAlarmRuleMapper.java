package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppTeslaAlarmRule;
import com.xiaomi.mone.monitor.dao.model.AppTeslaAlarmRuleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppTeslaAlarmRuleMapper {
    long countByExample(AppTeslaAlarmRuleExample example);

    int deleteByExample(AppTeslaAlarmRuleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppTeslaAlarmRule record);

    int insertSelective(AppTeslaAlarmRule record);

    List<AppTeslaAlarmRule> selectByExampleWithBLOBs(AppTeslaAlarmRuleExample example);

    List<AppTeslaAlarmRule> selectByExample(AppTeslaAlarmRuleExample example);

    AppTeslaAlarmRule selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppTeslaAlarmRule record, @Param("example") AppTeslaAlarmRuleExample example);

    int updateByExampleWithBLOBs(@Param("record") AppTeslaAlarmRule record, @Param("example") AppTeslaAlarmRuleExample example);

    int updateByExample(@Param("record") AppTeslaAlarmRule record, @Param("example") AppTeslaAlarmRuleExample example);

    int updateByPrimaryKeySelective(AppTeslaAlarmRule record);

    int updateByPrimaryKeyWithBLOBs(AppTeslaAlarmRule record);

    int updateByPrimaryKey(AppTeslaAlarmRule record);

    int batchInsert(@Param("list") List<AppTeslaAlarmRule> list);

    int batchInsertSelective(@Param("list") List<AppTeslaAlarmRule> list, @Param("selective") AppTeslaAlarmRule.Column ... selective);
}