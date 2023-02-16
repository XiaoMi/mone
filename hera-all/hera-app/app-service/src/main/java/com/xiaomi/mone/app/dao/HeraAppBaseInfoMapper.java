package com.xiaomi.mone.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.app.model.HeraAppBaseInfo;
import com.xiaomi.mone.app.model.HeraAppBaseInfoExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 12:13
 */
@Component
public interface HeraAppBaseInfoMapper extends BaseMapper<HeraAppBaseInfo> {

    List<AppBaseInfo> queryAppInfoWithLog(String appName, Integer type);

    List<AppBaseInfo> queryByIds(List<Long> ids);

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

    Long countNormalData();
}
