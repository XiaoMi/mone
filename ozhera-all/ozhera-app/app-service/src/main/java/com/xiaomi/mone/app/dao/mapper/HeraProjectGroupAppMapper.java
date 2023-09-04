package com.xiaomi.mone.app.dao.mapper;

import com.xiaomi.mone.app.model.HeraProjectGroupApp;
import com.xiaomi.mone.app.model.HeraProjectGroupAppExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HeraProjectGroupAppMapper {
    long countByExample(HeraProjectGroupAppExample example);

    int deleteByExample(HeraProjectGroupAppExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(HeraProjectGroupApp record);

    int insertSelective(HeraProjectGroupApp record);

    List<HeraProjectGroupApp> selectByExample(HeraProjectGroupAppExample example);

    HeraProjectGroupApp selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") HeraProjectGroupApp record, @Param("example") HeraProjectGroupAppExample example);

    int updateByExample(@Param("record") HeraProjectGroupApp record, @Param("example") HeraProjectGroupAppExample example);

    int updateByPrimaryKeySelective(HeraProjectGroupApp record);

    int updateByPrimaryKey(HeraProjectGroupApp record);

    int batchInsert(@Param("list") List<HeraProjectGroupApp> list);

    int batchInsertSelective(@Param("list") List<HeraProjectGroupApp> list, @Param("selective") HeraProjectGroupApp.Column ... selective);
}