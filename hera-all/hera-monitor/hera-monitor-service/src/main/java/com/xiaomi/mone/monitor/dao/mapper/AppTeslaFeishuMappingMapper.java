package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppTeslaFeishuMapping;
import com.xiaomi.mone.monitor.dao.model.AppTeslaFeishuMappingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppTeslaFeishuMappingMapper {
    long countByExample(AppTeslaFeishuMappingExample example);

    int deleteByExample(AppTeslaFeishuMappingExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppTeslaFeishuMapping record);

    int insertSelective(AppTeslaFeishuMapping record);

    List<AppTeslaFeishuMapping> selectByExample(AppTeslaFeishuMappingExample example);

    AppTeslaFeishuMapping selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppTeslaFeishuMapping record, @Param("example") AppTeslaFeishuMappingExample example);

    int updateByExample(@Param("record") AppTeslaFeishuMapping record, @Param("example") AppTeslaFeishuMappingExample example);

    int updateByPrimaryKeySelective(AppTeslaFeishuMapping record);

    int updateByPrimaryKey(AppTeslaFeishuMapping record);

    int batchInsert(@Param("list") List<AppTeslaFeishuMapping> list);

    int batchInsertSelective(@Param("list") List<AppTeslaFeishuMapping> list, @Param("selective") AppTeslaFeishuMapping.Column ... selective);
}