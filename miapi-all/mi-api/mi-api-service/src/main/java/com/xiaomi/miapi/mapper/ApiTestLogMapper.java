package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ApiTestLog;
import com.xiaomi.miapi.pojo.ApiTestLogExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ApiTestLogMapper {
    long countByExample(ApiTestLogExample example);

    int deleteByExample(ApiTestLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ApiTestLog record);

    int insertSelective(ApiTestLog record);

    List<ApiTestLog> selectByExample(ApiTestLogExample example);

    ApiTestLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ApiTestLog record, @Param("example") ApiTestLogExample example);

    int updateByExample(@Param("record") ApiTestLog record, @Param("example") ApiTestLogExample example);

    int updateByPrimaryKeySelective(ApiTestLog record);

    int updateByPrimaryKey(ApiTestLog record);

    int batchInsert(@Param("list") List<ApiTestLog> list);

    int batchInsertSelective(@Param("list") List<ApiTestLog> list, @Param("selective") ApiTestLog.Column ... selective);
}