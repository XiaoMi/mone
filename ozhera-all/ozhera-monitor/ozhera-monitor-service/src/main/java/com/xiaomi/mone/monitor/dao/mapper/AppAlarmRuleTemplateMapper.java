package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppAlarmRuleTemplate;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRuleTemplateExample;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Resource;
import java.util.List;

@Resource
public interface AppAlarmRuleTemplateMapper {
    long countByExample(AppAlarmRuleTemplateExample example);

    int deleteByExample(AppAlarmRuleTemplateExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppAlarmRuleTemplate record);

    int insertSelective(AppAlarmRuleTemplate record);

    List<AppAlarmRuleTemplate> selectByExample(AppAlarmRuleTemplateExample example);

    AppAlarmRuleTemplate selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppAlarmRuleTemplate record, @Param("example") AppAlarmRuleTemplateExample example);

    int updateByExample(@Param("record") AppAlarmRuleTemplate record, @Param("example") AppAlarmRuleTemplateExample example);

    int updateByPrimaryKeySelective(AppAlarmRuleTemplate record);

    int updateByPrimaryKey(AppAlarmRuleTemplate record);

    int batchInsert(@Param("list") List<AppAlarmRuleTemplate> list);

    int batchInsertSelective(@Param("list") List<AppAlarmRuleTemplate> list, @Param("selective") AppAlarmRuleTemplate.Column ... selective);
}