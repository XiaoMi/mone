package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.common.pojo.GatewayApiInfo;
import com.xiaomi.miapi.common.pojo.GatewayApiInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GatewayApiInfoMapper {
    long countByExample(GatewayApiInfoExample example);

    int deleteByExample(GatewayApiInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(GatewayApiInfo record);

    int insertSelective(GatewayApiInfo record);

    List<GatewayApiInfo> selectByExampleWithBLOBs(GatewayApiInfoExample example);

    List<GatewayApiInfo> selectByExample(GatewayApiInfoExample example);

    GatewayApiInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") GatewayApiInfo record, @Param("example") GatewayApiInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") GatewayApiInfo record, @Param("example") GatewayApiInfoExample example);

    int updateByExample(@Param("record") GatewayApiInfo record, @Param("example") GatewayApiInfoExample example);

    int updateByPrimaryKeySelective(GatewayApiInfo record);

    int updateByPrimaryKeyWithBLOBs(GatewayApiInfo record);

    int updateByPrimaryKey(GatewayApiInfo record);

    int batchInsert(@Param("list") List<GatewayApiInfo> list);

    int batchInsertSelective(@Param("list") List<GatewayApiInfo> list, @Param("selective") GatewayApiInfo.Column ... selective);

    void batchDeleteGatewayApi(@Param("Ids") List<Integer> Ids);

}