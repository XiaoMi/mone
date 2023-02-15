package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ApiHistoryRecord;
import com.xiaomi.miapi.pojo.ApiHistoryRecordExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ApiHistoryRecordMapper {
    long countByExample(ApiHistoryRecordExample example);

    int deleteByExample(ApiHistoryRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ApiHistoryRecord record);

    int insertSelective(ApiHistoryRecord record);

    List<ApiHistoryRecord> selectByExampleWithBLOBs(ApiHistoryRecordExample example);

    List<ApiHistoryRecord> selectByExample(ApiHistoryRecordExample example);

    ApiHistoryRecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ApiHistoryRecord record, @Param("example") ApiHistoryRecordExample example);

    int updateByExampleWithBLOBs(@Param("record") ApiHistoryRecord record, @Param("example") ApiHistoryRecordExample example);

    int updateByExample(@Param("record") ApiHistoryRecord record, @Param("example") ApiHistoryRecordExample example);

    int updateByPrimaryKeySelective(ApiHistoryRecord record);

    int updateByPrimaryKeyWithBLOBs(ApiHistoryRecord record);

    int updateByPrimaryKey(ApiHistoryRecord record);

    int batchInsert(@Param("list") List<ApiHistoryRecord> list);

    int batchInsertSelective(@Param("list") List<ApiHistoryRecord> list, @Param("selective") ApiHistoryRecord.Column ... selective);
}