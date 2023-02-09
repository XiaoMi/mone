package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.EoDubboApiInfo;
import com.xiaomi.miapi.pojo.EoDubboApiInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface EoDubboApiInfoMapper {
    long countByExample(EoDubboApiInfoExample example);

    int deleteByExample(EoDubboApiInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(EoDubboApiInfo record);

    int insertSelective(EoDubboApiInfo record);

    List<EoDubboApiInfo> selectByExampleWithBLOBs(EoDubboApiInfoExample example);

    List<EoDubboApiInfo> selectByExample(EoDubboApiInfoExample example);

    EoDubboApiInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") EoDubboApiInfo record, @Param("example") EoDubboApiInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") EoDubboApiInfo record, @Param("example") EoDubboApiInfoExample example);

    int updateByExample(@Param("record") EoDubboApiInfo record, @Param("example") EoDubboApiInfoExample example);

    int updateByPrimaryKeySelective(EoDubboApiInfo record);

    int updateByPrimaryKeyWithBLOBs(EoDubboApiInfo record);

    int updateByPrimaryKey(EoDubboApiInfo record);

    int batchInsert(@Param("list") List<EoDubboApiInfo> list);

    int batchInsertSelective(@Param("list") List<EoDubboApiInfo> list, @Param("selective") EoDubboApiInfo.Column ... selective);

    void batchDeleteDubboApi(@Param("Ids") List<Integer> Ids);
}