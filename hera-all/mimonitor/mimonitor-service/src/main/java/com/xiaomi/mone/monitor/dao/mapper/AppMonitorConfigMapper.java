package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppMonitorConfig;
import com.xiaomi.mone.monitor.dao.model.AppMonitorConfigExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Resource;

@Mapper
public interface AppMonitorConfigMapper {
    long countByExample(AppMonitorConfigExample example);

    int deleteByExample(AppMonitorConfigExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppMonitorConfig record);

    int insertSelective(AppMonitorConfig record);

    List<AppMonitorConfig> selectByExample(AppMonitorConfigExample example);

    AppMonitorConfig selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppMonitorConfig record, @Param("example") AppMonitorConfigExample example);

    int updateByExample(@Param("record") AppMonitorConfig record, @Param("example") AppMonitorConfigExample example);

    int updateByPrimaryKeySelective(AppMonitorConfig record);

    int updateByPrimaryKey(AppMonitorConfig record);

    int batchInsert(@Param("list") List<AppMonitorConfig> list);

    int batchInsertSelective(@Param("list") List<AppMonitorConfig> list, @Param("selective") AppMonitorConfig.Column ... selective);
}