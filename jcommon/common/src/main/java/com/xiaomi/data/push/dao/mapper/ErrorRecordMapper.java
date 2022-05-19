package com.xiaomi.data.push.dao.mapper;

import com.xiaomi.data.push.dao.model.ErrorRecord;
import com.xiaomi.data.push.dao.model.ErrorRecordExample;
import com.xiaomi.data.push.dao.model.ErrorRecordWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ErrorRecordMapper {
    int countByExample(ErrorRecordExample example);

    int deleteByExample(ErrorRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ErrorRecordWithBLOBs record);

    int insertSelective(ErrorRecordWithBLOBs record);

    List<ErrorRecordWithBLOBs> selectByExampleWithBLOBs(ErrorRecordExample example);

    List<ErrorRecord> selectByExample(ErrorRecordExample example);

    ErrorRecordWithBLOBs selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ErrorRecordWithBLOBs record, @Param("example") ErrorRecordExample example);

    int updateByExampleWithBLOBs(@Param("record") ErrorRecordWithBLOBs record, @Param("example") ErrorRecordExample example);

    int updateByExample(@Param("record") ErrorRecord record, @Param("example") ErrorRecordExample example);

    int updateByPrimaryKeySelective(ErrorRecordWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ErrorRecordWithBLOBs record);

    int updateByPrimaryKey(ErrorRecord record);
}