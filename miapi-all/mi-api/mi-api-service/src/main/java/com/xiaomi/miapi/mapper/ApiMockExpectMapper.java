package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ApiMockExpect;
import com.xiaomi.miapi.pojo.ApiMockExpectExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ApiMockExpectMapper {
    long countByExample(ApiMockExpectExample example);

    int deleteByExample(ApiMockExpectExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ApiMockExpect record);

    int insertSelective(ApiMockExpect record);

    List<ApiMockExpect> selectByExampleWithBLOBs(ApiMockExpectExample example);

    List<ApiMockExpect> selectByExample(ApiMockExpectExample example);

    ApiMockExpect selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ApiMockExpect record, @Param("example") ApiMockExpectExample example);

    int updateByExampleWithBLOBs(@Param("record") ApiMockExpect record, @Param("example") ApiMockExpectExample example);

    int updateByExample(@Param("record") ApiMockExpect record, @Param("example") ApiMockExpectExample example);

    int updateByPrimaryKeySelective(ApiMockExpect record);

    int updateByPrimaryKeyWithBLOBs(ApiMockExpect record);

    int updateByPrimaryKey(ApiMockExpect record);

    int batchInsert(@Param("list") List<ApiMockExpect> list);

    int batchInsertSelective(@Param("list") List<ApiMockExpect> list, @Param("selective") ApiMockExpect.Column ... selective);
}