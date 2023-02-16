package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.HeraAppBaseInfo;
import com.xiaomi.mone.monitor.dao.model.HeraAppBaseInfoExample;
import java.util.List;

import com.xiaomi.mone.monitor.service.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.monitor.service.model.HeraAppBaseQuery;
import org.apache.ibatis.annotations.Param;

public interface HeraAppBaseInfoMapper {

    List<HeraAppBaseInfoParticipant> selectByParticipant(HeraAppBaseQuery query);

    Long countByParticipant(HeraAppBaseQuery query);

    long countByExample(HeraAppBaseInfoExample example);

    int deleteByExample(HeraAppBaseInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(HeraAppBaseInfo record);

    int insertSelective(HeraAppBaseInfo record);

    List<HeraAppBaseInfo> selectByExampleWithBLOBs(HeraAppBaseInfoExample example);

    List<HeraAppBaseInfo> selectByExample(HeraAppBaseInfoExample example);

    HeraAppBaseInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") HeraAppBaseInfo record, @Param("example") HeraAppBaseInfoExample example);

    int updateByExampleWithBLOBs(@Param("record") HeraAppBaseInfo record, @Param("example") HeraAppBaseInfoExample example);

    int updateByExample(@Param("record") HeraAppBaseInfo record, @Param("example") HeraAppBaseInfoExample example);

    int updateByPrimaryKeySelective(HeraAppBaseInfo record);

    int updateByPrimaryKeyWithBLOBs(HeraAppBaseInfo record);

    int updateByPrimaryKey(HeraAppBaseInfo record);

    int batchInsert(@Param("list") List<HeraAppBaseInfo> list);

    int batchInsertSelective(@Param("list") List<HeraAppBaseInfo> list, @Param("selective") HeraAppBaseInfo.Column ... selective);
}