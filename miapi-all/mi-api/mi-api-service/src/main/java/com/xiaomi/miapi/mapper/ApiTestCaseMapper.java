package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.common.pojo.ApiTestCase;
import com.xiaomi.miapi.common.pojo.ApiTestCaseExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiTestCaseMapper {
    long countByExample(ApiTestCaseExample example);

    int deleteByExample(ApiTestCaseExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ApiTestCase record);

    int insertSelective(ApiTestCase record);

    List<ApiTestCase> selectByExampleWithBLOBs(ApiTestCaseExample example);

    List<ApiTestCase> selectByExample(ApiTestCaseExample example);

    ApiTestCase selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ApiTestCase record, @Param("example") ApiTestCaseExample example);

    int updateByExampleWithBLOBs(@Param("record") ApiTestCase record, @Param("example") ApiTestCaseExample example);

    int updateByExample(@Param("record") ApiTestCase record, @Param("example") ApiTestCaseExample example);

    int updateByPrimaryKeySelective(ApiTestCase record);

    int updateByPrimaryKeyWithBLOBs(ApiTestCase record);

    int updateByPrimaryKey(ApiTestCase record);

    int batchInsert(@Param("list") List<ApiTestCase> list);

    int batchInsertSelective(@Param("list") List<ApiTestCase> list, @Param("selective") ApiTestCase.Column ... selective);
}