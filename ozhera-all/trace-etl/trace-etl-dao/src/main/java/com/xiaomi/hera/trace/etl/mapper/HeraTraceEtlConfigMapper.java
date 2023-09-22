package com.xiaomi.hera.trace.etl.mapper;

import com.github.pagehelper.Page;
import com.xiaomi.hera.trace.etl.domain.HeraTraceConfigVo;
import com.xiaomi.hera.trace.etl.domain.HeraTraceEtlConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HeraTraceEtlConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HeraTraceEtlConfig record);

    int insertSelective(HeraTraceEtlConfig record);

    HeraTraceEtlConfig selectByPrimaryKey(Integer id);

    List<HeraTraceEtlConfig> getAll(HeraTraceConfigVo vo);

    Page<HeraTraceEtlConfig> getAllPage(@Param("user") String user);

    HeraTraceEtlConfig getByBaseInfoId(Integer baseInfoId);

    int updateByPrimaryKeySelective(HeraTraceEtlConfig record);

    int updateByPrimaryKey(HeraTraceEtlConfig record);
}