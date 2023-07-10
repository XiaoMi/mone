package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjustRecord;
import com.xiaomi.mone.monitor.dao.model.AppCapacityAutoAdjustRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppCapacityAutoAdjustRecordMapper {
    long countByExample(AppCapacityAutoAdjustRecordExample example);

    int deleteByExample(AppCapacityAutoAdjustRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppCapacityAutoAdjustRecord record);

    int insertSelective(AppCapacityAutoAdjustRecord record);

    List<AppCapacityAutoAdjustRecord> selectByExample(AppCapacityAutoAdjustRecordExample example);

    AppCapacityAutoAdjustRecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppCapacityAutoAdjustRecord record, @Param("example") AppCapacityAutoAdjustRecordExample example);

    int updateByExample(@Param("record") AppCapacityAutoAdjustRecord record, @Param("example") AppCapacityAutoAdjustRecordExample example);

    int updateByPrimaryKeySelective(AppCapacityAutoAdjustRecord record);

    int updateByPrimaryKey(AppCapacityAutoAdjustRecord record);

    int batchInsert(@Param("list") List<AppCapacityAutoAdjustRecord> list);

    int batchInsertSelective(@Param("list") List<AppCapacityAutoAdjustRecord> list, @Param("selective") AppCapacityAutoAdjustRecord.Column ... selective);
}