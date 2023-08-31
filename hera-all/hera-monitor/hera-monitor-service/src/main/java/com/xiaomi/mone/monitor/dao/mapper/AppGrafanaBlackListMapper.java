package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppGrafanaBlackList;
import com.xiaomi.mone.monitor.dao.model.AppGrafanaBlackListExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppGrafanaBlackListMapper {
    long countByExample(AppGrafanaBlackListExample example);

    int deleteByExample(AppGrafanaBlackListExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppGrafanaBlackList record);

    int insertSelective(AppGrafanaBlackList record);

    List<AppGrafanaBlackList> selectByExample(AppGrafanaBlackListExample example);

    AppGrafanaBlackList selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppGrafanaBlackList record, @Param("example") AppGrafanaBlackListExample example);

    int updateByExample(@Param("record") AppGrafanaBlackList record, @Param("example") AppGrafanaBlackListExample example);

    int updateByPrimaryKeySelective(AppGrafanaBlackList record);

    int updateByPrimaryKey(AppGrafanaBlackList record);

    int batchInsert(@Param("list") List<AppGrafanaBlackList> list);

    int batchInsertSelective(@Param("list") List<AppGrafanaBlackList> list, @Param("selective") AppGrafanaBlackList.Column ... selective);
}