package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.DubboPushData;
import com.xiaomi.miapi.pojo.DubboPushDataExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface DubboPushDataMapper {
    long countByExample(DubboPushDataExample example);

    int deleteByExample(DubboPushDataExample example);

    int deleteByPrimaryKey(Integer id);

    void insert(DubboPushData record);

    int insertSelective(DubboPushData record);

    List<DubboPushData> selectByExampleWithBLOBs(DubboPushDataExample example);

    List<DubboPushData> selectByExample(DubboPushDataExample example);

    DubboPushData selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DubboPushData record, @Param("example") DubboPushDataExample example);

    int updateByExampleWithBLOBs(@Param("record") DubboPushData record, @Param("example") DubboPushDataExample example);

    int updateByExample(@Param("record") DubboPushData record, @Param("example") DubboPushDataExample example);

    int updateByPrimaryKeySelective(DubboPushData record);

    int updateByPrimaryKeyWithBLOBs(DubboPushData record);

    int updateByPrimaryKey(DubboPushData record);

    int batchInsert(@Param("list") List<DubboPushData> list);

    int batchInsertSelective(@Param("list") List<DubboPushData> list, @Param("selective") DubboPushData.Column ... selective);
}