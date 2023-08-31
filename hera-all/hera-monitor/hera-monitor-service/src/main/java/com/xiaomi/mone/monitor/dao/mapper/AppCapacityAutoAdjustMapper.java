package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjust;
import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjustExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppCapacityAutoAdjustMapper {
    long countByExample(AppCapacityAutoAdjustExample example);

    int deleteByExample(AppCapacityAutoAdjustExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppCapacityAutoAdjust record);

    int insertSelective(AppCapacityAutoAdjust record);

    List<AppCapacityAutoAdjust> selectByExample(AppCapacityAutoAdjustExample example);

    AppCapacityAutoAdjust selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppCapacityAutoAdjust record, @Param("example") AppCapacityAutoAdjustExample example);

    int updateByExample(@Param("record") AppCapacityAutoAdjust record, @Param("example") AppCapacityAutoAdjustExample example);

    int updateByPrimaryKeySelective(AppCapacityAutoAdjust record);

    int updateByPrimaryKey(AppCapacityAutoAdjust record);

    int batchInsert(@Param("list") List<AppCapacityAutoAdjust> list);

    int batchInsertSelective(@Param("list") List<AppCapacityAutoAdjust> list, @Param("selective") AppCapacityAutoAdjust.Column ... selective);
}