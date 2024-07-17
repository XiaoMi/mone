package run.mone.z.desensitization.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import run.mone.z.desensitization.pojo.ZDesensitizationRecord;
import run.mone.z.desensitization.pojo.ZDesensitizationRecordExample;

public interface ZDesensitizationRecordMapper {
    long countByExample(ZDesensitizationRecordExample example);

    int deleteByExample(ZDesensitizationRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ZDesensitizationRecord record);

    int insertSelective(ZDesensitizationRecord record);

    List<ZDesensitizationRecord> selectByExampleWithBLOBs(ZDesensitizationRecordExample example);

    List<ZDesensitizationRecord> selectByExample(ZDesensitizationRecordExample example);

    ZDesensitizationRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ZDesensitizationRecord record, @Param("example") ZDesensitizationRecordExample example);

    int updateByExampleWithBLOBs(@Param("record") ZDesensitizationRecord record, @Param("example") ZDesensitizationRecordExample example);

    int updateByExample(@Param("record") ZDesensitizationRecord record, @Param("example") ZDesensitizationRecordExample example);

    int updateByPrimaryKeySelective(ZDesensitizationRecord record);

    int updateByPrimaryKeyWithBLOBs(ZDesensitizationRecord record);

    int updateByPrimaryKey(ZDesensitizationRecord record);

    int batchInsert(@Param("list") List<ZDesensitizationRecord> list);

    int batchInsertSelective(@Param("list") List<ZDesensitizationRecord> list, @Param("selective") ZDesensitizationRecord.Column ... selective);
}