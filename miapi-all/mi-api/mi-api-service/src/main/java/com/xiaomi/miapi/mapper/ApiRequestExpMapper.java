package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ApiRequestExp;
import com.xiaomi.miapi.pojo.ApiRequestExpExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ApiRequestExpMapper {
    long countByExample(ApiRequestExpExample example);

    int deleteByExample(ApiRequestExpExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ApiRequestExp record);

    int insertSelective(ApiRequestExp record);

    List<ApiRequestExp> selectByExampleWithBLOBs(ApiRequestExpExample example);

    List<ApiRequestExp> selectByExample(ApiRequestExpExample example);

    ApiRequestExp selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ApiRequestExp record, @Param("example") ApiRequestExpExample example);

    int updateByExampleWithBLOBs(@Param("record") ApiRequestExp record, @Param("example") ApiRequestExpExample example);

    int updateByExample(@Param("record") ApiRequestExp record, @Param("example") ApiRequestExpExample example);

    int updateByPrimaryKeySelective(ApiRequestExp record);

    int updateByPrimaryKeyWithBLOBs(ApiRequestExp record);

    int updateByPrimaryKey(ApiRequestExp record);

    int batchInsert(@Param("list") List<ApiRequestExp> list);

    int batchInsertSelective(@Param("list") List<ApiRequestExp> list, @Param("selective") ApiRequestExp.Column ... selective);
}