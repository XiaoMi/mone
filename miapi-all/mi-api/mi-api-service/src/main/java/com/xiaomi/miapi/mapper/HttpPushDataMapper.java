package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.HttpPushData;
import com.xiaomi.miapi.pojo.HttpPushDataExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface HttpPushDataMapper {
    long countByExample(HttpPushDataExample example);

    int deleteByExample(HttpPushDataExample example);

    int deleteByPrimaryKey(Integer id);

    void insert(HttpPushData record);

    int insertSelective(HttpPushData record);

    List<HttpPushData> selectByExampleWithBLOBs(HttpPushDataExample example);

    List<HttpPushData> selectByExample(HttpPushDataExample example);

    HttpPushData selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") HttpPushData record, @Param("example") HttpPushDataExample example);

    int updateByExampleWithBLOBs(@Param("record") HttpPushData record, @Param("example") HttpPushDataExample example);

    int updateByExample(@Param("record") HttpPushData record, @Param("example") HttpPushDataExample example);

    int updateByPrimaryKeySelective(HttpPushData record);

    int updateByPrimaryKeyWithBLOBs(HttpPushData record);

    int updateByPrimaryKey(HttpPushData record);

    int batchInsert(@Param("list") List<HttpPushData> list);

    int batchInsertSelective(@Param("list") List<HttpPushData> list, @Param("selective") HttpPushData.Column ... selective);
}