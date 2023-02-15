package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.IndexInfo;
import com.xiaomi.miapi.pojo.IndexInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface IndexInfoMapper {
    long countByExample(IndexInfoExample example);

    int deleteByExample(IndexInfoExample example);

    int deleteByPrimaryKey(Integer indexId);

    int insert(IndexInfo record);

    int insertSelective(IndexInfo record);

    List<IndexInfo> selectByExampleWithBLOBs(IndexInfoExample example);

    List<IndexInfo> selectByExample(IndexInfoExample example);

    IndexInfo selectByPrimaryKey(Integer indexId);

    int updateByExampleSelective(@Param("record") IndexInfo record, @Param("example") IndexInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") IndexInfo record, @Param("example") IndexInfoExample example);

    int updateByExample(@Param("record") IndexInfo record, @Param("example") IndexInfoExample example);

    int updateByPrimaryKeySelective(IndexInfo record);

    int updateByPrimaryKeyWithBLOBs(IndexInfo record);

    int updateByPrimaryKey(IndexInfo record);

    int batchInsert(@Param("list") List<IndexInfo> list);

    int batchInsertSelective(@Param("list") List<IndexInfo> list, @Param("selective") IndexInfo.Column ... selective);
}