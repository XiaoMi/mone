package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.common.pojo.ApiIndex;
import com.xiaomi.miapi.common.pojo.ApiIndexExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiIndexMapper {
    long countByExample(ApiIndexExample example);

    int deleteByExample(ApiIndexExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ApiIndex record);

    int insertSelective(ApiIndex record);

    List<ApiIndex> selectByExample(ApiIndexExample example);

    ApiIndex selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ApiIndex record, @Param("example") ApiIndexExample example);

    int updateByExample(@Param("record") ApiIndex record, @Param("example") ApiIndexExample example);

    int updateByPrimaryKeySelective(ApiIndex record);

    int updateByPrimaryKey(ApiIndex record);

    int batchInsert(@Param("list") List<ApiIndex> list);

    int batchInsertSelective(@Param("list") List<ApiIndex> list, @Param("selective") ApiIndex.Column ... selective);
}