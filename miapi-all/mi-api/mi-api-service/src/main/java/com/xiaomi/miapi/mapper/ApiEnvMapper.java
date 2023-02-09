package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ApiEnv;
import com.xiaomi.miapi.pojo.ApiEnvExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ApiEnvMapper {
    long countByExample(ApiEnvExample example);

    int deleteByExample(ApiEnvExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ApiEnv record);

    int insertSelective(ApiEnv record);

    List<ApiEnv> selectByExampleWithBLOBs(ApiEnvExample example);

    List<ApiEnv> selectByExample(ApiEnvExample example);

    ApiEnv selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ApiEnv record, @Param("example") ApiEnvExample example);

    int updateByExampleWithBLOBs(@Param("record") ApiEnv record, @Param("example") ApiEnvExample example);

    int updateByExample(@Param("record") ApiEnv record, @Param("example") ApiEnvExample example);

    int updateByPrimaryKeySelective(ApiEnv record);

    int updateByPrimaryKeyWithBLOBs(ApiEnv record);

    int updateByPrimaryKey(ApiEnv record);

    int batchInsert(@Param("list") List<ApiEnv> list);

    int batchInsertSelective(@Param("list") List<ApiEnv> list, @Param("selective") ApiEnv.Column ... selective);
}