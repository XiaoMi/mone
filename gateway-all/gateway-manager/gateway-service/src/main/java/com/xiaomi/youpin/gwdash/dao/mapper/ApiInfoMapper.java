package com.xiaomi.youpin.gwdash.dao.mapper;

import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ApiInfoMapper {
    long countByExample(ApiInfoExample example);

    int deleteByExample(ApiInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ApiInfo record);

    int insertSelective(ApiInfo record);

    List<ApiInfo> selectByExampleWithBLOBs(ApiInfoExample example);

    List<ApiInfo> selectByExample(ApiInfoExample example);

    ApiInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ApiInfo record, @Param("example") ApiInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") ApiInfo record, @Param("example") ApiInfoExample example);

    int updateByExample(@Param("record") ApiInfo record, @Param("example") ApiInfoExample example);

    int updateByPrimaryKeySelective(ApiInfo record);

    int updateByPrimaryKeyWithBLOBs(ApiInfo record);

    int updateByPrimaryKey(ApiInfo record);

    int batchInsert(@Param("list") List<ApiInfo> list);

    int batchInsertSelective(@Param("list") List<ApiInfo> list, @Param("selective") ApiInfo.Column ... selective);
}