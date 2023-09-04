package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.GrafanaTemplate;
import com.xiaomi.mone.monitor.dao.model.GrafanaTemplateExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GrafanaTemplateMapper {
    long countByExample(GrafanaTemplateExample example);

    int deleteByExample(GrafanaTemplateExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(GrafanaTemplate record);

    int insertSelective(GrafanaTemplate record);

    List<GrafanaTemplate> selectByExampleWithBLOBs(GrafanaTemplateExample example);

    List<GrafanaTemplate> selectByExample(GrafanaTemplateExample example);

    GrafanaTemplate selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") GrafanaTemplate record, @Param("example") GrafanaTemplateExample example);

    int updateByExampleWithBLOBs(@Param("record") GrafanaTemplate record, @Param("example") GrafanaTemplateExample example);

    int updateByExample(@Param("record") GrafanaTemplate record, @Param("example") GrafanaTemplateExample example);

    int updateByPrimaryKeySelective(GrafanaTemplate record);

    int updateByPrimaryKeyWithBLOBs(GrafanaTemplate record);

    int updateByPrimaryKey(GrafanaTemplate record);

    int batchInsert(@Param("list") List<GrafanaTemplate> list);

    int batchInsertSelective(@Param("list") List<GrafanaTemplate> list, @Param("selective") GrafanaTemplate.Column ... selective);
}