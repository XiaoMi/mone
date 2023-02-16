package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppGrafanaMapping;
import com.xiaomi.mone.monitor.dao.model.AppGrafanaMappingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Resource;

@Resource
public interface AppGrafanaMappingMapper {
    long countByExample(AppGrafanaMappingExample example);

    int deleteByExample(AppGrafanaMappingExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppGrafanaMapping record);

    int insertSelective(AppGrafanaMapping record);

    List<AppGrafanaMapping> selectByExample(AppGrafanaMappingExample example);

    AppGrafanaMapping selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppGrafanaMapping record, @Param("example") AppGrafanaMappingExample example);

    int updateByExample(@Param("record") AppGrafanaMapping record, @Param("example") AppGrafanaMappingExample example);

    int updateByPrimaryKeySelective(AppGrafanaMapping record);

    int updateByPrimaryKey(AppGrafanaMapping record);

    int batchInsert(@Param("list") List<AppGrafanaMapping> list);

    int batchInsertSelective(@Param("list") List<AppGrafanaMapping> list, @Param("selective") AppGrafanaMapping.Column ... selective);
}