package com.xiaomi.mone.app.dao.mapper;

import com.xiaomi.mone.app.model.HeraProjectGroupUser;
import com.xiaomi.mone.app.model.HeraProjectGroupUserExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HeraProjectGroupUserMapper {
    long countByExample(HeraProjectGroupUserExample example);

    int deleteByExample(HeraProjectGroupUserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(HeraProjectGroupUser record);

    int insertSelective(HeraProjectGroupUser record);

    List<HeraProjectGroupUser> selectByExample(HeraProjectGroupUserExample example);

    HeraProjectGroupUser selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") HeraProjectGroupUser record, @Param("example") HeraProjectGroupUserExample example);

    int updateByExample(@Param("record") HeraProjectGroupUser record, @Param("example") HeraProjectGroupUserExample example);

    int updateByPrimaryKeySelective(HeraProjectGroupUser record);

    int updateByPrimaryKey(HeraProjectGroupUser record);

    int batchInsert(@Param("list") List<HeraProjectGroupUser> list);

    int batchInsertSelective(@Param("list") List<HeraProjectGroupUser> list, @Param("selective") HeraProjectGroupUser.Column ... selective);
}