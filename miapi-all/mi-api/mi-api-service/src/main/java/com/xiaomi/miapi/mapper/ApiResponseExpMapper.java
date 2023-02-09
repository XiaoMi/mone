package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ApiResponseExp;
import com.xiaomi.miapi.pojo.ApiResponseExpExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ApiResponseExpMapper {
    long countByExample(ApiResponseExpExample example);

    int deleteByExample(ApiResponseExpExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ApiResponseExp record);

    int insertSelective(ApiResponseExp record);

    List<ApiResponseExp> selectByExampleWithBLOBs(ApiResponseExpExample example);

    List<ApiResponseExp> selectByExample(ApiResponseExpExample example);

    ApiResponseExp selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ApiResponseExp record, @Param("example") ApiResponseExpExample example);

    int updateByExampleWithBLOBs(@Param("record") ApiResponseExp record, @Param("example") ApiResponseExpExample example);

    int updateByExample(@Param("record") ApiResponseExp record, @Param("example") ApiResponseExpExample example);

    int updateByPrimaryKeySelective(ApiResponseExp record);

    int updateByPrimaryKeyWithBLOBs(ApiResponseExp record);

    int updateByPrimaryKey(ApiResponseExp record);

    int batchInsert(@Param("list") List<ApiResponseExp> list);

    int batchInsertSelective(@Param("list") List<ApiResponseExp> list, @Param("selective") ApiResponseExp.Column ... selective);
}