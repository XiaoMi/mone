package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AlarmHealthQuery;
import com.xiaomi.mone.monitor.dao.model.AlarmHealthResult;
import com.xiaomi.mone.monitor.dao.model.AlertManagerRules;
import com.xiaomi.mone.monitor.dao.model.AlertManagerRulesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AlertManagerRulesMapper {
    long countByExample(AlertManagerRulesExample example);

    int deleteByExample(AlertManagerRulesExample example);

    int deleteByPrimaryKey(Integer ruleId);

    int insert(AlertManagerRules record);

    int insertSelective(AlertManagerRules record);

    List<AlertManagerRules> selectByExampleWithBLOBs(AlertManagerRulesExample example);

    List<AlertManagerRules> selectByExample(AlertManagerRulesExample example);

    AlertManagerRules selectByPrimaryKey(Integer ruleId);

    int updateByExampleSelective(@Param("record") AlertManagerRules record, @Param("example") AlertManagerRulesExample example);

    int updateByExampleWithBLOBs(@Param("record") AlertManagerRules record, @Param("example") AlertManagerRulesExample example);

    int updateByExample(@Param("record") AlertManagerRules record, @Param("example") AlertManagerRulesExample example);

    int updateByPrimaryKeySelective(AlertManagerRules record);

    int updateByPrimaryKeyWithBLOBs(AlertManagerRules record);

    int updateByPrimaryKey(AlertManagerRules record);

    int batchInsert(@Param("list") List<AlertManagerRules> list);

    int batchInsertSelective(@Param("list") List<AlertManagerRules> list, @Param("selective") AlertManagerRules.Column ... selective);
}